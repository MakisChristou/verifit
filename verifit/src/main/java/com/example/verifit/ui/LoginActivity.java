package com.example.verifit.ui;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.verifit.KeyboardHider;
import com.example.verifit.R;
import com.example.verifit.SharedPreferences;
import com.example.verifit.verifitrs.UsersApi;

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

        // Default should be Sign Up
        if(sharedPreferences.load("user_state").isEmpty() || sharedPreferences.load("user_state").equals("signup"))
        {
            verifit_rs_signup(view);
        }
        else
        {
            verifit_rs_login(view);
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
        users.login();
    }


    public void verifit_rs_logout(View view)
    {
        UsersApi users = new UsersApi(this,"http://192.168.1.116:3000", "", "");
        users.logout();
    }

    public void cancel(View view)
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void verifit_rs_signup(View view)
    {
        KeyboardHider keyboardHider = new KeyboardHider(this);
        keyboardHider.hideKeyboard();


        EditText et_username = findViewById(R.id.et_username);
        EditText et_password = findViewById(R.id.et_password);

        String username = et_username.getText().toString();
        String password = et_password.getText().toString();


        UsersApi users = new UsersApi(this,"http://192.168.1.116:3000", username, password);
        users.createAccount();
    }


    public void toggle_signup(View view)
    {
        SharedPreferences sharedPreferences = new SharedPreferences(this);

        // Default should be Sign Up
        if(sharedPreferences.load("user_state").isEmpty() || sharedPreferences.load("user_state").equals("signup"))
        {
            sharedPreferences.save("login", "user_state");
        }
        else
        {
            sharedPreferences.save("signup", "user_state");
        }

        apply_changes(view);
    }

    public void apply_changes(View view)
    {
        SharedPreferences sharedPreferences = new SharedPreferences(this);

        Button bt_login_signup = findViewById(R.id.bt_login_signup);
        TextView tv_9 = findViewById(R.id.textView9);
        TextView tv_11 = findViewById(R.id.textView11);

        // Default should be Sign Up
        if(sharedPreferences.load("user_state").isEmpty() || sharedPreferences.load("user_state").equals("signup"))
        {
            setTitle("Sign Up");
            bt_login_signup.setText("Sign Up");

            tv_9.setText("Already have an account?");
            tv_11.setText("Login Instead");

        }
        else
        {
            setTitle("Login");
            bt_login_signup.setText("Login");

            tv_9.setText("Don't have an account yet?");
            tv_11.setText("Create a free account");
        }

    }


}