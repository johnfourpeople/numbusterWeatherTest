package com.test.jb.numbusterweathertest.Network;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.test.jb.numbusterweathertest.Database.Contract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JB on 17.09.2015.
 */
public class AsyncWeatherConnection extends AsyncTask<Void, Void, List<ContentValues>> {

    private final static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?units=metric&lang=ru&q=";
    private List<String> mCities = new ArrayList<>();
    private List<ContentValues> data = new ArrayList<>();
    private WeatherResponseHandler mHandler;

    public AsyncWeatherConnection(String city, WeatherResponseHandler handler) {
        mCities.add(city);
        mHandler = handler;
    }

    public AsyncWeatherConnection(List<String> cities, WeatherResponseHandler handler) {
        mCities.addAll(cities);
        mHandler = handler;
    }
    @Override
    protected List<ContentValues> doInBackground(Void... params) {
        for (String city : mCities) {
            try {
                URL url = new URL(BASE_URL + URLEncoder.encode(city, "UTF-8"));
                HttpURLConnection networkConnection = (HttpURLConnection)(url)
                         .openConnection();
                networkConnection.setRequestMethod("GET");
                networkConnection.setDoInput(true);
                networkConnection.setDoOutput(true);
                networkConnection.connect();
                InputStream inputStream = networkConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                JsonParser parser = new JsonParser();
                JsonElement root = parser.parse(buffer.toString());
                if (root.isJsonObject()) {
                    if (root.getAsJsonObject().get("cod").getAsInt() == 200) {
                        int dt = root.getAsJsonObject().get("dt").getAsInt();
                        int city_id = root.getAsJsonObject().get("id").getAsInt();
                        JsonObject weather = root.getAsJsonObject().get("weather")
                                .getAsJsonArray().get(0).getAsJsonObject();
                        String descr = weather.get("description").getAsString();
                        JsonObject main = root.getAsJsonObject().get("main").getAsJsonObject();
                        int temp = main.get("temp").getAsInt();
                        int humidity = main.get("humidity").getAsInt();

                        ContentValues contentValues = new ContentValues();
                        contentValues.put(Contract.Weather.DATE, dt);
                        contentValues.put(Contract.Weather.CITY_ID, city_id);
                        contentValues.put(Contract.Weather.DESCRIPTION, descr);
                        contentValues.put(Contract.Weather.TEMPERATURE, temp);
                        contentValues.put(Contract.Weather.HUMIDITY, humidity);
                        contentValues.put(Contract.Weather.CITY_ID, city_id);
                        contentValues.put(Contract.City._ID, city_id);
                        contentValues.put(Contract.City.NAME, city);

                        data.add(contentValues);
                     }
                 }

             } catch (IOException e) {
                 e.printStackTrace();
             }
             }

        return data;
    }

    @Override
    protected void onPostExecute(List<ContentValues> contentValues) {
        super.onPostExecute(contentValues);
        mHandler.responseHandling(contentValues);
    }
}
