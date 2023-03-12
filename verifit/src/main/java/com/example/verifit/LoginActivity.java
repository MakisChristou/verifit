package com.example.verifit;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.verifit.verifitrs.Users;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");
    }


    public void verifit_rs_login(View view)
    {
        KeyboardHider keyboardHider = new KeyboardHider(this);
        keyboardHider.hideKeyboard();


        EditText et_username = findViewById(R.id.et_username);
        EditText et_password = findViewById(R.id.et_password);

        String username = et_username.getText().toString();
        String password = et_password.getText().toString();


        Users users = new Users(this,"http://192.168.1.116:3000/users/login", username, password);
        users.login();

    }

    public void cancel(View view)
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void verifit_rs_signup(View view)
    {

    }


}