package com.example.harderthanlasttime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import java.io.InputStream;
import java.lang.reflect.Type;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start Loading Animation
        // LoadingDialog ld = new LoadingDialog(MainActivity.this);
        // ld.startLoadingAnimation();

        // Read csv file line by line
        InputStream inputStream = getResources().openRawResource(R.raw.fitnotes);
        CSVFile csvFile = new CSVFile(inputStream);
        List csvList = csvFile.read();

        // Loads Data Structures from shared preferences
        loadData();

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
        KnownExercises.add(new Exercise("Flat Barbell Bench Press","Compound","Chest",""));
        KnownExercises.add(new Exercise("Incline Barbell Bench Press","Compound","Chest",""));
        KnownExercises.add(new Exercise("Decline Barbell Bench Press","Compound","Chest",""));
        KnownExercises.add(new Exercise("Flat Dumbell Bench Press","Compound","Chest",""));
        KnownExercises.add(new Exercise("Incline Dumbell Bench Press","Compound","Chest",""));
        KnownExercises.add(new Exercise("Decline Dumbell Bench Press","Compound","Chest",""));
        KnownExercises.add(new Exercise("Chin Up","Compound","Back",""));
        KnownExercises.add(new Exercise("Seated Dumbell Press","Compound","Shoulders",""));
        KnownExercises.add(new Exercise("Ring Dip","Compound","Chest",""));
        KnownExercises.add(new Exercise("Lateral Cable Raise","Isolation","Shoulders",""));
        KnownExercises.add(new Exercise("Lateral Dumbell Raise","Isolation","Shoulders",""));
        KnownExercises.add(new Exercise("Barbell Curl","Compound","Biceps",""));
        KnownExercises.add(new Exercise("Tricep Extension","Isolation","Triceps",""));
        KnownExercises.add(new Exercise("Squat","Compound","Legs",""));
        KnownExercises.add(new Exercise("Leg Extension","Isolation","Legs",""));
        KnownExercises.add(new Exercise("Hammstring Leg Curl","Isolation","Legs",""));
        KnownExercises.add(new Exercise("Deadlift","Compound","Back",""));
        KnownExercises.add(new Exercise("Sumo Deadlift","Compound","Back",""));
        KnownExercises.add(new Exercise("Seated Machine Chest Press","Compound","Chest",""));
        KnownExercises.add(new Exercise("Seated Machine Shoulder Press","Compound","Shoulders",""));
        KnownExercises.add(new Exercise("Seated Calf Raise","Isolation","Legs",""));
        KnownExercises.add(new Exercise("Donkey Calf Raise","Isolation","Legs",""));
        KnownExercises.add(new Exercise("Standing Calf Raise","Isolation","Legs",""));
        KnownExercises.add(new Exercise("Seated Machine Curl","Isolation","Biceps",""));
        KnownExercises.add(new Exercise("Lat Pulldown","Compound","Back",""));
        KnownExercises.add(new Exercise("Pull Up","Compound","Back",""));
        KnownExercises.add(new Exercise("Push Up","Compound","Chest",""));
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
            events.add(new EventDay(calendar, R.drawable.ic_brightness_1_8px, Color.parseColor("#567ad5")));
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


    // Self explanatory I guess
    public void exportCSV()
    {

    }

    // Navigates to given activity based on the selected menu item
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        if(item.getItemId() == R.id.home)
        {
            System.out.println("Home");
            Intent in = new Intent(this,MainActivity.class);
            startActivity(in);
            overridePendingTransition(0,0);
        }
        else if(item.getItemId() == R.id.exercises)
        {
            System.out.println("Diary");
            Intent in = new Intent(this,ExercisesActivity.class);
            startActivity(in);
            overridePendingTransition(0,0);
        }
        else if(item.getItemId() == R.id.diary)
        {
            System.out.println("Diary");
            Intent in = new Intent(this,DiaryActivity.class);
            startActivity(in);
            overridePendingTransition(0,0);
        }
        else if(item.getItemId() == R.id.charts)
        {
            System.out.println("Diary");
            Intent in = new Intent(this,ChartsActivity.class);
            startActivity(in);
            overridePendingTransition(0,0);
        }
        else if(item.getItemId() == R.id.me)
        {
            System.out.println("Settings");
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
