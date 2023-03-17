package com.example.verifit.webdav;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.widget.Toast;

import com.example.verifit.CSVFile;
import com.example.verifit.LoadingDialog;
import com.example.verifit.ui.MainActivity;
import com.thegrizzlylabs.sardineandroid.DavResource;
import com.thegrizzlylabs.sardineandroid.Sardine;
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
    void importWebDav(Context context, String webdavurl, String webdavusername, String webdavpassword, String webdavresourcename, LoadingDialog loadingDialog)
    {
        // Enable networking on main thread  (this is not needed anymore)
        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);

        // Sardine Stuff
        Sardine sardine = new OkHttpSardine();
        sardine.setCredentials(webdavusername, webdavpassword);

        // Initalize for file I/O
        InputStream inputStream;
        List csvList = new ArrayList();

        try {
            List<DavResource> resources = sardine.list(webdavurl);

            for (DavResource res : resources)
            {
                if(res.getName().equals(webdavresourcename))
                {
                    inputStream = sardine.get(webdavurl+res.getName());

                    CSVFile csvFile = new CSVFile(inputStream);
                    csvList = csvFile.read();

                    // Here is where the magic happens
                    MainActivity.dataStorage.csvToSets(csvList); // Read File and Construct Local Objects
                    MainActivity.dataStorage.setsToEverything(); // Convert Set Objects to Day Objects
                    MainActivity.dataStorage.csvToKnownExercises(); // Find all Exercises in CSV and add them to known exercises
                    MainActivity.dataStorage.saveKnownExerciseData(context); // Save KnownExercises in CSV
                    MainActivity.dataStorage.saveWorkoutData(context); // Save WorkoutDays in Shared Preferences

                    // This is done to somehow run initViewPager()
                    Intent in = new Intent(context, MainActivity.class);
                    in.putExtra("doit", "importwebdav");
                    context.startActivity(in);
                }
            }

            // Toast from a non UI thread
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(context, "Import Sucessful", Toast.LENGTH_SHORT);
                    toast.show();
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
            loadingDialog.dismissDialog();
        }
    }

    @Override
    public void run() {
        importWebDav(context, webdavurl, webdavusername, webdavpassword, webdavresourcename, loadingDialog);
    }
}