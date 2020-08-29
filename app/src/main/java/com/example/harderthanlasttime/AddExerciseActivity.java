package com.example.harderthanlasttime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddExerciseActivity extends AppCompatActivity {

    // Helper Data Structures
    public RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);

        // Self Explanatory I guess
        initActivity();


        // Find Recycler View Object
//        recyclerView = findViewById(R.id.recycler_view);
//
//        ExerciseAdapter exerciseAdapter = new ExerciseAdapter(this, MainActivity.Workout_Days.get(2).getExercises());
//        recyclerView.setAdapter(exerciseAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM YYYY");
            String formated_date = formatter.format(date);
            getSupportActionBar().setTitle(formated_date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }



}