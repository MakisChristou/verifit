package com.example.verifit;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialog {
    public static void showDialog(Context context, int layoutResourceId, String message, String yesButtonText, String noButtonText, View.OnClickListener yesClickListener, View.OnClickListener noClickListener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view1 = inflater.inflate(layoutResourceId, null);
        AlertDialog alertDialog = new AlertDialog.Builder(context).setView(view1).create();

        TextView tv_date = view1.findViewById(R.id.tv_date);
        tv_date.setText(message);

        Button bt_yes3 = view1.findViewById(R.id.bt_yes3);
        bt_yes3.setText(yesButtonText);
        Button bt_no3 = view1.findViewById(R.id.bt_no3);
        bt_no3.setText(noButtonText);

        bt_yes3.setOnClickListener(view -> {
            alertDialog.dismiss();
            if (yesClickListener != null) {
                yesClickListener.onClick(view);
            }
        });

        bt_no3.setOnClickListener(view -> {
            alertDialog.dismiss();
            if (noClickListener != null) {
                noClickListener.onClick(view);
            }
        });

        alertDialog.show();
    }
}


