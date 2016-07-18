package com.examples.pj.einkaufsapp.dbentities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

// DIE ARBEITERKLASSE
// Herstellen der Verbindung zur SQLite Datenbank
// -> ShoppingMemoDataSource – Diese Klasse ist unser Data Access Object und für das Verwalten der Daten verantwortlich. Es unterhält die Datenbankverbindung und ist für das Hinzufügen, Auslesen und Löschen von Datensätzen zuständig. Außerdem wandelt es Datensätze in Java-Objekte für uns um, so dass der Code der Benutzeroberfläche nicht direkt mit den Datensätzen arbeiten muss.
public class ShoppingMemoDataSource {

    private static final String LOG_TAG = ShoppingMemoDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private ShoppingMemoDbHelper dbHelper;  //Verbindung zur SQLite

    private String[] columns = {
            ShoppingMemoDbHelper.COLUMN_ID,
            ShoppingMemoDbHelper.COLUMN_PRODUCT,
            ShoppingMemoDbHelper.COLUMN_CATEGORY,
            ShoppingMemoDbHelper.COLUMN_BOUGHT,
            ShoppingMemoDbHelper.COLUMN_CHECKED,
            ShoppingMemoDbHelper.COLUMN_FAVOURITE
    };

    public ShoppingMemoDataSource(Context context) {
        Log.d(LOG_TAG, "Die DataSource erzeugt jetzt den dbHelper.");
        dbHelper = new ShoppingMemoDbHelper(context);
    }

    //Datenbank beschreibbar öffnen
    public void open() {
        Log.d(LOG_TAG, "Eine Referenz auf die Datenbank wird jetzt angefragt.");
        database = dbHelper.getWritableDatabase();
        Log.d(LOG_TAG, "Datenbank-Referenz erhalten. Pfad zur Datenbank: " + database.getPath());
    }

    //Datenbank wieder schliessen
    public void close() {
        dbHelper.close();
        Log.d(LOG_TAG, "Datenbank mit Hilfe des DbHelpers geschlossen.");
    }

    //Einfügen von Datensätzen in die SQLite Tabelle
    public ShoppingMemo createShoppingMemo(String product, String category) {
        ContentValues values = new ContentValues(); //Content Values mit Anzahl und Produkt
        values.put(ShoppingMemoDbHelper.COLUMN_PRODUCT, product);
        values.put(ShoppingMemoDbHelper.COLUMN_CATEGORY, category);

        //Einfügen der values in die Tabelle, Rückgabe der ID
        long insertId = database.insert(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST, null, values);

        //Auslesen des angelegten Datensatzes zur Kontrolle
        Cursor cursor = database.query(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST,
                columns, ShoppingMemoDbHelper.COLUMN_ID + "=" + insertId,   //suche eben erst zurückgegebener ID, Rückgabe ist ein Cursor Objekt
                null, null, null, null);

        cursor.moveToFirst();   //Cursor auf den ersten Datensatz setzen
        ShoppingMemo shoppingMemo = cursorToShoppingMemo(cursor); //Umwandlung der CursorPosition in einen ShoppingMemo Datensatz
        cursor.close();

        return shoppingMemo;    //Rückgabe des Datensatzes
    }

    //Rückgabe des ShoppingMemo Objekts, auf dem der Cursor sitzt
    private ShoppingMemo cursorToShoppingMemo(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(ShoppingMemoDbHelper.COLUMN_ID);
        int idProduct = cursor.getColumnIndex(ShoppingMemoDbHelper.COLUMN_PRODUCT);
        int idCategory = cursor.getColumnIndex(ShoppingMemoDbHelper.COLUMN_CATEGORY);
        int idBought = cursor.getColumnIndex(ShoppingMemoDbHelper.COLUMN_BOUGHT);
        int idChecked = cursor.getColumnIndex(ShoppingMemoDbHelper.COLUMN_CHECKED);
        int idFavourite = cursor.getColumnIndex(ShoppingMemoDbHelper.COLUMN_FAVOURITE);

        long id = cursor.getLong(idIndex);
        String product = cursor.getString(idProduct);
        String category = cursor.getString(idCategory);
        int bought = cursor.getInt(idBought);
        int intValueChecked = cursor.getInt(idChecked);
        int intValueFavourite = cursor.getInt(idFavourite);

        boolean isChecked = (intValueChecked != 0);
        boolean isFavourite = (intValueFavourite != 0);

        ShoppingMemo shoppingMemo = new ShoppingMemo(id, product, category, bought, isChecked, isFavourite);

        return shoppingMemo;
    }


