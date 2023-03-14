package com.example.verifit.webdav;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.widget.Toast;

import com.example.verifit.LoadingDialog;
import com.thegrizzlylabs.sardineandroid.DavResource;
import com.thegrizzlylabs.sardineandroid.Sardine;
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine;

import java.util.List;

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

    static void checkWebdav(Context context, String webdavurl, String webdavusername, String webdavpassword, LoadingDialog loadingDialog)
    {
        // Enable networking on main thread
        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);

        Sardine sardine = new OkHttpSardine();
        sardine.setCredentials(webdavusername, webdavpassword);

        try
        {
            List<DavResource> resources = sardine.list(webdavurl);

            for (DavResource res : resources)
            {
                System.out.println("Resources: " + res.getName());
            }

            // Toast from a non UI thread
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(context, "Connection Successful", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });

        }
        catch(Exception e)
        {
            // Toast from a non UI thread
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
        loadingDialog.dismissDialog();
    }

    @Override
    public void run() {
        checkWebdav(context, webdavurl, webdavusername, webdavpassword, loadingDialog);
    }
}
