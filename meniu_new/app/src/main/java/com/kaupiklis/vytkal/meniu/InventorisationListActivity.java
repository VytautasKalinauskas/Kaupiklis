package com.kaupiklis.vytkal.meniu;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by vytkal on 3/28/2018.
 */

public class InventorisationListActivity extends AppCompatActivity {

    private ListView list;
    private DatabaseHelper db;

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventorisation_list);
        list = findViewById(R.id.inventorisationDataList);

        db = new DatabaseHelper(this);

        loadListView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void loadListView() {

        Cursor dataCursor = db.getDataInventorisation();
        ArrayList<String> dataList = new ArrayList<>();

        while(dataCursor.moveToNext()) {
            dataList.add(dataCursor.getString(1));
        }

        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = adapterView.getItemAtPosition(i).toString();
                Log.d("InventorisationEditing", "Jūsų pasirinktas barkodas: " + name );

                Cursor cursorAmount = db.getAmountInventorisationByBarcode(name);
                int amount = -1;

                while (cursorAmount.moveToNext()) {
                    amount = cursorAmount.getInt(0);
                }

                if (amount > -1) {

                    Intent intent = new Intent(InventorisationListActivity.this, InventorisationEditActivity.class);
                    intent.putExtra("barcode", name);
                    intent.putExtra("amount", amount);
                    startActivity(intent);
                }
            }
        });
    }

}
