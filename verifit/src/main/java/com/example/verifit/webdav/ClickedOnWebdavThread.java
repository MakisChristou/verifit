package com.example.verifit.webdav;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.verifit.LoadingDialog;
import com.example.verifit.ui.MainActivity;
import com.example.verifit.R;
import com.example.verifit.adapters.WebdavAdapter;
import com.thegrizzlylabs.sardineandroid.DavResource;
import com.thegrizzlylabs.sardineandroid.Sardine;
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine;

import java.util.List;

public class ClickedOnWebdavThread extends Thread{

    String webdavurl;
    String webdavusername;
    String webdavpassword;
    Activity context;
    LoadingDialog loadingDialog;
    AlertDialog alertDialog;
    View view;

    WebdavAdapter webdavAdapter;

    public ClickedOnWebdavThread(Activity context, String webdavurl, String webdavusername, String webdavpassword, LoadingDialog loadingDialog, AlertDialog alertDialog, View view, WebdavAdapter webdavAdapter)
    {
        this.context = context;
        this.webdavurl = webdavurl;
        this.webdavusername = webdavusername;
        this.webdavpassword = webdavpassword;
        this.loadingDialog = loadingDialog;
        this.alertDialog = alertDialog;
        this.view = view;
        this.webdavAdapter = webdavAdapter;
    }

    void clickedOnImportWebdav(Activity context, String webdavurl, String webdavusername, String webdavpassword, LoadingDialog loadingDialog, AlertDialog alertDialog, View view)
    {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_Webdav);

        // Enable networking on main thread (this is not needed anymore)
        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);

        // Sardine Stuff
        Sardine sardine = new OkHttpSardine();
        sardine.setCredentials(webdavusername, webdavpassword);
        List<DavResource> Resources;


        try
        {
            Resources = sardine.list(webdavurl);

            // Add list sorting so user can make sense of their backups
            Resources.sort((o2, o1) -> o1.getName().compareTo(o2.getName()));

            // To Do: Don't show unwanted files

            // Set Webdav Recycler View
            // Need Mainacticity's webadapter to modify state
            MainActivity.webdavAdapter = new WebdavAdapter(context, Resources);
            recyclerView.setAdapter(MainActivity.webdavAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            // Dismiss loading dialog on normal functionality
            loadingDialog.dismissDialog();


            // Show Alert Dialog from a non UI thread
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    alertDialog.show();
                }
            });
        }
        catch (Exception e)
        {
            System.out.println(e.toString());

            // Toast from a non UI thread
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            });

            // Dismiss loading dialog on exception
            loadingDialog.dismissDialog();
        }
    }

    @Override
    public void run() {
        clickedOnImportWebdav(context, webdavurl, webdavusername, webdavpassword, loadingDialog, alertDialog, view);
    }
}