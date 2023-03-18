package com.example.verifit.ui;


import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.verifit.KeyboardHider;
import com.example.verifit.R;
import com.example.verifit.SharedPreferences;
import com.example.verifit.SnackBarWithMessage;
import com.example.verifit.verifitrs.ResponseUser;
import com.example.verifit.verifitrs.UsersApi;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");
    }


    public void clicked_login_signup(View view)
    {
        SharedPreferences sharedPreferences = new SharedPreferences(this);

        // Default should be Login
        if(sharedPreferences.load("user_state").isEmpty() || sharedPreferences.load("user_state").equals("login"))
        {
            verifit_rs_login(view);
        }
        else
        {
            verifit_rs_signup(view);
        }
    }


    public void verifit_rs_login(View view)
    {
        KeyboardHider keyboardHider = new KeyboardHider(this);
        keyboardHider.hideKeyboard();


        EditText et_username = findViewById(R.id.et_username);
        EditText et_password = findViewById(R.id.et_password);

        String username = et_username.getText().toString();
        String password = et_password.getText().toString();

        UsersApi users = new UsersApi(this,"http://192.168.1.116:3000", username, password);

        users.login(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle error
                runOnUiThread(() -> {
                    SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(LoginActivity.this);
                    snackBarWithMessage.showSnackbar(e.toString());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();

                if (200 == response.code())
                {
                    Gson gson = new Gson();
                    ResponseUser responseUser = gson.fromJson(responseBody, ResponseUser.class);

                    SharedPreferences sharedPreferences = new SharedPreferences(getApplicationContext());
                    sharedPreferences.save(responseUser.getToken(), "verifit_rs_token");
                    sharedPreferences.save(username, "verifit_rs_username");
                    sharedPreferences.save(password, "verifit_rs_password");
                    sharedPreferences.save("online","mode");


                    runOnUiThread(() -> {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("message", "verifit_rs_login");
                        startActivity(intent);
                    });
                }
                else
                {
                    // You are logged out
                    SharedPreferences sharedPreferences = new SharedPreferences(getApplicationContext());
                    sharedPreferences.save("", "verifit_rs_username");
                    sharedPreferences.save("", "verifit_rs_password");
                    sharedPreferences.save("", "verifit_rs_token");
                    sharedPreferences.save("offline","mode");
                    MainActivity.dataStorage.clearDataStructures(getApplicationContext());

                    runOnUiThread(() -> {
                        SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(LoginActivity.this);
                        snackBarWithMessage.showSnackbar(response.toString());
                    });
                }
            }
        });
    }


    public void verifit_rs_logout(View view)
    {
        UsersApi users = new UsersApi(this,"http://192.168.1.116:3000", "", "");
        users.logout();
    }

    public void cancel(View view)
    {
        SharedPreferences sharedPreferences = new SharedPreferences(getApplicationContext());
        sharedPreferences.save("", "verifit_rs_username");
        sharedPreferences.save("", "verifit_rs_password");
        sharedPreferences.save("", "verifit_rs_token");
        sharedPreferences.save("offline","mode");
        MainActivity.dataStorage.clearDataStructures(getApplicationContext());

        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void verifit_rs_signup(View view)
    {
        KeyboardHider keyboardHider = new KeyboardHider(this);
        keyboardHider.hideKeyboard();


        EditText et_username = findViewById(R.id.et_username);
        EditText et_password = findViewById(R.id.et_password);
        EditText et_password_2 = findViewById(R.id.et_password2);

        String username = et_username.getText().toString();
        String password = et_password.getText().toString();
        String password2 = et_password_2.getText().toString();

        if(password.equals(password2))
        {
            UsersApi users = new UsersApi(this,"http://192.168.1.116:3000", username, password);
            users.createAccount();
        }
        else
        {
            runOnUiThread(() -> {
                SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(LoginActivity.this);
                snackBarWithMessage.showSnackbar("Passwords do not match");
            });
        }
    }


    public void toggle_signup(View view)
    {
        SharedPreferences sharedPreferences = new SharedPreferences(this);

        // Default should be Login
        if(sharedPreferences.load("user_state").isEmpty() || sharedPreferences.load("user_state").equals("login"))
        {
            sharedPreferences.save("signup", "user_state");
        }
        else if(sharedPreferences.load("user_state").equals("signup"))
        {
            sharedPreferences.save("login", "user_state");
        }

        apply_changes(view);
    }

    public void apply_changes(View view)
    {
        SharedPreferences sharedPreferences = new SharedPreferences(this);

        Button bt_login_signup = findViewById(R.id.bt_login_signup);
        TextView tv_9 = findViewById(R.id.textView9);
        TextView tv_11 = findViewById(R.id.textView11);
        EditText et_pass = findViewById(R.id.et_password2);

        // Default should be Sign Up
        if(sharedPreferences.load("user_state").isEmpty() || sharedPreferences.load("user_state").equals("login"))
        {
            setTitle("Login");
            bt_login_signup.setText("Login");

            tv_9.setText("Don't have an account yet?");
            tv_11.setText("Create a free account");

            et_pass.setVisibility(View.GONE);
        }
        else
        {
            setTitle("Sign Up");
            bt_login_signup.setText("Sign Up");

            tv_9.setText("Already have an account?");
            tv_11.setText("Login Instead");

            et_pass.setVisibility(View.VISIBLE);
        }

    }


}