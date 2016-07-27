package com.examples.pj.einkaufsapp.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private final static String LOG_TAG = "DateUtils";

    public static Date getCurrentDate() {
        return new Date();
    }

    // input Date
    // output 27.07.2016 11:45
    public static String dateToString (Date date) {
        DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy hh:mm", Locale.GERMAN);
        return dateFormatter.format(date);
    }

    // input 27.07.2016 11:45 (date)
    // output 27.07.2016, 11:45 Uhr
    public static String dateToDateHourMinuteString (Date date) {
        DateFormat dateFormatterDate = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
        String dateString =  dateFormatterDate.format(date);
        DateFormat dateFormatterHourMinute = new SimpleDateFormat("hh:mm", Locale.GERMAN);
        String hourMinuteString =  dateFormatterHourMinute.format(date);
        return (dateString + ", " + hourMinuteString + " Uhr");
    }

    // input 27.07.2016 11:45 (string)
    // output 27.07.2016, 11:45 Uhr
    public static String stringDateToHourMinuteString (String stringDate) {
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy hh:mm", Locale.GERMAN);
        Date date = null;
        try {
            date = format.parse(stringDate);
        } catch (ParseException e) {
            Log.d(LOG_TAG, e.toString());
        }
        if (date == null) {
            date = getCurrentDate();
        }
        DateFormat dateFormatterDate = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
        String dateString =  dateFormatterDate.format(date);
        DateFormat dateFormatterHourMinute = new SimpleDateFormat("hh:mm", Locale.GERMAN);
        String hourMinuteString =  dateFormatterHourMinute.format(date);
        return (dateString + ", " + hourMinuteString + " Uhr");
    }

    //TODO AM PM transfer

}
