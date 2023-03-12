package com.example.verifit;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;

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
}
