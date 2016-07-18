package com.examples.pj.einkaufsapp.util;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

//MANDATORY CLASS #6
public class ViewUtils {

    private static final String TAG = "ViewUtils";

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
}
