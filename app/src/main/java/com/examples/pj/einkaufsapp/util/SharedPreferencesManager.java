package com.examples.pj.einkaufsapp.util;

import android.app.Activity;
import android.content.SharedPreferences;

import com.examples.pj.einkaufsapp.dbentities.ProductItem;
import com.examples.pj.einkaufsapp.dbentities.ShoppingTrip;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

/**
 * Class Shared Preferences Manager
 */
public class SharedPreferencesManager {

    private static final String CURRENT_SHOPPING_LIST = "CurrentShoppingList";
    private static final String HISTORIC_SHOPPING_TRIPS_LIST = "HistoricShoppingTripsList";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    /**
     * Initialization of Shared Preferences Manager
     *
     * @param activity MainActivity
     * @return
     */
    public static SharedPreferencesManager initSharedPreferences(Activity activity) {
        SharedPreferences sprFile = activity.getSharedPreferences("SLSPfile", 0);
        SharedPreferences.Editor editor = sprFile.edit();
        return new SharedPreferencesManager(sprFile, editor);
    }

    /**
     * Constructor
     *
     * @param sharedPreferences
     * @param editor
     */
    public SharedPreferencesManager(SharedPreferences sharedPreferences, SharedPreferences.Editor editor) {
        this.sharedPreferences = sharedPreferences;
        this.editor = editor;
    }

    //---------------------------------------------------------------
    // CURRENT SHOPPING LIST
    //---------------------------------------------------------------

    /**
     * saves list to SP in json format
     *
     * @param shoppingList
     */
    public void saveCurrentShoppingListToLocalStore(List<ProductItem> shoppingList) {
        Gson gson = new Gson();
        String json = gson.toJson(shoppingList, LinkedList.class);
        editor.putString(CURRENT_SHOPPING_LIST, json);
        editor.commit();
    }

    /**
     * loads all SP entries of last saved Shopping list from SP (stored in json format)
     *
     * @return list
     */
    public List<ProductItem> loadCurrentShoppingListFromLocalStore() {
        String currentShoppingListString = sharedPreferences.getString(CURRENT_SHOPPING_LIST, null);
        Gson gson = new Gson();
        Type type = new TypeToken<List<ProductItem>>() {
        }.getType();
        return gson.fromJson(currentShoppingListString, type);
    }

    //---------------------------------------------------------------
    // HISTORIC SHOPPING TRIPS LIST
    //---------------------------------------------------------------

    /** saves historic shopping list to SP in json format
     *
     * @param shoppingTripList
     */
    public void saveHistoricShoppingTripsListToLocalStore(List<ShoppingTrip> shoppingTripList) {
        Gson gson = new Gson();
        String json = gson.toJson(shoppingTripList, LinkedList.class);
        editor.putString(HISTORIC_SHOPPING_TRIPS_LIST, json);
        editor.commit();
    }

    /** loads all SP entries of historic Shopping list from SP (stored in json format)
     *
      * @return list
     */
    public List<ShoppingTrip> loadHistoricShoppingTripsListFromLocalStore() {
        String historicShoppingListString = sharedPreferences.getString(HISTORIC_SHOPPING_TRIPS_LIST, null);
        Gson gson = new Gson();
        Type type = new TypeToken<List<ShoppingTrip>>() {
        }.getType();
        return gson.fromJson(historicShoppingListString, type);
    }
}
