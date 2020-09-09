package com.example.verifit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class AddExerciseActivity extends AppCompatActivity {

    // Helper Data Structures
    public RecyclerView recyclerView;
    public String exercise_name;
    public ArrayList<WorkoutSet> Todays_Exercise_Sets = new ArrayList<WorkoutSet>();
    public AddExerciseWorkoutSetAdapter workoutSetAdapter2;
    public static int Clicked_Set = 0;

    // Add Exercise Activity Specifics
    public EditText et_reps;
    public EditText et_weight;
    public ImageButton plus_reps;
    public ImageButton minus_reps;
    public ImageButton plus_weight;
    public ImageButton minus_weight;
    public Button bt_save;
    public Button bt_clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);

        // find views
        et_reps = findViewById(R.id.et_reps);
        et_weight = findViewById(R.id.et_seconds);
        plus_reps = findViewById(R.id.plus_reps);
        minus_reps = findViewById(R.id.minus_reps);
        plus_weight = findViewById(R.id.plus_weight);
        minus_weight = findViewById(R.id.minus_weight);
        bt_clear = findViewById(R.id.bt_clear);
        bt_save = findViewById(R.id.bt_save);

        // Self Explanatory I guess
        initActivity();

        // Self Explanatory I guess
        initrecyclerView();

        System.out.println(MainActivity.date_selected);
    }

    // Button On Click Methods
    public void clickSave(View view)
    {
        long start = System.currentTimeMillis();

        if(et_weight.getText().toString().isEmpty() || et_reps.getText().toString().isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Please write Weight and Reps",Toast.LENGTH_SHORT).show();
        }
        else{

            // Create New Set Object
            Double reps = Double.parseDouble(et_reps.getText().toString());
            Double weight = Double.parseDouble(et_weight.getText().toString());
            WorkoutSet workoutSet = new WorkoutSet(MainActivity.date_selected,exercise_name, MainActivity.getExerciseCategory(exercise_name),reps,weight);


            int position = MainActivity.getDayPosition(MainActivity.date_selected);

            if(position >= 0)
            {
                MainActivity.Workout_Days.get(position).addSet(workoutSet);
            }
            else
            {
                WorkoutDay workoutDay = new WorkoutDay();
                workoutDay.addSet(workoutSet);
                MainActivity.Workout_Days.add(workoutDay);
            }

            // Update Local Data Structure
            updateTodaysExercises();
            Toast.makeText(getApplicationContext(),"Set Logged",Toast.LENGTH_SHORT).show();
        }

        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.println("Save Data Button: " + timeElapsed);
    }

    // Clear / Delete
    public void clickClear(View view)
    {
        long start = System.currentTimeMillis();

        if(Todays_Exercise_Sets.isEmpty())
        {
            et_reps.setText("");
            et_weight.setText("");
        }
        else
        {
            // Get soon to be deleted set attributes
            WorkoutSet to_be_removed_set = Todays_Exercise_Sets.get(Clicked_Set);

            // Find the set in main data structure and delete it
            for(int i = 0; i < MainActivity.Workout_Days.size(); i++)
            {
                if(MainActivity.Workout_Days.get(i).getSets().contains(to_be_removed_set))
                {
                    // If last set the delete the whole object
                    if(MainActivity.Workout_Days.get(i).getSets().size() == 1)
                    {
                        MainActivity.Workout_Days.remove(MainActivity.Workout_Days.get(i));
                    }
                    // Just delete the set
                    else
                    {
                        MainActivity.Workout_Days.get(i).removeSet(to_be_removed_set);
                        break;
                    }

                }
            }

            Toast.makeText(getApplicationContext(),"Set Deleted",Toast.LENGTH_SHORT).show();

            // Inefficient
            // Actually Save Changes in shared preferences
//            MainActivity.saveData(getApplicationContext());

            // Update Local Data Structure
            updateTodaysExercises();
        }

        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.println("Clear Data Button: " + timeElapsed);
    }

    // Save Changes in main data structure, save data structure in shared preferences
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // Sort Before Saving
        MainActivity.sortWorkoutDaysDate();

        // Actually Save Changes in shared preferences
        MainActivity.saveWorkoutData(getApplicationContext());
    }

    // Do I even need to explain this?
    public void clickPlusWeight(View view)
    {
        if(!et_weight.getText().toString().isEmpty())
        {
            Double weight = Double.parseDouble(et_weight.getText().toString());
            weight = weight + 1;
            et_weight.setText(weight.toString());
        }

    }

    public void clickPlusReps(View view)
    {
        if(!et_reps.getText().toString().isEmpty())
        {
            int reps = Integer.parseInt(et_reps.getText().toString());
            reps = reps + 1;
            et_reps.setText(String.valueOf(reps));
        }

    }

    public void clickMinusWeight(View view)
    {
        if(!et_weight.getText().toString().isEmpty())
        {
            Double weight = Double.parseDouble(et_weight.getText().toString());
            weight = weight - 1;
            et_weight.setText(weight.toString());
        }

    }

    public void clickMinusReps(View view)
    {
        if(!et_reps.getText().toString().isEmpty())
        {
            int reps = Integer.parseInt(et_reps.getText().toString());
            reps = reps - 1;
            et_reps.setText(String.valueOf(reps));
        }

    }

    // Handles Intent Stuff
    public void initActivity()
    {
        Intent in = getIntent();
        exercise_name = in.getStringExtra("exercise");
        getSupportActionBar().setTitle(exercise_name);
    }

    // Updates Local Data Structure
    public void updateTodaysExercises()
    {
        // Clear since we don't want duplicates
        Todays_Exercise_Sets.clear();

        // Find Sets for a specific date and exercise
        for(int i = 0; i < MainActivity.Workout_Days.size(); i++)
        {
            // If date matches
            if(MainActivity.Workout_Days.get(i).getDate().equals(MainActivity.date_selected))
            {
                for(int j  = 0; j < MainActivity.Workout_Days.get(i).getSets().size(); j++)
                {
                    // If exercise matches
                    if(exercise_name.equals(MainActivity.Workout_Days.get(i).getSets().get(j).getExercise()))
                    {
                        Todays_Exercise_Sets.add(MainActivity.Workout_Days.get(i).getSets().get(j));
                    }
                }
            }
        }

        // Change Button Functionality
        if(Todays_Exercise_Sets.isEmpty())
        {
            bt_clear.setText("Clear");
        }
        else
        {
            bt_clear.setText("Delete");
        }

        // Update Recycler View
        workoutSetAdapter2.notifyDataSetChanged();

    }

    // Initialize Recycler View Object
    public void initrecyclerView()
    {
        // Clear since we don't want duplicates
        Todays_Exercise_Sets.clear();

        // Find Sets for a specific date and exercise
        for(int i = 0; i < MainActivity.Workout_Days.size(); i++)
        {
            // If date matches
            if(MainActivity.Workout_Days.get(i).getDate().equals(MainActivity.date_selected))
            {
                for(int j  = 0; j < MainActivity.Workout_Days.get(i).getSets().size(); j++)
                {
                    // If exercise matches
                    if(exercise_name.equals(MainActivity.Workout_Days.get(i).getSets().get(j).getExercise()))
                    {
                        Todays_Exercise_Sets.add(MainActivity.Workout_Days.get(i).getSets().get(j));
                    }
                }
            }
        }

        // Find Recycler View Object
        recyclerView = findViewById(R.id.recycler_view);
        workoutSetAdapter2 = new AddExerciseWorkoutSetAdapter(this,Todays_Exercise_Sets);
        recyclerView.setAdapter(workoutSetAdapter2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // Set Edit Text values to max set volume if possible
        initEditTexts();


        // Change Button Functionality
        if(Todays_Exercise_Sets.isEmpty())
        {
            bt_clear.setText("Clear");
        }
        else
        {
            bt_clear.setText("Delete");
        }

    }

    // Set Edit Text values to max set volume if sets exist
    public void initEditTexts()
    {
        Double max_weight = 0.0;
        int max_reps = 0;
        Double max_exercise_volume = 0.0;

        // Find Max Weight and Reps for a specific exercise
        for(int i = 0; i < MainActivity.Workout_Days.size(); i++)
        {
            for(int j = 0; j < MainActivity.Workout_Days.get(i).getSets().size(); j++)
            {
                if(MainActivity.Workout_Days.get(i).getSets().get(j).getVolume() > max_exercise_volume && MainActivity.Workout_Days.get(i).getSets().get(j).getExercise().equals(exercise_name))
                {
                    max_exercise_volume = MainActivity.Workout_Days.get(i).getSets().get(j).getVolume();
                    max_reps = (int)Math.round(MainActivity.Workout_Days.get(i).getSets().get(j).getReps());
                    max_weight = MainActivity.Workout_Days.get(i).getSets().get(j).getWeight();
                }
            }
        }

        // If never performed the exercise leave Edit Texts blank
        if(max_reps == 0 || max_weight == 0.0)
        {
            et_reps.setText("");
            et_weight.setText("");
        }else
        {
            et_reps.setText(String.valueOf(max_reps));
            et_weight.setText(max_weight.toString());
        }
    }

    // Menu Stuff
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_exercise_activity_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.timer)
        {
            // Prepare to show timer dialog box
            LayoutInflater inflater = LayoutInflater.from(AddExerciseActivity.this);
            View view = inflater.inflate(R.layout.timer_dialog,null);
            AlertDialog alertDialog = new AlertDialog.Builder(AddExerciseActivity.this).setView(view).create();

            // Get Objects
            EditText et_seconds = findViewById(R.id.et_seconds);
            ImageButton minus_seconds = findViewById(R.id.minus_seconds);
            ImageButton plus_seconds = findViewById(R.id.plus_seconds);
            Button bt_start = findViewById(R.id.bt_start);
            Button bt_reset = findViewById(R.id.bt_close);

            // Timer code here

            alertDialog.show();
        }

        else if(item.getItemId() == R.id.history)
        {
            // Prepare to show exercise history dialog box
            LayoutInflater inflater = LayoutInflater.from(AddExerciseActivity.this);
            View view = inflater.inflate(R.layout.exercise_history_dialog,null);
            AlertDialog alertDialog = new AlertDialog.Builder(AddExerciseActivity.this).setView(view).create();


            // Declare local data structure
            ArrayList<WorkoutExercise> All_Performed_Sessions = new ArrayList<>();

            // Find all performed sessions of a specific exercise and add them to local data structure
            for(int i = MainActivity.Workout_Days.size()-1; i >= 0; i--)
            {
                for(int j = 0; j < MainActivity.Workout_Days.get(i).getExercises().size(); j++)
                {
                    if(MainActivity.Workout_Days.get(i).getExercises().get(j).getExercise().equals(exercise_name))
                    {
                        All_Performed_Sessions.add(MainActivity.Workout_Days.get(i).getExercises().get(j));
                    }
                }
            }


            // Set Exercise Name
            TextView tv_exercise_name = view.findViewById(R.id.tv_exercise_name);
            tv_exercise_name.setText(exercise_name);


            // Set Exercise History Recycler View
            RecyclerView recyclerView = view.findViewById(R.id.recyclerView_Exercise_History);
            ExerciseHistoryExerciseAdapter workoutExerciseAdapter4 = new ExerciseHistoryExerciseAdapter(AddExerciseActivity.this,All_Performed_Sessions);


            // Crash Here
            recyclerView.setAdapter(workoutExerciseAdapter4);
            recyclerView.setLayoutManager(new LinearLayoutManager(AddExerciseActivity.this));





            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

}