    //Auslesen aller vorhandenen Datensätze aus der Datenbank und Rückgabe einer Liste
    public List<ShoppingMemo> getAllShoppingMemos() {
        List<ShoppingMemo> shoppingMemoList = new ArrayList<>();

        Cursor cursor = database.query(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST,
                columns, null, null, null, null, null); //Such-String ist null, damit alle Ergebnisse der Tabelle zurückgegeben werden

        cursor.moveToFirst();
        ShoppingMemo shoppingMemo;

        while(!cursor.isAfterLast()) {  //Befüllen der Liste solange Einträge vorhanden sind
            shoppingMemo = cursorToShoppingMemo(cursor);
            shoppingMemoList.add(shoppingMemo);
            Log.d(LOG_TAG, "Inhalt: " + shoppingMemo.toString());
            cursor.moveToNext();
        }

        cursor.close(); //Schliessen = wichtig!

        return shoppingMemoList;
    }

    //Eintrag aus Liste löschen
    public void deleteShoppingMemo(ShoppingMemo shoppingMemo) {
        long id = shoppingMemo.getId();

        database.delete(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST,
                ShoppingMemoDbHelper.COLUMN_ID + "=" + id,
                null);

        Log.d(LOG_TAG, "Eintrag gelöscht! Inhalt: " + shoppingMemo.toString());
    }

    //Holen des aktuellen Werts Bought aus der Tabelle nach ID und Rückgabe
    public int getTimesBought (long id) {
        Cursor cursor = database.query(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST,
                columns, ShoppingMemoDbHelper.COLUMN_ID + "=" + id,   //suche eben erst zurückgegebener ID, Rückgabe ist ein Cursor Objekt
                null, null, null, null);

        cursor.moveToFirst();   //Cursor auf den ersten Datensatz setzen
        ShoppingMemo shoppingMemo = cursorToShoppingMemo(cursor); //Umwandlung der CursorPosition in einen ShoppingMemo Datensatz
        cursor.close();

        //get bought value
        int bought = shoppingMemo.getBought();
        return bought;    //Rückgabe des Datensatzes
    }

    //Einträge aus der Liste editieren
    public ShoppingMemo updateShoppingMemo(long id, String newProduct, String newCategory, int bought, boolean newChecked, boolean newFavourite) {
        int intValueChecked = (newChecked)? 1 : 0;
        int intValueFavourite = (newFavourite) ? 1 : 0;
        int newBought = getTimesBought(id) + bought;

        ContentValues values = new ContentValues();
        values.put(ShoppingMemoDbHelper.COLUMN_PRODUCT, newProduct);
        values.put(ShoppingMemoDbHelper.COLUMN_CATEGORY, newCategory);
        values.put(ShoppingMemoDbHelper.COLUMN_BOUGHT, newBought);
        values.put(ShoppingMemoDbHelper.COLUMN_CHECKED, intValueChecked);
        values.put(ShoppingMemoDbHelper.COLUMN_FAVOURITE, intValueFavourite);

        database.update(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST,
                values,
                ShoppingMemoDbHelper.COLUMN_ID + "=" + id,
                null);

        //Rückgabe des geänderten Objekts
        Cursor cursor = database.query(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST,
                columns, ShoppingMemoDbHelper.COLUMN_ID + "=" + id,
                null, null, null, null);

        cursor.moveToFirst();
        ShoppingMemo shoppingMemo = cursorToShoppingMemo(cursor);
        cursor.close();

        return shoppingMemo;
    }

    //Holen des aktuellen Werts Bought aus der Tabelle nach ID und Rückgabe
    public long getHighestID() {
        String query = "SELECT MAX(_id) AS max_id FROM shopping_list";
        Cursor cursor = database.rawQuery(query, null);

        int id = 0;
        if (cursor.moveToFirst())
        {
            do
            {
                id = cursor.getInt(0);
            } while(cursor.moveToNext());
        }
        return id;
    }

}
