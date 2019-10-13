package com.ketchup.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


import javax.inject.Inject;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class KeypadUtils {

    private Context context;

    @Inject
    public KeypadUtils(Context context) {
        this.context = context;
    }

    public void hideKeypad(View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showKeypad() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
}
