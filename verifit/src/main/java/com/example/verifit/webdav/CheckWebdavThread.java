package com.example.verifit.webdav;

import android.app.Activity;

import com.example.verifit.LoadingDialog;
import com.example.verifit.MainActivity;

public class CheckWebdavThread extends Thread{

    String webdavurl;
    String webdavusername;
    String webdavpassword;
    Activity context;
    LoadingDialog loadingDialog;

    public CheckWebdavThread(Activity context, String webdavurl, String webdavusername, String webdavpassword, LoadingDialog loadingDialog)
    {
        this.context = context;
        this.webdavurl = webdavurl;
        this.webdavusername = webdavusername;
        this.webdavpassword = webdavpassword;
        this.loadingDialog = loadingDialog;
    }

    @Override
    public void run() {
        MainActivity.checkWebdav(context, webdavurl, webdavusername, webdavpassword, loadingDialog);
    }
}
