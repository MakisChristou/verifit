package com.example.harderthanlasttime;

import java.util.ArrayList;

public class WorkoutDay {

    // Attributes
    private ArrayList<WorkoutExercise> Exercises;
    private ArrayList<WorkoutSet> Sets;
    private Double DayVolume;
    private String Date;
    private int Reps;

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
        this.Reps = 0;
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
