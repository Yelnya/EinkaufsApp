package com.examples.pj.einkaufsapp.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper Class for String methods
 */
public class StringUtils {

    private StringUtils() {
    }

    /**
     * stringContainsSpecialCharacters Check for:
     *
     * @param string given text to check
     * @return boolean if special char has been found
     */
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
