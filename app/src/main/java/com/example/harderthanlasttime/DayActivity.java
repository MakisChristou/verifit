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
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DayActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    public WorkoutExerciseAdapter2 workoutExerciseAdapter;
    public TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);


        // Recycler View Stuff
        recyclerView = findViewById(R.id.recycler_view_day);
        tv = findViewById(R.id.tv);

        initActivity();


    }


    public void initActivity()
    {
        // From Main Activity
        Intent mIntent = getIntent();
        Bundle extras = mIntent.getExtras();
        String date_clicked = extras.getString("date");

        //Date Stuff
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;

        try {
            date = parser.parse(date_clicked);
            SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMM dd YYYY");
            String formated_date = formatter.format(date);
            getSupportActionBar().setTitle(formated_date);

            ArrayList<WorkoutExercise> Today_Execrises = new ArrayList<WorkoutExercise>();

            for(int i = 0; i < MainActivity.Workout_Days.size(); i++)
            {
                if(date_clicked.equals(MainActivity.Workout_Days.get(i).getDate()))
                {
                    Today_Execrises = MainActivity.Workout_Days.get(i).getExercises();
                }
            }


            // Set Recycler View
            workoutExerciseAdapter = new WorkoutExerciseAdapter2(this, Today_Execrises);
            recyclerView.setAdapter(workoutExerciseAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            if(Today_Execrises.isEmpty())
            {
                tv.setVisibility(View.VISIBLE);
            }
            else
            {
                tv.setVisibility(View.GONE);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

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

        if(item.getItemId() == R.id.add)
        {

        }
        return super.onOptionsItemSelected(item);
    }
}