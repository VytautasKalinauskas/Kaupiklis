package com.kaupiklis.vytkal.meniu;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Egle on 2018-03-14.
 */

public class AdminSettingsActivity extends AppCompatActivity implements View.OnClickListener {

    DatabaseHelper myDb;
    EditText ftpAdress;
    EditText ftpUsername;
    EditText ftpPassword;
    EditText ftpDirectory;
    Button save;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adminsettings);
        myDb = new DatabaseHelper(this);

        ftpAdress = findViewById(R.id.ftpadress);
        ftpUsername = findViewById(R.id.ftpusername);
        ftpPassword = findViewById(R.id.ftppassword);
        ftpDirectory = findViewById(R.id.ftpdirectory);
        save = findViewById(R.id.buttonsaveadminsettings);
        save.setOnClickListener(this);

        ftpAdress.setText(myDb.getFtpAdress());
        ftpUsername.setText(myDb.getFtpUsername());
        ftpPassword.setText(myDb.getFtpPassword());
        ftpDirectory.setText(myDb.getFtpDirectory());

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
   }

    @Override
    public void onBackPressed(){

        Intent intent = new Intent(AdminSettingsActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(AdminSettingsActivity.this, SettingsActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.buttonsaveadminsettings: {

                MessageHelper messageHelper = new MessageHelper();

                boolean save = myDb.insertFtpData(ftpAdress.getText().toString(), ftpUsername.getText().toString(),
                        ftpPassword.getText().toString(), ftpDirectory.getText().toString());
                if (save){
                    messageHelper.showToast(AdminSettingsActivity.this, "Išsaugota sėkmingai");
                    Intent intent = new Intent(AdminSettingsActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    messageHelper.showToast(AdminSettingsActivity.this, "Išsaugoti nepavyko");
                }

            }

        }

    }
}
