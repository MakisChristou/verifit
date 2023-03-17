package com.example.verifit.webdav;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.widget.Toast;

import com.example.verifit.LoadingDialog;
import com.example.verifit.ui.MainActivity;
import com.thegrizzlylabs.sardineandroid.Sardine;
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine;

import java.io.ByteArrayOutputStream;

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

    void exportWebDav(Context context, String webdavurl, String webdavusername, String webdavpassword, LoadingDialog loadingDialog)
    {
        // Enable networking on main thread  (this is not needed anymore)
        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);

        // Sardine Stuff
        Sardine sardine = new OkHttpSardine();
        sardine.setCredentials(webdavusername, webdavpassword);

        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            output.write("Date,Exercise,Category,Weight (kg),Reps,Comment\n".getBytes());
            for(int i = 0; i < MainActivity.dataStorage.getWorkoutDays().size(); i++)
            {
                for(int j = 0; j < MainActivity.dataStorage.getWorkoutDays().get(i).getExercises().size(); j++)
                {
                    String exerciseComment = MainActivity.dataStorage.getWorkoutDays().get(i).getExercises().get(j).getComment();
                    for(int k = 0; k < MainActivity.dataStorage.getWorkoutDays().get(i).getExercises().get(j).getSets().size(); k++)
                    {
                        String Date = MainActivity.dataStorage.getWorkoutDays().get(i).getExercises().get(j).getDate();
                        String exerciseName = MainActivity.dataStorage.getWorkoutDays().get(i).getExercises().get(j).getSets().get(k).getExerciseName();
                        String exerciseCategory = MainActivity.dataStorage.getWorkoutDays().get(i).getExercises().get(j).getSets().get(k).getCategory();
                        Double Weight = MainActivity.dataStorage.getWorkoutDays().get(i).getExercises().get(j).getSets().get(k).getWeight();
                        Double Reps = MainActivity.dataStorage.getWorkoutDays().get(i).getExercises().get(j).getSets().get(k).getReps();
                        output.write((Date + "," + exerciseName+ "," + exerciseCategory + "," + Weight + "," + Reps + "," + exerciseComment + "\n").getBytes());
                    }
                }
            }
            output.close();

            byte[] data = output.toByteArray();
            MainActivity.setExportBackupName();
            sardine.put(webdavurl+ MainActivity.EXPORT_FILENAME+".txt", data);

            // Toast from a non UI thread
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(context, "Backup saved in " + webdavurl + MainActivity.EXPORT_FILENAME+".txt", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });

            // This is done to somehow run initViewPager()
            Intent in = new Intent(context, MainActivity.class);
            in.putExtra("doit", "exportwebdav");
            context.startActivity(in);

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
        exportWebDav(context, webdavurl, webdavusername, webdavpassword, loadingDialog);
    }
}