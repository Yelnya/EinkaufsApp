package com.examples.pj.einkaufsapp.util;

import android.content.SharedPreferences;

import com.examples.pj.einkaufsapp.dbentities.ShoppingMemo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class SharedPreferencesManager {

    private static final String CURRENT_SHOPPING_LIST = "CurrentShoppingList";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPreferencesManager(SharedPreferences sharedPreferences, SharedPreferences.Editor editor) {
        this.sharedPreferences = sharedPreferences;
        this.editor = editor;
    }

    //***************************************************************
    // CURRENT SHOPPING LIST
    //***************************************************************

    public List<ShoppingMemo> loadCurrentShoppingListFromLocalStore() {
        String currentShoppingListString = sharedPreferences.getString(CURRENT_SHOPPING_LIST, null);
        Gson gson = new Gson();
        Type type = new TypeToken<List<ShoppingMemo>>() {
        }.getType();
        return gson.fromJson(currentShoppingListString, type);
    }

    public void saveCurrentShoppingListToLocalStore(List<ShoppingMemo> shoppingList) {
        Gson gson = new Gson();
        String json = gson.toJson(shoppingList, LinkedList.class);
        editor.putString(CURRENT_SHOPPING_LIST, json);
        editor.commit();
    }

}
