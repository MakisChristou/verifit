package com.example.harderthanlasttime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CalendarView;

import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.MaterialCalendar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    // "Data Structures"
    public Set<String> Days = new TreeSet<String>();
    public static ArrayList<WorkoutDay> Workout_Days = new ArrayList<WorkoutDay>();
    public com.applandeo.materialcalendarview.CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start Loading Animation
        // LoadingDialog ld = new LoadingDialog(MainActivity.this);
        // ld.startLoadingAnimation();

        // Read CSV
        InputStream inputStream = getResources().openRawResource(R.raw.fitnotes);
        CSVFile csvFile = new CSVFile(inputStream);
        List csvList = csvFile.read();

        // Writes in Workout_Sets and Days_Set
        parseCSV(csvList);

        // To JSON (for debugging)
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(Workout_Days));


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

        // Remove top bar for aesthetic purposes
        // getSupportActionBar().hide();


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
                Intent in = new Intent(getApplicationContext(), AddExerciseActivity.class);
                Bundle mBundle = new Bundle();

                // Date -> String
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String date_selected = dateFormat.format(date_clicked);

                // Send Date and start activity
                mBundle.putString("date", date_selected);
                in.putExtras(mBundle);
                startActivity(in);

            }
        });

        // Stop Loading Animation
        // ld.dismissDialog();


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

    // Parses CSV and updates data structures
    public void parseCSV(List csvList)
    {

        // Clear Data Structures
        Days.clear();
        Workout_Days.clear();

        // i = 1 since first row is only Strings
        for(int i = 1; i < csvList.size(); i++)
        {
            String[] row = (String[]) csvList.get(i);
            Days.add(row[0]);
        }

        Iterator<String> it = Days.iterator();

        // Iterate Dates Set
        while (it.hasNext())
        {
            String Date = it.next();

            WorkoutDay temp_day = new WorkoutDay();
            ArrayList<WorkoutSet> temp_day_sets = new ArrayList<WorkoutSet>();
            ArrayList<WorkoutExercise> temp_day_exercises = new ArrayList<WorkoutExercise>();

            // Iterate CSV
            for(int j = 1; j < csvList.size(); j++)
            {
                String[] row = (String[]) csvList.get(j);

                if(Date.equals(row[0]))
                {
                    // Create and save WorkoutSet Object
                    WorkoutSet temp_set = new WorkoutSet();
                    temp_set.setDate(row[0]);
                    temp_set.setExercise(row[1]);
                    temp_set.setCategory(row[2]);
                    temp_set.setWeight(Double.parseDouble(row[3]));
                    temp_set.setReps(Double.parseDouble(row[4]));
                    temp_day_sets.add(temp_set);
                }
            }

            temp_day.setDate(Date);
            temp_day.setSets(temp_day_sets);
            Workout_Days.add(temp_day);

        }

        for(int i = 0; i < Workout_Days.size(); i++)
        {
            WorkoutDay temp_day = Workout_Days.get(i);
            ArrayList day_sets = temp_day.getSets();
            ArrayList<WorkoutExercise> Day_Exercises = new ArrayList<WorkoutExercise>();
            Set<String> Exercises_Set = new TreeSet<String>();
            Double Day_Volume = 0.0;


            for(int j = 0; j < day_sets.size(); j++)
            {
                WorkoutSet temp_set = (WorkoutSet) day_sets.get(j);
                Day_Volume = Day_Volume + temp_set.getVolume();
                Exercises_Set.add(temp_set.getExercise());
            }

            // Iterate Day's Performed Exercises
            Iterator<String> itt = Exercises_Set.iterator();
            while (itt.hasNext())
            {
                String exercise_name = itt.next();
                WorkoutExercise day_exercise = new WorkoutExercise();

                day_exercise.setExercise(exercise_name);
                Double exercise_volume = 0.0;
                Double exercise_one_rep_max = 0.0;
                Double exercise_max_reps = 0.0;
                Double exercise_max_weight = 0.0;
                Double exercise_total_reps = 0.0;
                Double exercise_total_sets = 0.0;

                for(int j = 0; j < day_sets.size(); j++)
                {
                    WorkoutSet temp_set = (WorkoutSet) day_sets.get(j);
                    if(temp_set.getExercise().equals(exercise_name))
                    {
                        // Cumulative Values
                        exercise_volume = exercise_volume + temp_set.getVolume();
                        exercise_total_reps = exercise_total_reps + temp_set.getReps();
                        exercise_total_sets = exercise_total_sets + 1;

                        // Max Values
                        if(temp_set.getEplayOneRepMax() > exercise_one_rep_max)
                        {
                            exercise_one_rep_max = temp_set.getEplayOneRepMax();
                        }
                        if(temp_set.getReps() > exercise_max_reps)
                        {
                            exercise_max_reps = temp_set.getReps();
                        }
                        if(temp_set.getWeight() > exercise_max_weight)
                        {
                            exercise_max_weight = temp_set.getWeight();
                        }
                    }

                    // Update exercise object
                    day_exercise.setVolume(exercise_volume);
                    day_exercise.setEstimatedOneRepMax(exercise_one_rep_max);
                    day_exercise.setMaxReps(exercise_max_reps);
                    day_exercise.setMaxWeight(exercise_max_weight);
                    day_exercise.setTotalReps(exercise_total_reps);
                    day_exercise.setTotalSets(exercise_total_sets);

                }
                Day_Exercises.add(day_exercise);
            }


            // Set Day's Volume && Performed Exercises
            Workout_Days.get(i).setDayVolume(Day_Volume);
            Workout_Days.get(i).setExercises(Day_Exercises);
        }
    }

    // Returns true if volume was greater than previous workout
    public boolean harderThanLastTime(String Exercise, String Date)
    {
        return false;
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
        else if(item.getItemId() == R.id.diary)
        {
            System.out.println("Diary");
            Intent in = new Intent(this,DiaryActivity.class);
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
}
