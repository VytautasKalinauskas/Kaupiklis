package com.kaupiklis.vytkal.meniu;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by vytkal on 12/27/2017.
 */

public class ScannerDBActivity extends AppCompatActivity implements View.OnClickListener {

    private String barcode;
    private String dokNr;
    private Bundle extras;
    private TextView prekesPav;
    private TextView prekesKaina;
    private TextView prekesLikutis;
    private EditText textAmount;
    private Button save;
    private Button finish;
    private DatabaseHelper db;
    private MessageHelper messageHelper;
    private String pavadinimas;
    private Float kaina;
    private Integer likutis;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner_db);

        db  = new DatabaseHelper(this);
        messageHelper = new MessageHelper();


        save = findViewById(R.id.saugoti);
        finish = findViewById(R.id.baigti);
        textAmount = findViewById(R.id.amount);

        save.setOnClickListener(this);
        finish.setOnClickListener(this);

        if (savedInstanceState == null)
            extras = getIntent().getExtras();
        if (extras == null) {
            barcode = null;
            dokNr = null;
        } else {
            barcode = extras.getString("barcode");
            dokNr = extras.getString("doknr");
            pavadinimas = extras.getString("pavadinimas");
            kaina = extras.getFloat("kaina");
            likutis = extras.getInt("likutis");

        }


        TextView textBarcode = findViewById(R.id.barcode);
        TextView textDokNr = findViewById(R.id.dokNr);
        prekesPav = findViewById(R.id.prekesPav);
        prekesKaina = findViewById(R.id.prekesKaina);
        prekesLikutis = findViewById(R.id.prekesLikutis);

        prekesKaina.setText(Float.toString(kaina));
        prekesLikutis.setText(Integer.toString(likutis));
        textDokNr.setText(dokNr);
        textBarcode.setText(barcode);
        prekesPav.setText(pavadinimas);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBackPressed(){
        Intent intent;
        if (db.getScanSettings() == 1) {
            intent = new Intent(ScannerDBActivity.this, ScannerActivity.class);
        }
        else {
            intent = new Intent(ScannerDBActivity.this, BarcodeInputActivity.class);
        }

        intent.putExtra("doknr", dokNr);
        ScannerDBActivity.this.startActivity(intent);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            Intent intent;
            if (db.getScanSettings() == 1) {
                intent = new Intent(ScannerDBActivity.this, ScannerActivity.class);
            }
            else {
                intent = new Intent(ScannerDBActivity.this, BarcodeInputActivity.class);
            }

            intent.putExtra("doknr", dokNr);
            ScannerDBActivity.this.startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.baigti:

                Integer amount = Integer.valueOf(textAmount.getText().toString());
                boolean isInserted = db.insertData(dokNr,barcode, amount, Float.parseFloat(prekesKaina.getText().toString()));
                boolean isInsertedArchive = db.insertDataArchive(dokNr,barcode, amount, Float.parseFloat(prekesKaina.getText().toString()));
                if(isInserted == true && isInsertedArchive == true) {
                    messageHelper.showToast(ScannerDBActivity.this, "Įrašyta sėkmingai");
                }
                else
                    messageHelper.showToast(ScannerDBActivity.this, "Įrašyti nepavyko");
                Intent menu = new Intent(ScannerDBActivity.this, MainActivity.class);
                ScannerDBActivity.this.startActivity(menu);
                finish();
                break;

            case R.id.saugoti:

                amount = Integer.valueOf(textAmount.getText().toString());
                isInserted = db.insertData(dokNr,barcode, amount, Float.parseFloat(prekesKaina.getText().toString()));
                isInsertedArchive = db.insertDataArchive(dokNr,barcode, amount, Float.parseFloat(prekesKaina.getText().toString()));
                Intent intent;
                if(isInserted == true && isInsertedArchive == true) {
                    messageHelper.showToast(ScannerDBActivity.this, "Įrašyta sėkmingai");
                    if (db.getScanSettings() == 1) {
                        intent = new Intent(ScannerDBActivity.this, ScannerActivity.class);
                    }
                    else {
                        intent = new Intent(ScannerDBActivity.this, BarcodeInputActivity.class);
                    }

                    intent.putExtra("doknr", dokNr);
                    ScannerDBActivity.this.startActivity(intent);
                    finish();
                }
                else {
                    messageHelper.showToast(ScannerDBActivity.this, "Įrašyti nepavyko");
                    Intent scanner = new Intent(ScannerDBActivity.this, ScannerActivity.class);
                    scanner.putExtra("doknr", dokNr);
                    ScannerDBActivity.this.startActivity(scanner);
                    finish();
                }
                break;
                }



        }
    }


