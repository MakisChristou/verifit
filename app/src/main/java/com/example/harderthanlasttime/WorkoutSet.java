package com.example.harderthanlasttime;

public class WorkoutSet {

    // Attributes
    private String Date;
    private String Exercise;
    private String Category;
    private Double Reps;
    private Double Weight;

    // Methods
    // Setters
    public void setDate(String Date)
    {
        this.Date = Date;
    }
    public void setExercise(String Exercise)
    {
        this.Exercise = Exercise;
    }
    public void setCategory(String Category)
    {
        this.Category = Category;
    }
    public void setReps(Double Reps) {
        this.Reps = Reps;
    }
    public void setWeight(Double Weight)
    {
        this.Weight = Weight;
    }

    // Getters
    public String getDate()
    {
        return this.Date;
    }
    public String getExercise()
    {
        return this.Exercise;
    }
    public String getCategory()
    {
        return this.Category;
    }
    public Double getReps()
    {
        return this.Reps;
    }
    public Double getWeight()
    {
        return this.Weight;
    }

    // Other
    public Double getVolume()
    {
        return this.Reps * this.Weight;
    }
    public Double getEplayOneRepMax(){return this.Weight*(1+(this.Reps/30));}
}
