package com.test.jb.numbusterweathertest.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by JB on 17.09.2015.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static String dbName = "weatherDB";
    private static int version = 1;
    public DBHelper(Context context) {
        super(context, dbName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ Contract.Weather.TABLE +" ("
                + Contract.Weather.TEMPERATURE + " INTEGER, "
                + Contract.Weather.HUMIDITY + " INTEGER, "
                + Contract.Weather.DESCRIPTION + " TEXT, "
                + Contract.Weather.DATE + " INTEGER, "
                + Contract.Weather.CITY_ID + " INTEGER, "
                +" PRIMARY KEY ("
                + Contract.Weather.CITY_ID + ", "
                + Contract.Weather.DATE + ")" + ");"
         );

        db.execSQL("CREATE TABLE " + Contract.City.TABLE + " ("
                + Contract.City._ID + " INTEGER PRIMARY KEY, "
                + Contract.City.NAME + " TEXT" + ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
