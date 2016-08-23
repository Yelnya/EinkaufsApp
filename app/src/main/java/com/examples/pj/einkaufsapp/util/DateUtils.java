package com.examples.pj.einkaufsapp.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final String LOG_TAG = "DateUtils";
    private static final String DATE_FORMAT = "dd.MM.yyyy hh:mm";

    private DateUtils() {
    }

    public static Date getCurrentDate() {
        return new Date();
    }

    // input Date
    // output 27.07.2016 11:45
    public static String dateToString (Date date) {
        DateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT, Locale.GERMAN);
        return dateFormatter.format(date);
    }

    // input 27.07.2016 11:45 (date)
    // output 27.07.2016, 11:45 Uhr
    public static String dateToDateHourMinuteString (Date date) {
        DateFormat dateFormatterDate = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
        String dateString =  dateFormatterDate.format(date);
        DateFormat dateFormatterHourMinute = new SimpleDateFormat("hh:mm", Locale.GERMAN);
        String hourMinuteString =  dateFormatterHourMinute.format(date);
        return dateString + ", " + hourMinuteString + " Uhr";
    }

    // input 27.07.2016 11:45 (string)
    // output 27.07.2016, 11:45 Uhr
    public static String stringDateToHourMinuteString (String stringDate) {
        DateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.GERMAN);
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
        return dateString + ", " + hourMinuteString;
    }

    // input 27.07.2016 11:45 (string)
    // output 2016-07-27, 11:45 AM / PM
    public static String stringDateToHourMinuteStringENG (String stringDate) {
        DateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.GERMAN);
        Date date = null;
        try {
            date = format.parse(stringDate);
        } catch (ParseException e) {
            Log.d(LOG_TAG, e.toString());
        }
        if (date == null) {
            date = getCurrentDate();
        }
        DateFormat dateFormatterDate = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
        String dateString =  dateFormatterDate.format(date);

        //AM PM transfer
        int timeHour = Integer.parseInt(stringDate.substring(11, 13));
        String timeMinute = stringDate.substring(14, 16);
        String amPm = "";
        if(timeHour > 11) {
            if (timeHour > 12) {
                timeHour =- 12;
            }
            amPm = "PM";
        } else if (timeHour < 12){
            amPm = "AM";
        }
        return dateString + ", " + timeHour + ":" + timeMinute + " " + amPm;
    }
}
