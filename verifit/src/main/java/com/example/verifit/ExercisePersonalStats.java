package com.example.verifit;

import android.util.Pair;

import com.example.verifit.model.WorkoutSet;

public class ExercisePersonalStats {

    String exerciseName;
    String exerciseCategory;
    Boolean isFavorite;
    Double totalSets;
    Double totalReps;
    Double totalVolume;
    Double maxVolume;
    Double maxWeight;
    Double maxReps;
    Double maxSetVolume;
    Double maxSetVolumeReps;
    Double MaxSetVolumeWeight;
    Double actual1RM;
    Double estimated1RM;
    Double averageSetsPerWeek;
    Double averageSetsPerWeekLastMoth;
    Double lastWeekSets;

    WorkoutSet maxVolumeSet;
    WorkoutSet maxRepsSet;
    WorkoutSet maxWeightSet;


    public Boolean getFavorite() {
        return isFavorite;
    }

    public Double getEstimated1RM() {
        return estimated1RM;
    }

    public void setEstimated1RM(Double estimated1RM) {
        this.estimated1RM = estimated1RM;
    }

    public void setFavorite(Boolean favorite) {
        isFavorite = favorite;
    }

    public String getExerciseCategory() {
        return exerciseCategory;
    }

    public void setExerciseCategory(String exerciseCategory) {
        this.exerciseCategory = exerciseCategory;
    }

    public String getExerciseName()
    {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public Double getTotalSets() {
        return totalSets;
    }

    public void setTotalSets(Double totalSets) {
        this.totalSets = totalSets;
    }

    public Double getTotalReps() {
        return totalReps;
    }

    public void setTotalReps(Double totalReps) {
        this.totalReps = totalReps;
    }

    public Double getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(Double totalVolume) {
        this.totalVolume = totalVolume;
    }

    public Double getMaxVolume() {
        return maxVolume;
    }

    public void setMaxVolume(Double maxVolume) {
        this.maxVolume = maxVolume;
    }

    public Double getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(Double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public Double getMaxReps() {
        return maxReps;
    }

    public void setMaxReps(Double maxReps) {
        this.maxReps = maxReps;
    }

    public Double getMaxSetVolume() {
        return maxSetVolume;
    }

    public void setMaxSetVolume(Double maxSetVolume) {
        this.maxSetVolume = maxSetVolume;
    }

    public Double getActual1RM() {
        return actual1RM;
    }

    public void setActual1RM(Double actual1RM) {
        this.actual1RM = actual1RM;
    }

    public Double getAverageSetsPerWeek() {
        return averageSetsPerWeek;
    }

    public void setAverageSetsPerWeek(Double averageSetsPerWeek) {
        this.averageSetsPerWeek = averageSetsPerWeek;
    }

    public Double getAverageSetsPerWeekLastMoth() {
        return averageSetsPerWeekLastMoth;
    }

    public void setAverageSetsPerWeekLastMoth(Double averageSetsPerWeekLastMoth) {
        this.averageSetsPerWeekLastMoth = averageSetsPerWeekLastMoth;
    }

    public Double getLastWeekSets() {
        return lastWeekSets;
    }

    public void setLastWeekSets(Double lastWeekSets) {
        this.lastWeekSets = lastWeekSets;
    }

    public Double getMaxSetVolumeReps() {
        return maxSetVolumeReps;
    }

    public void setMaxSetVolumeReps(Double maxSetVolumeReps) {
        this.maxSetVolumeReps = maxSetVolumeReps;
    }

    public Double getMaxSetVolumeWeight() {
        return MaxSetVolumeWeight;
    }

    public void setMaxSetVolumeWeight(Double MaxSetVolumeWeight) {
        this.MaxSetVolumeWeight = MaxSetVolumeWeight;
    }

    public WorkoutSet getMaxVolumeSet() {
        return maxVolumeSet;
    }

    public void setMaxVolumeSet(WorkoutSet maxVolumeSet) {
        this.maxVolumeSet = maxVolumeSet;
    }

    public WorkoutSet getMaxRepsSet() {
        return maxRepsSet;
    }

    public void setMaxRepsSet(WorkoutSet maxRepsSet) {
        this.maxRepsSet = maxRepsSet;
    }

    public WorkoutSet getMaxWeightSet() {
        return maxWeightSet;
    }

    public void setMaxWeightSet(WorkoutSet maxWeightSet) {
        this.maxWeightSet = maxWeightSet;
    }

    public ExercisePersonalStats()
    {
        this.exerciseName = "";
        this.totalSets = 0.0;
        this.isFavorite = false;
        this.totalReps = 0.0;
        this.totalVolume = 0.0;
        this.maxVolume = 0.0;
        this.maxWeight = 0.0;
        this.maxReps = 0.0;
        this.maxSetVolume = 0.0;
        this.maxSetVolumeReps = 0.0;
        this.MaxSetVolumeWeight = 0.0;
        this.actual1RM = 0.0;
        this.estimated1RM = 0.0;
        this.averageSetsPerWeek = 0.0;
        this.averageSetsPerWeekLastMoth = 0.0;
        this.lastWeekSets = 0.0;

        this.maxVolumeSet = new WorkoutSet();
        this.maxRepsSet = new WorkoutSet();
        this.maxWeightSet = new WorkoutSet();
    }



}
