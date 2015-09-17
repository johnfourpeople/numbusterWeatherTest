package com.test.jb.numbusterweathertest.Network;

import android.content.ContentValues;

import java.util.List;

/**
 * Created by JB on 17.09.2015.
 */
public interface WeatherResponseHandler {
    void responseHandling(List<ContentValues> data);
}
