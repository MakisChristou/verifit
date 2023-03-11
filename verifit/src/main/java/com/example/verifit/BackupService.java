package com.example.verifit;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.widget.Toast;
import android.os.Process;

import com.thegrizzlylabs.sardineandroid.Sardine;
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class BackupService extends Service {
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            try
            {
                while(true)
                {
                    String autowebdavbackup = loadSharedPreferences("autowebdavbackup");
                    String togglewebdav = loadSharedPreferences("togglewebdav");
                    String autobackup = loadSharedPreferences("autobackup");
                    Date now = new Date();

                    String webdavurl = loadSharedPreferences("webdav_url");
                    String webdavusername = loadSharedPreferences("webdav_username");
                    String webdavpassword = loadSharedPreferences("webdav_password");

                    String autoBackupRequired = loadSharedPreferences("autoBackupRequired");
                    String inAddExerciseActivity = loadSharedPreferences("inAddExerciseActivity");

                    
                    // Automatic webdav export
                    if(autowebdavbackup.equals("true") && togglewebdav.equals("true") && autoBackupRequired.equals("true") && inAddExerciseActivity.equals("false") &&  !webdavurl.equals("") && !webdavusername.equals("") && !webdavpassword.equals(""))
                    {
                        exportWebDavService(getApplicationContext(), webdavurl, webdavusername, webdavpassword);
                        MainActivity.autoBackupRequired = false;
                        saveSharedPreferences("false", "autoBackupRequired");
                    }

                    // Check if we should backup every 10 min
                    Thread.sleep(1000*60*10);
                }
            } catch (InterruptedException e)
            {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
                System.out.println(e.toString());
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }

        public void saveSharedPreferences(String value, String key)
        {
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("shared preferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.apply();
        }

        public String loadSharedPreferences(String key)
        {
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("shared preferences", MODE_PRIVATE);
            String text = sharedPreferences.getString(key, "");
            return text;
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work doesn't disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
    }

    void exportWebDavService(Context context, String webdavurl, String webdavusername, String webdavpassword)
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
            for(int i = 0; i < MainActivity.Workout_Days.size(); i++)
            {
                for(int j = 0; j < MainActivity.Workout_Days.get(i).getExercises().size(); j++)
                {
                    String exerciseComment = MainActivity.Workout_Days.get(i).getExercises().get(j).getComment();
                    for(int k=0; k < MainActivity.Workout_Days.get(i).getExercises().get(j).getSets().size(); k++)
                    {
                        String Date = MainActivity.Workout_Days.get(i).getExercises().get(j).getDate();
                        String exerciseName = MainActivity.Workout_Days.get(i).getExercises().get(j).getSets().get(k).getExercise();
                        String exerciseCategory = MainActivity.Workout_Days.get(i).getExercises().get(j).getSets().get(k).getCategory();
                        Double Weight = MainActivity.Workout_Days.get(i).getExercises().get(j).getSets().get(k).getWeight();
                        Double Reps = MainActivity.Workout_Days.get(i).getExercises().get(j).getSets().get(k).getReps();
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
        }
    }

}
