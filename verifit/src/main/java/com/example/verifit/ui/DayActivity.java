package com.example.verifit.ui;

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
import android.widget.Toast;

import com.example.verifit.adapters.DayExerciseAdapter;
import com.example.verifit.R;
import com.example.verifit.model.WorkoutExercise;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DayActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    public DayExerciseAdapter workoutExerciseAdapter;
    String date_clicked;

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        // Self Explanatory I guess
        initActivity();
    }


    // Haven't we said that already?
    public void initActivity()
    {

        fab = findViewById(R.id.floatingActionButton);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(DayActivity.this, ExercisesActivity.class);
                startActivity(in);
            }
        });

        // Recycler View Stuff
        recyclerView = findViewById(R.id.recycler_view_day);

        // From Main Activity
        Intent mIntent = getIntent();
        Bundle extras = mIntent.getExtras();
        date_clicked = extras.getString("date");

        //Date Stuff
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;

        try {
            date = parser.parse(date_clicked);
            SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMM dd YYYY");
            String formated_date = formatter.format(date);
            getSupportActionBar().setTitle(formated_date);

            ArrayList<WorkoutExercise> Today_Execrises = new ArrayList<WorkoutExercise>();

            for(int i = 0; i < MainActivity.dataStorage.getWorkoutDays().size(); i++)
            {
                if(date_clicked.equals(MainActivity.dataStorage.getWorkoutDays().get(i).getDate()))
                {
                    Today_Execrises = MainActivity.dataStorage.getWorkoutDays().get(i).getExercises();
                }
            }


            // Set Recycler View
            workoutExerciseAdapter = new DayExerciseAdapter(this, Today_Execrises);
            recyclerView.setAdapter(workoutExerciseAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            // Notify User
            if(Today_Execrises.isEmpty())
            {
                Toast.makeText(getApplicationContext(),"No Logged Exercises",Toast.LENGTH_SHORT).show();
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    // When back button is pressed by another app
    @Override
    protected void onRestart() {
        super.onRestart();

        // Self Explanatory I guess
        initActivity();
    }

    // Menu Methods
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.day_activity_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}