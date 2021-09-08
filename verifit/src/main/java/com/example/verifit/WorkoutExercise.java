package com.example.verifit;

import java.util.ArrayList;

public class WorkoutExercise {

    // Attributes
    private ArrayList<WorkoutSet> Sets;
    private String Date;
    private String Exercise;
    private Double Volume;
    private Double EstimatedOneRepMax; // Epley formula
    private Double ActualOneRepMax;
    private Double MaxWeight;
    private Double MaxReps;
    private Double TotalReps;
    private Double TotalSets;
    private Double MaxSetVolume;
    private boolean VolumePR; //is Volume PR?
    private boolean ActualOneRepMaxPR; // Is actual one rep max PR?
    private boolean EstimatedOneRepMaxPR; // Is estimated one rep max PR?
    private boolean MaxRepsPR; // Is reps PR?
    private boolean MaxWeightPR; // Is wight PR?
    private boolean HTLT; // Is HARDER THAN LAST TIME?
    private String Comment=""; // Comment about the specific exercise of that day (e.g. machine settings)



    public boolean isActualOneRepMaxPR() {
        return ActualOneRepMaxPR;
    }

    public void setActualOneRepMaxPR(boolean actualOneRepMaxPR) {
        ActualOneRepMaxPR = actualOneRepMaxPR;
    }

    public boolean isEstimatedOneRepMaxPR() {
        return EstimatedOneRepMaxPR;
    }

    public void setEstimatedOneRepMaxPR(boolean estimatedOneRepMaxPR) {
        EstimatedOneRepMaxPR = estimatedOneRepMaxPR;
    }

    public boolean isMaxRepsPR() {
        return MaxRepsPR;
    }

    public void setMaxRepsPR(boolean maxRepsPR) {
        MaxRepsPR = maxRepsPR;
    }

    public boolean isMaxWeightPR() {
        return MaxWeightPR;
    }

    public void setMaxWeightPR(boolean maxWeightPR) {
        MaxWeightPR = maxWeightPR;
    }

    public boolean isHTLT() {
        return HTLT;
    }

    public void setHTLT(boolean HTLT) {
        this.HTLT = HTLT;
    }


    public void setComment(String Comment) {this.Comment = Comment;}
    public String getComment(){return this.Comment; }

    // Methods
    public Double getActualOneRepMax()
    {
        return ActualOneRepMax;
    }
    public void setActualOneRepMax(Double actualOneRepMax)
    {
        ActualOneRepMax = actualOneRepMax;
    }
    public String getDate() {
        return Date;
    }
    public Double getMaxSetVolume() {
        return MaxSetVolume;
    }
    public void setMaxSetVolume(Double maxSetVolume) {
        MaxSetVolume = maxSetVolume;
    }
    public void setDate(String date) {
        Date = date;
    }
    public ArrayList<WorkoutSet> getSets() {
        return Sets;
    }
    public void setSets(ArrayList<WorkoutSet> sets) {
        Sets = sets;
    }
    public boolean isVolumePR() {
        return VolumePR;
    }
    public void setVolumePR(boolean volumePR) {
        this.VolumePR = volumePR;
    }
    public Double getTotalSets() {
        return TotalSets;
    }
    public void setTotalSets(Double totalSets) {
        TotalSets = totalSets;
    }
    public Double getEstimatedOneRepMax() {
        return EstimatedOneRepMax;
    }
    public void setEstimatedOneRepMax(Double estimatedOneRepMax) { EstimatedOneRepMax = estimatedOneRepMax; }
    public Double getMaxWeight() {
        return MaxWeight;
    }
    public void setMaxWeight(Double maxWeight) {
        MaxWeight = maxWeight;
    }
    public Double getMaxReps() {
        return MaxReps;
    }
    public void setMaxReps(Double maxReps) {
        MaxReps = maxReps;
    }
    public Double getTotalReps() {
        return TotalReps;
    }
    public void setTotalReps(Double totalReps) {
        TotalReps = totalReps;
    }
    public String getExercise() {
        return Exercise;
    }
    public Double getVolume() {
        return Volume;
    }
    public void setExercise(String exercise) {
        Exercise = exercise;
    }
    public void setVolume(Double volume) {
        Volume = volume;
    }
}
