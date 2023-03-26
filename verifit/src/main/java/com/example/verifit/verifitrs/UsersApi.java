package com.example.verifit.verifitrs;

import android.content.Context;
import android.content.Intent;

import com.example.verifit.DataStorage;
import com.example.verifit.SharedPreferences;
import com.example.verifit.SnackBarWithMessage;
import com.example.verifit.ui.LoginActivity;
import com.example.verifit.ui.MainActivity;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UsersApi {
    Context context;
    String url;
    String username;
    String password;

    public UsersApi(Context context, String url, String username, String password)
    {
        this.context = context;
        this.url = url;
        this.username = username;
        this.password = password;
    }


    public void login(okhttp3.Callback callback)
    {
        url += "/users/login";
        OkHttpClient client = new OkHttpClient();

        // Create a JSON object to send in the request body
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            System.out.println(e);
        }

        // Create a RequestBody object with the JSON object
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));

        // Create a Request object with the URL and RequestBody
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        // Send the HTTP request asynchronously
        client.newCall(request).enqueue(callback);
    }

    public void logout(okhttp3.Callback callback)
    {
        OkHttpClient client = new OkHttpClient();

        // Create a JSON object to send in the request body
        JSONObject jsonObject = new JSONObject();

        url += "/users/logout";

        SharedPreferences sharedPreferences = new SharedPreferences(context);
        String token = sharedPreferences.load("verifit_rs_token");

        // Create a RequestBody object with the JSON object
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));

        // Create a Request object with the URL and RequestBody
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .post(requestBody)
                .build();


        // Send the HTTP request asynchronously
        client.newCall(request).enqueue(callback);
    }

    public void createAccount(okhttp3.Callback callback)
    {
        OkHttpClient client = new OkHttpClient();

        // Create a JSON object to send in the request body
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            System.out.println(e);
        }

        url += "/users";

        // Create a RequestBody object with the JSON object
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));

        // Create a Request object with the URL and RequestBody
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        // Send the HTTP request asynchronously
        client.newCall(request).enqueue(callback);
    }

    public void requestPasswordReset(okhttp3.Callback callback)
    {
        OkHttpClient client = new OkHttpClient();

        // Create a JSON object to send in the request body
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
        } catch (JSONException e) {
            System.out.println(e);
        }

        url += "/users/request-password-reset";

        // Create a RequestBody object with the JSON object
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));

        // Create a Request object with the URL and RequestBody
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        // Send the HTTP request asynchronously
        client.newCall(request).enqueue(callback);
    }

    public void changePassword(String reset_code, okhttp3.Callback callback)
    {
        OkHttpClient client = new OkHttpClient();

        // Create a JSON object to send in the request body
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("new_password", password);
            jsonObject.put("reset_code", reset_code);
        } catch (JSONException e) {
            System.out.println(e);
        }

        url += "/users/change-password";

        // Create a RequestBody object with the JSON object
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));

        // Create a Request object with the URL and RequestBody
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        // Send the HTTP request asynchronously
        client.newCall(request).enqueue(callback);
    }

    public void requestEmailVerification(okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();

        // Create a JSON object to send in the request body
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
        } catch (JSONException e) {
            System.out.println(e);
        }

        url += "/users/request-email-verification";

        // Create a RequestBody object with the JSON object
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));

        // Create a Request object with the URL and RequestBody
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        // Send the HTTP request asynchronously
        client.newCall(request).enqueue(callback);
    }
}

