package com.example.verifit;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import static com.example.verifit.ui.MainActivity.EXPORT_FILENAME;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.example.verifit.model.Exercise;
import com.example.verifit.model.WorkoutDay;
import com.example.verifit.model.WorkoutExercise;
import com.example.verifit.model.WorkoutSet;
import com.example.verifit.verifitrs.WorkoutSetsApi;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class DataStorage {

    Set<String> days = new TreeSet<String>();
    ArrayList<WorkoutSet> sets = new ArrayList<WorkoutSet>();
    ArrayList<WorkoutDay> workoutDays = new ArrayList<WorkoutDay>();
    ArrayList<Exercise> knownExercises = new ArrayList<Exercise>(); // Initialized with hardcoded exercises
    HashMap<String,Double> volumePRs = new HashMap<String,Double>();
    HashMap<String, Pair<Double,Double>> setVolumePRs = new HashMap<String, Pair<Double,Double>>(); // first = reps, second = weight
    HashMap<String,Double> actualOneRepMaxPRs = new HashMap<String,Double>();
    HashMap<String,Double> estimatedOneRMPRs = new HashMap<String,Double>();
    HashMap<String,Double> maxRepsPRs = new HashMap<String,Double>();
    HashMap<String,Double> maxWeightPRs = new HashMap<String,Double>();
    HashMap<String,Double> lastTimeVolume = new HashMap<String,Double>(); // Holds last workout's volume for each exercise
    ArrayList<WorkoutDay> infiniteWorkoutDays = new ArrayList<WorkoutDay>(); // Used to populate the viewPager object in MainActivity with "infinite" days
    HashMap<String, WorkoutSet> maxVolumeSetPRs = new HashMap<String, WorkoutSet>();
    HashMap<String, WorkoutSet> maxRepsSetPRs = new HashMap<String, WorkoutSet>();
    HashMap<String, WorkoutSet> maxWeightSetPRs = new HashMap<String, WorkoutSet>();


    public HashMap<String, WorkoutSet> getMaxVolumeSetPRs() {
        return maxVolumeSetPRs;
    }

    public void setMaxVolumeSetPRs(HashMap<String, WorkoutSet> maxVolumeSetPRs) {
        this.maxVolumeSetPRs = maxVolumeSetPRs;
    }

    public HashMap<String, WorkoutSet> getMaxRepsSetPRs() {
        return maxRepsSetPRs;
    }

    public void setMaxRepsSetPRs(HashMap<String, WorkoutSet> maxRepsSetPRs) {
        this.maxRepsSetPRs = maxRepsSetPRs;
    }

    public HashMap<String, WorkoutSet> getMaxWeightSetPRs() {
        return maxWeightSetPRs;
    }

    public void setMaxWeightSetPRs(HashMap<String, WorkoutSet> maxWeightSetPRs) {
        this.maxWeightSetPRs = maxWeightSetPRs;
    }

    public Set<String> getDays() {
        return days;
    }

    public void setDays(Set<String> days) {
        this.days = days;
    }

    public ArrayList<WorkoutSet> getSets() {
        return sets;
    }

    public void setSets(ArrayList<WorkoutSet> sets) {
        this.sets = sets;
    }

    public ArrayList<WorkoutDay> getWorkoutDays() {
        return workoutDays;
    }

    public void setWorkoutDays(ArrayList<WorkoutDay> workoutDays) {
        this.workoutDays = workoutDays;
    }

    public ArrayList<Exercise> getKnownExercises() {
        return knownExercises;
    }

    public void setKnownExercises(ArrayList<Exercise> knownExercises) {
        this.knownExercises = knownExercises;
    }

    public HashMap<String, Double> getVolumePRs() {
        return volumePRs;
    }

    public void setVolumePRs(HashMap<String, Double> volumePRs) {
        this.volumePRs = volumePRs;
    }

    public HashMap<String, Pair<Double, Double>> getSetVolumePRs() {
        return setVolumePRs;
    }

    public void setSetVolumePRs(HashMap<String, Pair<Double, Double>> setVolumePRs) {
        this.setVolumePRs = setVolumePRs;
    }

    public HashMap<String, Double> getActualOneRepMaxPRs() {
        return actualOneRepMaxPRs;
    }

    public void setActualOneRepMaxPRs(HashMap<String, Double> actualOneRepMaxPRs) {
        this.actualOneRepMaxPRs = actualOneRepMaxPRs;
    }

    public HashMap<String, Double> getEstimatedOneRMPRs() {
        return estimatedOneRMPRs;
    }

    public void setEstimatedOneRMPRs(HashMap<String, Double> estimatedOneRMPRs) {
        this.estimatedOneRMPRs = estimatedOneRMPRs;
    }

    public HashMap<String, Double> getMaxRepsPRs() {
        return maxRepsPRs;
    }

    public void setMaxRepsPRs(HashMap<String, Double> maxRepsPRs) {
        this.maxRepsPRs = maxRepsPRs;
    }

    public HashMap<String, Double> getMaxWeightPRs() {
        return maxWeightPRs;
    }

    public void setMaxWeightPRs(HashMap<String, Double> maxWeightPRs) {
        this.maxWeightPRs = maxWeightPRs;
    }

    public HashMap<String, Double> getLastTimeVolume() {
        return lastTimeVolume;
    }

    public void setLastTimeVolume(HashMap<String, Double> lastTimeVolume) {
        this.lastTimeVolume = lastTimeVolume;
    }

    public ArrayList<WorkoutDay> getInfiniteWorkoutDays() {
        return infiniteWorkoutDays;
    }

    public void setInfiniteWorkoutDays(ArrayList<WorkoutDay> infiniteWorkoutDays) {
        this.infiniteWorkoutDays = infiniteWorkoutDays;
    }

    // Returns index of day
    public int getDayPosition(String Date)
    {
        for(int i = 0; i < workoutDays.size(); i++)
        {
            if(workoutDays.get(i).getDate().equals(Date))
            {
                return i;
            }
        }
        return -1;
    }

    // Returns index of exercise
    public int getExercisePosition(String Date, String exerciseName)
    {
        int day_position = getDayPosition(Date);

        // The day doesn't even have an exercise
        if (day_position == -1)
        {
            return -1;
        }

        ArrayList<WorkoutExercise> Exercises = workoutDays.get(day_position).getExercises();

        for(int i = 0; i < Exercises.size(); i++)
        {
            if(Exercises.get(i).getExercise().equals(exerciseName))
            {
                return i;
            }
        }
        return -1;
    }

    // Converts CSV file to Internally used Dat Structure
    public void csvToSets(List csvList)
    {
        // Remove potential Duplicates
        sets.clear();

        // i = 1 since first row is only Strings
        for(int i = 1; i < csvList.size(); i++)
        {
            String[] row = (String[]) csvList.get(i);

            String Date = row[0];
            String Exercise = row[1];
            String Category = row[2];
            String Reps = row[3];
            String Weight = row[4];

            String Comment = "";

            if(row.length == 6)
            {
                Comment = row[5];
            }

            WorkoutSet workoutSet = new WorkoutSet(Date,Exercise,Category,Double.parseDouble(Weight),Double.parseDouble(Reps),Comment);
            sets.add(workoutSet);
        }
    }

    // Updates All other Data Structures
    public void setsToEverything()
    {
        // Clear Data Structures
        days.clear();
        workoutDays.clear();

        // i = 1 since first row is only Strings
        for(int i = 0; i < sets.size(); i++)
        {
            days.add(sets.get(i).getDate());
        }


        Iterator<String> it = days.iterator();

        // Construct Workout_Days Array List
        while (it.hasNext())
        {
            String Date = it.next();
            WorkoutDay temp_day = new WorkoutDay();

            for(int i = 0; i < sets.size(); i++)
            {
                // If Date matches add Set Object to Workout_Day Object
                if(Date.equals(sets.get(i).getDate()))
                {
                    temp_day.addSet(sets.get(i));
                }
            }
            workoutDays.add(temp_day);
        }
    }

    // Initialized KnownExercises ArrayList with some hardcoded exercises
    public void initKnownExercises()
    {
        knownExercises.clear();
        // Some hardcoded Exercises
        knownExercises.add(new Exercise("Flat Barbell Bench Press","Chest"));
        knownExercises.add(new Exercise("Incline Barbell Bench Press","Chest"));
        knownExercises.add(new Exercise("Decline Barbell Bench Press","Chest"));
        knownExercises.add(new Exercise("Flat Dumbbell Bench Press","Chest"));
        knownExercises.add(new Exercise("Incline Dumbbell Bench Press","Chest"));
        knownExercises.add(new Exercise("Decline Dumbbell Bench Press","Chest"));
        knownExercises.add(new Exercise("Chin Up","Back"));
        knownExercises.add(new Exercise("Seated Dumbbell Press","Shoulders"));
        knownExercises.add(new Exercise("Ring Dip","Chest"));
        knownExercises.add(new Exercise("Lateral Cable Raise","Shoulders"));
        knownExercises.add(new Exercise("Lateral Dumbbell Raise","Shoulders"));
        knownExercises.add(new Exercise("Barbell Curl","Biceps"));
        knownExercises.add(new Exercise("Tricep Extension","Triceps"));
        knownExercises.add(new Exercise("Squat","Legs"));
        knownExercises.add(new Exercise("Leg Extension","Legs"));
        knownExercises.add(new Exercise("Hammstring Leg Curl","Legs"));
        knownExercises.add(new Exercise("Deadlift","Back"));
        knownExercises.add(new Exercise("Sumo Deadlift","Back"));
        knownExercises.add(new Exercise("Seated Machine Chest Press","Chest"));
        knownExercises.add(new Exercise("Seated Machine Shoulder Press","Shoulders"));
        knownExercises.add(new Exercise("Seated Calf Raise","Legs"));
        knownExercises.add(new Exercise("Donkey Calf Raise","Legs"));
        knownExercises.add(new Exercise("Standing Calf Raise","Legs"));
        knownExercises.add(new Exercise("Seated Machine Curl","Biceps"));
        knownExercises.add(new Exercise("Lat Pulldown","Back"));
        knownExercises.add(new Exercise("Pull Up","Back"));
        knownExercises.add(new Exercise("Push Up","Chest"));
        knownExercises.add(new Exercise("Leg Press","Legs"));
        knownExercises.add(new Exercise("Push Press","Shoulders"));
        knownExercises.add(new Exercise("Dumbbell Curl","Biceps"));
        knownExercises.add(new Exercise("Decline Hammer Strength Chest Press","Chest"));
        knownExercises.add(new Exercise("Leg Extension Machine","Legs"));
        knownExercises.add(new Exercise("Seated Calf Raise Machine","Legs"));
        knownExercises.add(new Exercise("Lying Triceps Extension","Triceps"));
        knownExercises.add(new Exercise("Cable Curl","Biceps"));
        knownExercises.add(new Exercise("Hammer Strength Shoulder Press","Shoulders"));
    }

    public void setFavoriteExercise(String exerciseName, Boolean isFavorite)
    {
        // Initialize Volume Record Hashmap
        for(int i = 0; i < knownExercises.size(); i++)
        {
            if(knownExercises.get(i).getName().equals(exerciseName))
            {
                knownExercises.get(i).setFavorite(isFavorite);
            }
        }
    }

    // Calculate all Volume Personal Records from scratch
    public void calculatePersonalRecords()
    {
        volumePRs.clear();
        setVolumePRs.clear();
        actualOneRepMaxPRs.clear();
        estimatedOneRMPRs.clear();
        maxRepsPRs.clear();
        maxWeightPRs.clear();
        lastTimeVolume.clear();

        // Initialize Volume Record Hashmap
        for(int i = 0; i < knownExercises.size(); i++)
        {
            volumePRs.put((knownExercises.get(i).getName()),0.0);
            setVolumePRs.put((knownExercises.get(i).getName()),new Pair(0.0, 0.0));
            actualOneRepMaxPRs.put((knownExercises.get(i).getName()),0.0);
            estimatedOneRMPRs.put((knownExercises.get(i).getName()),0.0);
            maxRepsPRs.put((knownExercises.get(i).getName()),0.0);
            maxWeightPRs.put((knownExercises.get(i).getName()),0.0);
            lastTimeVolume.put((knownExercises.get(i).getName()),0.0);
        }

        // Calculate Volume PRs
        for(int i = 0; i < knownExercises.size(); i++)
        {
            for(int j = 0; j < workoutDays.size(); j++)
            {
                for(int k = 0; k < workoutDays.get(j).getExercises().size(); k++)
                {
                    if(workoutDays.get(j).getExercises().get(k).getExercise().equals(knownExercises.get(i).getName()))
                    {
                        // Per Exercise Volume Personal Records
                        if(volumePRs.get(knownExercises.get(i).getName()) < (workoutDays.get(j).getExercises().get(k).getVolume()))
                        {
                            workoutDays.get(j).getExercises().get(k).setVolumePR(true);
                            volumePRs.put(knownExercises.get(i).getName(),workoutDays.get(j).getExercises().get(k).getVolume());
                        }

                        Double setVolume = setVolumePRs.get(knownExercises.get(i).getName()).first * setVolumePRs.get(knownExercises.get(i).getName()).second;

                        // Per Set Volume Personal Records
                        if(setVolume  < (workoutDays.get(j).getExercises().get(k).getMaxSetVolume()))
                        {
                            Double maxReps = workoutDays.get(j).getExercises().get(k).getMaxReps();
                            Double maxWeight = workoutDays.get(j).getExercises().get(k).getMaxWeight();

                            Pair pair = new Pair(maxReps, maxWeight);

                            setVolumePRs.put(knownExercises.get(i).getName(), pair);

                            WorkoutSet temp_set = new WorkoutSet();
                            temp_set.setReps(maxReps);
                            temp_set.setWeight(maxWeight);
                            maxVolumeSetPRs.put(knownExercises.get(i).getName(), temp_set);
                        }

                        // Actual One Repetition Maximum
                        if(actualOneRepMaxPRs.get(knownExercises.get(i).getName()) < (workoutDays.get(j).getExercises().get(k).getActualOneRepMax()))
                        {
                            workoutDays.get(j).getExercises().get(k).setActualOneRepMaxPR(true);
                            actualOneRepMaxPRs.put(knownExercises.get(i).getName(),workoutDays.get(j).getExercises().get(k).getActualOneRepMax());
                        }

                        // Estimated One Repetition Maximum
                        if(estimatedOneRMPRs.get(knownExercises.get(i).getName()) < (workoutDays.get(j).getExercises().get(k).getEstimatedOneRepMax()))
                        {
                            workoutDays.get(j).getExercises().get(k).setEstimatedOneRepMaxPR(true);
                            estimatedOneRMPRs.put(knownExercises.get(i).getName(),workoutDays.get(j).getExercises().get(k).getEstimatedOneRepMax());
                        }

                        // Max Repetitions Personal Records
                        if(maxRepsPRs.get(knownExercises.get(i).getName()) < (workoutDays.get(j).getExercises().get(k).getMaxReps()))
                        {
                            workoutDays.get(j).getExercises().get(k).setMaxRepsPR(true);
                            maxRepsPRs.put(knownExercises.get(i).getName(),workoutDays.get(j).getExercises().get(k).getMaxReps());
                            maxRepsSetPRs.put(knownExercises.get(i).getName(), workoutDays.get(j).getExercises().get(k).getMaxRepsSet());
                        }

                        // Max Weight Personal Records
                        if(maxWeightPRs.get(knownExercises.get(i).getName()) < (workoutDays.get(j).getExercises().get(k).getMaxWeight()))
                        {
                            workoutDays.get(j).getExercises().get(k).setMaxWeightPR(true);
                            maxWeightPRs.put(knownExercises.get(i).getName(),workoutDays.get(j).getExercises().get(k).getMaxWeight());
                            maxWeightSetPRs.put(knownExercises.get(i).getName(),workoutDays.get(j).getExercises().get(k).getMaxWeightSet());
                        }

                        // Harder Than Last Time!
                        if(lastTimeVolume.get(knownExercises.get(i).getName()) < (workoutDays.get(j).getExercises().get(k).getVolume()))
                        {
                            workoutDays.get(j).getExercises().get(k).setHTLT(true);
                            lastTimeVolume.put(knownExercises.get(i).getName(),workoutDays.get(j).getExercises().get(k).getVolume());
                        }
                        // This needs to be updates since we are dealing with last time and not overall maximums
                        else
                        {
                            lastTimeVolume.put(knownExercises.get(i).getName(),workoutDays.get(j).getExercises().get(k).getVolume());
                        }
                    }
                }
            }
        }
    }

    // Saves Workout_Days Array List in shared preferences
    // For some reason when I pass the context it works so let's roll with it :D
    public void saveWorkoutData(Context ct)
    {
        android.content.SharedPreferences sharedPreferences = ct.getSharedPreferences("shared preferences",MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(workoutDays);
        editor.putString("workouts",json);
        editor.apply();
    }

    // Loads Workout_Days Array List from shared preferences
    public void loadWorkoutData(Context context)
    {
        if(workoutDays.isEmpty())
        {
            android.content.SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences",MODE_PRIVATE);
            Gson gson = new Gson();
            String json = sharedPreferences.getString("workouts",null);
            Type type = new TypeToken<ArrayList<WorkoutDay>>(){}.getType();
            workoutDays = gson.fromJson(json,type);

            // If there are no previously saved entries make a new object
            if(workoutDays == null)
            {
                workoutDays = new ArrayList<WorkoutDay>();
            }
        }
    }

    // Saves Workout_Days Array List in shared preferences
    // For some reason when I pass the context it works so let's roll with it :D
    public void saveKnownExerciseData(Context ct)
    {
        android.content.SharedPreferences sharedPreferences = ct.getSharedPreferences("shared preferences",MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(knownExercises);
        editor.putString("known_exercises",json);
        editor.apply();
    }

    // Loads Workout_Days Array List from shared preferences
    public void loadKnownExercisesData(Context context)
    {
        if(knownExercises.isEmpty())
        {
            SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences",MODE_PRIVATE);
            Gson gson = new Gson();
            String json = sharedPreferences.getString("known_exercises",null);
            Type type = new TypeToken<ArrayList<Exercise>>(){}.getType();
            knownExercises = gson.fromJson(json,type);

            // If there are no previously saved entries make a new object
            if(knownExercises == null || knownExercises.isEmpty())
            {
                knownExercises = new ArrayList<Exercise>();
                initKnownExercises();
            }
        }

        // Those who have previously saved entries will have null in this case
        for(int i = 0; i < knownExercises.size(); i++)
        {
            if(knownExercises.get(i).getFavorite() == null)
            {
                knownExercises.get(i).setFavorite(false);
            }
        }
    }

    // Read CSV from internal storage
    public boolean readFile(Uri uri, Context context)
    {
        try
        {
            List csvList = new ArrayList();
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            CSVFile csvFile = new CSVFile(inputStream);
            csvList = csvFile.read();

            // Here is where the magic happens
            csvToSets(csvList); // Read File and Construct Local Objects
            setsToEverything(); // Convert Set Objects to Day Objects
            csvToKnownExercises(); // Find all Exercises in CSV and add them to known exercises
            saveKnownExerciseData(context); // Save KnownExercises in CSV
            saveWorkoutData(context); // Save WorkoutDays in Shared Preferences
            return true;
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            Toast.makeText(context, "Could not locate file " + uri.getPath(),Toast.LENGTH_SHORT).show();
            clearDataStructures(context);
            return false;
        }

    }

    public void readFromSets(ArrayList<WorkoutSet> sets, Context context)
    {
        clearDataStructures(context);

        this.sets = sets;
        initKnownExercises();
        modifySets(); // bad design requires this
        setsToEverything();
        csvToKnownExercises();
    }

    public void modifySets()
    {
        for(int i = 0; i < sets.size(); i++)
        {
            String date = sets.get(i).getDate().substring(0,10);
            sets.get(i).setDate(date);
        }
    }

    // Export backup function using Storage Access Framework
    public void writeFile(Context context)
    {
        String fileName = EXPORT_FILENAME;

        try
        {
            OutputStream outputStream;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName); // file name required to contain extestion file mime
                values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS+"/Verifit"); //DIRECTORY
                Uri extVolumeUri = MediaStore.Files.getContentUri("external");
                Uri fileUri = context.getContentResolver().insert(extVolumeUri, values);
                outputStream = context.getContentResolver().openOutputStream(fileUri);
            }
            else {
                File root = new File(Environment.getExternalStorageDirectory()+File.separator+"DIRECTORY_NAME", "images");
                File file = new File(root, fileName );
                Log.d(TAG, "saveFile: file path - " + file.getAbsolutePath());
                outputStream = new FileOutputStream(file);
            }
            outputStream.write("Date,Exercise,Category,Weight (kg),Reps,Comment\n".getBytes());

            for(int i = 0; i < workoutDays.size(); i++)
            {
                for(int j = 0; j < workoutDays.get(i).getExercises().size(); j++)
                {
                    String exerciseComment = workoutDays.get(i).getExercises().get(j).getComment();
                    for(int k = 0; k < workoutDays.get(i).getExercises().get(j).getSets().size(); k++)
                    {
                        String Date = workoutDays.get(i).getExercises().get(j).getDate();
                        String exerciseName = workoutDays.get(i).getExercises().get(j).getSets().get(k).getExerciseName();
                        String exerciseCategory = workoutDays.get(i).getExercises().get(j).getSets().get(k).getCategory();
                        Double Weight = workoutDays.get(i).getExercises().get(j).getSets().get(k).getWeight();
                        Double Reps = workoutDays.get(i).getExercises().get(j).getSets().get(k).getReps();
                        outputStream.write((Date + "," + exerciseName+ "," + exerciseCategory + "," + Weight + "," + Reps + "," + exerciseComment + "\n").getBytes());
                    }
                }
            }
            outputStream.close();
            Toast.makeText(context, "Backup saved in " + Environment.DIRECTORY_DOCUMENTS+"/Verifit" , Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    // Clears all locally used data structures
    public void clearDataStructures(Context context)
    {
        // Clear everything just in case
        this.workoutDays.clear();
        this.knownExercises.clear(); // This removes all known exercises
        this.sets.clear();
        this.days.clear();
        this.saveWorkoutData(context);
        this.saveKnownExerciseData(context);
    }

    // Inefficient bubble sort but does the job
    public void sortWorkoutDaysDate()
    {
        Collections.sort(workoutDays, new Comparator<WorkoutDay>() {
            @Override
            public int compare(WorkoutDay workoutDay, WorkoutDay t1)
            {
                String date1 = workoutDay.getDate();
                String date2 = t1.getDate();
                Date date_object1 = new Date();
                Date date_object2 = new Date();

                try {
                    date_object1 = new SimpleDateFormat("yyyy-MM-dd").parse(date1);
                    date_object2 = new SimpleDateFormat("yyyy-MM-dd").parse(date2);
                }
                catch (Exception e)
                {
                    System.out.println(e.getMessage());
                }
                return date_object1.compareTo(date_object2);
            }
        });
    }

    // Just Check if an exercise is known
    public boolean doesExerciseExist(String exercise_name)
    {
        for(int i = 0; i < knownExercises.size(); i++)
        {
            if(knownExercises.get(i).getName().equals(exercise_name))
            {
                return true;
            }
        }
        return false;
    }

    // Returns the exercise category if exists, else it returns an empty string
    public String getExerciseCategory(String Exercise)
    {
        for(int i = 0; i < knownExercises.size(); i++)
        {
            if(knownExercises.get(i).getName().equals(Exercise))
            {
                return knownExercises.get(i).getBodyPart();
            }
        }
        return "";
    }

    // Returns the exercise category if exists, else it returns an empty string
    public Boolean isExerciseFavorite(String Exercise)
    {
        for(int i = 0; i < knownExercises.size(); i++)
        {
            if(knownExercises.get(i).getName().equals(Exercise))
            {
                //System.out.println(Exercise + " " + KnownExercises.get(i).getFavorite());
                return knownExercises.get(i).getFavorite();
            }
        }
        return false;
    }

    // Deletes exercise from KnownExercises and from WorkoutDays
    public void deleteExercise(String exercise_name)
    {
        // Iterate Workout Days
        for (Iterator<WorkoutDay> dayIterator = workoutDays.iterator(); dayIterator.hasNext(); )
        {
            WorkoutDay currentDay = dayIterator.next();

            // Iterate Workout Exercises
            for(Iterator<WorkoutExercise> exerciseIterator = currentDay.getExercises().iterator(); exerciseIterator.hasNext();)
            {
                WorkoutExercise current_exercise = exerciseIterator.next();
                if(current_exercise.getExercise().equals(exercise_name))
                {
                    exerciseIterator.remove();
                }
            }

            // Iterate Workout Sets
            for(Iterator<WorkoutSet> setIterator = currentDay.getSets().iterator(); setIterator.hasNext();)
            {
                WorkoutSet current_set = setIterator.next();
                if(current_set.getExerciseName().equals(exercise_name))
                {
                    setIterator.remove();
                }
            }


            if(currentDay.getSets().isEmpty())
            {
                dayIterator.remove();
            }
        }

        // Iterate Known Exercises data structure
        for(Iterator<Exercise> exerciseIterator = knownExercises.iterator(); exerciseIterator.hasNext();)
        {
            Exercise current_exercise = exerciseIterator.next();

            if(current_exercise.getName().equals(exercise_name))
            {
                exerciseIterator.remove();
            }
        }
    }

    // Returns all the sets to be deleted if we were to run deleteExercise
    public List<WorkoutSet> deleteExerciseGetSets(String exercise_name)
    {
        List<WorkoutSet> to_be_delete_sets = new ArrayList<>();

        // Iterate Workout Days
        for (Iterator<WorkoutDay> dayIterator = workoutDays.iterator(); dayIterator.hasNext(); )
        {
            WorkoutDay currentDay = dayIterator.next();

            // Iterate Workout Sets
            for(Iterator<WorkoutSet> setIterator = currentDay.getSets().iterator(); setIterator.hasNext();)
            {
                WorkoutSet current_set = setIterator.next();
                if(current_set.getExerciseName().equals(exercise_name))
                {
                    to_be_delete_sets.add(current_set);
                }
            }
        }
        return to_be_delete_sets;
    }

    // Changes exercise name and body part
    public void editExercise(String exercise_name, String new_exercise_name, String new_exercise_bodypart)
    {
        for(int i = 0; i < knownExercises.size(); i++)
        {
            if(knownExercises.get(i).getName().equals(exercise_name))
            {
                knownExercises.get(i).setName(new_exercise_name);
                knownExercises.get(i).setBodyPart(new_exercise_bodypart);
            }
        }

        for(int i = 0; i < workoutDays.size(); i++)
        {
            for(int j = 0; j < workoutDays.get(i).getSets().size(); j++)
            {
                if(workoutDays.get(i).getSets().get(j).getExerciseName().equals(exercise_name))
                {
                    workoutDays.get(i).getSets().get(j).setExerciseName(new_exercise_name);
                    workoutDays.get(i).getSets().get(j).setCategory(new_exercise_bodypart);
                }
            }

            for(int j = 0; j < workoutDays.get(i).getExercises().size(); j++)
            {
                if(workoutDays.get(i).getExercises().get(j).getExercise().equals(exercise_name))
                {
                    workoutDays.get(i).getExercises().get(j).setExercise(new_exercise_name);

                    for(int k = 0; k < workoutDays.get(i).getExercises().get(j).getSets().size(); k++)
                    {
                        if(workoutDays.get(i).getExercises().get(j).getSets().get(k).getExerciseName().equals(exercise_name))
                        {
                            workoutDays.get(i).getExercises().get(j).getSets().get(k).setExerciseName(new_exercise_name);
                            workoutDays.get(i).getExercises().get(j).getSets().get(k).setCategory(new_exercise_bodypart);
                        }
                    }
                }
            }
        }
    }

    // Changes exercise name and body part
    public List<WorkoutSet> editExerciseGetSets(String exercise_name, String new_exercise_name, String new_exercise_bodypart)
    {
        List<WorkoutSet> to_be_updated_sets = new ArrayList<>();

        for(int i = 0; i < workoutDays.size(); i++)
        {
            for(int j = 0; j < workoutDays.get(i).getSets().size(); j++)
            {
                if(workoutDays.get(i).getSets().get(j).getExerciseName().equals(exercise_name))
                {
                    WorkoutSet temp_set = workoutDays.get(i).getSets().get(j);
                    temp_set.setExerciseName(new_exercise_name);
                    temp_set.setCategory(new_exercise_bodypart);
                    to_be_updated_sets.add(temp_set);
                }
            }

            for(int j = 0; j < workoutDays.get(i).getExercises().size(); j++)
            {
                if(workoutDays.get(i).getExercises().get(j).getExercise().equals(exercise_name))
                {
                    for(int k = 0; k < workoutDays.get(i).getExercises().get(j).getSets().size(); k++)
                    {
                        if(workoutDays.get(i).getExercises().get(j).getSets().get(k).getExerciseName().equals(exercise_name))
                        {
                            WorkoutSet temp_set = workoutDays.get(i).getExercises().get(j).getSets().get(k);
                            temp_set.setExerciseName(new_exercise_name);
                            temp_set.setCategory(new_exercise_bodypart);
                            to_be_updated_sets.add(temp_set);
                        }
                    }
                }
            }
        }
        return to_be_updated_sets;
    }

    // Add all exercises found in the csv to the Known Exercises local data structure
    public void csvToKnownExercises()
    {
        // Make new ArrayList which will hold duplicates
        ArrayList<Exercise> DuplicateKnownExercises = new ArrayList<>();

        for(int i = 0; i < workoutDays.size(); i++)
        {
            for(int j = 0; j < workoutDays.get(i).getSets().size(); j++)
            {
                String Name = workoutDays.get(i).getSets().get(j).getExerciseName();
                String Bodypart = workoutDays.get(i).getSets().get(j).getCategory();
                DuplicateKnownExercises.add(new Exercise(Name,Bodypart));
            }
        }


        // Manual Implementation "borrowed" from stack overflow
        for (Exercise event : DuplicateKnownExercises) {
            boolean isFound = false;
            // check if the event name exists in noRepeat
            for (Exercise e : knownExercises) {
                if (e.getName().equals(event.getName()) || (e.equals(event))) {
                    isFound = true;
                    break;
                }
            }
            if (!isFound) knownExercises.add(event);
        }
    }



}
