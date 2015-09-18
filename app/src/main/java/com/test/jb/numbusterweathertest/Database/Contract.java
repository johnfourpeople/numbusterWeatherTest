package com.test.jb.numbusterweathertest.Database;

import android.net.Uri;

/**
 * Created by JB on 17.09.2015.
 */
public final class Contract {
    public static final String AUTHORITY = "com.test.jb.numbusterweathertest.provider";
            public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
    public static final class Weather {

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/weather";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI,"weathers");
        public static final Uri EXTENDED_CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI,"extended_weathers");

        public static final String TABLE = "weather";

        public static final String TEMPERATURE = "temperature";
        public static final String HUMIDITY = "humidity";
        public static final String DESCRIPTION = "description";
        public static final String DATE = "weather_date";
        public static final String CITY_ID = "city_id";
    }
    public static class City {

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/city";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI,"cities");

        public static final String TABLE = "city";

        public static final String NAME = "name";
        public static final String _ID = "_id";


    }
}
