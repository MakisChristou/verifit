package com.example.harderthanlasttime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TrendsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trends);

        // Bottom Navigation Bar Intents
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.trends);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        Menu menu = bottomNavigationView.getMenu();
        menu.findItem(R.id.home).setIcon(R.drawable.ic_event_available_24px);
        menu.findItem(R.id.diary).setIcon(R.drawable.ic_assignment_24px);
        menu.findItem(R.id.trends).setIcon(R.drawable.ic_assessment_24px_selected);
        menu.findItem(R.id.goals).setIcon(R.drawable.ic_emoji_events_24px);
        menu.findItem(R.id.settings).setIcon(R.drawable.ic_build_circle_24px);

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
        else if(item.getItemId() == R.id.trends)
        {
            System.out.println("Trends");
            Intent in = new Intent(this,TrendsActivity.class);
            startActivity(in);
            overridePendingTransition(0,0);
        }
        else if(item.getItemId() == R.id.goals)
        {
            System.out.println("Settings");
            Intent in = new Intent(this,GoalsActivity.class);
            startActivity(in);
            overridePendingTransition(0,0);
        }
        else if(item.getItemId() == R.id.settings)
        {
            System.out.println("Settings");
            Intent in = new Intent(this,SettingsActivity.class);
            startActivity(in);
            overridePendingTransition(0,0);
        }
        return true;
    }
}