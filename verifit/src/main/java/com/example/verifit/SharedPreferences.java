package com.example.verifit;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;

import com.example.verifit.ui.MainActivity;
import com.example.verifit.verifitrs.ResponseUser;
import com.google.gson.Gson;

public class SharedPreferences {

    Context context;

    public SharedPreferences(Context context)
    {
        this.context = context;
    }


    public void save(String value, String key)
    {
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String load(String key)
    {
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE);
        String text = sharedPreferences.getString(key, "");
        return text;
    }

    public void enableOfflineMode()
    {
        SharedPreferences sharedPreferences = new SharedPreferences(context);
        sharedPreferences.save("", "verifit_rs_username");
        sharedPreferences.save("", "verifit_rs_password");
        sharedPreferences.save("", "verifit_rs_token");
        sharedPreferences.save("offline","mode");
        MainActivity.dataStorage.clearDataStructures(context);
    }

    public void enableOnlineMode(String responseBody, String username, String password)
    {
        Gson gson = new Gson();
        ResponseUser responseUser = gson.fromJson(responseBody, ResponseUser.class);
        SharedPreferences sharedPreferences = new SharedPreferences(context);
        sharedPreferences.save(responseUser.getToken(), "verifit_rs_token");
        sharedPreferences.save(username, "verifit_rs_username");
        sharedPreferences.save(password, "verifit_rs_password");
        sharedPreferences.save("online","mode");
    }

    public boolean isOfflineMode()
    {
        SharedPreferences sharedPreferences = new SharedPreferences(context);
        return sharedPreferences.load("mode").equals("offline") || sharedPreferences.load("mode").equals("");
    }

}
