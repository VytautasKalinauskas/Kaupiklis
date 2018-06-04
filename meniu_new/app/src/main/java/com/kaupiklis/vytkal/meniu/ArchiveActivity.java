package com.kaupiklis.vytkal.meniu;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by vytkal on 2/24/2018.
 */

public class ArchiveActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {


    EditText dateFrom;
    EditText dateTo;
    EditText doknr;
    CheckBox checkdate;
    CheckBox checkdoknr;
    Calendar myCalendar;
    MessageHelper messageHelper;
    FtpHelper ftpHelper;
    Button filter;
    boolean isdateto;
    DatabaseHelper db;
    Cursor res;
    Bundle extras;
    String imei;
    ProgressDialog progressDialog;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.archive);
         myCalendar = Calendar.getInstance();
         db = new DatabaseHelper(this);
         messageHelper = new MessageHelper();
         ftpHelper = new FtpHelper();
         progressDialog = new ProgressDialog(ArchiveActivity.this);

        if (savedInstanceState == null)
            extras = getIntent().getExtras();
        if (extras == null) {
            imei = null;

        } else {
            imei = extras.getString("imei");
        }

        dateFrom= findViewById(R.id.editdatefrom);
        dateTo = findViewById(R.id.editdateto);
        doknr = findViewById(R.id.editdoknr);
        checkdate = findViewById(R.id.checkboxdate);
        checkdoknr = findViewById(R.id.checkboxdoknr);
        filter = findViewById(R.id.buttonfilterarchive);

        filter.setOnClickListener(this);

        dateTo.setOnClickListener(this);
        dateFrom.setOnClickListener(this);

        checkdate.setOnCheckedChangeListener(this);
        checkdoknr.setOnCheckedChangeListener(this);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    @Override
    public void onBackPressed(){

        Intent intent = new Intent(ArchiveActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(ArchiveActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void updateLabel() {
        String myFormat = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        if(isdateto) {
            dateTo.setText(sdf.format(myCalendar.getTime()));
            dateTo.clearFocus();
        }
        else {
            dateFrom.setText(sdf.format(myCalendar.getTime()));
            dateFrom.clearFocus();
        }
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void exportData() {

        final StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            buffer.append(res.getString(0) + ",");
            buffer.append(res.getString(1) + ",");
            buffer.append(res.getInt(2) + ",");
            buffer.append(res.getString(4) + "\n");
        }


        MessageHelper.displayAlertMessage("Atrinkti įrašai", buffer.toString(), "Eksportuoti", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                progressDialog.setTitle("Prašome palaukti");
                progressDialog.setMessage("Vyksta duomenų eksportas");
                progressDialog.setCancelable(false);
                progressDialog.show();

                Runnable r = new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {

                        if (ftpHelper.verification(imei,db.getLicenseNr()) == -1){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.cancel();
                                    MessageHelper.showMessage("Nepavyko prisijungti prie FTP", "Patikrinkite interneto ryšį", ArchiveActivity.this, null);

                                }
                            });
                            return;
                        }

                        else if (ftpHelper.verification(imei,db.getLicenseNr()) < 1) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.cancel();
                                    MessageHelper.showMessage("Nelicenzijuota", "", ArchiveActivity.this, null);
                                }
                            });
                            db.deleteAllFromRegistracija();
                            return;
                        }


                        String path = Environment.getExternalStorageDirectory().toString()+"/rezultatai.txt";

                        if (checkdoknr.isChecked() && checkdate.isChecked()) {
                            res = db.getDataArchiveByDateAndDokNr(doknr.getText().toString(), dateFrom.getText().toString(), dateTo.getText().toString());
                        }

                        else if (checkdoknr.isChecked()) {
                            res = db.getDataArchiveByDokNr(doknr.getText().toString());
                        }

                        else if (checkdate.isChecked()) {
                            res = db.getDataArchiveByDate(dateFrom.getText().toString(), dateTo.getText().toString());
                        }

                        boolean append = ftpHelper.downloadFile(db.getFtpAdress(), db.getFtpUsername(), db.getFtpPassword(), db.getFtpDirectory(), "rezultatai.txt", "rezultatai.txt");
                        db.exportDB(path,res,append);
                        final boolean uploaded = ftpHelper.uploadFile(path, db.getFtpAdress(), db.getFtpUsername(),
                                db.getFtpPassword(), db.getFtpDirectory(), "rezultatai.txt");
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if(uploaded){
                                    progressDialog.cancel();
                                    MessageHelper.showMessage("Eksportuota sėkmingai", "", ArchiveActivity.this, null);
                                }
                                else {
                                    progressDialog.cancel();
                                    MessageHelper.showMessage("Eksportuoti nepavyko", "patikrinkite FTP parametrus", ArchiveActivity.this, null);
                                }
                            }
                        });
                    }
                };
                Thread upload = new Thread(r);
                upload.start();

            }
        },ArchiveActivity.this);
    }



    @Override
    public void onCheckedChanged(CompoundButton v, boolean b) {

        switch (v.getId()) {

            case R.id.checkboxdate:

                if(checkdate.isChecked()){
                    dateFrom.setVisibility(View.VISIBLE);
                    dateTo.setVisibility(View.VISIBLE);
                }
                else {
                    dateFrom.setVisibility(View.INVISIBLE);
                    dateTo.setVisibility(View.INVISIBLE);
                }
                break;

            case R.id.checkboxdoknr:

                if(checkdoknr.isChecked()) {
                    doknr.setVisibility(View.VISIBLE);
                }
                else {
                    doknr.setVisibility(View.INVISIBLE);
                }
                break;

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.editdatefrom:

                new DatePickerDialog(ArchiveActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                isdateto = false;
                break;

            case R.id.editdateto:

                new DatePickerDialog(ArchiveActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                isdateto = true;
                break;

            case R.id.buttonfilterarchive:

                if (checkdoknr.isChecked() && checkdate.isChecked()) {

                    res = db.getDataArchiveByDateAndDokNr(doknr.getText().toString(), dateFrom.getText().toString(), dateTo.getText().toString());

                    if(res.getCount() == 0) {
                        // show message
                        MessageHelper.showMessage("Klaida","Įrašų nerasta", ArchiveActivity.this, null);
                        break;

                    }

                    else {

                        exportData();
                    }

                }

                else if (checkdoknr.isChecked())  {

                    res = db.getDataArchiveByDokNr(doknr.getText().toString());
                    if(res.getCount() == 0) {
                        // show message
                        MessageHelper.showMessage("Klaida","Įrašų nerasta", ArchiveActivity.this, null);
                        break;

                    }

                    else {

                        exportData();
                    }

                }


                else if (checkdate.isChecked()) {

                    res = db.getDataArchiveByDate(dateFrom.getText().toString(), dateTo.getText().toString());
                    if(res.getCount() == 0) {
                        MessageHelper.showMessage("Klaida","Įrašų nerasta", ArchiveActivity.this, null);
                        break;

                    }

                    else {

                        exportData();
                    }

                }
                break;


        }
    }
}
