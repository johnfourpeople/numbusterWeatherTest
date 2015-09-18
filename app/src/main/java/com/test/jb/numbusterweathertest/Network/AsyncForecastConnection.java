package com.test.jb.numbusterweathertest.Network;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

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
import java.util.Iterator;
import java.util.List;

/**
 * Created by JB on 18.09.2015.
 */
public class AsyncForecastConnection extends AsyncTask<Void,Void,List<ContentValues>> {

    private final static String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?units=metric&count=16&lang=ru&q=";

    private List<String> mCityNames = new ArrayList<>();
    private ForecastResponseHandler mHandler;
    public AsyncForecastConnection(ForecastResponseHandler handler, String cityName) {
        mCityNames.add(cityName);
        mHandler = handler;
    }

    public AsyncForecastConnection(ForecastResponseHandler handler,List<String> cityNames) {
        mCityNames.addAll(cityNames);
        mHandler = handler;
    }
    @Override
    protected List<ContentValues> doInBackground(Void... params) {
        List<ContentValues> data = new ArrayList<>();
        try {
            for (String city : mCityNames) {

                URL url = new URL(BASE_URL + URLEncoder.encode(city, "UTF-8"));
                HttpURLConnection connection = (HttpURLConnection) (url).openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.connect();

                StringBuffer buffer = new StringBuffer();
                InputStream stream;
                stream = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                String line = null;
                while ((line = br.readLine()) != null){
                    buffer.append(line + "\r\n");
                }

                JsonParser parser = new JsonParser();
                JsonElement root = parser.parse(buffer.toString());
                if(root.isJsonObject()) {
                    if (root.getAsJsonObject().get("cod").getAsInt() == 200) {
                        int cityId = root.getAsJsonObject().get("city").getAsJsonObject().get("id").getAsInt();
                        Iterator<JsonElement> iterator = root.getAsJsonObject().get("list").getAsJsonArray().iterator();
                        while(iterator.hasNext()) {
                            JsonElement item = iterator.next();
                            int dt = item.getAsJsonObject().get("dt").getAsInt();

                            JsonObject weather = item.getAsJsonObject().get("weather").getAsJsonArray().get(0).getAsJsonObject();
                            String descr = weather.get("description").getAsString();

                            int humidity = item.getAsJsonObject().get("humidity").getAsInt();

                            int temp = item.getAsJsonObject().get("temp").getAsJsonObject().get("day").getAsInt();

                            ContentValues cv = new ContentValues();
                            cv.put(Contract.Weather.TEMPERATURE, temp);
                            cv.put(Contract.Weather.HUMIDITY, humidity);
                            cv.put(Contract.Weather.DESCRIPTION, descr);
                            cv.put(Contract.Weather.DATE, dt);
                            cv.put(Contract.Weather.CITY_ID, cityId);
                            cv.put(Contract.City._ID, cityId);
                            cv.put(Contract.City.NAME, city);
                            data.add(cv);
                        }
                    } else {
                        Log.d("error", root.getAsJsonObject().get("message").getAsString());
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    protected void onPostExecute(List<ContentValues> contentValues) {
        super.onPostExecute(contentValues);
        mHandler.handleResponse(contentValues);
    }
}
