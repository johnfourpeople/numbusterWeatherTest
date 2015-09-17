package com.test.jb.numbusterweathertest;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by JB on 17.09.2015.
 */
public class GeneralWeatherFragment extends Fragment {
    private Context mContext;
    private RecyclerView weatherList;
    private EditText cityName;
    private Button addCity;

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
        cityName = (EditText) view.findViewById(R.id.gw_city_name);
        addCity = (Button) view.findViewById(R.id.gw_add_city_button);
        Log.d("gwf", "created");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}
