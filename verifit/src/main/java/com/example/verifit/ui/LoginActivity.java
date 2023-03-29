package com.example.verifit.ui;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.verifit.CustomDialog;
import com.example.verifit.KeyboardHider;
import com.example.verifit.LoadingDialog;
import com.example.verifit.R;
import com.example.verifit.SharedPreferences;
import com.example.verifit.SnackBarWithMessage;
import com.example.verifit.verifitrs.UsersApi;
import com.google.android.material.snackbar.Snackbar;

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

        showSavedCredentials();
    }

    public void forgot_password(View view)
    {
        EditText et_username = findViewById(R.id.et_username);
        String username = et_username.getText().toString();

        if (username != null && !username.isEmpty()) {
            SharedPreferences sharedPreferences = new SharedPreferences(getApplicationContext());
            sharedPreferences.save(username, "verifit_rs_username");
        }

        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    public void showSavedCredentials()
    {
        EditText et_username = findViewById(R.id.et_username);

        SharedPreferences sharedPreferences = new SharedPreferences(getApplicationContext());
        String saved_username = sharedPreferences.load("verifit_rs_username");

        if(!saved_username.equals(""))
        {
            et_username.setText(saved_username);
        }
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
        if(view != null)
        {
            KeyboardHider keyboardHider = new KeyboardHider(this);
            keyboardHider.hideKeyboard();
        }

        EditText et_username = findViewById(R.id.et_username);
        EditText et_password = findViewById(R.id.et_reset_code);

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

        if(!checkPassword(password))
        {
            runOnUiThread(() -> {
                SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(LoginActivity.this);
                snackBarWithMessage.showSnackbar("Must contain letters and numbers and be of length >= 10");
            });
            return;
        }

        CustomDialog.showDialog(
                LoginActivity.this,
                R.layout.import_red_warning_dialog,
                "This will erase all local data",
                "Continue",
                "Cancel",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this);
                        loadingDialog.loadingAlertDialog();

                        UsersApi users = new UsersApi(LoginActivity.this,getString(R.string.API_ENDPOINT), username, password);
                        users.login(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                                // You are logged out
                                SharedPreferences sharedPreferences = new SharedPreferences(getApplicationContext());
                                sharedPreferences.enableOfflineMode();

                                // Disable caching because new data should be loaded
                                sharedPreferences.disableCaching();

                                // Handle error
                                runOnUiThread(() -> {
                                    loadingDialog.dismissDialog();
                                    SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(LoginActivity.this);
                                    snackBarWithMessage.showSnackbar("Can't connect to server");
                                });
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String responseBody = response.body().string();
                                SharedPreferences sharedPreferences = new SharedPreferences(getApplicationContext());

                                runOnUiThread(() -> {
                                    loadingDialog.dismissDialog();
                                });

                                if (200 == response.code())
                                {
                                    sharedPreferences.enableOnlineMode(responseBody, username, password);

                                    runOnUiThread(() -> {
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.putExtra("message", "verifit_rs_login");
                                        startActivity(intent);
                                    });
                                }
                                else
                                {
                                    sharedPreferences.enableOfflineMode();

                                    if(response.message().equals("Unauthorized"))
                                    {
                                        runOnUiThread(() -> {
                                            SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(LoginActivity.this);
                                            snackBarWithMessage.showSnackbar("Invalid password");
                                        });
                                    }
                                    else if(response.message().equals("Not Acceptable"))
                                    {
                                        runOnUiThread(() -> {
                                            // Create a standard Snackbar with a dismiss button
                                            Snackbar snackbar = Snackbar.make(((Activity) LoginActivity.this).findViewById(android.R.id.content), "Verify your email to login", Snackbar.LENGTH_SHORT);
                                            snackbar.setAction("Resend link", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    final LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this);
                                                    loadingDialog.loadingAlertDialog();

                                                    UsersApi usersApi = new UsersApi(LoginActivity.this,getString(R.string.API_ENDPOINT), username, "");
                                                    usersApi.requestEmailVerification(new Callback() {
                                                        @Override
                                                        public void onFailure(@NonNull Call call, @NonNull IOException e) {

                                                            runOnUiThread(() -> {
                                                                loadingDialog.dismissDialog();
                                                                SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(LoginActivity.this);
                                                                snackBarWithMessage.showSnackbar("Can't connect to server");
                                                            });

                                                        }

                                                        @Override
                                                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                                            runOnUiThread(() -> {
                                                                loadingDialog.dismissDialog();
                                                            });

                                                            if(200 == response.code()){
                                                                runOnUiThread(() -> {
                                                                    SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(LoginActivity.this);
                                                                    snackBarWithMessage.showSnackbar("Email Sent");
                                                                });

                                                            }
                                                            else
                                                            {
                                                                runOnUiThread(() -> {
                                                                    SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(LoginActivity.this);
                                                                    snackBarWithMessage.showSnackbar(response.message().toString());
                                                                });
                                                            }
                                                        }
                                                    });

                                                    snackbar.dismiss();
                                                }
                                            });

                                            snackbar.show();

                                        });
                                    }
                                    else if (response.message().equals("Not Found"))
                                    {
                                        runOnUiThread(() -> {
                                            SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(LoginActivity.this);
                                            snackBarWithMessage.showSnackbar("Account not found");
                                        });
                                    }
                                    else
                                    {
                                        runOnUiThread(() -> {
                                            SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(LoginActivity.this);
                                            snackBarWithMessage.showSnackbar(response.message().toString());
                                        });
                                    }
                                }
                            }
                        });

                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }
        );
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
        if (password.length() < 10 || password.length() > 64) {
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
        SharedPreferences sharedPreferences = new SharedPreferences(getApplicationContext());
        sharedPreferences.enableOfflineMode();

        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void verifit_rs_signup(View view)
    {
        KeyboardHider keyboardHider = new KeyboardHider(this);
        keyboardHider.hideKeyboard();


        EditText et_username = findViewById(R.id.et_username);
        EditText et_password = findViewById(R.id.et_reset_code);
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
            snackBarWithMessage.showSnackbar("Must contain letters and numbers and be of length >= 10");
            return;
        }


        final LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this);
        loadingDialog.loadingAlertDialog();

        UsersApi users = new UsersApi(this,getString(R.string.API_ENDPOINT), username, password);
        users.createAccount(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                loadingDialog.dismissDialog();

                // You are logged out
                SharedPreferences sharedPreferences = new SharedPreferences(getApplicationContext());
                sharedPreferences.enableOfflineMode();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String responseBody = response.body().string();

                loadingDialog.dismissDialog();

                if (200 == response.code())
                {

                    SharedPreferences sharedPreferences = new SharedPreferences(getApplicationContext());
                    sharedPreferences.saveUsername(username);

                        runOnUiThread(() -> {
                            loadingDialog.dismissDialog();
                            SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(LoginActivity.this);
                            snackBarWithMessage.showSnackbar("Account Created, Verify your email to login");
                        });


                }
                else
                {
                    // You are logged out
                    SharedPreferences sharedPreferences = new SharedPreferences(getApplicationContext());
                    sharedPreferences.enableOfflineMode();

                    runOnUiThread(() -> {
                        SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(LoginActivity.this);
                        snackBarWithMessage.showSnackbar(response.message().toString());
                    });

                }
            }
        });
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
        TextView tv_forgot_password = findViewById(R.id.tv_forgot_password);

        // Default should be Sign Up
        if(sharedPreferences.load("user_state").isEmpty() || sharedPreferences.load("user_state").equals("login"))
        {
            setTitle("Login");
            bt_login_signup.setText("Login");

            tv_9.setText("Don't have an account yet?");
            tv_11.setText("Create a free account");

            et_pass.setVisibility(View.GONE);
            tv_forgot_password.setVisibility(View.VISIBLE);
        }
        else
        {
            setTitle("Sign Up");
            bt_login_signup.setText("Sign Up");

            tv_9.setText("Already have an account?");
            tv_11.setText("Login Instead");

            et_pass.setVisibility(View.VISIBLE);
            tv_forgot_password.setVisibility(View.GONE);
        }

    }


}