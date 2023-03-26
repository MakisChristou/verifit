package com.example.verifit;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyboardHider {
    private Context mContext;

    public KeyboardHider(Context context) {
        mContext = context;
    }

    public void hideKeyboard() {
        // Get a reference to the input method manager
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

        // Get a reference to the currently focused view
        View focusedView = ((Activity) mContext).getCurrentFocus();

        // Hide the soft keyboard if shown
        if (focusedView != null && imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }

}

