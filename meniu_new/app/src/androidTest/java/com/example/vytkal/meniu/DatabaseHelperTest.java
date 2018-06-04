package com.kaupiklis.vytkal.meniu;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Created by vytkal on 5/16/2018.
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest {
    @Test
    public void getScanSettings() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseHelper dbHelper = new DatabaseHelper(appContext);
        dbHelper.setSettings(1,1,1);
        int scan = dbHelper.getScanSettings();
        assertEquals(scan,1);
    }

    @Test
    public void getCheckSettings() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseHelper dbHelper = new DatabaseHelper(appContext);
        dbHelper.setSettings(1,1,1);
        int scan = dbHelper.getCheckSettings();
        assertEquals(scan,1);
    }

    @Test
    public void getAutoSettings() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseHelper dbHelper = new DatabaseHelper(appContext);
        dbHelper.setSettings(1,1,1);
        int scan = dbHelper.getAutoSettings();
        assertEquals(scan,1);
    }

    @Test
    public void getCompanyName() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseHelper dbHelper = new DatabaseHelper(appContext);
        dbHelper.register();
        String company = dbHelper.getCompanyName();
        assertEquals(company, "UAB Gruste");
    }

    @Test
    public void getLicenseNr() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseHelper dbHelper = new DatabaseHelper(appContext);
        dbHelper.register();
        int license = dbHelper.getLicenseNr();
        assertEquals(license, 180001);
    }


    @Test
    public void getFtpAdress() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseHelper dbHelper = new DatabaseHelper(appContext);
        dbHelper.insertFtpData("FTP.TESTAS.LT", "VARDAS", "SLAPTAZODIS", "/DIREKTORIJA");
        String adress = dbHelper.getFtpAdress();
        assertEquals(adress, "FTP.TESTAS.LT");
    }

    @Test
    public void getFtpUsername() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseHelper dbHelper = new DatabaseHelper(appContext);
        dbHelper.insertFtpData("FTP.TESTAS.LT", "VARDAS", "SLAPTAZODIS", "/DIREKTORIJA");
        String adress = dbHelper.getFtpUsername();
        assertEquals(adress, "VARDAS");
    }

    @Test
    public void getFtpPassword() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseHelper dbHelper = new DatabaseHelper(appContext);
        dbHelper.insertFtpData("FTP.TESTAS.LT", "VARDAS", "SLAPTAZODIS", "/DIREKTORIJA");
        String adress = dbHelper.getFtpPassword();
        assertEquals(adress, "SLAPTAZODIS");
    }

    @Test
    public void getFtpDirectory() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseHelper dbHelper = new DatabaseHelper(appContext);
        dbHelper.insertFtpData("FTP.TESTAS.LT", "VARDAS", "SLAPTAZODIS", "/DIREKTORIJA");
        String adress = dbHelper.getFtpDirectory();
        assertEquals(adress, "/DIREKTORIJA");
    }

    @Test
    public void deleteAllFromInventorizacija() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseHelper dbHelper = new DatabaseHelper(appContext);
        dbHelper.insertData("TESTAS", "123456789", 10);
        dbHelper.deleteAllFromInventorizacija();
        Cursor results = dbHelper.getDataInventorisation();
        assertEquals(results.getCount(), 0);
    }

    @Test
    public void deleteAllFromArchyvas() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseHelper dbHelper = new DatabaseHelper(appContext);
        dbHelper.insertDataArchive("TESTAS", "123456789", 10);
        dbHelper.deleteAllFromArchyvas();
        Cursor results = dbHelper.getDataArchiveByDokNr("TESTAS");
        assertEquals(results.getCount(), 0);
    }

    @Test
    public void deleteAllFromRegistracija() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseHelper dbHelper = new DatabaseHelper(appContext);
        dbHelper.insertDataArchive("TESTAS", "123456789", 10);
        dbHelper.deleteAllFromArchyvas();
        Cursor results = dbHelper.getDataArchiveByDokNr("TESTAS");
        assertEquals(results.getCount(), 0);
    }

    @Test
    public void deleteAllFromProduktai() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseHelper dbHelper = new DatabaseHelper(appContext);
        dbHelper.insertDataProduktai("TESTAS", "123456789", 10, 2.99, 0.99);
        dbHelper.deleteAllFromProduktai();
        Cursor results = dbHelper.getProductListByBarcode("TESTAS");
        assertEquals(results.getCount(), 0);
    }


    @Test
    public void insertDataArchive() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseHelper dbHelper = new DatabaseHelper(appContext);
        boolean inserted = dbHelper.insertDataArchive("TESTAS", "123456789", 10);
        assertTrue(inserted);
    }

    @Test
    public void insertDataProduktai() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseHelper dbHelper = new DatabaseHelper(appContext);
        dbHelper.deleteAllFromProduktai();
        boolean inserted = dbHelper.insertDataProduktai("TESTAS", "123456789", 10, 2.99, 0.99);
        assertTrue(inserted);
    }

    @Test
    public void setSettings() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseHelper dbHelper = new DatabaseHelper(appContext);
        boolean set = dbHelper.setSettings(1,1,1);
        assertTrue(set);
    }

    @Test
    public void loadProduktai() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseHelper dbHelper = new DatabaseHelper(appContext);
        boolean loaded = dbHelper.loadProduktai();
        assertTrue(loaded);
    }

    @Test
    public void register() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseHelper dbHelper = new DatabaseHelper(appContext);
        boolean registered = dbHelper.register();
        assertTrue(registered);
    }


    @Test
    public void getDataArchiveByDokNr() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseHelper dbHelper = new DatabaseHelper(appContext);
        dbHelper.deleteAllFromArchyvas();
        dbHelper.insertDataArchive("TESTAS", "123456789", 10);
        Cursor results = dbHelper.getDataArchiveByDokNr("TESTAS");
        assertNotEquals(results.getCount(), 0);
    }

    @Test
    public void getDataArchiveByDate() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseHelper dbHelper = new DatabaseHelper(appContext);
        dbHelper.deleteAllFromArchyvas();
        dbHelper.insertDataArchive("TESTAS", "123456789", 10);
        Cursor results = dbHelper.getDataArchiveByDate("2018/05/16", "2018/05/16");
        assertNotEquals(results.getCount(), 0);
    }

    @Test
    public void getDataArchiveByDateAndDokNr() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseHelper dbHelper = new DatabaseHelper(appContext);
        dbHelper.deleteAllFromArchyvas();
        dbHelper.insertDataArchive("TESTAS", "123456789", 10);
        Cursor results = dbHelper.getDataArchiveByDateAndDokNr("TESTAS","2018/05/16", "2018/05/16");
        results.moveToFirst();
        assertNotEquals(results.getCount(), 0);
        assertEquals(results.getString(0), "TESTAS");
    }

    @Test
    public void getProductListByBarcode() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseHelper dbHelper = new DatabaseHelper(appContext);
        dbHelper.deleteAllFromProduktai();
        dbHelper.insertDataProduktai("TESTAS", "123456789", 10, 2.99, 0.99);
        Cursor results = dbHelper.getProductListByBarcode("123456789");
        results.moveToFirst();
        assertNotEquals(results.getCount(), 0);
        assertEquals(results.getString(0), "123456789");
    }

    @Test
    public void getProductListByName() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseHelper dbHelper = new DatabaseHelper(appContext);
        dbHelper.deleteAllFromProduktai();
        dbHelper.insertDataProduktai("TESTAS", "123456789", 10, 2.99, 0.99);
        Cursor results = dbHelper.getProductListByName("TEST");
        results.moveToFirst();
        assertNotEquals(results.getCount(), 0);
        assertEquals(results.getString(0), "123456789");
    }

    @Test
    public void getProductListByBarcodeAndName() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseHelper dbHelper = new DatabaseHelper(appContext);
        dbHelper.deleteAllFromProduktai();
        dbHelper.insertDataProduktai("TESTAS", "123456789", 10, 2.99, 0.99);
        Cursor results = dbHelper.getProductListByBarcodeAndName("TEST", "123456789");
        results.moveToFirst();
        assertNotEquals(results.getCount(), 0);
        assertEquals(results.getString(0), "123456789");
    }


    @Test
    public void getNameByBarcode() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseHelper dbHelper = new DatabaseHelper(appContext);
        dbHelper.deleteAllFromProduktai();
        dbHelper.insertDataProduktai("TESTAS", "123456789", 10, 2.99, 0.99);
        Cursor results = dbHelper.getNameByBarcode("123456789");
        results.moveToFirst();
        assertNotEquals(results.getCount(), 0);
        assertEquals(results.getString(0), "TESTAS");
    }


    @Test
    public void dbTest() throws Exception {

        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseHelper dbHelper = new DatabaseHelper(appContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        assertTrue(db.isOpen());
        db.close();
    }

    @Test
    public void insertFtpData() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseHelper dbHelper = new DatabaseHelper(appContext);
        boolean isInserted = dbHelper.insertFtpData("ftp.lt", "rivile", "142536", "/kaupik");
        assertTrue(isInserted);
    }

    @Test
    public void insertData() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseHelper dbHelper = new DatabaseHelper(appContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        assertTrue(db.isOpen());
        db.close();
    }


}