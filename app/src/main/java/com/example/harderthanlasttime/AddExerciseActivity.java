package com.example.harderthanlasttime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddExerciseActivity extends AppCompatActivity {

    // Helper Data Structures
    public RecyclerView recyclerView;
    public String exercise_name;
    public ArrayList<WorkoutSet> Todays_Exercise_Sets = new ArrayList<WorkoutSet>();
    public WorkoutSetAdapter2  workoutSetAdapter2;

    // Add Exercise Activity Specifics
    public EditText et_reps;
    public EditText et_weight;
    public ImageButton plus_reps;
    public ImageButton minus_reps;
    public ImageButton plus_weight;
    public ImageButton minus_weight;
    public Button bt_save;
    public Button bt_clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);


        // Self Explanatory I guess
        initActivity();

        // Self Explanatory I guess
        initrecyclerView();

        // Add Exercise Activity Specifics
        et_reps = findViewById(R.id.et_reps);
        et_weight = findViewById(R.id.et_weight);
        plus_reps = findViewById(R.id.plus_reps);
        minus_reps = findViewById(R.id.minus_reps);
        plus_weight = findViewById(R.id.plus_weight);
        minus_weight = findViewById(R.id.minus_weight);
        bt_clear = findViewById(R.id.bt_clear);
        bt_save = findViewById(R.id.bt_save);
    }

    // Button On Click Methods
    public void clickSave(View view)
    {
        System.out.println("Clicked Save");

        if(et_weight.getText().toString().isEmpty() || et_weight.getText().toString().isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Please write Weight and Reps",Toast.LENGTH_SHORT).show();
        }
        else{

            // Create New Set Object
            Double reps = Double.parseDouble(et_reps.getText().toString());
            Double weight = Double.parseDouble(et_weight.getText().toString());
            WorkoutSet workoutSet = new WorkoutSet(MainActivity.date_selected,exercise_name,"Unknown Category",reps,weight);



            // WorkoutDay workoutDay = new WorkoutDay();

            // workoutDay.getSets().add(workoutSet);



            Toast.makeText(getApplicationContext(),"Set Logged",Toast.LENGTH_SHORT).show();
        }



    }

    public void clickClear(View view)
    {
        System.out.println("Clicked Clear");
    }

    public void clickPlusWeight(View view)
    {
        System.out.println("Clicked Plus Weight");
    }

    public void clickPlusReps(View view)
    {
        System.out.println("Clicked Plus Reps");
    }

    public void clickMinusWeight(View view)
    {
        System.out.println("Clicked Minus Weight");
    }

    public void clickMinusReps(View view)
    {
        System.out.println("Clicked Minus Reps");
    }

    // Init Methods
    // Handles Intent Stuff
    public void initActivity()
    {
        Intent in = getIntent();
        exercise_name = in.getStringExtra("exercise");
        getSupportActionBar().setTitle(exercise_name);

    }

    // Updates Local Data Structure
    public void updateTodaysExercises()
    {
        // Clear since we don't want duplicates
        Todays_Exercise_Sets.clear();

        // Find Sets for a specific date and exercise
        for(int i = 0; i < MainActivity.Workout_Days.size(); i++)
        {
            // If date matches
            if(MainActivity.Workout_Days.get(i).getDate().equals(MainActivity.date_selected))
            {
                for(int j  = 0; j < MainActivity.Workout_Days.get(i).getSets().size(); j++)
                {
                    // If exercise matches
                    if(exercise_name.equals(MainActivity.Workout_Days.get(i).getSets().get(j).getExercise()))
                    {
                        Todays_Exercise_Sets.add(MainActivity.Workout_Days.get(i).getSets().get(j));
                    }
                }
            }
        }

        // Update Recycler View
        workoutSetAdapter2.notifyDataSetChanged();

    }

    // Initialized Recycler View Object
    public void initrecyclerView()
    {
        // Clear since we don't want duplicates
        Todays_Exercise_Sets.clear();


        // Find Sets for a specific date and exercise
        for(int i = 0; i < MainActivity.Workout_Days.size(); i++)
        {
            // If date matches
            if(MainActivity.Workout_Days.get(i).getDate().equals(MainActivity.date_selected))
            {
                for(int j  = 0; j < MainActivity.Workout_Days.get(i).getSets().size(); j++)
                {
                    // If exercise matches
                    if(exercise_name.equals(MainActivity.Workout_Days.get(i).getSets().get(j).getExercise()))
                    {
                        Todays_Exercise_Sets.add(MainActivity.Workout_Days.get(i).getSets().get(j));
                    }
                }
            }
        }

        // Find Recycler View Object
        recyclerView = findViewById(R.id.recycler_view);
        workoutSetAdapter2 = new WorkoutSetAdapter2(this,Todays_Exercise_Sets);
        recyclerView.setAdapter(workoutSetAdapter2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // Menu Stuff
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_exercise_activity_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.calculator)
        {
            Toast bread = Toast.makeText(getApplicationContext(), "Plate Calculator Selected", Toast.LENGTH_SHORT);
            bread.show();
        }
        else if(item.getItemId() == R.id.timer)
        {
            Toast bread = Toast.makeText(getApplicationContext(), "Timer Selected", Toast.LENGTH_SHORT);
            bread.show();
        }
        return super.onOptionsItemSelected(item);
    }

}