package com.examples.pj.einkaufsapp.util;

import android.app.Activity;
import android.content.SharedPreferences;

import com.examples.pj.einkaufsapp.dbentities.ProductItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class SharedPreferencesManager {

    private static final String CURRENT_SHOPPING_LIST = "CurrentShoppingList";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public static SharedPreferencesManager initSharedPreferences(Activity activity){
        SharedPreferences SPRfile = activity.getSharedPreferences("SLSPfile", 0);
        SharedPreferences.Editor editor = SPRfile.edit();
        return new SharedPreferencesManager(SPRfile, editor);
    }

    public SharedPreferencesManager(SharedPreferences sharedPreferences, SharedPreferences.Editor editor) {
        this.sharedPreferences = sharedPreferences;
        this.editor = editor;
    }

    //***************************************************************
    // CURRENT SHOPPING LIST
    //***************************************************************

    public List<ProductItem> loadCurrentShoppingListFromLocalStore() {
        String currentShoppingListString = sharedPreferences.getString(CURRENT_SHOPPING_LIST, null);
        Gson gson = new Gson();
        Type type = new TypeToken<List<ProductItem>>() {
        }.getType();
        return gson.fromJson(currentShoppingListString, type);
    }

    public void saveCurrentShoppingListToLocalStore(List<ProductItem> shoppingList) {
        Gson gson = new Gson();
        String json = gson.toJson(shoppingList, LinkedList.class);
        editor.putString(CURRENT_SHOPPING_LIST, json);
        editor.commit();
    }

}
