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
// -> ProductItemDataSource – Diese Klasse ist unser Data Access Object und für das Verwalten der Daten verantwortlich. Es unterhält die Datenbankverbindung und ist für das Hinzufügen, Auslesen und Löschen von Datensätzen zuständig. Außerdem wandelt es Datensätze in Java-Objekte für uns um, so dass der Code der Benutzeroberfläche nicht direkt mit den Datensätzen arbeiten muss.
public class ProductItemDataSource {

    private static final String LOG_TAG = ProductItemDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private ProductItemDbHelper dbHelper;  //Verbindung zur SQLite

    private String[] columns = {
            ProductItemDbHelper.COLUMN_ID,
            ProductItemDbHelper.COLUMN_PRODUCT,
            ProductItemDbHelper.COLUMN_CATEGORY,
            ProductItemDbHelper.COLUMN_BOUGHT,
            ProductItemDbHelper.COLUMN_CHECKED,
            ProductItemDbHelper.COLUMN_FAVOURITE
    };

    public ProductItemDataSource(Context context) {
        Log.d(LOG_TAG, "Die DataSource erzeugt jetzt den dbHelper.");
        dbHelper = new ProductItemDbHelper(context);
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
    public ProductItem createProductItem(String product, String category) {
        ContentValues values = new ContentValues(); //Content Values mit Anzahl und Produkt
        values.put(ProductItemDbHelper.COLUMN_PRODUCT, product);
        values.put(ProductItemDbHelper.COLUMN_CATEGORY, category);

        //Einfügen der values in die Tabelle, Rückgabe der ID
        long insertId = database.insert(ProductItemDbHelper.TABLE_SHOPPING_LIST, null, values);

        //Auslesen des angelegten Datensatzes zur Kontrolle
        Cursor cursor = database.query(ProductItemDbHelper.TABLE_SHOPPING_LIST,
                columns, ProductItemDbHelper.COLUMN_ID + "=" + insertId,   //suche eben erst zurückgegebener ID, Rückgabe ist ein Cursor Objekt
                null, null, null, null);

        cursor.moveToFirst();   //Cursor auf den ersten Datensatz setzen
        ProductItem productItem = cursorToProductItem(cursor); //Umwandlung der CursorPosition in einen ProductItem Datensatz
        cursor.close();

        return productItem;    //Rückgabe des Datensatzes
    }

    //Rückgabe des ProductItem Objekts, auf dem der Cursor sitzt
    private ProductItem cursorToProductItem(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(ProductItemDbHelper.COLUMN_ID);
        int idProduct = cursor.getColumnIndex(ProductItemDbHelper.COLUMN_PRODUCT);
        int idCategory = cursor.getColumnIndex(ProductItemDbHelper.COLUMN_CATEGORY);
        int idBought = cursor.getColumnIndex(ProductItemDbHelper.COLUMN_BOUGHT);
        int idChecked = cursor.getColumnIndex(ProductItemDbHelper.COLUMN_CHECKED);
        int idFavourite = cursor.getColumnIndex(ProductItemDbHelper.COLUMN_FAVOURITE);

        long id = cursor.getLong(idIndex);
        String product = cursor.getString(idProduct);
        String category = cursor.getString(idCategory);
        int bought = cursor.getInt(idBought);
        int intValueChecked = cursor.getInt(idChecked);
        int intValueFavourite = cursor.getInt(idFavourite);

        boolean isChecked = (intValueChecked != 0);
        boolean isFavourite = (intValueFavourite != 0);

        ProductItem productItem = new ProductItem(id, product, category, bought, isChecked, isFavourite);

        return productItem;
    }


    //Auslesen aller vorhandenen Datensätze aus der Datenbank und Rückgabe einer Liste
    public List<ProductItem> getAllProductItems() {
        List<ProductItem> productItemList = new ArrayList<>();

        Cursor cursor = database.query(ProductItemDbHelper.TABLE_SHOPPING_LIST,
                columns, null, null, null, null, null); //Such-String ist null, damit alle Ergebnisse der Tabelle zurückgegeben werden

        cursor.moveToFirst();
        ProductItem productItem;

        while(!cursor.isAfterLast()) {  //Befüllen der Liste solange Einträge vorhanden sind
            productItem = cursorToProductItem(cursor);
            productItemList.add(productItem);
            Log.d(LOG_TAG, "Inhalt: " + productItem.toString());
            cursor.moveToNext();
        }

        cursor.close(); //Schliessen = wichtig!

        return productItemList;
    }

    //Eintrag aus Liste löschen
    public void deleteProductItem(ProductItem productItem) {
        long id = productItem.getId();

        database.delete(ProductItemDbHelper.TABLE_SHOPPING_LIST,
                ProductItemDbHelper.COLUMN_ID + "=" + id,
                null);

        Log.d(LOG_TAG, "Eintrag gelöscht! Inhalt: " + productItem.toString());
    }

    //Holen des aktuellen Werts Bought aus der Tabelle nach ID und Rückgabe
    public int getTimesBought (long id) {
        Cursor cursor = database.query(ProductItemDbHelper.TABLE_SHOPPING_LIST,
                columns, ProductItemDbHelper.COLUMN_ID + "=" + id,   //suche eben erst zurückgegebener ID, Rückgabe ist ein Cursor Objekt
                null, null, null, null);

        cursor.moveToFirst();   //Cursor auf den ersten Datensatz setzen
        ProductItem productItem = cursorToProductItem(cursor); //Umwandlung der CursorPosition in einen ProductItem Datensatz
        cursor.close();

        //get bought value
        int bought = productItem.getBought();
        return bought;    //Rückgabe des Datensatzes
    }

    //Einträge aus der Liste editieren
    public ProductItem updateProductItem(long id, String newProduct, String newCategory, int bought, boolean newChecked, boolean newFavourite) {
        int intValueChecked = (newChecked)? 1 : 0;
        int intValueFavourite = (newFavourite) ? 1 : 0;
        int newBought = getTimesBought(id) + bought;

        ContentValues values = new ContentValues();
        values.put(ProductItemDbHelper.COLUMN_PRODUCT, newProduct);
        values.put(ProductItemDbHelper.COLUMN_CATEGORY, newCategory);
        values.put(ProductItemDbHelper.COLUMN_BOUGHT, newBought);
        values.put(ProductItemDbHelper.COLUMN_CHECKED, intValueChecked);
        values.put(ProductItemDbHelper.COLUMN_FAVOURITE, intValueFavourite);

        database.update(ProductItemDbHelper.TABLE_SHOPPING_LIST,
                values,
                ProductItemDbHelper.COLUMN_ID + "=" + id,
                null);

        //Rückgabe des geänderten Objekts
        Cursor cursor = database.query(ProductItemDbHelper.TABLE_SHOPPING_LIST,
                columns, ProductItemDbHelper.COLUMN_ID + "=" + id,
                null, null, null, null);

        cursor.moveToFirst();
        ProductItem productItem = cursorToProductItem(cursor);
        cursor.close();

        return productItem;
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
