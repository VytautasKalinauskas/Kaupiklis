package com.kaupiklis.vytkal.meniu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnKeyListener;
import android.widget.Toast;

import java.util.Scanner;

/**
 * Created by vytkal on 1/30/2018.
 */

public class BarcodeInputActivity extends AppCompatActivity implements OnKeyListener {

    private EditText textBarcode;
    private Button next;
    private Bundle extras;
    private String dokNr;
    private DatabaseHelper db;
    private MessageHelper messageHelper;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_input);
        db = new DatabaseHelper(this);
        messageHelper = new MessageHelper();

        textBarcode = findViewById(R.id.editBarcode);
        textBarcode.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(textBarcode, InputMethodManager.SHOW_IMPLICIT);

        next = findViewById(R.id.buttonNext);

        if (db.getAutoSettings() == 1) {
            next.setText("Baigti darbą");
        }

        if (savedInstanceState == null)
            extras = getIntent().getExtras();
        if (extras == null) {
            dokNr = null;
        } else {
            dokNr = extras.getString("doknr");
        }


        textBarcode.setOnKeyListener(this);
        finishScan();

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    @Override
    public void onBackPressed(){

        String prefix;
        switch (dokNr.substring(0,3)) {
            case "Pi:":
                prefix = "Pi: ";
                break;

            case "Pa:":
                prefix = "Pa: ";
                break;

            default:
                prefix = "In: ";
                break;
        }
        Intent myIntent = new Intent(BarcodeInputActivity.this, DocumentNrInputActivity.class);
        myIntent.putExtra("prefix", prefix);
        startActivity(myIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String prefix;
        if(item.getItemId() == android.R.id.home) {
            switch (dokNr.substring(0,3)) {
                case "Pi:":
                    prefix = "Pi: ";
                    break;

                case "Pa:":
                    prefix = "Pa: ";
                    break;

                default:
                    prefix = "In: ";
                    break;
            }
            Intent myIntent = new Intent(BarcodeInputActivity.this, DocumentNrInputActivity.class);
            myIntent.putExtra("prefix", prefix);
            startActivity(myIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onKey(View view, int keycode, KeyEvent keyEvent) {

        if (keyEvent.getAction() == KeyEvent.ACTION_UP) {

            if (keycode == KeyEvent.KEYCODE_ENTER) {
                int autoOption = db.getAutoSettings();
                int checkOption = db.getCheckSettings();
                Cursor cursor = db.getNameByBarcode(textBarcode.getText().toString());

                switch (checkOption) {

                    case 1:

                        if (cursor.moveToFirst() && (cursor.getCount() > 0)) {

                            if (autoOption == 1) {

                                boolean isInserted = db.insertData(dokNr, textBarcode.getText().toString(), 1, cursor.getFloat(1));
                                boolean isInsertedArchive = db.insertDataArchive(dokNr, textBarcode.getText().toString(), 1, cursor.getFloat(1));
                                if (isInserted == true && isInsertedArchive == true) {
                                    messageHelper.showToast(BarcodeInputActivity.this, "Įrašyta sėkmingai");
                                    Intent mIntent = getIntent();
                                    mIntent.putExtra("doknr", dokNr);
                                    finish();
                                    startActivity(mIntent);
                                    return true;
                                } else {
                                    messageHelper.showToast(BarcodeInputActivity.this, "Įrašyti nepavyko");
                                }

                            } else {

                                String pavadinimas = cursor.getString(0);
                                Float kaina = (cursor.getFloat(1));
                                Integer likutis = (cursor.getInt(2));
                                Intent intent = new Intent(BarcodeInputActivity.this, ScannerDBActivity.class);
                                intent.putExtra("pavadinimas", pavadinimas);
                                intent.putExtra("kaina", kaina);
                                intent.putExtra("likutis", likutis);
                                intent.putExtra("barcode", textBarcode.getText().toString());
                                intent.putExtra("doknr", dokNr);
                                BarcodeInputActivity.this.startActivity(intent);
                                finish();
                            }

                        } else {
                            MessageHelper.showMessage("Klaida", "Barkodas nerastas",
                                    BarcodeInputActivity.this,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = getIntent();
                                            intent.putExtra("doknr", dokNr);
                                            finish();
                                            startActivity(intent);
                                        }
                                    });
                        }

                        break;

                    case 0:

                        cursor = db.getNameByBarcode(textBarcode.getText().toString());

                        if (autoOption == 1) {

                            boolean isInserted = db.insertData(dokNr, textBarcode.getText().toString(), 1, db.getPriceByBarcode(textBarcode.getText().toString()));
                            boolean isInsertedArchive = db.insertDataArchive(dokNr, textBarcode.getText().toString(), 1, db.getPriceByBarcode(textBarcode.getText().toString()));
                            if (isInserted == true && isInsertedArchive == true) {
                                messageHelper.showToast(BarcodeInputActivity.this, "Įrašyta sėkmingai");
                                Intent mIntent = getIntent();
                                mIntent.putExtra("doknr", dokNr);
                                finish();
                                startActivity(mIntent);
                                return true;
                            } else {
                                messageHelper.showToast(BarcodeInputActivity.this, "Įrašyti nepavyko");
                            }

                        } else {
                            String pavadinimas;
                            Float kaina;
                            Integer likutis;

                            if (cursor.moveToFirst()) {
                                pavadinimas = cursor.getString(0);
                                kaina = (cursor.getFloat(1));
                                likutis = (cursor.getInt(2));
                            }
                            else {
                                pavadinimas = "";
                                kaina = 0.0f;
                                likutis = 0;
                            }

                            Intent intent = new Intent(BarcodeInputActivity.this, ScannerDBActivity.class);
                            intent.putExtra("pavadinimas", pavadinimas);
                            intent.putExtra("kaina", kaina);
                            intent.putExtra("likutis", likutis);
                            intent.putExtra("barcode", textBarcode.getText().toString());
                            intent.putExtra("doknr", dokNr);
                            BarcodeInputActivity.this.startActivity(intent);
                            finish();
                        }

                        break;


                }
            }

        }
        return false;
    }

    public  void finishScan() {
        next.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                         Intent menu = new Intent(BarcodeInputActivity.this, MainActivity.class);
                         startActivity(menu);
                         finish();
                    }
                }
        );
    }
}
