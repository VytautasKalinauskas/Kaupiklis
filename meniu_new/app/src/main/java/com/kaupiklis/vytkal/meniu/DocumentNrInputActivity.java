package com.kaupiklis.vytkal.meniu;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by vytkal on 12/27/2017.
 */

public class DocumentNrInputActivity extends AppCompatActivity {

    EditText dokNr;
    Button scan;
    DatabaseHelper myDb;
    Bundle extras;
    String prefix;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.document_nr);
        myDb = new DatabaseHelper(this);

        if (savedInstanceState == null)
            extras = getIntent().getExtras();
        if (extras == null) {
            prefix = null;
        } else {
            prefix = extras.getString("prefix");
        }

        switch (prefix) {
            case "Pi: ": {
                setTitle("Pirkimai");
                break;
            }
            case "Pa: ": {
                setTitle("Pardavimai");
                break;
            }
            case "In: ": {
                setTitle("Inventorizacija");
                break;
            }
        }

        dokNr = findViewById(R.id.editBarcode);
        scan = findViewById(R.id.buttonNext);



        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int scantype = myDb.getScanSettings();
                Intent myIntent;
                if (scantype == 1) {
                    myIntent = new Intent(DocumentNrInputActivity.this, ScannerActivity.class);
                }
                else
                    myIntent = new Intent(DocumentNrInputActivity.this, BarcodeInputActivity.class);

                myIntent.putExtra("doknr",prefix + dokNr.getText().toString());
                DocumentNrInputActivity.this.startActivity(myIntent);
                finish();
            }
        });

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    @Override
    public void onBackPressed(){

        Intent myIntent = new Intent(DocumentNrInputActivity.this, MainActivity.class);
        startActivity(myIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            Intent myIntent = new Intent(DocumentNrInputActivity.this, MainActivity.class);
            startActivity(myIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}


