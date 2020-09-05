package com.example.harderthanlasttime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.Toast;

import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, DatePickerDialog.OnDateSetListener {

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
    public static String EXPORT_FILENAME = "FitBook_Backup";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        long start = System.currentTimeMillis();


        // Hacky way to have the same code run in onRestart() as well
        onCreateStuff();

        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.println("Main Activity: " + timeElapsed);

    }


    // When choosing date from menu
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

        // Start Intent
        Intent in = new Intent(getApplicationContext(), DayActivity.class);
        Bundle mBundle = new Bundle();


        // Send Date and start activity
        mBundle.putString("date", date_clicked);
        in.putExtras(mBundle);
        startActivity(in);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        onCreateStuff();
    }

    public void onCreateStuff()
    {
        initActivity();

        // Start Loading Animation
        // LoadingDialog ld = new LoadingDialog(MainActivity.this);
        // ld.startLoadingAnimation();

        // Read csv file line by line
        InputStream inputStream = getResources().openRawResource(R.raw.fitnotes);
        CSVFile csvFile = new CSVFile(inputStream);
        List csvList = csvFile.read();



        // To JSON (for debugging)
//         Gson gson = new GsonBuilder().setPrettyPrinting().create();
//         System.out.println(gson.toJson(Workout_Days));


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
        setExportBackupName();

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
                askReadPermission();
                fileSearch();
            }
            else if(WhatToDO.equals("exportcsv"))
            {

            }
        }

        loadData();
    }

    // Formats backup name in case of export
    public void setExportBackupName()
    {
        Format formatter = new SimpleDateFormat("_yyyy-MM-dd HH:mm:ss");
        String str_date = formatter.format(new Date());
        EXPORT_FILENAME = EXPORT_FILENAME + str_date;
    }

    // Ask For File I/O Permissions
    public void askReadPermission()
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
                System.out.println("Read Permission Granted!");
            }
            else
            {
                System.out.println("Read Permission Not Granted!");
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

    // Returns index of day
    public static int getReverseDayPosition(String Date)
    {
        ArrayList<WorkoutDay>  Reversed_Workout_Days= new ArrayList<WorkoutDay>(Workout_Days);
        Collections.reverse(Reversed_Workout_Days);

        for(int i = 0; i < Reversed_Workout_Days.size(); i++)
        {
            if(Reversed_Workout_Days.get(i).getDate().equals(Date))
            {
                return i;
            }
        }
        return -1;
    }



    // Haven't Tested this yet
    public static void deleteWorkoutDay(String Date)
    {
        int remove_position = -1;

        for(int i = 0; i < MainActivity.Workout_Days.size(); i++)
        {
            if(MainActivity.Workout_Days.get(i).getDate().equals(Date))
            {
                remove_position = i;
            }
        }

        // If date exists
        if(remove_position > 0)
        {
            MainActivity.Workout_Days.remove(remove_position);
        }
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

    // Update Workouts on Calendar (selected days not events for performance's sake!)
    public void updateCalendar()
    {

        // Let's See
        List<Calendar> calendars = new ArrayList<>();

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
            calendars.add(calendar);

            events.add(new EventDay(calendar, R.drawable.ic_check_24px, Color.parseColor("#567ad5")));
        }

        //calendarView.setEvents(events);
        calendarView.setSelectedDates(calendars);
    }


    // Returns hex string based on the exercise category
    public static String getCategoryColor(String exercise_name)
    {

        for(int i = 0; i < KnownExercises.size(); i++)
        {
            if(KnownExercises.get(i).getName().equals(exercise_name))
            {
                return KnownExercises.get(i).getBodyPart();
            }
        }
        return "";
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


    // Read CSV from internal storage
    public void readCSV(String filename)
    {
        List csvList = new ArrayList();

        try {
            System.out.println(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+ filename);
            File textFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename);
            FileInputStream inputStream = new FileInputStream(textFile);

            CSVFile csvFile = new CSVFile(inputStream);
            csvList = csvFile.read();


            // Here is where the magic happens
            CSVtoSets(csvList);
            SetsToEverything();
            updateCalendar();
            saveData(this);

        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
            Toast.makeText(getApplicationContext(), "Could not locate file",Toast.LENGTH_SHORT).show();

            clearDataStructures();
        }

    }


    // Self explanatory
    private boolean isExternalStorageWritable()
    {
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void clearDataStructures()
    {
        // Clear everything just in case
        this.Workout_Days.clear();
        this.KnownExercises.clear();
        this.Sets.clear();
        this.Days.clear();
        saveData(this);
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
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getSupportFragmentManager(),"date picker");
        }
        else if(item.getItemId() == R.id.settings)
        {
            Intent in = new Intent(this,SettingsActivity.class);
            startActivity(in);
        }
        return super.onOptionsItemSelected(item);
    }
}
