package com.examples.pj.einkaufsapp.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Util Class for View Helper methods
 */
public class ViewUtils {

    /**
     * snackbar with string
     *
     * @param message as string
     * @param parent  view
     * @return snackbar
     */
    public static Snackbar makeSnackbar(View parent, String message) {
        return makeSnackbar(parent, message, Snackbar.LENGTH_SHORT);
    }

    /**
     * snackbar with string id
     *
     * @param parent      view
     * @param stringResId as stringId
     * @return snackbar
     */
    public static Snackbar makeSnackbar(View parent, int stringResId) {
        return makeSnackbar(parent, stringResId, Snackbar.LENGTH_SHORT);
    }

    /**
     * snackbar with string and custom duration and view
     *
     * @param duration long, short
     * @param message  as string
     * @param parent   view
     * @return snackbar
     */
    public static Snackbar makeSnackbar(View parent, String message, int duration) {
        Snackbar snackbar = Snackbar.make(parent, message, duration);
        TextView tv = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        return snackbar;
    }

    /**
     * snackbar with stringId and custom duration and view
     *
     * @param duration    long, short
     * @param stringResId as stringId
     * @param parent      view
     * @return snackbar
     */
    public static Snackbar makeSnackbar(View parent, int stringResId, int duration) {
        Snackbar snackbar = Snackbar.make(parent, stringResId, duration);
        TextView tv = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        return snackbar;
    }

    /**
     * standard toast message for app
     *
     * @param text    as string
     * @param context from MainActivity
     */
    public void toast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * Hide Soft Keyboard
     *
     * @param activity from MainActivity
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager;
        inputMethodManager = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
}
