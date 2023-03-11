package com.example.verifit.webdav;

import android.content.Context;

import com.example.verifit.LoadingDialog;
import com.example.verifit.MainActivity;

public class ImportWebdavThread extends Thread{

    String webdavurl;
    String webdavusername;
    String webdavpassword;
    String webdavresourcename;
    Context context;

    LoadingDialog loadingDialog;

    public ImportWebdavThread(Context context, String webdavurl, String webdavusername, String webdavpassword, String webdavresourcename, LoadingDialog loadingDialog)
    {
        this.context = context;
        this.webdavurl = webdavurl;
        this.webdavusername = webdavusername;
        this.webdavpassword = webdavpassword;
        this.webdavresourcename = webdavresourcename;
        this.loadingDialog = loadingDialog;
    }

    @Override
    public void run() {
        MainActivity.importWebDav(context, webdavurl, webdavusername, webdavpassword, webdavresourcename, loadingDialog);
    }
}