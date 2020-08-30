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

public class ExercisesActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    public ExerciseAdapter exerciseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);

        // Find Recycler View Object
        recyclerView = findViewById(R.id.recycler_view_exercises);

        exerciseAdapter = new ExerciseAdapter(this, MainActivity.KnownExercises);
        recyclerView.setAdapter(exerciseAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.exercises_activity_menu,menu);
        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.search)
        {

        }
        else if(item.getItemId() == R.id.add)
        {
            Intent in = new Intent(this,CustomExerciseActivity.class);
            startActivity(in);
        }
        return super.onOptionsItemSelected(item);
    }
}