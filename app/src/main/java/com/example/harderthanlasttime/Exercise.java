package com.example.harderthanlasttime;

public class Exercise {

    // Attributes
    private String Name;
    private String Category;
    private String BodyPart;
    private String Comments;


    // Methods
    public Exercise(String name, String category, String bodypart, String comments)
    {
        this.Name = name;
        this.BodyPart = bodypart;
        this.Category = category;
        this.Comments = comments;
    }

    public String getBodyPart() {
        return BodyPart;
    }

    public void setBodyPart(String bodyPart) {
        BodyPart = bodyPart;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }
}
