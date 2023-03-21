package com.example.verifit.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;

import com.example.verifit.ExercisePersonalStats;
import com.example.verifit.adapters.ExerciseStatsAdapter;
import com.example.verifit.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class PersonalRecordsActivity extends AppCompatActivity {

    public static ArrayList<ExercisePersonalStats> exerciseStats = new ArrayList();
    public static RecyclerView recyclerView;
    public static ExerciseStatsAdapter exerciseStatsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_records);

        calculatePersonalRecords();

        recyclerView = findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        exerciseStatsAdapter = new ExerciseStatsAdapter(this, PersonalRecordsActivity.exerciseStats);
        recyclerView.setAdapter(exerciseStatsAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        MainActivity.dataStorage.saveKnownExerciseData(this);
    }

    public static void calculatePersonalRecords()
    {
        // Calculate PRs before parsing Hashmaps
        MainActivity.dataStorage.calculatePersonalRecords();

        PersonalRecordsActivity.exerciseStats.clear();

        for (HashMap.Entry<String,Double> entry : MainActivity.dataStorage.getVolumePRs().entrySet())
        {
            String currentExerciseName = entry.getKey();
            String exerciseCategory = MainActivity.dataStorage.getExerciseCategory(currentExerciseName);

            ExercisePersonalStats exercisePersonalStats = new ExercisePersonalStats();
            exercisePersonalStats.setExerciseName(currentExerciseName);
            exercisePersonalStats.setExerciseCategory(exerciseCategory);
            exercisePersonalStats.setMaxVolume(MainActivity.dataStorage.getVolumePRs().get(currentExerciseName));
            exercisePersonalStats.setMaxSetVolume(MainActivity.dataStorage.getSetVolumePRs().get(currentExerciseName).first * MainActivity.dataStorage.getSetVolumePRs().get(currentExerciseName).second);
            exercisePersonalStats.setMaxSetVolumeReps(MainActivity.dataStorage.getSetVolumePRs().get(currentExerciseName).first); // First = Reps
            exercisePersonalStats.setMaxSetVolumeWeight(MainActivity.dataStorage.getSetVolumePRs().get(currentExerciseName).second); // Second = Weight
            exercisePersonalStats.setMaxReps(MainActivity.dataStorage.getMaxRepsPRs().get(currentExerciseName));
            exercisePersonalStats.setMaxWeight(MainActivity.dataStorage.getMaxWeightPRs().get(currentExerciseName));
            exercisePersonalStats.setActual1RM(MainActivity.dataStorage.getActualOneRepMaxPRs().get(currentExerciseName));
            exercisePersonalStats.setEstimated1RM(MainActivity.dataStorage.getEstimatedOneRMPRs().get(currentExerciseName));
            exercisePersonalStats.setFavorite(MainActivity.dataStorage.isExerciseFavorite(currentExerciseName));

            exercisePersonalStats.setMaxWeightSet(MainActivity.dataStorage.getMaxWeightSetPRs().get(currentExerciseName));
            exercisePersonalStats.setMaxRepsSet(MainActivity.dataStorage.getMaxRepsSetPRs().get(currentExerciseName));
            exercisePersonalStats.setMaxVolumeSet(MainActivity.dataStorage.getMaxVolumeSetPRs().get(currentExerciseName));


            if(MainActivity.dataStorage.getVolumePRs().get(currentExerciseName) == 0.0)
            {
                // Skip this exercise, it was not even performed
            }
            else
            {
                PersonalRecordsActivity.exerciseStats.add(exercisePersonalStats);
            }

            // Show favorites first
            Collections.sort(PersonalRecordsActivity.exerciseStats, new Comparator<ExercisePersonalStats>() {
                @Override
                public int compare(ExercisePersonalStats exerciseStats1, ExercisePersonalStats exerciseStats2) {
                    return Boolean.compare(exerciseStats2.getFavorite(), exerciseStats1.getFavorite());
                }
            });
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.personal_records_activity_menu,menu);


        // Search Stuff
        MenuItem searchItem = menu.findItem(R.id.search);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s)
            {
                exerciseStatsAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
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