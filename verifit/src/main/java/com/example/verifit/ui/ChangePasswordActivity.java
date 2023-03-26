package com.example.verifit.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.verifit.KeyboardHider;
import com.example.verifit.LoadingDialog;
import com.example.verifit.R;
import com.example.verifit.SnackBarWithMessage;
import com.example.verifit.verifitrs.UsersApi;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    String reset_code;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        reset_code = getIntent().getStringExtra("reset_code");
        username = getIntent().getStringExtra("username");
    }

    public void submit(View view)
    {
        EditText et_password1 = findViewById(R.id.et_password1);
        EditText et_password2 = findViewById(R.id.et_password2);

        String password1 = et_password1.getText().toString();
        String password2 = et_password2.getText().toString();

        KeyboardHider keyboardHider = new KeyboardHider(ChangePasswordActivity.this);
        keyboardHider.hideKeyboard();

        if (!password1.equals(password2)) {
            SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(ChangePasswordActivity.this);
            snackBarWithMessage.showSnackbar("Passwords don't match");
            return;
        }

        if(!checkPassword(password1))
        {
            SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(ChangePasswordActivity.this);
            snackBarWithMessage.showSnackbar("Password must contain letters and numbers and be of length >= 10");
            return;
        }



        final LoadingDialog loadingDialog = new LoadingDialog(ChangePasswordActivity.this);
        loadingDialog.loadingAlertDialog();

        UsersApi usersApi = new UsersApi(this,getString(R.string.API_ENDPOINT), username, password1);
        usersApi.changePassword(reset_code, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    loadingDialog.dismissDialog();
                    SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(ChangePasswordActivity.this);
                    snackBarWithMessage.showSnackbar("Can't connect to server");
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                loadingDialog.dismissDialog();

                if(200 == response.code()){
                    runOnUiThread(() -> {
                        SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(ChangePasswordActivity.this);
                        snackBarWithMessage.showSnackbar("Password changed");

                        Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                    });
                }
                else {
                    runOnUiThread(() -> {
                        SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(ChangePasswordActivity.this);
                        snackBarWithMessage.showSnackbar(response.message().toString());
                    });
                }

            }
        });

    }

    public void cancel(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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

}