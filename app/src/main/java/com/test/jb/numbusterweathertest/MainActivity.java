package com.test.jb.numbusterweathertest;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentById(R.id.container) == null) {
           fragmentManager.beginTransaction()
                   .add(R.id.container, new GeneralWeatherFragment()).commit();
        }
    }
}
