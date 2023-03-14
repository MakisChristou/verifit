package com.example.verifit.webdav;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;

import com.example.verifit.LoadingDialog;
import com.example.verifit.adapters.WebdavAdapter;
import com.thegrizzlylabs.sardineandroid.Sardine;
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine;

import java.io.IOException;

public class DeleteWebdavThread extends Thread{

    String webdavurl;
    String webdavusername;
    String webdavpassword;
    String webdavresource;
    Activity context;
    LoadingDialog loadingDialog;
    WebdavAdapter webdavAdapter;

    public DeleteWebdavThread(Activity context, String webdavurl, String webdavusername, String webdavpassword, String webdavresource, LoadingDialog loadingDialog, WebdavAdapter webdavAdapter)
    {
        this.context = context;
        this.webdavurl = webdavurl;
        this.webdavusername = webdavusername;
        this.webdavpassword = webdavpassword;
        this.loadingDialog = loadingDialog;
        this.webdavresource = webdavresource;
        this.webdavAdapter = webdavAdapter;
    }

    void DeleteWebdav(Activity context, String webdavurl, String webdavusername, String webdavpassword, String webdavresource, LoadingDialog loadingDialog, WebdavAdapter webdavAdapter)
    {
        // Enable networking on main thread
        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);

        // Sardine Stuff
        Sardine sardine = new OkHttpSardine();
        sardine.setCredentials(webdavusername, webdavpassword);

        try
        {
            // Delete remote file and notify adapter that data has changed
            sardine.delete(webdavurl+webdavresource);
            webdavAdapter.Resources = sardine.list(webdavurl);

            // UI Stuff from a non UI thread
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    webdavAdapter.notifyDataSetChanged();
                }
            });
            loadingDialog.dismissDialog();
        }
        catch (IOException e)
        {
            System.out.println(e.toString());
            loadingDialog.dismissDialog();
        }
    }

    @Override
    public void run() {
        DeleteWebdav(context, webdavurl, webdavusername, webdavpassword, webdavresource, loadingDialog, webdavAdapter);
    }
}
