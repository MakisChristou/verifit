package com.example.verifit.webdav;

import android.app.Activity;

import com.example.verifit.LoadingDialog;
import com.example.verifit.MainActivity;

public class DeleteWebdavThread extends Thread{

    String webdavurl;
    String webdavusername;
    String webdavpassword;
    String webdavresource;
    Activity context;
    LoadingDialog loadingDialog;

    public DeleteWebdavThread(Activity context, String webdavurl, String webdavusername, String webdavpassword, String webdavresource, LoadingDialog loadingDialog)
    {
        this.context = context;
        this.webdavurl = webdavurl;
        this.webdavusername = webdavusername;
        this.webdavpassword = webdavpassword;
        this.loadingDialog = loadingDialog;
        this.webdavresource = webdavresource;
    }

    @Override
    public void run() {
        MainActivity.DeleteWebdavThread(context, webdavurl, webdavusername, webdavpassword, webdavresource, loadingDialog);
    }
}
