package com.test.jb.numbusterweathertest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.test.jb.numbusterweathertest.Database.Contract;
import com.test.jb.numbusterweathertest.Network.ForecastResponseHandler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by JB on 18.09.2015.
 */
public class ExtendedWeatherFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        ForecastResponseHandler, View.OnClickListener{


    private static final String CITY_NAME = "CityName";
    private static final int WEATHER_LOADER = 0;

    private String mCityName;
    private ExtendedWeatherAdapter mAdapter;
    private boolean forecastRangeTrigger = true;

    RecyclerView forecastList;
    TextView cityName;
    Button changeForecastRange;

    public static ExtendedWeatherFragment newInstance(String cityName){
        ExtendedWeatherFragment extendedWeatherFragment = new ExtendedWeatherFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CITY_NAME, cityName);
        extendedWeatherFragment.setArguments(bundle);

        return extendedWeatherFragment;
    }

    Context mContext;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCityName = getArguments().getString(CITY_NAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.f_extended_weather, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        forecastList = (RecyclerView)view.findViewById(R.id.ew_forecast_list);
        forecastList.setLayoutManager(new LinearLayoutManager(
                mContext,
                LinearLayoutManager.HORIZONTAL,
                false
        ));
        cityName = (TextView)view.findViewById(R.id.ew_city_name);
        changeForecastRange = (Button) view.findViewById(R.id.ew_change_forecast_range);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new ExtendedWeatherAdapter();
        forecastList.setAdapter(mAdapter);

        cityName.setText(mCityName);

        changeForecastRange.setOnClickListener(this);
        changeForecastRange.setText(forecastRangeTrigger?"3 days":"7 days");

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(WEATHER_LOADER, null, this);

    }

    @Override
    public void handleResponse(List<ContentValues> data) {
        for (ContentValues values : data) {
            mContext.getContentResolver().insert(Contract.Weather.EXTENDED_CONTENT_URI,values);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("create loader", Contract.Weather.DATE + " LIMIT " + (forecastRangeTrigger ? "7" : "3"));
        return new CursorLoader(
                mContext,
                Contract.Weather.EXTENDED_CONTENT_URI,
                null,
                Contract.City.NAME + " = ?",
                new String[]{mCityName},
                Contract.Weather.DATE + " LIMIT " + (forecastRangeTrigger?"7":"3"));

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.setCursor(null);
    }

    @Override
    public void onClick(View v) {
        forecastRangeTrigger = ! forecastRangeTrigger;
        changeForecastRange.setText(forecastRangeTrigger?"3 days":"7 days");
        LoaderManager loaderManager = getLoaderManager();
        mAdapter.setCursor(null);
        loaderManager.restartLoader(WEATHER_LOADER, null, this);
    }

    private class ExtendedWeatherAdapter extends RecyclerView.Adapter<ExtendedWeatherAdapter.ViewHolder>{

        Cursor mCursor;
        public void setCursor(Cursor cursor) {
            mCursor = cursor;
            notifyDataSetChanged();
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.extended_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (mCursor != null) {
                mCursor.moveToPosition(position);
                holder.temperature.setText(mCursor.getString(mCursor.getColumnIndex(Contract.Weather.TEMPERATURE))+ " \u2103");
                holder.description.setText(mCursor.getString(mCursor.getColumnIndex(Contract.Weather.DESCRIPTION)));
                holder.humidity.setText(mCursor.getString(mCursor.getColumnIndex(Contract.Weather.HUMIDITY)) + "%");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(mCursor.getLong(mCursor.getColumnIndex(Contract.Weather.DATE)) * 1000);
                DateFormat formatter = new SimpleDateFormat("dd/MM HH:mm");

                holder.date.setText(formatter.format(calendar.getTime()));
            }
        }

        @Override
        public int getItemCount() {
            if (mCursor == null) {
                return 0;
            }
            return mCursor.getCount();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView temperature;
            TextView description;
            TextView humidity;
            TextView date;
            public ViewHolder(View itemView) {
                super(itemView);
                temperature = (TextView)itemView.findViewById(R.id.ei_temperature);
                description = (TextView)itemView.findViewById(R.id.ei_description);
                humidity = (TextView)itemView.findViewById(R.id.ei_humidity);
                date = (TextView)itemView.findViewById(R.id.ei_date);
            }
        }
    }
}
