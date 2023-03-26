package com.example.verifit.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.verifit.LoadingDialog;
import com.example.verifit.R;
import com.example.verifit.SharedPreferences;
import com.example.verifit.SnackBarWithMessage;
import com.example.verifit.verifitrs.UsersApi;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        showSavedCredentials();
    }

    public void showSavedCredentials()
    {
        EditText et_username = findViewById(R.id.et_username);

        SharedPreferences sharedPreferences = new SharedPreferences(getApplicationContext());
        String saved_username = sharedPreferences.load("verifit_rs_username");

        if(!saved_username.equals("") && !saved_username.equals(""))
        {
            et_username.setText(saved_username);

        }
    }

    public void proceed(View view)
    {

        EditText et_reset_code = findViewById(R.id.et_reset_code);

        String reset_code = et_reset_code.getText().toString();

        System.out.println("MAKIS" + reset_code);

        if (reset_code.isEmpty() || reset_code == null || reset_code.equals("")){
            SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(ForgotPasswordActivity.this);
            snackBarWithMessage.showSnackbar("Code is empty");
            return;
        }

        EditText et_username = findViewById(R.id.et_username);
        String username = et_username.getText().toString();

        Intent intent = new Intent(ForgotPasswordActivity.this, ChangePasswordActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("reset_code", reset_code);
        startActivity(intent);
    }

    public void send_code(View view)
    {
        EditText et_username = findViewById(R.id.et_username);
        String username = et_username.getText().toString();

        final LoadingDialog loadingDialog = new LoadingDialog(ForgotPasswordActivity.this);
        loadingDialog.loadingAlertDialog();

        UsersApi usersApi = new UsersApi(this,getString(R.string.API_ENDPOINT), username, "");
        usersApi.requestPasswordReset(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    loadingDialog.dismissDialog();
                    SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(ForgotPasswordActivity.this);
                    snackBarWithMessage.showSnackbar("Can't connect to server");
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                loadingDialog.dismissDialog();

                if(200 == response.code()){
                    runOnUiThread(() -> {

                        EditText editText = findViewById(R.id.et_reset_code);
                        editText.setVisibility(view.VISIBLE);

                        SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(ForgotPasswordActivity.this);
                        snackBarWithMessage.showSnackbar("Code Sent");
                    });
                }
                else
                {
                    runOnUiThread(() -> {
                        SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(ForgotPasswordActivity.this);
                        snackBarWithMessage.showSnackbar(response.message());
                    });
                }
            }
        });
    }
}