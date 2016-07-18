package com.examples.pj.einkaufsapp.dbentities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

// DER HELFER
// Definieren der Eigenschaften der Datenbank
// -> ShoppingMemoDbHelper – Sie ist eine Hilfsklasse mit deren Hilfe wir die SQLite-Datenbank erstellen lassen. Sie enthält weiterhin wichtige Konstanten, die wir für die Arbeit mit der Datenbank benötigen, wie den Tabellennamen, die Datenbankversion oder die Namen der Spalten.
public class ShoppingMemoDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = ShoppingMemoDbHelper.class.getSimpleName();

    public static final String DB_NAME = "shopping_list.db";
    public static final int DB_VERSION = 6;

    public static final String TABLE_SHOPPING_LIST = "shopping_list";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PRODUCT = "product";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_BOUGHT = "bought";
    public static final String COLUMN_CHECKED = "checked";
    public static final String COLUMN_FAVOURITE = "favourite";

    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_SHOPPING_LIST +
                    "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PRODUCT + " TEXT NOT NULL, " +
                    COLUMN_CATEGORY + " TEXT NOT NULL, " +
                    COLUMN_BOUGHT + " INTEGER NOT NULL DEFAULT 0, " +
                    COLUMN_CHECKED + " BOOLEAN NOT NULL DEFAULT 0, " +
                    COLUMN_FAVOURITE + " BOOLEAN NOT NULL DEFAULT 0);";

    public static final String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_SHOPPING_LIST;

    public ShoppingMemoDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(LOG_TAG, "DbHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(LOG_TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE + " angelegt.");
            db.execSQL(SQL_CREATE); //Erzeugung der neuen DB WENN noch keine vorhanden ist
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }
    }

    // Die onUpgrade-Methode wird aufgerufen, sobald die neue Versionsnummer höher
    // als die alte Versionsnummer ist und somit ein Upgrade notwendig wird
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, "Die Tabelle mit Versionsnummer " + oldVersion + " wird entfernt.");
        db.execSQL(SQL_DROP);
        onCreate(db);
    }


}
