package com.examples.pj.einkaufsapp.dbentities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * communication class for database
 */
public class ProductItemDataSource {

    private static final String LOG_TAG = ProductItemDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private ProductItemDbHelper dbHelper;  //connection to SQLite

    private String[] columns = {
            ProductItemDbHelper.COLUMN_ID,
            ProductItemDbHelper.COLUMN_PRODUCT,
            ProductItemDbHelper.COLUMN_CATEGORY,
            ProductItemDbHelper.COLUMN_BOUGHT,
            ProductItemDbHelper.COLUMN_CHECKED,
            ProductItemDbHelper.COLUMN_FAVOURITE
    };

    /**
     * Constructor
     *
     * @param context
     */
    public ProductItemDataSource(Context context) {
        Log.d(LOG_TAG, "Die DataSource erzeugt jetzt den dbHelper.");
        dbHelper = new ProductItemDbHelper(context);
    }

    /**
     * open db writable
     */
    public void open() {
        Log.d(LOG_TAG, "Eine Referenz auf die Datenbank wird jetzt angefragt.");
        database = dbHelper.getWritableDatabase();
        Log.d(LOG_TAG, "Datenbank-Referenz erhalten. Pfad zur Datenbank: " + database.getPath());
    }

    /**
     * close db
     */
    public void close() {
        dbHelper.close();
        Log.d(LOG_TAG, "Datenbank mit Hilfe des DbHelpers geschlossen.");
    }

    /**
     * entry of new productItem in db when only name and category is known
     *
     * @param product  productItem name
     * @param category productItem category
     * @return
     */
    public ProductItem createProductItem(String product, String category) {
        ContentValues values = new ContentValues();
        values.put(ProductItemDbHelper.COLUMN_PRODUCT, product);
        values.put(ProductItemDbHelper.COLUMN_CATEGORY, category);

        //entry of values in table, return of id
        long insertId = database.insert(ProductItemDbHelper.TABLE_SHOPPING_LIST, null, values);

        //read the newly added productItem from db for quality check
        Cursor cursor = database.query(ProductItemDbHelper.TABLE_SHOPPING_LIST,
                columns, ProductItemDbHelper.COLUMN_ID + "=" + insertId,   //search for id, return is a cursur object
                null, null, null, null);

        cursor.moveToFirst();   //set cursor to first dataset
        ProductItem productItem = cursorToProductItem(cursor); //transfer of cursor object to productItem entity
        cursor.close();

        return productItem;
    }

    //get productItem of current cursor position
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

        boolean isChecked = intValueChecked != 0;
        boolean isFavourite = intValueFavourite != 0;

        return new ProductItem(id, product, category, bought, isChecked, isFavourite, false);
    }


    //read all available data from db, return as list
    public List<ProductItem> getAllProductItems() {
        List<ProductItem> productItemList = new ArrayList<>();

        Cursor cursor = database.query(ProductItemDbHelper.TABLE_SHOPPING_LIST,
                columns, null, null, null, null, null); //search string is null to get all results of table

        cursor.moveToFirst();
        ProductItem productItem;

        while (!cursor.isAfterLast()) {  //fill list as long as entries are available in table
            productItem = cursorToProductItem(cursor);
            productItemList.add(productItem);
            Log.d(LOG_TAG, "Inhalt: " + productItem.toString());
            cursor.moveToNext();
        }

        cursor.close(); //close = important!

        return productItemList;
    }

    /**
     * delete productItem from db
     *
     * @param productItem Object
     */
    public void deleteProductItem(ProductItem productItem) {
        long id = productItem.getId();

        database.delete(ProductItemDbHelper.TABLE_SHOPPING_LIST,
                ProductItemDbHelper.COLUMN_ID + "=" + id,
                null);

        Log.d(LOG_TAG, "Eintrag gelöscht! Inhalt: " + productItem.toString());
    }

    /**
     * get current value "timesBought" from table referring to ID, return as attribute of productItem
     *
     * @param id of productItem
     * @return timesBought value as int
     */
    public int getTimesBought(long id) {
        Cursor cursor = database.query(ProductItemDbHelper.TABLE_SHOPPING_LIST,
                columns, ProductItemDbHelper.COLUMN_ID + "=" + id,   //suche eben erst zurückgegebener ID, Rückgabe ist ein Cursor Objekt
                null, null, null, null);

        cursor.moveToFirst();
        ProductItem productItem = cursorToProductItem(cursor); //transfer cursorPosition to productItem entity
        cursor.close();

        return productItem.getBought();
    }

    /**
     * update existing db entry referring to id, return edited object as ProductItem object
     *
     * @param id
     * @param newProduct
     * @param newCategory
     * @param bought
     * @param newChecked
     * @param newFavourite
     * @return productItem object
     */
    public ProductItem updateProductItem(long id, String newProduct, String newCategory, int bought, boolean newChecked, boolean newFavourite) {
        int intValueChecked = newChecked ? 1 : 0;
        int intValueFavourite = newFavourite ? 1 : 0;
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

        //return of edited value
        Cursor cursor = database.query(ProductItemDbHelper.TABLE_SHOPPING_LIST,
                columns, ProductItemDbHelper.COLUMN_ID + "=" + id,
                null, null, null, null);

        cursor.moveToFirst();
        ProductItem productItem = cursorToProductItem(cursor);
        cursor.close();

        return productItem;
    }

    //get the highest id from database and return
    public long getHighestID() {
        String query = "SELECT MAX(_id) AS max_id FROM shopping_list";
        Cursor cursor = database.rawQuery(query, null);

        int id = 0;
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        return id;
    }
}
