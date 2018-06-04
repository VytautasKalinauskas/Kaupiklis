package com.kaupiklis.vytkal.meniu;

/**
 * Created by vytkal on 1/9/2018.
 */

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import org.apache.commons.net.ftp.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


@RequiresApi(api = Build.VERSION_CODES.O)
public class FtpHelper extends AppCompatActivity {

    public FTPClient mFTPClient = null;
    DatabaseHelper db = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(this);

    }


    public boolean downloadFile(String adress, String username, String password, String directory, String fileName, String downloadedName) {

        boolean parsiunte = false;

        boolean prisijunge = ftpConnect(adress, username, password, 21);
        if (prisijunge) {
            String path = Environment.getExternalStorageDirectory().toString() + "/"+fileName;
            parsiunte = ftpDownload(directory + "/"+downloadedName, path);
        }
        return parsiunte;
    }

    public boolean uploadFile(String path, String adress, String username, String password, String directory, String fileName) {

        boolean uploaded = false;
        ftpConnect(adress, username, password, 21);
        uploaded = ftpUpload(path, directory, fileName);


        return uploaded;
    }


    public boolean ftpConnect(String host, String username,
                       String password, int port) {
        try {
            mFTPClient = new FTPClient();
            mFTPClient.connect(host, port);

            if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
                boolean status = mFTPClient.login(username, password);

                mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                mFTPClient.enterLocalPassiveMode();

                return status;
            }
        } catch (Exception e) {
            System.out.println("error");
        }

        return false;
    }

    public boolean ftpChangeDirectory(String directory_path) {
        try {
            mFTPClient.changeWorkingDirectory(directory_path);
        } catch (Exception e) {
            System.out.println("Error: could not change directory to " + directory_path);
            return false;
        }

        return true;
    }


    public boolean ftpDownload(String srcFilePath, String desFilePath) {
        boolean status = false;
        try {
            FileOutputStream desFileStream = new FileOutputStream(desFilePath);
            status = mFTPClient.retrieveFile(srcFilePath, desFileStream);
            desFileStream.close();

            return status;
        } catch (Exception e) {
            System.out.println("Error: could not download file to ");
        }

        return status;
    }

    public int verification(String imei,int licenseNr) {

        FTPFile[] files = new FTPFile[2];

        final boolean connected = ftpConnect("ftp.berzuna.lt", "virgis", "Faksas37", 21);
        if (connected) {
            try {

                files = mFTPClient.listFiles("/dk/" + licenseNr + "-" + imei + ".txt");
                mFTPClient.disconnect();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else return -1;

        return files.length;
    }


    public boolean ftpUpload(String srcFilePath, String desDirectory, String desFileName) {
        boolean status = false;
        try {
            FileInputStream srcFileStream = new FileInputStream(srcFilePath);
            ftpChangeDirectory(desDirectory);
            status = mFTPClient.storeFile(desFileName, srcFileStream);
            srcFileStream.close();
            return status;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: could not upload file to ");
        }
        return status;
    }



}
