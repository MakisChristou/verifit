package com.example.verifit;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class LoadingDialog {


    private Activity activity;
    private AlertDialog dialog;


    public LoadingDialog(Activity activity)
    {
        this.activity = activity;
    }

    public void loadingAlertDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.network_loading_dialog, null));
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
    }

    public void dismissDialog()
    {
        dialog.dismiss();
    }

}
