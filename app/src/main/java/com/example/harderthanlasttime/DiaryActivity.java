package com.example.harderthanlasttime;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.utils.FSize;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Date;

import javax.crypto.AEADBadTagException;

public class DiaryActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    // Helper Data Structures
    public RecyclerView recyclerView;
    public DiaryAdapter diaryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        initActivity();

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        initActivity();
    }

    public void initActivity()
    {
        // From Day Activity
        Intent in = getIntent();
        String date_clicked = in.getStringExtra("date");

        // If day exists scroll to it otherwise scroll to the last day
        int position = -1;

        if(date_clicked != null)
        {
            position = MainActivity.getDayPosition(date_clicked);
        }


        // Bottom Navigation Bar Intents
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.diary);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Find Recycler View Object
        recyclerView = findViewById(R.id.recycler_view);

        // Notify User in case of empty diary
        if(MainActivity.Workout_Days.isEmpty())
        {
            Toast.makeText(this, "Empty Diary", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // Crash Otherwise
            diaryAdapter = new DiaryAdapter(this, MainActivity.Workout_Days);
            recyclerView.setAdapter(diaryAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));


            if(position > 0)
            {
                // Scroll to the selected date
                recyclerView.scrollToPosition(position);
            }
            else
            {
                // Scroll to the bottom
                recyclerView.scrollToPosition(MainActivity.Workout_Days.size()-1);
            }
        }


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
}