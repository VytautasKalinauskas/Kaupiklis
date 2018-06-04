package com.kaupiklis.vytkal.meniu;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mToggle;
    DatabaseHelper myDb;
    FtpHelper ftpHelper;
    MessageHelper messageHelper;
    TelephonyManager tm;
    Handler handler;
    TextView textLicenseOwner;
    TextView textLicenseNumber;
    ProgressDialog progressDialog;
    public static final int MULTIPLE_PERMISSIONS = 10;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setNavigationViewListener();
        myDb = new DatabaseHelper(this);
        ftpHelper = new FtpHelper();
        messageHelper = new MessageHelper();
        handler = new Handler(getApplicationContext().getMainLooper());
        progressDialog = new ProgressDialog(MainActivity.this);
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        setTitle("Pradžia");


        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        textLicenseOwner = headerView.findViewById(R.id.navigation_license_owner);
        textLicenseNumber = headerView.findViewById(R.id.navigation_license_number);


        if (myDb.getCompanyName().length() > 0 && myDb.getLicenseNr() != -1) {

            textLicenseOwner.setText(myDb.getCompanyName());
            textLicenseNumber.setText("Licenzijos nr: " + myDb.getLicenseNr());
            textLicenseNumber.setVisibility(View.VISIBLE);
        }
        else {
            textLicenseOwner.setText("Demo versija");
            textLicenseNumber.setVisibility(View.INVISIBLE);
        }


        mDrawerLayout = findViewById(R.id.drawerLayout);
        mDrawerLayout.openDrawer(GravityCompat.START);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        String[] permissions= new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE};

        checkPermissions(permissions);

    }

    private  boolean checkPermissions(String[] permissions) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(MainActivity.this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_scan: {

                Intent myIntent = new Intent(MainActivity.this, DocumentNrInputActivity.class);
                myIntent.putExtra("prefix", "In: ");
                MainActivity.this.startActivity(myIntent);
                finish();
                break;
            }

            case R.id.nav_purchasing: {

                Intent myIntent = new Intent(MainActivity.this, DocumentNrInputActivity.class);
                myIntent.putExtra("prefix", "Pi: ");
                MainActivity.this.startActivity(myIntent);
                finish();
                break;
            }

            case R.id.nav_selling: {

                Intent myIntent = new Intent(MainActivity.this, DocumentNrInputActivity.class);
                myIntent.putExtra("prefix", "Pa: ");
                MainActivity.this.startActivity(myIntent);
                finish();
                break;
            }

            case R.id.nav_list: {

                /*Cursor res = myDb.getAllDataProducts();
                if(res.getCount() == 0){
                    MessageHelper.showMessage("Klaida", "Nera duomenu", MainActivity.this);
                    break;
                }
                else
                    buffer = new StringBuffer();
                    while(res.moveToNext()) {

                        buffer.append("Barkodas :"+ res.getString(0)+"\n");
                        buffer.append("Pavadinimas :"+ res.getString(1)+"\n");
                        buffer.append("Kaina :"+ res.getFloat(2)+"\n");
                        buffer.append("Savikaina :"+ res.getFloat(3)+"\n");
                        buffer.append("Likutis :"+ res.getInt(4)+"\n\n");

                    }
                MessageHelper.showMessage("Inventorizacijos duomenys", buffer.toString(), MainActivity.this);*/

                Intent myIntent = new Intent(MainActivity.this, ProductListActivity.class);
                startActivity(myIntent);
                finish();

                break;
            }
            case  R.id.nav_export: {

                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED ) {

                    MessageHelper.showMessage("Eksportas negalimas", "Prašome suteikti leidimus", MainActivity.this, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            startActivity(getIntent());
                        }
                    });

                }

                else {

                   exportData();

                }

                break;

            }
            case R.id.nav_import: {


                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {


                    MessageHelper.showMessage("Importas negalimas", "Prašome suteikti leidimus", MainActivity.this, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            startActivity(getIntent());
                        }
                    });

                } else {

                    importData();

                }

                break;
            }

            /*case R.id.editInventorisationData: {

                Intent myIntent = new Intent(MainActivity.this, InventorisationListActivity.class);
                MainActivity.this.startActivity(myIntent);

                break;
            }*/

            case R.id.nav_settings: {

                Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
                MainActivity.this.startActivity(myIntent);
                finish();

                break;
            }

            case R.id.nav_archive: {

                Intent myIntent = new Intent(MainActivity.this, ArchiveActivity.class);
                myIntent.putExtra("imei", tm.getDeviceId());
                MainActivity.this.startActivity(myIntent);
                finish();

                break;

            }

            case R.id.nav_register: {

                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {


                    MessageHelper.showMessage("Registracija negalima", "Prašome suteikti leidimus", MainActivity.this, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            startActivity(getIntent());
                        }
                    });

                } else {

                    register();

                }

                break;
            }

            case R.id.nav_contacts: {

                Intent myIntent = new Intent(MainActivity.this, ContactActivity.class);
                MainActivity.this.startActivity(myIntent);
                finish();
                break;

            }

            case R.id.nav_exit: {

                finish();
                System.exit(0);
                break;

            }

        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed(){

    }



    private void setNavigationViewListener() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void register() {

        final EditText input = new EditText(this);

        MessageHelper.displayRegistrationDialog(MainActivity.this, input, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                Runnable r = new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {

                        final boolean connected = ftpHelper.ftpConnect("ftp.berzuna.lt", "virgis", "Faksas37", 21);
                        if (connected) {
                            try {
                                final FTPFile[] files = ftpHelper.mFTPClient.listFiles("/dk/"+input.getText().toString()+".txt");
                                final boolean downloaded;

                                if (files.length > 0)
                                    downloaded = ftpHelper.ftpDownload("/dk/"+input.getText().toString()+".txt", Environment.getExternalStorageDirectory().toString() + "/register.txt");
                                else
                                    downloaded = false;

                                final boolean registered;

                                if (downloaded) {
                                    registered = myDb.register();
                                }

                                else {
                                    registered = false;
                                }

                                if (registered) {
                                    ftpHelper.mFTPClient.rename("/dk/" + input.getText().toString() + ".txt", "/dk/" + myDb.getLicenseNr() + "-" + tm.getDeviceId()+".txt");
                                }

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (registered) {
                                            MessageHelper.showMessage("Užregistruota sėkmingai", "Galite naudotis programa", MainActivity.this, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    finish();
                                                    startActivity(getIntent());
                                                }
                                            });

                                        }

                                        else
                                            MessageHelper.showMessage("Užregistruoti nepavyko", "Neteisingas registracijos kodas", MainActivity.this, null);
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        else {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MessageHelper.showMessage("Nepavyko prisijungti", "Patikrinkite interneto ryšį", MainActivity.this, null);
                                }
                            });
                        }

                    }
                };

                Thread naujagija = new Thread(r);
                naujagija.start();

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void exportData() {
        final Cursor res = myDb.getDataInventorisation();
        if (res.getCount() == 0) {
            MessageHelper.showMessage("Klaida", "Nėra įrašų", MainActivity.this, null);
            return;
        }

        progressDialog.setTitle("Prašome palaukti");
        progressDialog.setMessage("Vyksta duomenų eksportas");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final Runnable r = new Runnable() {
            @Override
            public void run() {

                if (ftpHelper.verification(tm.getDeviceId(), myDb.getLicenseNr()) == -1){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.cancel();
                            MessageHelper.showMessage("Nepavyko prisijungti prie FTP", "Patikrinkite interneto ryšį", MainActivity.this, null);

                        }
                    });
                    return;
                }

                else if (ftpHelper.verification(tm.getDeviceId(), myDb.getLicenseNr()) < 1) {

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.cancel();
                            MessageHelper.showMessage("Nelicenzijuota", "", MainActivity.this, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                    startActivity(getIntent());
                                }
                            });
                        }
                    });
                    myDb.deleteAllFromRegistracija();
                    return;
                }

                String path = Environment.getExternalStorageDirectory().toString()+"/rezultatai.txt";
                boolean append = ftpHelper.downloadFile(myDb.getFtpAdress(), myDb.getFtpUsername(), myDb.getFtpPassword(), myDb.getFtpDirectory(), "rezultatai.txt", "rezultatai.txt");

                myDb.exportDB(path, res, append);
                final boolean uploaded = ftpHelper.uploadFile(path, myDb.getFtpAdress(), myDb.getFtpUsername(),
                        myDb.getFtpPassword(), myDb.getFtpDirectory(), "rezultatai.txt");

                handler.post(new Runnable() {
                    public void run() {
                        if(uploaded) {

                            myDb.deleteAllFromInventorizacija();
                            progressDialog.cancel();
                            MessageHelper.showMessage("Eksportuota sėkmingai", "", MainActivity.this, null);
                        }

                        else {
                            progressDialog.cancel();
                            MessageHelper.showMessage("Eksportuoti nepavyko", "Patikrinkite FTP parametrus", MainActivity.this, null);
                        }

                    }
                });

            }
        };
        Thread export = new Thread(r);
        export.start();
    }

    private void importData() {

        progressDialog.setTitle("Prašome palaukti");
        progressDialog.setMessage("Vyksta duomenų importas");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Runnable r = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {

                if (ftpHelper.verification(tm.getDeviceId(), myDb.getLicenseNr()) == -1){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.cancel();
                            MessageHelper.showMessage("Nepavyko prisijungti prie FTP", "Patikrinkite interneto ryšį", MainActivity.this, null);

                        }
                    });
                    return;
                }

                else if (ftpHelper.verification(tm.getDeviceId(), myDb.getLicenseNr()) < 1) {

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.cancel();
                            MessageHelper.showMessage("Nelicenzijuota", "", MainActivity.this, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                    startActivity(getIntent());
                                }
                            });
                        }
                    });
                    myDb.deleteAllFromRegistracija();
                    return;
                }


                boolean isDownloaded = ftpHelper.downloadFile(myDb.getFtpAdress(), myDb.getFtpUsername(), myDb.getFtpPassword(), myDb.getFtpDirectory(), "data.txt", "data.txt");
                if (!isDownloaded) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.cancel();
                            MessageHelper.showMessage("Importuoti nepavyko", "Patikrinkite FTP parametrus", MainActivity.this, null);
                        }
                    });
                    return;
                }
                final boolean isLoaded = myDb.loadProduktai();
                handler.post(new Runnable() {
                    public void run() {
                        if(isLoaded) {

                            progressDialog.cancel();
                            MessageHelper.showMessage("Importuota sėkmingai", "", MainActivity.this, null);
                        }

                        else {
                            progressDialog.cancel();
                            MessageHelper.showMessage("Importuoti nepavyko", "Patikrinkite FTP parametrus", MainActivity.this, null);
                        }

                    }
                });

            }
        };
        Thread naujagija = new Thread(r);
        naujagija.start();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // permissions granted.
                } else {
                    String permissionss = "";
                    for (String per : permissions) {
                        permissionss += "\n" + per;
                    }
                    // permissions list of don't granted permission
                }
                return;
            }
        }
    }

    }


