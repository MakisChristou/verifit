package com.example.verifit.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.verifit.BackupService;
import com.example.verifit.DataStorage;
import com.example.verifit.R;
import com.example.verifit.SnackBarWithMessage;
import com.example.verifit.model.WorkoutSet;
import com.example.verifit.adapters.ViewPagerWorkoutDayAdapter;
import com.example.verifit.adapters.WebdavAdapter;
import com.example.verifit.model.WorkoutDay;
import com.example.verifit.verifitrs.WorkoutSetsApi;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener , DatePickerDialog.OnDateSetListener{

    public static DataStorage dataStorage = new DataStorage(); // Holds all Verifit data and handles file I/O
    public static String dateSelected; // Used for other activities to get the selected date, by default it's set to today
    public static ViewPager2 viewPager2; // View Pager that is used in main activity
    public static Boolean autoBackupRequired = false;
    public static Boolean inAddExerciseActivity = false;
    public static WebdavAdapter webdavAdapter;
    public static final int READ_REQUEST_CODE = 42;
    public static String EXPORT_FILENAME = "verifit_backup";

    public FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.floatingActionButton);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(MainActivity.this, ExercisesActivity.class);
                startActivity(in);
            }
        });

        com.example.verifit.SharedPreferences sharedPreferences = new com.example.verifit.SharedPreferences(getApplicationContext());

        // No need for backup, not adding exercises
        if(!doesSharedPreferenceExist("autoBackupRequired"))
        {
            sharedPreferences.save("false", "autoBackupRequired");
        }

        if(!doesSharedPreferenceExist("inAddExerciseActivity"))
        {
            sharedPreferences.save("false", "inAddExerciseActivity");
        }

        // Hacky way to have the same code run in onRestart() as well
        onCreateStuff();
    }

    public Boolean doesSharedPreferenceExist(String key)
    {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("shared preferences", MODE_PRIVATE);

        if(sharedPreferences.contains(key))
        {
            return true;
        }
        return false;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass)
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (serviceClass.getName().equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }

    // General Initialization Stuff
    public void onCreateStuff()
    {
        initActivity();

        // If backup background service has not started, start it
        if(!isMyServiceRunning(BackupService.class))
        {
            // Start background service
            Intent intent = new Intent(this, BackupService.class);
            startService(intent);
        }

        // Bottom Navigation Bar Intents
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);


        // Date selected is by default today
        Date date_clicked = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateSelected = dateFormat.format(date_clicked);
    }

    // When choosing date from DatePicker
    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2)
    {
        i1++;
        String year = String.valueOf(i);
        String month;
        String day;

        month = String.format("%02d", i1);
        day = String.format("%02d", i2);

        String date_clicked = year+"-"+month+"-"+day;
        MainActivity.dateSelected = date_clicked;

        // Start Intent
        Intent in = new Intent(getApplicationContext(), DayActivity.class);
        Bundle mBundle = new Bundle();


        // Send Date and start activity
        mBundle.putString("date", date_clicked);
        in.putExtras(mBundle);
        startActivity(in);
    }

    // You guessed it!
    public void initActivity()
    {
        setExportBackupName();

        // Rename top bar to something sensible
        getSupportActionBar().setTitle("Verifit");

        // From Settings Activity when importing CSV
        Intent in = getIntent();

        String whatToDo = in.getStringExtra("doit");

        String message = in.getStringExtra("message");

        // If Intent coming from settings activity
        if(whatToDo != null)
        {
            if(whatToDo.equals("importcsv"))
            {
                fileSearch();
            }
            else if(whatToDo.equals("exportcsv"))
            {
                // Android 11 and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                {
                    dataStorage.writeFile(getApplicationContext());
                }

                // Or else nothing comes up
                initViewPager();
            }
            else if(whatToDo.equals("exportwebdav"))
            {
                // After Loading Data Initialize ViewPager
                initViewPager();
            }
            // Data already saved, just init view pager
            else if(whatToDo.equals("importwebdav"))
            {
                // After Loading Data Initialize ViewPager
                initViewPager();
            }
        }
        // No intent
        else
        {
            // Offline / Webdav Mode
            com.example.verifit.SharedPreferences sharedPreferences = new com.example.verifit.SharedPreferences(getApplicationContext());

            if(sharedPreferences.isOfflineMode())
            {
                sharedPreferences.save("offline", "mode");
                dataStorage.loadWorkoutData(getApplicationContext());
                dataStorage.loadKnownExercisesData(getApplicationContext());
                initViewPager();
            }
            // Caching: Update screen with local data
            else if(dataStorage.getWorkoutDays().size() > 0 && sharedPreferences.shouldUseCache())
            {
                initViewPager();
            }
            // Fetch data from Rest API and then update screen
            else
            {
                // Cloud Mode
                WorkoutSetsApi workoutSetsApi = new WorkoutSetsApi(getApplicationContext(), getString(R.string.API_ENDPOINT));
                workoutSetsApi.getAllWorkoutSets(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                        sharedPreferences.enableOfflineMode();

                        // Show error
                        runOnUiThread(() -> {
                            initViewPager();
                            SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(MainActivity.this);
                            snackBarWithMessage.showSnackbar("Can't connect to server");
                        });
                    }

                    @Override
                    public void onResponse(Call call, okhttp3.Response response) throws IOException
                    {
                        if (200 == response.code())
                        {
                            String jsonString = response.body().string();
                            Gson gson = new Gson();
                            Type listType = new TypeToken<ArrayList<WorkoutSet>>() {}.getType();
                            ArrayList<WorkoutSet> sets = gson.fromJson(jsonString, listType);

                            MainActivity.dataStorage.readFromSets(sets, getApplicationContext());

                            // Data loaded successfully, enable caching from now on
                            sharedPreferences.enableCaching();

                            runOnUiThread(() -> {
                                initViewPager();
                            });
                        }
                        else
                        {
                            // If logged out login again
                            if(response.message().equals("Unauthorized"))
                            {
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                            else if(response.message().equals("Bad Gateway"))
                            {
                                sharedPreferences.enableOfflineMode();
                                runOnUiThread(() -> {
                                    initViewPager();
                                });
                            }
                            else
                            {
                                sharedPreferences.enableOfflineMode();
                            }

                            runOnUiThread(() -> {
                                SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(MainActivity.this);
                                snackBarWithMessage.showSnackbar(response.message().toString());
                            });
                        }
                    }
                });
            }
        }
        // Display login/logout messages
        if(message != null)
        {
            if(message.equals("verifit_rs_login"))
            {
                SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(MainActivity.this);
                snackBarWithMessage.showSnackbar("Welcome back");
            }
            else if(message.equals("verifit_rs_logout"))
            {
                SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(MainActivity.this);
                snackBarWithMessage.showSnackbar("Logged out");
            }
            else if(message.equals("verifit_rs_signup"))
            {
                com.example.verifit.SharedPreferences sharedPreferences = new com.example.verifit.SharedPreferences(getApplicationContext());
                String email = sharedPreferences.load("verifit_rs_username");
                SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(MainActivity.this);
                snackBarWithMessage.showSnackbar("Account created for " + email);
            }
        }
    }


    @Override
    protected void onRestart()
    {
        // This was already there so I am not deleting it
        super.onRestart();

        // Get WorkoutDays from shared preferences
        dataStorage.loadWorkoutData(getApplicationContext());

        // Get Known Exercises from shared preferences
        dataStorage.loadKnownExercisesData(getApplicationContext());

        // After Loading Data Initialize ViewPager
        initViewPager();

        // Bottom Navigation Bar Intents
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    // Initialize View pager object
    public void initViewPager()
    {
        // Skip creation of empty workouts if you don't have to
        if(dataStorage.getInfiniteWorkoutDays().isEmpty() || dataStorage.getInfiniteWorkoutDays() == null)
        {
            // "Infinite" Data Structure
            dataStorage.getInfiniteWorkoutDays().clear();

            // Find start and End Dates
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.YEAR, -5);
            Date startDate = c.getTime();
            c.add(Calendar.YEAR, +10);
            Date endDate = c.getTime();

            // Create Calendar Objects that represent start and end date
            Calendar start = Calendar.getInstance();
            start.setTime(startDate);
            Calendar end = Calendar.getInstance();
            end.setTime(endDate);

            // Construct 20 years worth of empty workout days
            for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime())
            {
                // Get Date in String format
                String date_str = new SimpleDateFormat("yyyy-MM-dd").format(date);

                // Create new mostly empty object
                WorkoutDay today = new WorkoutDay();
                today.setDate(date_str);
                dataStorage.getInfiniteWorkoutDays().add(today);
            }
        }

        // Use View Pager with Infinite Days
        viewPager2 = findViewById(R.id.viewPager2);
        viewPager2.setAdapter(new ViewPagerWorkoutDayAdapter(this, dataStorage.getInfiniteWorkoutDays()));
        viewPager2.setVisibility(View.VISIBLE);
        viewPager2.setCurrentItem(((dataStorage.getInfiniteWorkoutDays().size()+1)/2)-1); // Navigate to today

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                String dateScrolled = dataStorage.getInfiniteWorkoutDays().get(position).getDate();
                MainActivity.dateSelected = dateScrolled;
            }
        });

        ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
    }

    // Formats backup name in case of export
    public static void setExportBackupName()
    {
        EXPORT_FILENAME = "verifit";
        Format formatter = new SimpleDateFormat("_yyyy-MM-dd_HH:mm:ss");
        String str_date = formatter.format(new Date());
        EXPORT_FILENAME = EXPORT_FILENAME + str_date;
    }

    // Select a file using the build in file manager
    public void fileSearch()
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        startActivityForResult(intent,READ_REQUEST_CODE);
    }

    // When File explorer stops this function runs
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
        {
            if (data != null)
            {
                Uri uri = data.getData();
                if (requestCode == READ_REQUEST_CODE)
                {
                    if(dataStorage.readFile(uri, getApplicationContext()))
                    {
                        initViewPager();
                    }
                }
            }
        }
    }


    // Navigates to given activity based on the selected menu item
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        if(item.getItemId() == R.id.home)
        {
            Intent in = new Intent(this,MainActivity.class);
            startActivity(in);
            overridePendingTransition(0,0);
        }
        else if(item.getItemId() == R.id.exercises)
        {
            Intent in = new Intent(this, ExercisesActivity.class);
            startActivity(in);
            overridePendingTransition(0,0);
        }
        else if(item.getItemId() == R.id.diary)
        {
            Intent in = new Intent(this,DiaryActivity.class);
            in.putExtra("date", dateSelected);
            startActivity(in);
            overridePendingTransition(0,0);
        }
        else if(item.getItemId() == R.id.charts)
        {
            Intent in = new Intent(this,ChartsActivity.class);
            startActivity(in);
            overridePendingTransition(0,0);
        }
        else if(item.getItemId() == R.id.me)
        {
            Intent in = new Intent(this, SettingsActivity.class);
            startActivity(in);
            overridePendingTransition(0,0);
        }
        return true;
    }

    // Menu Stuff
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if(item.getItemId() == R.id.home)
        {
            viewPager2.setCurrentItem(((dataStorage.getInfiniteWorkoutDays().size()+1)/2)-1); // Navigate to today
        }
        else if(item.getItemId() == R.id.settings)
        {
            Intent in = new Intent(this,SettingsActivity.class);
            startActivity(in);
        }
        return super.onOptionsItemSelected(item);
    }
}

