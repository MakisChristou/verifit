package com.example.verifit.webdav;

import android.content.Context;

import com.example.verifit.LoadingDialog;
import com.example.verifit.MainActivity;

public class ExportWebdavThread extends Thread{

    String webdavurl;
    String webdavusername;
    String webdavpassword;
    Context context;
    LoadingDialog loadingDialog;

    public ExportWebdavThread(Context context, String webdavurl, String webdavusername, String webdavpassword, LoadingDialog loadingDialog)
    {
        this.context = context;
        this.webdavurl = webdavurl;
        this.webdavusername = webdavusername;
        this.webdavpassword = webdavpassword;
        this.loadingDialog = loadingDialog;
    }

    @Override
    public void run() {
        MainActivity.exportWebDav(context, webdavurl, webdavusername, webdavpassword, loadingDialog);
    }
}