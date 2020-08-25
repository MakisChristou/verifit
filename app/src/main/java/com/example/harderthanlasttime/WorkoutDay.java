package com.example.harderthanlasttime;

import java.util.ArrayList;

public class WorkoutDay {

    private ArrayList<WorkoutExercise> Exercises;
    private ArrayList<WorkoutSet> Sets;
    private Double DayVolume;
    private String Date;

    public ArrayList<WorkoutSet> getSets() {
        return Sets;
    }
    public void setSets(ArrayList<WorkoutSet> sets) {
        Sets = sets;
    }
    public void setExercises(ArrayList<WorkoutExercise> exercises) {
        Exercises = exercises;
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
