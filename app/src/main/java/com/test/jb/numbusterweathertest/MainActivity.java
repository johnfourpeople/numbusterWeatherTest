package com.test.jb.numbusterweathertest;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.test.jb.numbusterweathertest.Database.Contract;
import com.test.jb.numbusterweathertest.Network.AsyncForecastConnection;
import com.test.jb.numbusterweathertest.Network.ForecastResponseHandler;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ForecastResponseHandler {

    SharedPreferences preferences = null;
    AsyncForecastConnection networkConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);

        preferences = getPreferences(MODE_PRIVATE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentById(R.id.container) == null) {
           fragmentManager.beginTransaction()
                   .add(R.id.container, new GeneralWeatherFragment()).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (preferences.getBoolean("defaultStart", true)) {
            String[] defaultCities = getResources().getStringArray(R.array.default_cities);
            networkConnection = new AsyncForecastConnection(
                    this,
                    Arrays.asList(defaultCities)

            );
            networkConnection.execute();
        }
    }



    @Override
    public void handleResponse(List<ContentValues> data) {
        ContentValues cityValues = new ContentValues();
        ContentValues weatherValues = new ContentValues();

        if (!data.isEmpty()) {
            preferences.edit().putBoolean("defaultStart", false).commit();
        }
        for (ContentValues values : data) {
            cityValues.clear();
            weatherValues.clear();
            Log.d("mainact", values.getAsString(Contract.City.NAME));

            cityValues.put(Contract.City.NAME, values.getAsString(Contract.City.NAME));
            cityValues.put(Contract.City._ID, values.getAsInteger(Contract.City._ID));
            weatherValues.put(Contract.Weather.TEMPERATURE, values.getAsInteger(Contract.Weather.TEMPERATURE));
            weatherValues.put(Contract.Weather.DESCRIPTION, values.getAsString(Contract.Weather.DESCRIPTION));
            weatherValues.put(Contract.Weather.HUMIDITY, values.getAsInteger(Contract.Weather.HUMIDITY));
            weatherValues.put(Contract.Weather.DATE, values.getAsInteger(Contract.Weather.DATE));
            weatherValues.put(Contract.Weather.CITY_ID, values.getAsString(Contract.Weather.CITY_ID));

            getContentResolver().insert(Contract.City.CONTENT_URI, cityValues);
            getContentResolver().insert(Contract.Weather.CONTENT_URI, weatherValues);
        }
    }
}
