package com.example.trailxplorer;

import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private LocationManager lm;

    // All text view useful for GPS:
    Map<String, TextView> tv_uiInterface = new HashMap<String, TextView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing the Toolbar.
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        initUiInterface();

        GpsHelper gpsHelper = new GpsHelper(this, MainActivity.this, tv_uiInterface);

        lm = gpsHelper.addLocationListener();
    }

    //Initializes the Toolbar.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    private void initUiInterface() {
        tv_uiInterface.put("time_run", (TextView) findViewById(R.id.timeRun));
        tv_uiInterface.put("current_speed", (TextView) findViewById(R.id.curSpeed));
        tv_uiInterface.put("current_altitude", (TextView) findViewById(R.id.curAlt));
    }
}
