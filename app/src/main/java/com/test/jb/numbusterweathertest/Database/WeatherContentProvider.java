package com.test.jb.numbusterweathertest.Database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by JB on 17.09.2015.
 */
public class WeatherContentProvider extends ContentProvider {

    private static final int WEATHERS = 0;
    private static final int CITIES = 1;
    private static final int EXTENDED_WEATHERS = 2;

    private static final SQLiteQueryBuilder sWeatherWithCitiesQueryBuilder;
    static {
        sWeatherWithCitiesQueryBuilder = new SQLiteQueryBuilder();
        sWeatherWithCitiesQueryBuilder.setTables(
                Contract.Weather.TABLE + " JOIN "
                + Contract.City.TABLE
                + " ON " + Contract.Weather.TABLE + "." + Contract.Weather.CITY_ID
                + " = " + Contract.City.TABLE + "." + Contract.City._ID
        );
    }

    private final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    DBHelper mHelper;
    @Override
    public boolean onCreate() {
        mHelper = new DBHelper(getContext());

        mUriMatcher.addURI(Contract.AUTHORITY, "weathers", WEATHERS);
        mUriMatcher.addURI(Contract.AUTHORITY, "cities", CITIES);
        mUriMatcher.addURI(Contract.AUTHORITY, "extended_weathers", EXTENDED_WEATHERS);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (mUriMatcher.match(uri)) {
            case WEATHERS:
                Cursor weatherCursor = sWeatherWithCitiesQueryBuilder.query(
                        mHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        Contract.Weather.CITY_ID,
                        null,
                        sortOrder
                );
                weatherCursor.setNotificationUri(getContext().getContentResolver(), Contract.Weather.CONTENT_URI);
                return weatherCursor;
            case EXTENDED_WEATHERS:
                Cursor extendedWeatherCursor = sWeatherWithCitiesQueryBuilder.query(
                        mHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                extendedWeatherCursor.setNotificationUri(
                        getContext().getContentResolver(),
                        Contract.Weather.EXTENDED_CONTENT_URI
                        );
                return extendedWeatherCursor;
            case CITIES:
                Cursor cityCursor = mHelper.getReadableDatabase().query(
                        Contract.City.TABLE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                cityCursor.setNotificationUri(getContext().getContentResolver(), Contract.City.CONTENT_URI);
                return cityCursor;
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case CITIES:
                return Contract.City.CONTENT_TYPE;
            default:
                return Contract.Weather.CONTENT_TYPE;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id;
        switch (mUriMatcher.match(uri)) {
            case CITIES:
                id = mHelper.getWritableDatabase().insertWithOnConflict(
                        Contract.City.TABLE,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_REPLACE
                );
                if (id > 0) {
                    getContext().getContentResolver().notifyChange(uri,null);
                    return Uri.withAppendedPath(uri, String.valueOf(id));
                }
                return Uri.withAppendedPath(uri, String.valueOf(id));
            case WEATHERS:
                id = mHelper.getWritableDatabase().insertWithOnConflict(
                        Contract.Weather.TABLE,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_REPLACE
                );
                if (id > 0 ) {
                    getContext().getContentResolver().notifyChange(uri,null);
                    getContext().getContentResolver().notifyChange(Contract.Weather.EXTENDED_CONTENT_URI, null);
                    return Uri.withAppendedPath(uri, String.valueOf(id));
                }
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int affectedRows;
        switch (mUriMatcher.match(uri)) {
            case WEATHERS:
                affectedRows = mHelper.getWritableDatabase().delete(Contract.Weather.TABLE, selection, selectionArgs);
                return affectedRows;
            case CITIES:
                affectedRows = mHelper.getWritableDatabase().delete(Contract.City.TABLE, selection, selectionArgs);
                return affectedRows;
            default:
                return 0;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
