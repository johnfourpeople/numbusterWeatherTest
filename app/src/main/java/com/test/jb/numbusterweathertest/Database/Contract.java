package com.test.jb.numbusterweathertest.Database;

/**
 * Created by JB on 17.09.2015.
 */
public final class Contract {
    public static final class Weather {
        public static final String TABLE = "weather";

        public static final String TEMPERATURE = "temperature";
        public static final String HUMIDITY = "humidity";
        public static final String DESCRIPTION = "description";
        public static final String DATE = "weather_date";
        public static final String CITY_ID = "city_id";
    }
    public static class City {
        public static final String TABLE = "city";

        public static final String NAME = "name";
        public static final String _ID = "_id";


    }
}
