package com.example.verifit;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import android.os.Process;
import java.util.Date;

public class WebdavBackupService extends Service {
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.

            try
            {
                while(true)
                {
                    String autowebdavbackup = loadSharedPreferences("autowebdavbackup");
                    String togglewebdav = loadSharedPreferences("togglewebdav");
                    Date now = new Date();


                    String webdavurl = loadSharedPreferences("webdav_url");
                    String webdavusername = loadSharedPreferences("webdav_username");
                    String webdavpassword = loadSharedPreferences("webdav_password");

                    System.out.println("MainActivity.autoBackup = " + MainActivity.autoBackupRequired);
                    System.out.println("MainActivity.inAddExerciseActivity = " + MainActivity.inAddExerciseActivity);


                    // Automatic webdav backup is on and a new workout is saved
                    if(autowebdavbackup.equals("true") && togglewebdav.equals("true") && MainActivity.autoBackupRequired && !MainActivity.inAddExerciseActivity &&  !webdavurl.equals("") && !webdavusername.equals("") && !webdavpassword.equals(""))
                    {
                        System.out.println("Exporting silently in the background");
                        MainActivity.exportWebDavService(getApplicationContext(), webdavurl, webdavusername, webdavpassword);
                        MainActivity.autoBackupRequired = false;
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

        Toast.makeText(this, "Backup Service started", Toast.LENGTH_SHORT).show();


        // Get the HandlerThread's Looper and use it for our Handler
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(this, "Auto backup enabled", Toast.LENGTH_SHORT).show();

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
        Toast.makeText(this, "Backup Service stopped", Toast.LENGTH_SHORT).show();
    }
}
