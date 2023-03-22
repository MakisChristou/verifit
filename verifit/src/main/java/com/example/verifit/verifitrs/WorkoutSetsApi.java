package com.example.verifit.verifitrs;

import android.content.Context;

import com.example.verifit.SharedPreferences;
import com.example.verifit.model.WorkoutSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;


public class WorkoutSetsApi {
    Context context;
    String url;
    String username;
    String password;

    public WorkoutSetsApi(Context context, String url)
    {
        this.context = context;
        this.url = url;

        com.example.verifit.SharedPreferences sharedPreferences = new com.example.verifit.SharedPreferences(context);
        String username = sharedPreferences.load("verifit_rs_username");
        String password = sharedPreferences.load("verifit_rs_password");

        this.username = username;
        this.password = password;
    }

    public void postWorkoutSet(WorkoutSet set, okhttp3.Callback callback)
    {
        url += "/sets";

        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObject = new JSONObject();

        try
        {
            jsonObject.put("date", set.getDate()+"T00:00:00.000000+00:00"); // has to be same formating as the server
            jsonObject.put("exercise_name", set.getExerciseName());
            jsonObject.put("category", set.getCategory());
            jsonObject.put("reps", set.getReps());
            jsonObject.put("weight", set.getWeight());
            jsonObject.put("comment", set.getComment());

        }
        catch (JSONException e)
        {
            System.out.println(e);
        }

        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));

        SharedPreferences sharedPreferences = new SharedPreferences(context);
        String token = sharedPreferences.load("verifit_rs_token");

        // Create a Request object with the URL and RequestBody
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .post(requestBody)
                .build();


        // Send the HTTP request asynchronously
        client.newCall(request).enqueue(callback);
    }

    public void postWorkoutSets(List<WorkoutSet> sets, okhttp3.Callback callback) {
        url += "/sets/bulk";

        OkHttpClient client = new OkHttpClient();

        JSONArray jsonArray = new JSONArray();

        for (WorkoutSet set : sets) {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("date", set.getDate() + "T00:00:00.000000+00:00"); // has to be same formatting as the server
                jsonObject.put("exercise_name", set.getExerciseName());
                jsonObject.put("category", set.getCategory());
                jsonObject.put("reps", set.getReps());
                jsonObject.put("weight", set.getWeight());
                jsonObject.put("comment", set.getComment());

                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                System.out.println(e);
            }
        }

        RequestBody requestBody = RequestBody.create(jsonArray.toString(), MediaType.parse("application/json; charset=utf-8"));

        SharedPreferences sharedPreferences = new SharedPreferences(context);
        String token = sharedPreferences.load("verifit_rs_token");

        // Create a Request object with the URL and RequestBody
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .post(requestBody)
                .build();

        // Send the HTTP request asynchronously
        client.newCall(request).enqueue(callback);
    }

    public void deleteWorkoutSets(List<WorkoutSet> sets, okhttp3.Callback callback)
    {
        url += "/sets/bulk";

        OkHttpClient client = new OkHttpClient();

        JSONArray jsonArray = new JSONArray();

        for (WorkoutSet set : sets)
        {
            jsonArray.put(set.getId());
        }


        RequestBody requestBody = RequestBody.create(jsonArray.toString(), MediaType.parse("application/json; charset=utf-8"));

        SharedPreferences sharedPreferences = new SharedPreferences(context);
        String token = sharedPreferences.load("verifit_rs_token");

        // Create a Request object with the URL and RequestBody
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .delete(requestBody)
                .build();

        System.out.println(request.toString());


        // Send the HTTP request asynchronously
        client.newCall(request).enqueue(callback);
    }

    public void deleteWorkoutSet(WorkoutSet set, okhttp3.Callback callback)
    {
        url += "/sets/" + set.getId();

        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObject = new JSONObject();

        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));

        SharedPreferences sharedPreferences = new SharedPreferences(context);
        String token = sharedPreferences.load("verifit_rs_token");

        // Create a Request object with the URL and RequestBody
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .delete(requestBody)
                .build();


        // Send the HTTP request asynchronously
        client.newCall(request).enqueue(callback);
    }

    public void updateWorkoutSet(WorkoutSet set, okhttp3.Callback callback)
    {
        url += "/sets/" + set.getId();

        OkHttpClient client = new OkHttpClient();
        JSONObject jsonObject = new JSONObject();

        try
        {
            jsonObject.put("exercise_name", set.getExerciseName());
            jsonObject.put("category", set.getCategory());
            jsonObject.put("reps", set.getReps());
            jsonObject.put("weight", set.getWeight());
            jsonObject.put("comment", set.getComment());
        }
        catch (JSONException e)
        {
            System.out.println(e);
        }

        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));

        SharedPreferences sharedPreferences = new SharedPreferences(context);
        String token = sharedPreferences.load("verifit_rs_token");

        // Create a Request object with the URL and RequestBody
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .put(requestBody)
                .build();


        // Send the HTTP request asynchronously
        client.newCall(request).enqueue(callback);
    }

    public void updateWorkoutSets(List<WorkoutSet> sets, okhttp3.Callback callback) {
        url += "/sets/bulk";

        OkHttpClient client = new OkHttpClient();
        JSONArray jsonArray = new JSONArray();

        for (WorkoutSet set : sets) {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("id", set.getId());
                jsonObject.put("date", set.getDate() + "T00:00:00.000000+00:00"); // has to be same formatting as the server
                jsonObject.put("exercise_name", set.getExerciseName());
                jsonObject.put("category", set.getCategory());
                jsonObject.put("reps", set.getReps());
                jsonObject.put("weight", set.getWeight());
                jsonObject.put("comment", set.getComment());

                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                System.out.println(e);
            }
        }

        RequestBody requestBody = RequestBody.create(jsonArray.toString(), MediaType.parse("application/json; charset=utf-8"));

        SharedPreferences sharedPreferences = new SharedPreferences(context);
        String token = sharedPreferences.load("verifit_rs_token");

        // Create a Request object with the URL and RequestBody
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .put(requestBody)
                .build();

        // Send the HTTP request asynchronously
        client.newCall(request).enqueue(callback);
    }

    public void getAllWorkoutSets(okhttp3.Callback callback)
    {
        OkHttpClient client = new OkHttpClient();

        url += "/sets";

        SharedPreferences sharedPreferences = new SharedPreferences(context);
        String token = sharedPreferences.load("verifit_rs_token");

        // Create a Request object with the URL and RequestBody
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .build();


        // Send the HTTP request asynchronously
        client.newCall(request).enqueue(callback);
    }
}
