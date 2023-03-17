package com.example.verifit.model;

public class WorkoutSet {

    // Attributes
    private int id;
    private String date;
    private String exercise_name;
    private String category;
    private Double reps;
    private Double weight;
    private String comment;

    private int user_id;


    public WorkoutSet()
    {

    }

    public WorkoutSet(String Date, String Exercise, String Category, Double Reps, Double Weight)
    {
        this.date = Date;
        this.exercise_name = Exercise;
        this.category = Category;
        this.reps = Reps;
        this.weight = Weight;
        this.comment = "";
    }
    public WorkoutSet(String Date, String Exercise, String Category, Double Reps, Double Weight,String Comment)
    {
        this.date = Date;
        this.exercise_name = Exercise;
        this.category = Category;
        this.reps = Reps;
        this.weight = Weight;
        this.comment = Comment;
    }


    // Methods
    // Setters
    public void setDate(String Date)
    {
        this.date = Date;
    }
    public void setExerciseName(String Exercise)
    {
        this.exercise_name = Exercise;
    }
    public void setCategory(String Category)
    {
        this.category = Category;
    }
    public void setReps(Double Reps) {
        this.reps = Reps;
    }
    public void setWeight(Double Weight)
    {
        this.weight = Weight;
    }
    public void setComment(String Comment){this.comment = Comment;}
    public void setId(int id) {this.id = id;}
    public void setUser_id(int user_id) {this.user_id = user_id;}


    // Getters
    public String getDate()
    {
        return this.date;
    }
    public String getExerciseName()
    {
        return this.exercise_name;
    }
    public String getCategory()
    {
        return this.category;
    }
    public Double getReps()
    {
        return this.reps;
    }
    public Double getWeight()
    {
        return this.weight;
    }
    public String getComment() {return this.comment;}
    public int getUser_id() {return user_id;}
    public int getId() {return id;}


    // Other
    public Double getVolume()
    {
        return this.reps * this.weight;
    }
    public Double getEplayOneRepMax(){return this.weight *(1+(this.reps /30));}
}
