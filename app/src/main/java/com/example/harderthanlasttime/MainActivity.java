package com.example.harderthanlasttime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.MaterialCalendar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.security.spec.EncodedKeySpec;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    // Root Data Structure
    public ArrayList<WorkoutSet> Sets = new ArrayList<WorkoutSet>();

    // "Data Structures"
    public Set<String> Days = new TreeSet<String>();
    public static ArrayList<WorkoutDay> Workout_Days = new ArrayList<WorkoutDay>();
    public com.applandeo.materialcalendarview.CalendarView calendarView;
    public static ArrayList<Exercise> KnownExercises = new ArrayList<Exercise>(); // initialized with hardcoded exercises
    public static String date_selected;

    // For File I/O permissions
    public static final int READ_REQUEST_CODE = 42;
    public static final int PERMISSION_REQUEST_STORAGE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initActivity();

        // Start Loading Animation
        // LoadingDialog ld = new LoadingDialog(MainActivity.this);
        // ld.startLoadingAnimation();

        // Read csv file line by line
        InputStream inputStream = getResources().openRawResource(R.raw.fitnotes);
        CSVFile csvFile = new CSVFile(inputStream);
        List csvList = csvFile.read();

        // Loads Data Structures from shared preferences
        // loadData();

        // Update Sets Root Data Structure
        // CSVtoSets(csvList);

        // Update all other Data Structures
        // SetsToEverything();


        // To JSON (for debugging)
        // Gson gson = new GsonBuilder().setPrettyPrinting().create();
        // System.out.println(gson.toJson(Workout_Days));


        // Bottom Navigation Bar Intents
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Change selected menu item icon to filled
        // Menu menu = bottomNavigationView.getMenu();
        // menu.findItem(R.id.home).setIcon(R.drawable.ic_event_available_24px_selected);
        // menu.findItem(R.id.diary).setIcon(R.drawable.ic_assignment_24px);
        // menu.findItem(R.id.trends).setIcon(R.drawable.ic_assessment_24px);
        // menu.findItem(R.id.goals).setIcon(R.drawable.ic_emoji_events_24px);
        // menu.findItem(R.id.settings).setIcon(R.drawable.ic_build_circle_24px);


        // Date selected is by default today
        Date date_clicked = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        date_selected = dateFormat.format(date_clicked);

        // Initialize Exercise Data Structures
        initExercises();

        // Get Material Calendar Instance
        calendarView = findViewById(R.id.calendarView);


        // Update Workouts on Calendar
        updateCalendar();


        // Returns Date clicked as Event Object
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {

                // Save Date Object
                Date date_clicked = eventDay.getCalendar().getTime();

                // Start Intent
                Intent in = new Intent(getApplicationContext(), DayActivity.class);
                Bundle mBundle = new Bundle();

                // Date -> String
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                date_selected = dateFormat.format(date_clicked);

                // Send Date and start activity
                mBundle.putString("date", date_selected);
                in.putExtras(mBundle);
                startActivity(in);

            }
        });

        // Stop Loading Animation
        // ld.dismissDialog();

    }


    // You guessed it!
    public void initActivity()
    {
        // Rename top bar to something sensible
        getSupportActionBar().setTitle("Calendar");

        // From Settings Activity when importing CSV
        Intent in = getIntent();

        String WhatToDO = null;
        WhatToDO = in.getStringExtra("doit");

        if(WhatToDO != null)
        {
            if(WhatToDO.equals("importcsv"))
            {
                permissionStuff();
                fileSearch();
            }
            else if(WhatToDO.equals("exportcsv"))
            {

            }

        }
    }


    // Ask For File I/O Permissions
    public void permissionStuff()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
        }
    }

    // Get the results after user gives/denies permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_STORAGE)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                System.out.println("Permission Granted!");
            }
            else
            {
                System.out.println("Permission Not Granted!");
                finish();
            }
        }
    }


    // Returns index of day
    public static int getDayPosition(String Date)
    {
        for(int i = 0; i < MainActivity.Workout_Days.size(); i++)
        {
            if(MainActivity.Workout_Days.get(i).getDate().equals(Date))
            {
               return i;
            }
        }
        return -1;
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            if(data != null)
            {
                Uri uri = data.getData();
                String filename = uri.getPath();
                filename = filename.substring(filename.indexOf(":") + 1);
                readCSV(filename);
                saveData(this);
            }
        }
    }

    // Converts CSV file to Internally used Dat Structure
    public void CSVtoSets(List csvList)
    {
        // Remove potential Duplicates
        Sets.clear();

        // i = 1 since first row is only Strings
        for(int i = 1; i < csvList.size(); i++)
        {
            String[] row = (String[]) csvList.get(i);
            String Date = row[0];
            String Exercise = row[1];
            String Category = row[2];
            String Reps = row[3];
            String Weight = row[4];

            WorkoutSet workoutSet = new WorkoutSet(Date,Exercise,Category,Double.parseDouble(Weight),Double.parseDouble(Reps));
            Sets.add(workoutSet);
        }
    }


    // Updates All other Data Structures
    public void SetsToEverything()
    {
        // Clear Data Structures
        Days.clear();
        Workout_Days.clear();

        // i = 1 since first row is only Strings
        for(int i = 0; i < Sets.size(); i++)
        {
           Days.add(Sets.get(i).getDate());
        }


        Iterator<String> it = Days.iterator();

        // Construct Workout_Days Array List
        while (it.hasNext())
        {
            String Date = it.next();

            WorkoutDay temp_day = new WorkoutDay();
            ArrayList<WorkoutSet> temp_day_sets = new ArrayList<WorkoutSet>();

            // For all Sets
            for(int i = 0; i < Sets.size(); i++)
            {
                // If Date matches add Set Object to Workout_Day Object
                if(Date.equals(Sets.get(i).getDate()))
                {
                    temp_day.addSet(Sets.get(i));
                }
            }
            Workout_Days.add(temp_day);
        }
    }


    // Initialized KnownExercises ArrayList with some hardcoded exercises
    public void initExercises()
    {
        KnownExercises.clear();
        // Some hardcoded Exercises
        KnownExercises.add(new Exercise("Flat Barbell Bench Press","Chest"));
        KnownExercises.add(new Exercise("Incline Barbell Bench Press","Chest"));
        KnownExercises.add(new Exercise("Decline Barbell Bench Press","Chest"));
        KnownExercises.add(new Exercise("Flat Dumbell Bench Press","Chest"));
        KnownExercises.add(new Exercise("Incline Dumbell Bench Press","Chest"));
        KnownExercises.add(new Exercise("Decline Dumbell Bench Press","Chest"));
        KnownExercises.add(new Exercise("Chin Up","Back"));
        KnownExercises.add(new Exercise("Seated Dumbell Press","Shoulders"));
        KnownExercises.add(new Exercise("Ring Dip","Chest"));
        KnownExercises.add(new Exercise("Lateral Cable Raise","Shoulders"));
        KnownExercises.add(new Exercise("Lateral Dumbell Raise","Shoulders"));
        KnownExercises.add(new Exercise("Barbell Curl","Biceps"));
        KnownExercises.add(new Exercise("Tricep Extension","Triceps"));
        KnownExercises.add(new Exercise("Squat","Legs"));
        KnownExercises.add(new Exercise("Leg Extension","Legs"));
        KnownExercises.add(new Exercise("Hammstring Leg Curl","Legs"));
        KnownExercises.add(new Exercise("Deadlift","Back"));
        KnownExercises.add(new Exercise("Sumo Deadlift","Back"));
        KnownExercises.add(new Exercise("Seated Machine Chest Press","Chest"));
        KnownExercises.add(new Exercise("Seated Machine Shoulder Press","Shoulders"));
        KnownExercises.add(new Exercise("Seated Calf Raise","Legs"));
        KnownExercises.add(new Exercise("Donkey Calf Raise","Legs"));
        KnownExercises.add(new Exercise("Standing Calf Raise","Legs"));
        KnownExercises.add(new Exercise("Seated Machine Curl","Biceps"));
        KnownExercises.add(new Exercise("Lat Pulldown","Back"));
        KnownExercises.add(new Exercise("Pull Up","Back"));
        KnownExercises.add(new Exercise("Push Up","Chest"));
    }

    // Update Workouts on Calendar
    public void updateCalendar()
    {
        // Parse Data Structure and obtain workout days
        List<EventDay> events = new ArrayList<>();

        // For Date Parsing According to CSV Data
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        // Parse Workout Days
        for(int i = 0; i < Workout_Days.size(); i++)
        {
            Calendar calendar = Calendar.getInstance();
            Date date = null;
            try
            {
                date = format.parse(Workout_Days.get(i).getDate());
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
            calendar.setTime(date);
            events.add(new EventDay(calendar, R.drawable.ic_check_24px, Color.parseColor("#567ad5")));
        }
        calendarView.setEvents(events);
    }


    // Saves Workout_Days Array List in shared preferences
    // For some reason when I pass the context it works so let's roll with it :D
    public static void saveData(Context ct)
    {
        SharedPreferences sharedPreferences = ct.getSharedPreferences("shared preferences",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(Workout_Days);
        editor.putString("workouts",json);
        editor.apply();
    }

    // Loads Workout_Days Array List from shared preferences
    public void loadData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("workouts",null);
        Type type = new TypeToken<ArrayList<WorkoutDay>>(){}.getType();
        Workout_Days = gson.fromJson(json,type);

        // If there are no previously saved entries make a new object
        if(Workout_Days == null)
        {
            Workout_Days = new ArrayList<WorkoutDay>();
        }

    }


    public void readCSV(String filename)
    {
        List csvList = new ArrayList();

        try {
            File textFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename);
            FileInputStream inputStream = new FileInputStream(textFile);

            CSVFile csvFile = new CSVFile(inputStream);
            csvList = csvFile.read();
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }

        // Here is where the magic happens
        CSVtoSets(csvList);
        SetsToEverything();
        saveData(this);
        updateCalendar();
    }


    // Reads a text file in internal storage
    public void readCSV2(String filename)
    {

        StringBuilder sb = new StringBuilder();
        try
        {
            File textFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename);
            FileInputStream fis = new FileInputStream(textFile);

            if(fis != null)
            {
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader buff = new BufferedReader(isr);

                String line = null;

                while((line = buff.readLine()) != null)
                {
                    System.out.println(line);
                    sb.append(line + "\n");

                }
                fis.close();
            }
            else
            {
                System.out.println("File is empty");
            }

        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }



    }



    // Returns the exercise category if exists, else it returns an empty string
    public static String getexerciseCategory(String Exercise)
    {
        for(int i = 0; i < KnownExercises.size(); i++)
        {
            if(KnownExercises.get(i).getName().equals(Exercise))
            {
                return KnownExercises.get(i).getBodyPart();
            }
        }
        return "";
    }

    // Navigates to given activity based on the selected menu item
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        if(item.getItemId() == R.id.home)
        {
            Intent in = new Intent(this,MainActivity.class);
            startActivity(in);
            overridePendingTransition(0,0);
        }
        else if(item.getItemId() == R.id.exercises)
        {
            Intent in = new Intent(this,ExercisesActivity.class);
            startActivity(in);
            overridePendingTransition(0,0);
        }
        else if(item.getItemId() == R.id.diary)
        {
            Intent in = new Intent(this,DiaryActivity.class);
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
            Intent in = new Intent(this,MeActivity.class);
            startActivity(in);
            overridePendingTransition(0,0);
        }
        return true;
    }

    // Menu Stuff
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.home)
        {
            Toast.makeText(this, "Today", Toast.LENGTH_SHORT).show();
        }
        else if(item.getItemId() == R.id.settings)
        {
            Intent in = new Intent(this,SettingsActivity.class);
            startActivity(in);
        }
        return super.onOptionsItemSelected(item);
    }
}
