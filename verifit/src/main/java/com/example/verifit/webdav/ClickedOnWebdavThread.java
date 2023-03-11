package com.example.verifit.webdav;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;

import com.example.verifit.LoadingDialog;
import com.example.verifit.MainActivity;

public class ClickedOnWebdavThread extends Thread{

    String webdavurl;
    String webdavusername;
    String webdavpassword;
    Activity context;
    LoadingDialog loadingDialog;
    AlertDialog alertDialog;
    View view;

    public ClickedOnWebdavThread(Activity context, String webdavurl, String webdavusername, String webdavpassword, LoadingDialog loadingDialog, AlertDialog alertDialog, View view)
    {
        this.context = context;
        this.webdavurl = webdavurl;
        this.webdavusername = webdavusername;
        this.webdavpassword = webdavpassword;
        this.loadingDialog = loadingDialog;
        this.alertDialog = alertDialog;
        this.view = view;
    }

    @Override
    public void run() {
        MainActivity.clickedOnImportWebdav(context, webdavurl, webdavusername, webdavpassword, loadingDialog, alertDialog, view);
    }
}