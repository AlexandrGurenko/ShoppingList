package com.alexandr.gurenko.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "shopping.db"; // название базы данных
    private static final int SCHEMA = 2; // версия базы данных

    // имя таблицы
    public static final String TABLE_LIST_NAME = "listName";
    public static final String TABLE_LIST_ITEM = "listItem";

    // столбцы таблицы
    public static final String LIST_NAME_ID = "id";
    public static final String LIST_NAME = "name";
    public static final String ITEM_ID = "id";
    public static final String ITEM_NAME = "item";
    public static final String ITEM_LN_ID = "listNameId";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_LIST_NAME + " (" + LIST_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + LIST_NAME + " TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_LIST_ITEM + " (" + ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ITEM_NAME + " TEXT, " + ITEM_LN_ID + " INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion == 1 && newVersion == 2) {
            db.beginTransaction();
            try {
                db.delete(TABLE_LIST_NAME, null, null);
                db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_LIST_ITEM + " (" + ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ITEM_NAME + " TEXT, " + ITEM_LN_ID + " INTEGER);");
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }
}
