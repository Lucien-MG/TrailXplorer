package com.example.trailxplorer;

import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private LocationManager lm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GpsHelper gpsHelper = new GpsHelper(this, MainActivity.this);

        lm = gpsHelper.addLocationListener();
    }
}
