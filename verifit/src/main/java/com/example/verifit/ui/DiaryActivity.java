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
import android.widget.Toast;

import com.example.verifit.DiaryAdapter;
import com.example.verifit.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DiaryActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    // Helper Data Structures
    public RecyclerView recyclerView;
    public DiaryAdapter diaryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        System.out.println("DiaryActivity::OnCreate()");
        System.out.println("Size: " + MainActivity.Workout_Days.size());

        initActivity();
    }


    @Override
    protected void onRestart()
    {
        super.onRestart();

        System.out.println("DiaryActivity::OnRestart()");
        initActivity();
    }


    public void initActivity()
    {
        System.out.println("DiaryActivity::initActivity()");

        // From Day Activity
        Intent in = getIntent();
        String date_clicked = in.getStringExtra("date");
        // String date_clicked = MainActivity.date_selected;

        // If day exists scroll to it otherwise scroll to the last day
        int position = -1;

        if(date_clicked != null)
        {
            position = MainActivity.getDayPosition(date_clicked);
        }

        System.out.println("Date is " + date_clicked + " and position is: " + position);
        System.out.println("MainActivity.Workout_Days.size(): " + MainActivity.Workout_Days.size());

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
            // So the adapter has correct information
            MainActivity.calculatePersonalRecords();

            // Crash Otherwise
            diaryAdapter = new DiaryAdapter(this, MainActivity.Workout_Days);
            recyclerView.setAdapter(diaryAdapter);
//            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            LinearLayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true); // last argument (true) is flag for reverse layout
            lm.setReverseLayout(true);
            lm.setStackFromEnd(true);
            recyclerView.setLayoutManager(lm);

//            recyclerView.scrollToPosition();

            // BUG HERE FIX IT SER
            if(position >= 0)
            {
                // Scroll to the selected date
                System.out.println("Scroll to position");
//                recyclerView.scrollToPosition(position);
            }
            else
            {
                // Scroll to the bottom
                System.out.println("Scroll to the bottom");
//                recyclerView.scrollToPosition(MainActivity.Workout_Days.size()-1);
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
            Intent in = new Intent(this, ExercisesActivity.class);
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
            Intent in = new Intent(this, PersonalRecordsActivity.class);
            startActivity(in);
            overridePendingTransition(0,0);
        }
        return true;
    }

    // Menu Stuff
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.diary_activity_menu,menu);
        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.settings)
        {
            Intent in = new Intent(this,SettingsActivity.class);
            startActivity(in);
        }
        return super.onOptionsItemSelected(item);
    }
}