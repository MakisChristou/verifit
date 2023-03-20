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

    public void logout()
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
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle error
                SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(context);
                snackBarWithMessage.showSnackbar(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {


                sharedPreferences.enableOfflineMode();

                if (200 == response.code())
                {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("message", "verifit_rs_logout"); // Replace "key" with a key identifier and "value" with the actual string value
                    context.startActivity(intent);
                }
                else
                {
                    SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(context);
                    snackBarWithMessage.showSnackbar(response.message().toString());
                }
            }
        });

    }

    public void createAccount()
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
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle error
                System.out.println(e);

                // You are logged out
                SharedPreferences sharedPreferences = new SharedPreferences(context);
                sharedPreferences.save("", "verifit_rs_username");
                sharedPreferences.save("", "verifit_rs_password");
                sharedPreferences.save("", "verifit_rs_token");
                sharedPreferences.save("offline","mode");
                MainActivity.dataStorage.clearDataStructures(context);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(context);

                if (200 == response.code())
                {
                    Gson gson = new Gson();
                    ResponseUser responseUser = gson.fromJson(responseBody, ResponseUser.class);

                    SharedPreferences sharedPreferences = new SharedPreferences(context);
                    sharedPreferences.save(responseUser.getToken(), "verifit_rs_token");
                    sharedPreferences.save(username, "verifit_rs_username");
                    sharedPreferences.save(password, "verifit_rs_password");

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("message", "verifit_rs_signup");
                    context.startActivity(intent);
                }
                else
                {
                    // You are logged out
                    SharedPreferences sharedPreferences = new SharedPreferences(context);
                    sharedPreferences.save("", "verifit_rs_username");
                    sharedPreferences.save("", "verifit_rs_password");
                    sharedPreferences.save("", "verifit_rs_token");
                    sharedPreferences.save("offline","mode");
                    MainActivity.dataStorage.clearDataStructures(context);

                    snackBarWithMessage.showSnackbar(response.message().toString());
                }
            }
        });

    }

    public void deleteAccount()
    {

    }

    public void updateEmail()
    {

    }
    
    public void updatePassword()
    {

    }
}

