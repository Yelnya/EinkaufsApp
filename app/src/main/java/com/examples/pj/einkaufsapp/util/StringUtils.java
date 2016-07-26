package com.examples.pj.einkaufsapp.util;

import com.examples.pj.einkaufsapp.dbentities.ProductItem;

import java.util.Comparator;
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
        Pattern pattern = Pattern.compile("[^A-Za-zäöü ]");      //check string if input contains special characters
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

    //---------------------------------------------------------------
    // COMPARATORS FOR LIST SORTING
    //---------------------------------------------------------------

    /**
     * Helper Class for Sorting List alphabetically
     */
    public static class CurrentListAlphabeticalComparator implements Comparator<ProductItem> {
        @Override
        public int compare(ProductItem left, ProductItem right) {
            return left.getProduct().compareTo(right.getProduct());
        }
    }

    /**
     * Helper Class for Sorting List referring to Categories
     */
    public static class CurrentListCategoryComparator implements Comparator<ProductItem> {
        @Override
        public int compare(ProductItem left, ProductItem right) {
            return left.getCategory().compareTo(right.getCategory());
        }
    }
}
