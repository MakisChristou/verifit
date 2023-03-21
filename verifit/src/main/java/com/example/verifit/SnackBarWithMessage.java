package com.example.verifit;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class SnackBarWithMessage {
    private Context mContext;

    public SnackBarWithMessage(Context context) {
        mContext = context;
    }

    public void showSnackbar(String message) {
        // Create the Snackbar
        Snackbar snackbar = Snackbar.make(((Activity) mContext).findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);


        // Set an action for the Snackbar
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the action click
                snackbar.dismiss();
            }
        });


        // Show the Snackbar
        snackbar.show();
    }
}

