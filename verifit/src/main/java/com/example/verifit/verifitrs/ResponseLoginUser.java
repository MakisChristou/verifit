package com.example.verifit.verifitrs;

public class ResponseLoginUser {
    String username;
    int id;
    String token;


    public ResponseLoginUser(String username, int id, String token)
    {
        this.username = username;
        this.id = id;
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
