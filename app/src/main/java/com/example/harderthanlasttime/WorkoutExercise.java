package com.example.harderthanlasttime;

public class WorkoutExercise {

    private String Exercise;
    private Double Volume;
    private Double EstimatedOneRepMax; // Epley formula
    private Double MaxWeight;
    private Double MaxReps;
    private Double TotalReps;
    private Double TotalSets;

    public boolean isPR() {
        return PR;
    }
    public void setPR(boolean PR) {
        this.PR = PR;
    }
    private boolean PR;


    public Double getTotalSets() {
        return TotalSets;
    }

    public void setTotalSets(Double totalSets) {
        TotalSets = totalSets;
    }

    public Double getEstimatedOneRepMax() {
        return EstimatedOneRepMax;
    }

    public void setEstimatedOneRepMax(Double estimatedOneRepMax) {
        EstimatedOneRepMax = estimatedOneRepMax;
    }

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

    // Geters
    public String getExercise() {
        return Exercise;
    }

    public Double getVolume() {
        return Volume;
    }

    // Setters
    public void setExercise(String exercise) {
        Exercise = exercise;
    }

    public void setVolume(Double volume) {
        Volume = volume;
    }
}
