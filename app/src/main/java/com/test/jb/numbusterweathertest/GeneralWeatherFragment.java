package com.test.jb.numbusterweathertest;

import android.content.ContentValues;
import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.test.jb.numbusterweathertest.Database.Contract;
import com.test.jb.numbusterweathertest.Network.AsyncWeatherConnection;
import com.test.jb.numbusterweathertest.Network.WeatherResponseHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JB on 17.09.2015.
 */
public class GeneralWeatherFragment extends Fragment implements WeatherResponseHandler,
        LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    private static final int WEATHER_LOADER = 0;
    private static final int CITY_LOADER = 1;
    private Context mContext;
    private GeneralWeatherAdapter mAdapter;
    private RecyclerView weatherList;
    private EditText cityName;
    private Button addCity;
    private AsyncWeatherConnection mNetworkConnection;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.f_general_weather,container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        weatherList = (RecyclerView) view.findViewById(R.id.gw_weather_list);
        weatherList.setLayoutManager(new LinearLayoutManager(mContext));

        cityName = (EditText) view.findViewById(R.id.gw_city_name);
        addCity = (Button) view.findViewById(R.id.gw_add_city_button);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new GeneralWeatherAdapter();
        weatherList.setAdapter(mAdapter);

        mContext.getContentResolver().delete(
                Contract.Weather.CONTENT_URI,
                Contract.Weather.DATE + " < strftime('%s', 'now', '-1 day')",
                null
        );

        android.support.v4.app.LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(WEATHER_LOADER, null, this);
    }

    @Override
    public void responseHandling(List<ContentValues> data) {
        ContentValues cityValues = new ContentValues();
        ContentValues weatherValues = new ContentValues();
        for (ContentValues values : data) {
            cityValues.clear();
            weatherValues.clear();
            cityValues.put(Contract.City.NAME, values.getAsString(Contract.City.NAME));
            cityValues.put(Contract.City._ID, values.getAsInteger(Contract.City._ID));
            weatherValues.put(Contract.Weather.TEMPERATURE, values.getAsInteger(Contract.Weather.TEMPERATURE));
            weatherValues.put(Contract.Weather.DESCRIPTION, values.getAsString(Contract.Weather.DESCRIPTION));
            weatherValues.put(Contract.Weather.HUMIDITY, values.getAsInteger(Contract.Weather.HUMIDITY));
            weatherValues.put(Contract.Weather.DATE, values.getAsInteger(Contract.Weather.DATE));
            weatherValues.put(Contract.Weather.CITY_ID, values.getAsString(Contract.Weather.CITY_ID));

            mContext.getContentResolver().insert(Contract.City.CONTENT_URI, cityValues);
            mContext.getContentResolver().insert(Contract.Weather.CONTENT_URI, weatherValues);
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case WEATHER_LOADER:
                return new CursorLoader(
                        mContext,
                        Contract.Weather.CONTENT_URI,
                        new String[] {
                                Contract.City.NAME,
                                Contract.Weather.TEMPERATURE,
                                "MIN(" + Contract.Weather.DATE + ")"
                        },
                        null,
                        null,
                        Contract.Weather.DATE
                );
            case CITY_LOADER:
                return new CursorLoader(
                        mContext,
                        Contract.City.CONTENT_URI,
                        null,
                        null,
                        null,
                        Contract.City.NAME
                );
        }
        return null;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case WEATHER_LOADER:
                mAdapter.setCursor(data);
                break;
            case CITY_LOADER:
                List<String> citiesList = new ArrayList<>();
                int nameColumnIndex = data.getColumnIndex(Contract.City.NAME);
                while (data.moveToNext()) {
                    citiesList.add(data.getString(nameColumnIndex));
                }
                mNetworkConnection = new AsyncWeatherConnection(citiesList,this);
                mNetworkConnection.execute();
                break;
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        mAdapter.setCursor(null);
    }

    @Override
    public void onClick(View v) {

    }

    private class GeneralWeatherAdapter extends RecyclerView.Adapter<GeneralWeatherAdapter.ViewHolder> {

        private Cursor mCursor;

        public void setCursor(Cursor cursor) {
            mCursor = cursor;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(
                    R.layout.general_item,
                    parent,
                    false
            ));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Log.d("binder","geted");
            if (mCursor != null) {
                mCursor.moveToPosition(position);
                holder.cityName.setText(mCursor.getString(mCursor.getColumnIndex(Contract.City.NAME)));
                holder.temperature.setText(mCursor.getString(mCursor.getColumnIndex(Contract.Weather.TEMPERATURE)));
            }
        }

        @Override
        public int getItemCount() {
            if (mCursor == null ) {
                return 0;
            }
            Log.d("Cursor not null","count get");
            return mCursor.getCount();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView cityName;
            TextView temperature;
            public ViewHolder(View itemView) {
                super(itemView);
                cityName = (TextView) itemView.findViewById(R.id.gi_city_name);
                temperature = (TextView) itemView.findViewById(R.id.gi_temperature);
            }
        }
    }
}
