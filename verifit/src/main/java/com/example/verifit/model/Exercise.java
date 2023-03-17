package com.example.verifit.model;

public class Exercise {

    // Attributes
    private String Name;
    private String BodyPart;
    private Boolean favorite;

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }




    // Methods
    public Exercise(String name, String bodypart)
    {
        this.Name = name;
        this.BodyPart = bodypart;
        this.favorite = false;
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

}
