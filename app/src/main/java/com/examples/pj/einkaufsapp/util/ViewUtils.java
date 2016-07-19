package com.examples.pj.einkaufsapp.util;

import android.app.Activity;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

public class ViewUtils {
    private static final String LOG_TAG = "ViewUtils";

    public static Snackbar makeSnackbar(View parent, String message) {
        return makeSnackbar(parent, message, Snackbar.LENGTH_SHORT);
    }

    public static Snackbar makeSnackbar(View parent, int stringResId) {
        return makeSnackbar(parent, stringResId, Snackbar.LENGTH_SHORT);
    }

    public static Snackbar makeSnackbar(View parent, String message, int duration) {
        Snackbar snackbar = Snackbar.make(parent, message, duration);
        TextView tv = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        return snackbar;
    }

    public static Snackbar makeSnackbar(View parent, int stringResId, int duration) {
        Snackbar snackbar = Snackbar.make(parent, stringResId, duration);
        TextView tv = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        return snackbar;
    }

    //Hide Softkeyboard
    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager;
        inputMethodManager = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
}
