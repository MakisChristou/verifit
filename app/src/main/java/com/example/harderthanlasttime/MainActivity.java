package com.example.harderthanlasttime;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity {

    // "Data Structures"
    public Set<String> Days = new TreeSet<String>();
    public ArrayList<WorkoutDay> Workout_Days = new ArrayList<WorkoutDay>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Read CSV
        InputStream inputStream = getResources().openRawResource(R.raw.fitnotes);
        CSVFile csvFile = new CSVFile(inputStream);
        List csvList = csvFile.read();

        // Writes in Workout_Sets and Days_Set
        parseCSV(csvList);

        // To JSON
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(Workout_Days));
    }


    // Parses CSV
    public void parseCSV(List csvList)
    {

        // i = 1 since first row is only Strings
        for(int i = 1; i < csvList.size(); i++)
        {
            String[] row = (String[]) csvList.get(i);
            Days.add(row[0]);
        }

        Iterator<String> it = Days.iterator();

        // Iterate Dates Set
        while (it.hasNext())
        {
            String Date = it.next();

            WorkoutDay temp_day = new WorkoutDay();
            ArrayList<WorkoutSet> temp_day_sets = new ArrayList<WorkoutSet>();
            ArrayList<WorkoutExercise> temp_day_exercises = new ArrayList<WorkoutExercise>();

            // Iterate CSV
            for(int j = 1; j < csvList.size(); j++)
            {
                String[] row = (String[]) csvList.get(j);

                if(Date.equals(row[0]))
                {
                    // Create and save WorkoutSet Object
                    WorkoutSet temp_set = new WorkoutSet();
                    temp_set.setDate(row[0]);
                    temp_set.setExercise(row[1]);
                    temp_set.setCategory(row[2]);
                    temp_set.setWeight(Double.parseDouble(row[3]));
                    temp_set.setReps(Double.parseDouble(row[4]));
                    temp_day_sets.add(temp_set);
                }
            }

            temp_day.setDate(Date);
            temp_day.setSets(temp_day_sets);
            Workout_Days.add(temp_day);

        }

        for(int i = 0; i < Workout_Days.size(); i++)
        {
            WorkoutDay temp_day = Workout_Days.get(i);
            System.out.print(temp_day.getDate());
            ArrayList day_sets = temp_day.getSets();
            ArrayList<WorkoutExercise> Day_Exercises = new ArrayList<WorkoutExercise>();
            Set<String> Exercises_Set = new TreeSet<String>();
            Double Day_Volume = 0.0;


            for(int j = 0; j < day_sets.size(); j++)
            {
                WorkoutSet temp_set = (WorkoutSet) day_sets.get(j);
                Day_Volume = Day_Volume + temp_set.getVolume();
                Exercises_Set.add(temp_set.getExercise());
            }

            // Iterate Day's Performed Exercises
            Iterator<String> itt = Exercises_Set.iterator();
            while (itt.hasNext())
            {
                String exercise_name = itt.next();
                WorkoutExercise day_exercise = new WorkoutExercise();

                day_exercise.setExercise(exercise_name);
                Double exercise_volume = 0.0;
                Double exercise_one_rep_max = 0.0;
                Double exercise_max_reps = 0.0;
                Double exercise_max_weight = 0.0;
                Double exercise_total_reps = 0.0;

                for(int j = 0; j < day_sets.size(); j++)
                {
                    WorkoutSet temp_set = (WorkoutSet) day_sets.get(j);
                    if(temp_set.getExercise().equals(exercise_name))
                    {
                        // Cumulative Values
                        exercise_volume = exercise_volume + temp_set.getVolume();
                        exercise_total_reps = exercise_total_reps + temp_set.getReps();

                        // Max Values
                        if(temp_set.getEplayOneRepMax() > exercise_one_rep_max)
                        {
                            exercise_one_rep_max = temp_set.getEplayOneRepMax();
                        }
                        if(temp_set.getReps() > exercise_max_reps)
                        {
                            exercise_max_reps = temp_set.getReps();
                        }
                        if(temp_set.getWeight() > exercise_max_weight)
                        {
                            exercise_max_weight = temp_set.getWeight();
                        }
                    }

                    // Update exercise object
                    day_exercise.setVolume(exercise_volume);
                    day_exercise.setEstimatedOneRepMax(exercise_one_rep_max);
                    day_exercise.setMaxReps(exercise_max_reps);
                    day_exercise.setMaxWeight(exercise_max_weight);
                    day_exercise.setTotalReps(exercise_total_reps);

                }
                Day_Exercises.add(day_exercise);
            }


            // Set Day's Volume && Performed Exercises
            Workout_Days.get(i).setDayVolume(Day_Volume);
            Workout_Days.get(i).setExercises(Day_Exercises);
        }
    }

    // Returns true if volume was greater than previous workout
    public boolean harderThanLastTime(String Exercise, String Date)
    {
        return false;
    }

}