package com.kaupiklis.vytkal.meniu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Set;

/**
 * Created by vytkal on 2/11/2018.
 */

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {



    Button saveButton;
    Button adminSettings;
    DatabaseHelper myDb;
    Switch switchCamera;
    Switch switchCheck;
    Switch switchAuto;
    int scanOption;
    int checkOption;
    int autoOption;
    MessageHelper messageHelper;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        myDb = new DatabaseHelper(this);
        messageHelper = new MessageHelper();
        saveButton = findViewById(R.id.buttonSettings);
        adminSettings = findViewById(R.id.buttonadminsettings);

        saveButton.setOnClickListener(this);
        adminSettings.setOnClickListener(this);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        CompoundButton.OnCheckedChangeListener multiListener = new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton v, boolean b) {
                switch (v.getId()) {

                    case R.id.switchcamera:
                        if(switchCamera.isChecked()) {
                            scanOption = 1;
                        }
                        else {
                            scanOption = 0;
                        }
                        break;

                    case R.id.switchcheck:
                        if (switchCheck.isChecked()) {
                            checkOption = 1;
                        }
                        else {
                            checkOption = 0;
                        }
                        break;

                    case R.id.switchauto:
                        if (switchAuto.isChecked()) {
                            autoOption = 1;
                        }
                        else {
                            autoOption = 0;
                        }
                        break;

                }
            }

        };


        switchCamera = findViewById(R.id.switchcamera);
        switchCamera.setOnCheckedChangeListener(multiListener);
        scanOption = myDb.getScanSettings();

        if (scanOption == 1) {
            switchCamera.setChecked(true);
        }
        else {
            switchCamera.setChecked(false);
        }

        switchCheck = findViewById(R.id.switchcheck);
        switchCheck.setOnCheckedChangeListener(multiListener);
        checkOption = myDb.getCheckSettings();

        if (checkOption == 1) {
            switchCheck.setChecked(true);
        }
        else {
            switchCheck.setChecked(false);
        }

        switchAuto = findViewById(R.id.switchauto);
        switchAuto.setOnCheckedChangeListener(multiListener);
        autoOption = myDb.getAutoSettings();


        if (autoOption == 1) {
            switchAuto.setChecked(true);
        }
        else {
            switchAuto.setChecked(false);
        }

    }

    @Override
    public void onBackPressed(){
        Intent myIntent = new Intent(SettingsActivity.this, MainActivity.class);
        SettingsActivity.this.startActivity(myIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            Intent myIntent = new Intent(SettingsActivity.this, MainActivity.class);
            SettingsActivity.this.startActivity(myIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.buttonSettings:


                boolean saved = myDb.setSettings(scanOption, checkOption, autoOption);
                if (saved)
                    messageHelper.showToast(SettingsActivity.this, "Išsaugota sėkmingai");
                else
                    Toast.makeText(this, "Nepavyko išsaugoti", Toast.LENGTH_LONG).show();

                Intent myIntent = new Intent(SettingsActivity.this, MainActivity.class);
                SettingsActivity.this.startActivity(myIntent);
                finish();
                break;


            case R.id.buttonadminsettings:

                final EditText input = new EditText(this);
                MessageHelper.displayAdminPasswordDialog(SettingsActivity.this, input, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(input.getText().toString().equalsIgnoreCase("rivile")) {
                            Intent myIntent2 = new Intent(SettingsActivity.this, AdminSettingsActivity.class);
                            SettingsActivity.this.startActivity(myIntent2);
                            finish();
                        }
                        else {
                            MessageHelper.showMessage("Slaptažodis neteisingas", "bandykite dar kartą", SettingsActivity.this, null);
                        }
                    }
                });


                break;
        }

    }

}
