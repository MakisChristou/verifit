package com.example.verifit.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.verifit.model.Exercise;
import com.example.verifit.R;

public class CustomExerciseActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    public String selected_category;
    public Spinner spinner;
    public EditText et_exercise_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_exercise);


        // Initialize Spinner Object
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // Initialize Edit Text Object
        et_exercise_name = findViewById(R.id.et_exercise_name);
    }

    // Menu Stuff
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.custom_exercise_activity_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if(item.getItemId() == R.id.save)
        {
            if(!MainActivity.dataStorage.doesExerciseExist(et_exercise_name.getText().toString()))
            {
                Exercise new_exercise = new Exercise(et_exercise_name.getText().toString(),selected_category);
                MainActivity.dataStorage.getKnownExercises().add(new_exercise);
                MainActivity.dataStorage.saveKnownExerciseData(getApplicationContext());
                Toast.makeText(getApplicationContext(),"Exercise Saved",Toast.LENGTH_SHORT).show();
                Intent in = new Intent(this, ExercisesActivity.class);
                startActivity(in);
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Exercise Already Exists",Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // On Seleted Stuff (spinner)
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        selected_category = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {
        selected_category = "";
    }
}