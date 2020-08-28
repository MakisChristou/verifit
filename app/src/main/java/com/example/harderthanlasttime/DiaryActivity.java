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

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Date;

import javax.crypto.AEADBadTagException;

public class DiaryActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    // Helper Data Structures
    public RecyclerView recyclerView;
    public ArrayList<String> Dates;
    public ArrayList<Double> Volumes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        // Bottom Navigation Bar Intents
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.diary);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);



        Dates = new ArrayList();
        Volumes = new ArrayList();

        // Find Recycler View Object
        recyclerView = findViewById(R.id.recycler_view);

        // Crash Here
        populateArrays();

        System.out.println("Debug1");
        DiaryAdapter diaryAdapter = new DiaryAdapter(this, Dates,Volumes);
        System.out.println("Debug2");
        recyclerView.setAdapter(diaryAdapter);
        System.out.println("Debug3");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        System.out.println("Debug4");


    }

    // Populates RecyclerView Arrays
    public void populateArrays()
    {
        for(int i = 0; i < MainActivity.Workout_Days.size(); i++)
        {
            System.out.println("Debug0");
            String Date = MainActivity.Workout_Days.get(i).getDate();
            System.out.println("Debug1");
            Dates.add(Date);
            System.out.println("Debug2");
            Double Volume = MainActivity.Workout_Days.get(i).getDayVolume();
            System.out.println("Debug3");
            Volumes.add(Volume);
            System.out.println("Debug4");

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu1,menu);
        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.settings)
        {
            Intent in = new Intent(this,SettingsActivity.class);
            startActivity(in);
        }
        else if(item.getItemId() == R.id.calculator)
        {
            Intent in = new Intent(this,CalculatorActivity.class);
            startActivity(in);
        }
        return super.onOptionsItemSelected(item);
    }

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