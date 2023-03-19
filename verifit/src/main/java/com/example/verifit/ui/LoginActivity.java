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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set the mode to login to avoid bugs
        SharedPreferences sharedPreferences = new SharedPreferences(getApplicationContext());
        sharedPreferences.save("login", "user_state");
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

        if(!checkEmail(username))
        {
            runOnUiThread(() -> {
                SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(LoginActivity.this);
                snackBarWithMessage.showSnackbar("Not a valid email");
            });
            return;
        }

        UsersApi users = new UsersApi(this,getString(R.string.API_ENDPOINT), username, password);

        users.login(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                // You are logged out
                enableOfflineMode();

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
                    enableOnlineMode(responseBody, username, password);

                    runOnUiThread(() -> {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("message", "verifit_rs_login");
                        startActivity(intent);
                    });
                }
                else
                {
                    enableOfflineMode();

                    runOnUiThread(() -> {
                        SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(LoginActivity.this);
                        snackBarWithMessage.showSnackbar(response.toString());
                    });
                }
            }
        });
    }


    public void enableOfflineMode()
    {
        SharedPreferences sharedPreferences = new SharedPreferences(getApplicationContext());
        sharedPreferences.save("", "verifit_rs_username");
        sharedPreferences.save("", "verifit_rs_password");
        sharedPreferences.save("", "verifit_rs_token");
        sharedPreferences.save("offline","mode");
        MainActivity.dataStorage.clearDataStructures(getApplicationContext());
    }

    public void enableOnlineMode(String responseBody, String username, String password)
    {
        Gson gson = new Gson();
        ResponseUser responseUser = gson.fromJson(responseBody, ResponseUser.class);
        SharedPreferences sharedPreferences = new SharedPreferences(getApplicationContext());
        sharedPreferences.save(responseUser.getToken(), "verifit_rs_token");
        sharedPreferences.save(username, "verifit_rs_username");
        sharedPreferences.save(password, "verifit_rs_password");
        sharedPreferences.save("online","mode");
    }

    public boolean checkEmail(String email)
    {
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean checkPassword(String password) {
        // Check for null or empty input
        if (password == null || password.trim().isEmpty()) {
            return false;
        }

        // Check if the password is at least 8 characters long
        if (password.length() < 10) {
            return false;
        }

        // Check if the password contains at least one letter
        boolean hasLetter = false;
        // Check if the password contains at least one number
        boolean hasNumber = false;

        for (char ch : password.toCharArray()) {
            if (Character.isLetter(ch)) {
                hasLetter = true;
            } else if (Character.isDigit(ch)) {
                hasNumber = true;
            }

            // If both conditions are met, return true
            if (hasLetter && hasNumber) {
                return true;
            }
        }

        // If either condition is not met, return false
        return false;
    }

    public void cancel(View view)
    {
        enableOfflineMode();

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

        if(!password.equals(password2))
        {
            runOnUiThread(() -> {
                SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(LoginActivity.this);
                snackBarWithMessage.showSnackbar("Passwords do not match");
            });
            return;
        }


        if(!checkEmail(username))
        {
            runOnUiThread(() -> {
                SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(LoginActivity.this);
                snackBarWithMessage.showSnackbar("Not a valid email");
            });
            return;
        }


        if(!checkPassword(password))
        {
            SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(LoginActivity.this);
            snackBarWithMessage.showSnackbar("Password must contain letters and numbers and be of length >= 10");
            return;
        }


        UsersApi users = new UsersApi(this,getString(R.string.API_ENDPOINT), username, password);
        users.createAccount();
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