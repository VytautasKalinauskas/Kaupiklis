package com.kaupiklis.vytkal.meniu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Calendar;

/**
 * Created by vytkal on 4/29/2018.
 */

public class ProductListActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    DatabaseHelper db;
    MessageHelper messageHelper;
    CheckBox checkBoxBarcode;
    CheckBox checkBoxProductName;
    Button filter;
    EditText editBarcode;
    EditText editProductName;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_filter);
        db = new DatabaseHelper(this);
        messageHelper = new MessageHelper();


        editProductName = findViewById(R.id.editproductname);
        editBarcode = findViewById(R.id.editbarcode);
        checkBoxBarcode = findViewById(R.id.checkboxbarcode);
        checkBoxProductName = findViewById(R.id.checkboxproductname);
        filter = findViewById(R.id.buttonfilterproductlist);

        //editBarcode.setOnClickListener(this);
        //editProductName.setOnClickListener(this);

        checkBoxBarcode.setOnCheckedChangeListener(this);
        checkBoxProductName.setOnCheckedChangeListener(this);

        filter.setOnClickListener(this);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(ProductListActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProductListActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonfilterproductlist:

                if (checkBoxProductName.isChecked() && checkBoxBarcode.isChecked()) {

                    Cursor res = db.getProductListByBarcodeAndName(editProductName.getText().toString(), editBarcode.getText().toString());

                    if(res.getCount() == 0) {

                        MessageHelper.showMessage("Klaida","Įrašų nerasta", ProductListActivity.this, null);


                    }

                    else {

                        StringBuffer buffer = new StringBuffer();
                        formatList(res, buffer);

                        MessageHelper.showMessage("Inventorizacijos duomenys", buffer.toString(), ProductListActivity.this, null);
                    }

                    break;

                    }


                 else if (checkBoxBarcode.isChecked())  {

                    Cursor res = db.getProductListByBarcode(editBarcode.getText().toString());
                    if(res.getCount() == 0) {

                        MessageHelper.showMessage("Klaida","Įrašų nerasta", ProductListActivity.this, null);

                    }

                    else {

                        StringBuffer buffer = new StringBuffer();
                        formatList(res, buffer);

                        MessageHelper.showMessage("Inventorizacijos duomenys", buffer.toString(), ProductListActivity.this, null);

                    }

                     break;

                    }


                else if (checkBoxProductName.isChecked()) {

                    Cursor res = db.getProductListByName(editProductName.getText().toString());
                    if(res.getCount() == 0) {
                        MessageHelper.showMessage("Klaida","Įrašų nerasta", ProductListActivity.this, null);

                    }

                    else {

                        StringBuffer buffer = new StringBuffer();
                        formatList(res, buffer);

                        MessageHelper.showMessage("Inventorizacijos duomenys", buffer.toString(), ProductListActivity.this, null);

                    }
                    break;
                }


        }
    }

    private void formatList(Cursor res, StringBuffer buffer) {

        while(res.moveToNext()) {

            buffer.append("Barkodas :"+ res.getString(0)+"\n");
            buffer.append("Pavadinimas :"+ res.getString(1)+"\n");
            buffer.append("Kaina :"+ res.getFloat(2)+"\n");
            buffer.append("Savikaina :"+ res.getFloat(3)+"\n");
            buffer.append("Likutis :"+ res.getInt(4)+"\n\n");

        }

    }

    @Override
    public void onCheckedChanged(CompoundButton v, boolean b) {

        switch (v.getId()) {

            case R.id.checkboxproductname:

                if(checkBoxProductName.isChecked()){
                    editProductName.setVisibility(View.VISIBLE);
                }
                else {
                    editProductName.setVisibility(View.INVISIBLE);
                }
                break;

            case R.id.checkboxbarcode:

                if(checkBoxBarcode.isChecked()) {
                    editBarcode.setVisibility(View.VISIBLE);
                }
                else {
                    editBarcode.setVisibility(View.INVISIBLE);
                }
                break;

        }

    }
}
