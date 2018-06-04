package com.kaupiklis.vytkal.meniu;

import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by vytkal on 12/26/2017.
 */

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView scannerView;
    private Bundle extras;
    private String dokNr;
    final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 150);
    private DatabaseHelper db;
    private PermissionHelper permissionHelper;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db  = new DatabaseHelper(this);
        permissionHelper = new PermissionHelper();

        if (savedInstanceState == null)
            extras = getIntent().getExtras();
        if (extras == null) {
            dokNr = null;
        } else {
            dokNr = extras.getString("doknr");
        }

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (!permissionHelper.checkPermission(ScannerActivity.this, CAMERA))
            {
                permissionHelper.requestPermission(ScannerActivity.this, CAMERA);
            }
        }
    }

    public void reload(String key, String value) {
        Intent mIntent = getIntent();
        mIntent.putExtra(key, value);
        finish();
        startActivity(mIntent);
    }

    @Override
    public void onBackPressed() {
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
            Intent myIntent = new Intent(ScannerActivity.this, DocumentNrInputActivity.class);
            myIntent.putExtra("prefix", prefix);
            startActivity(myIntent);
            finish();
    }


    @Override
    public void onResume()
    {
        super.onResume();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(permissionHelper.checkPermission(ScannerActivity.this, CAMERA))
            {
                if(scannerView == null)
                {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            }
            else
            {
                permissionHelper.requestPermission(ScannerActivity.this, CAMERA);
            }
        }

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        scannerView.stopCamera();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void handleResult(Result result) {


        final String myResult = result.getText();
        tg.startTone(ToneGenerator.TONE_PROP_BEEP);
        Cursor cursor = db.getNameByBarcode(myResult);
        int checkOption = db.getCheckSettings();
        int autoOption = db.getAutoSettings();

        switch (checkOption){

            case 1:

                if (cursor.moveToFirst() && (cursor.getCount() > 0)) {

                    if (autoOption == 1) {

                        boolean isInserted = db.insertData(dokNr, myResult, 1, cursor.getFloat(1));
                        boolean isInsertedArchive = db.insertDataArchive(dokNr, myResult, 1, cursor.getFloat(1));
                        if(isInserted == true && isInsertedArchive == true) {
                            Toast.makeText(ScannerActivity.this, "Įrašas išsaugotas", Toast.LENGTH_LONG).show();
                            reload("doknr", dokNr);

                        } else {
                            Toast.makeText(ScannerActivity.this, "Įrašo išsaugoti nepavyko", Toast.LENGTH_LONG).show();
                            reload("doknr", dokNr);
                        }

                    } else {

                        String pavadinimas = cursor.getString(0);
                        Float kaina = (cursor.getFloat(1));
                        Integer likutis = (cursor.getInt(2));
                        Intent intent = new Intent(ScannerActivity.this, ScannerDBActivity.class);
                        intent.putExtra("pavadinimas", pavadinimas);
                        intent.putExtra("kaina", kaina);
                        intent.putExtra("likutis", likutis);
                        intent.putExtra("barcode", myResult);
                        intent.putExtra("doknr", dokNr);
                        ScannerActivity.this.startActivity(intent);
                        finish();
                    }

                } else {
                    MessageHelper.showMessage("Klaida", "Barkodas nerastas", ScannerActivity.this,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    reload("doknr", dokNr);
                                }
                            });
                }

                break;

            case 0:

                cursor = db.getNameByBarcode(myResult);

                if (autoOption == 1) {

                    boolean isInserted = db.insertData(dokNr, myResult, 1, db.getPriceByBarcode(myResult));
                    boolean isInsertedArchive = db.insertDataArchive(dokNr, myResult, 1, db.getPriceByBarcode(myResult));
                    if(isInserted == true && isInsertedArchive == true) {
                        Toast.makeText(ScannerActivity.this, "Įrašas išsaugotas", Toast.LENGTH_LONG).show();
                        reload("doknr", dokNr);

                    } else {
                        Toast.makeText(ScannerActivity.this, "Įrašo išsaugoti nepavyko", Toast.LENGTH_LONG).show();
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

                    Intent intent = new Intent(ScannerActivity.this, ScannerDBActivity.class);
                    intent.putExtra("pavadinimas", pavadinimas);
                    intent.putExtra("kaina", kaina);
                    intent.putExtra("likutis", likutis);
                    intent.putExtra("barcode", myResult);
                    intent.putExtra("doknr", dokNr);
                    ScannerActivity.this.startActivity(intent);
                    finish();
                }

                break;

        }

    }
}
