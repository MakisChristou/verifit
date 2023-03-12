package com.example.verifit.verifitrs;

import android.content.Context;

import com.example.verifit.SnackBarWithMessage;

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

public class Users {
    Context context;
    String url;
    String username;
    String password;

    public Users(Context context, String url, String username, String password)
    {
        this.context = context;
        this.url = url;
        this.username = username;
        this.password = password;
    }


    public void login()
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
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();

                SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(context);

                if (!responseBody.isEmpty())
                {
                    snackBarWithMessage.showSnackbar("Logged In");
                }
                else
                {
                    snackBarWithMessage.showSnackbar(response.toString());
                }
            }
        });

    }

    public void logout()
    {

    }

    public void createAccount()
    {

    }
}
