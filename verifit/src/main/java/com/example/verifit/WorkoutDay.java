package com.example.verifit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class WorkoutDay {

    // Attributes
    private ArrayList<WorkoutExercise> Exercises;
    private ArrayList<WorkoutSet> Sets;
    private Double DayVolume;
    private String Date;
    private int Reps;


    // Default Constructor
    public WorkoutDay()
    {
        Sets = new ArrayList<WorkoutSet>();
        Exercises = new ArrayList<WorkoutExercise>();
        DayVolume = 0.0;
        Date = "0000-00-00";
        Reps = 0;
    }

    // Add Set to Object
    public void addSet(WorkoutSet Set)
    {
        this.getSets().add(Set);
        UpdateData();
    }

    public void removeSet(WorkoutSet Set)
    {

        assert this.Sets.size() > 1 : "removeSet should not be called in this case but delete the whole object instead";
        this.getSets().remove(Set);
        UpdateData();

    }

    // Update Data Structure Data
    public void UpdateData()
    {
        if(Sets.isEmpty())
        {
            Sets.clear();
            Exercises.clear();
            DayVolume = 0.0;
            Date = "0000-00-00";
            Reps = 0;
            return;
        }

        Date = Sets.get(0).getDate();

        ArrayList day_sets = Sets;
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
            Double exercise_total_sets = 0.0;
            Double exercise_actual_one_rep_max = 0.0;
            String exercise_date = "";
            double exercise_max_set_volume = 0.0;
            String exercise_comment = "";

            ArrayList<WorkoutSet> exercise_sets = new ArrayList<WorkoutSet>();

            for(int j = 0; j < day_sets.size(); j++)
            {
                WorkoutSet temp_set = (WorkoutSet) day_sets.get(j);
                if(temp_set.getExercise().equals(exercise_name))
                {
                    // Cumulative Values
                    exercise_volume = exercise_volume + temp_set.getVolume();
                    exercise_total_reps = exercise_total_reps + temp_set.getReps();
                    exercise_total_sets = exercise_total_sets + 1;
                    exercise_sets.add(temp_set);
                    exercise_date = temp_set.getDate();
                    exercise_comment = temp_set.getComment(); // This should be done once but this still works

                    // Max Values
                    if(temp_set.getEplayOneRepMax() > exercise_one_rep_max)
                    {
                        exercise_one_rep_max = temp_set.getEplayOneRepMax();
                    }
                    if(temp_set.getReps() == 1 && temp_set.getWeight() > exercise_actual_one_rep_max)
                    {
                        exercise_actual_one_rep_max = temp_set.getWeight();
                    }
                    if(temp_set.getReps() > exercise_max_reps)
                    {
                        exercise_max_reps = temp_set.getReps();
                    }
                    if(temp_set.getWeight() > exercise_max_weight)
                    {
                        exercise_max_weight = temp_set.getWeight();
                    }
                    if((temp_set.getReps()* temp_set.getWeight()) > exercise_max_set_volume)
                    {
                        exercise_max_set_volume = temp_set.getReps()* temp_set.getWeight();
                    }
                }

                // Update exercise object
                day_exercise.setVolume(exercise_volume);
                day_exercise.setEstimatedOneRepMax(exercise_one_rep_max);
                day_exercise.setMaxReps(exercise_max_reps);
                day_exercise.setMaxWeight(exercise_max_weight);
                day_exercise.setTotalReps(exercise_total_reps);
                day_exercise.setTotalSets(exercise_total_sets);
                day_exercise.setSets(exercise_sets);
                day_exercise.setDate(exercise_date);
                day_exercise.setMaxSetVolume(exercise_max_set_volume);
                day_exercise.setActualOneRepMax(exercise_actual_one_rep_max);
                day_exercise.setComment(exercise_comment);

            }
            Day_Exercises.add(day_exercise);
        }

        Exercises = Day_Exercises;
        DayVolume = Day_Volume;
        Reps = 0;

        // Calculate Total Daily Reps
        for(int i = 0; i < Exercises.size(); i++)
        {
            Reps = Reps + (int)Math.round(Exercises.get(i).getTotalReps());
        }

    }

    public int getReps()
    {
        return this.Reps;
    }

    // Methods
    public ArrayList<WorkoutSet> getSets() {
        return Sets;
    }
    public void setSets(ArrayList<WorkoutSet> sets) {
        this.Sets = sets;
    }
    public void setExercises(ArrayList<WorkoutExercise> exercises) {
        this.Exercises = exercises;
        Reps = 0;
        // Calculate Total Daily Reps
        for(int i = 0; i < Exercises.size(); i++)
        {
            Reps = Reps + (int)Math.round(Exercises.get(i).getTotalReps());
        }

    }
    public void setDate(String date) {
        Date = date;
    }
    public String getDate() {
        return Date;
    }
    public ArrayList<WorkoutExercise> getExercises() {
        return Exercises;
    }
    public Double getDayVolume() {
        return DayVolume;
    }
    public void setDayVolume(Double volume) {
        DayVolume = volume;
    }

}
