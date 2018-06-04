package com.kaupiklis.vytkal.meniu;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by vytkal on 5/16/2018.
 */
public class FtpHelperTest {
    @Test
    public void ftpConnect() throws Exception {
        FtpHelper ftpHelper = new FtpHelper();
        boolean connected = ftpHelper.ftpConnect("ftp.berzuna.lt", "rivile", "142536", 21);
        assertTrue(connected);
    }

    @Test
    public void ftpChangeDirectory() throws Exception {
        FtpHelper ftpHelper = new FtpHelper();
        ftpHelper.ftpConnect("ftp.berzuna.lt", "rivile", "142536", 21);
        boolean changed = ftpHelper.ftpChangeDirectory("/kaupik/im01");
        assertTrue(changed);
    }

    @Test
    public void ftpDownload() throws Exception {
        FtpHelper ftpHelper = new FtpHelper();
        ftpHelper.ftpConnect("ftp.berzuna.lt", "rivile", "142536", 21);
        boolean downloaded = ftpHelper.ftpDownload("/kaupik/im01/rezultatai.txt",
                "C:\\Users\\vytkal\\Desktop\\OS\\rezultatai.txt");
        assertTrue(downloaded);
    }

    @Test
    public void verification() throws Exception {
        FtpHelper ftpHelper = new FtpHelper();
        int files = ftpHelper.verification("355828064672327", 180001);
        assertNotEquals(files, 0);
    }

    @Test
    public void ftpUpload() throws Exception {
        FtpHelper ftpHelper = new FtpHelper();
        ftpHelper.ftpConnect("ftp.berzuna.lt", "rivile", "142536", 21);
        boolean upload = ftpHelper.ftpUpload("C:\\Users\\vytkal\\Desktop\\OS\\rezultatai.txt",
                "/kaupik/im01/", "rezultatai2.txt");
        assertTrue(upload);
    }

}