package com.test.jb.numbusterweathertest.Network;

import android.content.ContentValues;

import java.util.List;

/**
 * Created by JB on 18.09.2015.
 */
public interface ForecastResponseHandler {
    void handleResponse(List<ContentValues> data);
}
