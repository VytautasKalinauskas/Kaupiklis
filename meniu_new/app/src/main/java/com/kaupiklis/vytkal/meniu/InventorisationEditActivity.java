package com.kaupiklis.vytkal.meniu;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by vytkal on 3/29/2018.
 */

public class InventorisationEditActivity extends AppCompatActivity implements View.OnClickListener {

    DatabaseHelper db;
    Button save;
    Button delete;
    EditText barcode;
    EditText amount;
    Bundle extras;
    String stringbarcode;
    int intamount;

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventorisation_edit);
        db = new DatabaseHelper(this);
        barcode = findViewById(R.id.editInventorisationBarcode);
        amount = findViewById(R.id.editInventorisationAmount);

        save = findViewById(R.id.buttonSaveInventorisation);
        delete = findViewById(R.id.buttonDeleteInventorisation);

        if (savedInstanceState == null)
            extras = getIntent().getExtras();
        if (extras == null) {
           stringbarcode = null;
           intamount = 0;
        } else {
            stringbarcode = (extras.getString("barcode"));
            intamount = (extras.getInt("amount"));
        }


        barcode.setText(stringbarcode);
        amount.setText(String.valueOf(intamount));

        save.setOnClickListener(this);
        delete.setOnClickListener(this);


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.buttonSaveInventorisation:

                break;

            case R.id.buttonDeleteInventorisation:

                int deleted = db.deleteRecordInventorisationByBarcode(barcode.getText().toString());
                if (deleted > 0) {
                    Toast.makeText(this, "Sėkmingai ištrinta", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(InventorisationEditActivity.this, InventorisationListActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }
}
