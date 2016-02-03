package com.example.phobos.places.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import static com.example.phobos.places.data.PlacesContract.PlaceEntry;

public class PlacesProvider extends ContentProvider {
    private static final UriMatcher uriMatcher = buildUriMatcher();
    private PlacesDbHelper dbHelper;

    static final int PLACES = 100;
    static final int PLACE = 101;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PlacesContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, PlacesContract.PATH_PLACES, PLACES);
        matcher.addURI(authority, PlacesContract.PATH_PLACES + "/#", PLACE);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new PlacesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case PLACES:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = "_ID ASC";
                }
                break;

            case PLACE:
                selection = PlaceEntry._ID + " = ? ";
                selectionArgs = new String[] {uri.getLastPathSegment()};
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor = dbHelper.getReadableDatabase().query(
                PlaceEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);

        switch (match) {
            case PLACES:
                return PlaceEntry.CONTENT_TYPE;
            case PLACE:
                return PlaceEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case PLACES:
                long placeId = db.insert(PlaceEntry.TABLE_NAME, null, values);
                if (placeId > 0) {
                    returnUri = PlaceEntry.buildPlaceUri(placeId);
                }
                else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int deletedRows;

        if (selection == null) {
            selection = "1";
        }

        switch (match) {
            case PLACES:
                break;

            case PLACE:
                selection = PlaceEntry._ID + " = ? ";
                selectionArgs = new String[] {uri.getLastPathSegment()};
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        deletedRows = db.delete(PlaceEntry.TABLE_NAME, selection, selectionArgs);
        if (deletedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deletedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int updatedRows;

        switch (match) {
            case PLACES:
                break;

            case PLACE:
                selection = PlaceEntry._ID + " = ? ";
                selectionArgs = new String[] {uri.getLastPathSegment()};
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        updatedRows = db.update(PlaceEntry.TABLE_NAME, values, selection, selectionArgs);
        if (updatedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updatedRows;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        switch (match) {
            case PLACES:
                db.beginTransaction();
                int count = 0;
                try {
                    for (ContentValues value : values) {
                        long id = db.insert(PlaceEntry.TABLE_NAME, null, value);
                        if (id != -1) {
                            count++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
