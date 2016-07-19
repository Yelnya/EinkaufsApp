package com.examples.pj.einkaufsapp.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    private static final String LOG_TAG = "StringUtils";

    public static boolean stringContainsSpecialCharacters(String string) {
        Pattern pattern = Pattern.compile("[^A-Za-z ]");      //check string if input contains special characters
        Matcher matcher = pattern.matcher(string);
        boolean matchFound = false;
        for (int i = 0; i < string.length(); i++) {
            if (matcher.find(i)) {
                matchFound = true;
                break;
            }
        }
        return matchFound;
    }
}
