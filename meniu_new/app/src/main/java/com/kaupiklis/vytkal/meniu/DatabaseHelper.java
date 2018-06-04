package com.kaupiklis.vytkal.meniu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by vytkal on 12/27/2017.
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Duomenys.db";
    public static final String TABLE_INVENTORIZACIJA = "Inventorizacija";
    public static final String TABLE_PRODUKTAI = "Produktai";
    public static final String INVENTORIZACIJA_COL_1 = "Dok_nr";
    public static final String INVENTORIZACIJA_COL_2 = "Barkodas";
    public static final String INVENTORIZACIJA_COL_3 = "Kiekis";
    public static final String INVENTORIZACIJA_COL_4 = "Kaina";
    public static final String PRODUKTAI_COL_1 = "Barkodas";
    public static final String PRODUKTAI_COL_2 = "Pavadinimas";
    public static final String PRODUKTAI_COL_3 = "Kaina";
    public static final String PRODUKTAI_COL_4 = "Savikaina";
    public static final String PRODUKTAI_COL_5 = "Likutis";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_INVENTORIZACIJA + " (DOK_NR TEXT, BARKODAS TEXT, KIEKIS INTEGER, KAINA FLOAT)");
        db.execSQL("create table " + TABLE_PRODUKTAI + " (BARKODAS TEXT PRIMARY KEY,PAVADINIMAS TEXT,KAINA FLOAT,SAVIKAINA FLOAT, LIKUTIS INTEGER)");
        db.execSQL("create table Archyvas (DOK_NR TEXT, BARKODAS TEXT, KIEKIS INTEGER,KAINA FLOAT, DATA TEXT )");
        db.execSQL("create table Nustatymai (SKANAVIMAS INTEGER, TIKRINIMAS INTEGER, AUTO INTEGER)");
        db.execSQL("create table FtpNustatymai (ADRESAS TEXT, VARDAS TEXT, SLAPTAZODIS TEXT, DIREKTORIJA TEXT)");
        db.execSQL("create table Registracija (IMONES_KODAS TEXT, IMONES_PAVADINIMAS TEXT, LICENZIJOS_NR INTEGER)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORIZACIJA);
        onCreate(db);
    }

    public boolean insertData(String dokNr, String barcode, Integer amount, Float price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(INVENTORIZACIJA_COL_1, dokNr);
        contentValues.put(INVENTORIZACIJA_COL_2, barcode);
        contentValues.put(INVENTORIZACIJA_COL_3, amount);
        contentValues.put(INVENTORIZACIJA_COL_4, price);
        long result = db.insert(TABLE_INVENTORIZACIJA, null, contentValues);
        return result != -1;
    }


    public boolean insertFtpData(String adresss, String username, String password, String directory) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Delete From FtpNustatymai");
        ContentValues contentValues = new ContentValues();
        contentValues.put("ADRESAS", adresss);
        contentValues.put("VARDAS", username);
        contentValues.put("SLAPTAZODIS", password);
        contentValues.put("DIREKTORIJA", directory);
        long result = db.insert("FtpNustatymai", null, contentValues);
        return result != -1;
    }

    public boolean insertDataArchive(String dokNr, String barcode, Integer amount, Float price) {

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String formattedDate = df.format(c);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(INVENTORIZACIJA_COL_1, dokNr);
        contentValues.put(INVENTORIZACIJA_COL_2, barcode);
        contentValues.put(INVENTORIZACIJA_COL_3, amount);
        contentValues.put(INVENTORIZACIJA_COL_4, price);
        contentValues.put("Data", formattedDate);
        long result = db.insert("Archyvas", null, contentValues);
        return result != -1;
    }



    public boolean insertDataProduktai(String pavadinimas, String barkodas, Integer likutis, double kaina, double savikaina) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PRODUKTAI_COL_1, barkodas);
        contentValues.put(PRODUKTAI_COL_2, pavadinimas);
        contentValues.put(PRODUKTAI_COL_3, kaina);
        contentValues.put(PRODUKTAI_COL_4, savikaina);
        contentValues.put(PRODUKTAI_COL_5, likutis);
        long result = db.insert(TABLE_PRODUKTAI, null, contentValues);
        return result != -1;
    }

    public boolean setSettings(int scan, int check, int auto ) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Delete From Nustatymai");

        ContentValues contentValues = new ContentValues();
        contentValues.put("SKANAVIMAS", scan);
        contentValues.put("TIKRINIMAS", check);
        contentValues.put("AUTO", auto);
        long result = db.insert("Nustatymai", null, contentValues);
        return result != -1;
    }

    public boolean loadProduktai() {
            ArrayList<String> barkodas = new ArrayList<>();
            ArrayList<String> pavadinimas = new ArrayList<>();
            ArrayList<Double> kaina = new ArrayList<>();
            ArrayList<Double> savikaina = new ArrayList<>();
            ArrayList<Integer> likutis = new ArrayList<>();
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            db.execSQL("Delete From " + TABLE_PRODUKTAI);

            readFile(barkodas, pavadinimas, kaina, savikaina, likutis);

            long result = -1;
            for (int i = 0; i < barkodas.size(); i++) {
                contentValues.put(PRODUKTAI_COL_1, barkodas.get(i));
                contentValues.put(PRODUKTAI_COL_2, pavadinimas.get(i));
                contentValues.put(PRODUKTAI_COL_3, kaina.get(i));
                contentValues.put(PRODUKTAI_COL_4, savikaina.get(i));
                contentValues.put(PRODUKTAI_COL_5, likutis.get(i));
                result = db.insert(TABLE_PRODUKTAI, null, contentValues);

            }
        return result != -1;
    }

        public boolean register() {

            ArrayList<String> data = new ArrayList<>();

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            db.execSQL("Delete From Registracija" );
            readRegistrationFile(data);

            contentValues.put("IMONES_KODAS", data.get(0));
            contentValues.put("IMONES_PAVADINIMAS", data.get(1));
            contentValues.put("LICENZIJOS_NR", Integer.parseInt(data.get(2)));

            long result = db.insert("Registracija", null, contentValues);

            return result != -1;
        }



    public Cursor getDataInventorisation() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_INVENTORIZACIJA, null);
        return res;
    }

    public Cursor getAmountInventorisationByBarcode(String barkodas) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select kiekis from " + TABLE_INVENTORIZACIJA + " where barkodas = " + barkodas, null);
        return res;
    }


    public Cursor getDataArchiveByDokNr(String doknr) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from archyvas where DOK_NR = '" + doknr+ "'" , null);
        return res;
    }

    public Cursor getDataArchiveByDate(String dateFrom, String dateTo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from archyvas where DATA >= '" + dateFrom + "' AND DATA <= '" + dateTo + "'", null);
        return res;
    }

    public Cursor getDataArchiveByDateAndDokNr(String doknr, String dateFrom, String dateTo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from archyvas where DATA >= '" + dateFrom + "' AND DATA <= '" + dateTo + "'" + "AND DOK_NR = '" + doknr+ "'", null);
        return res;
    }


    public Cursor getProductListByBarcode(String barcode) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_PRODUKTAI + " where BARKODAS = '" + barcode + "'", null);
        return res;
    }

    public Cursor getProductListByName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_PRODUKTAI + " where PAVADINIMAS LIKE '%" + name + "%'", null);
        return res;
    }

    public Cursor getProductListByBarcodeAndName(String name, String barcode) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_PRODUKTAI + " where BARKODAS = '" + barcode + "' AND PAVADINIMAS LIKE '%" + name + "%'", null);
        return res;
    }

    public Float getPriceByBarcode(String barkodas){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select " + PRODUKTAI_COL_3 +  " Where Barkodas = '" + barkodas + "'" , null);
        res.moveToFirst();
        return res.getFloat(0);

    }


    public Cursor getNameByBarcode(String barkodas) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select " + PRODUKTAI_COL_2 + "," + PRODUKTAI_COL_3 + "," + PRODUKTAI_COL_5 + " from " + TABLE_PRODUKTAI + " Where Barkodas = '" + barkodas + "'" , null);
        return res;
    }

    public int getScanSettings() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select skanavimas from Nustatymai", null);
        if (res.getCount() == 0)
            return 0;
        else {
            res.moveToFirst();
            return res.getInt(0);
        }
    }

    public int getCheckSettings() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select tikrinimas from Nustatymai", null);
        if (res.getCount() == 0)
            return 0;
        else {
            res.moveToFirst();
            return res.getInt(0);
        }
    }

    public String getCompanyName() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select IMONES_PAVADINIMAS from Registracija", null);
        if (res.getCount() == 0)
            return "";
        else {
            res.moveToFirst();
            return res.getString(0);
        }
    }

    public int getLicenseNr() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select LICENZIJOS_NR from Registracija", null);
        if (res.getCount() == 0)
            return -1;
        else {
            res.moveToFirst();
            return res.getInt(0);
        }
    }

    public int getAutoSettings() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select auto from Nustatymai", null);
        if (res.getCount() == 0)
            return 0;
        else {
            res.moveToFirst();
            return res.getInt(0);
        }
    }

    public String getFtpAdress() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select adresas from FtpNustatymai", null);
        if (res.getCount() == 0)
            return "";
        else {
            res.moveToFirst();
            return res.getString(0);
        }
    }

    public String getFtpUsername() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select vardas from FtpNustatymai", null);
        if (res.getCount() == 0)
            return "";
        else {
            res.moveToFirst();
            return res.getString(0);
        }
    }

    public String getFtpPassword() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select slaptazodis from FtpNustatymai", null);
        if (res.getCount() == 0)
            return "";
        else {
            res.moveToFirst();
            return res.getString(0);
        }
    }

    public String getFtpDirectory() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select direktorija from FtpNustatymai", null);
        if (res.getCount() == 0)
            return "";
        else {
            res.moveToFirst();
            return res.getString(0);
        }
    }

    public void deleteAllFromInventorizacija() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_INVENTORIZACIJA, null, null);
    }

    public void deleteAllFromArchyvas() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("ARCHYVAS", null, null);
    }

    public void deleteAllFromRegistracija() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Registracija", null, null);
    }

    public void deleteAllFromProduktai() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUKTAI, null, null);
    }

    public int deleteRecordInventorisationByBarcode(String barcode) {

        SQLiteDatabase db = this.getWritableDatabase();
        int deleted = db.delete(TABLE_INVENTORIZACIJA, INVENTORIZACIJA_COL_2 + "=" + barcode, null);
        return deleted;
    }



    public void exportDB(String path, Cursor res, boolean append) {

        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            buffer.append(res.getString(0)+",");
            buffer.append(res.getString(1)+",");
            buffer.append(res.getInt(2)+",");
            buffer.append(res.getFloat(3)+"\n");

        }
        try {

            FileOutputStream out = new FileOutputStream(path, append);
            out.write(buffer.toString().getBytes());
            out.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }



    private void readFile(ArrayList<String> barkodas,
                          ArrayList<String> pavadinimas,
                          ArrayList<Double> kaina,
                          ArrayList<Double> savikaina,
                          ArrayList<Integer> likutis) {


        try {
            FileInputStream data = new FileInputStream(Environment.getExternalStorageDirectory().toString()+"/data.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(data));

            String line;
            while ((line = br.readLine()) != null) {
                barkodas.add(line.substring(0, 13).trim());
                pavadinimas.add(line.substring(13,52));
                likutis.add(Integer.parseInt(line.substring(53,58).trim()));
                kaina.add(Double.parseDouble(line.substring(59,71)));
                savikaina.add(0.0);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: could not load data ");
        }
    }

    private void readRegistrationFile(ArrayList<String> data) {

        try {
            FileInputStream input = new FileInputStream(Environment.getExternalStorageDirectory().toString()+"/register.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(input));

            String line;
            while ((line = br.readLine()) != null) {
                data.add(line);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: could not load data ");
        }



    }
}

