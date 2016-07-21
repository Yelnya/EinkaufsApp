package com.examples.pj.einkaufsapp.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static Date getCurrentDate() {
        return new Date();
    }

    public static String dateToString (Date date) {
        DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy hh:mm");
        return dateFormatter.format(date);
    }

    //TODO AM PM transfer

}
