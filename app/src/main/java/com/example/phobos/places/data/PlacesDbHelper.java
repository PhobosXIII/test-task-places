package com.example.phobos.places.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.phobos.places.data.PlacesContract.PlaceEntry;

public class PlacesDbHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    static final String DB_NAME = "places.db";

    public PlacesDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_PLACE_TABLE = "CREATE TABLE " + PlaceEntry.TABLE_NAME + " (" +
                PlaceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PlaceEntry.COLUMN_LATITUDE + " REAL NOT NULL, " +
                PlaceEntry.COLUMN_LONGITUDE + " REAL NOT NULL, " +
                PlaceEntry.COLUMN_TEXT + " TEXT NOT NULL, " +
                PlaceEntry.COLUMN_IMAGE + " TEXT, " +
                PlaceEntry.COLUMN_LAST_VISITED + " TEXT" +
                ");";

        db.execSQL(SQL_CREATE_PLACE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PlaceEntry.TABLE_NAME);
        onCreate(db);
    }
}
