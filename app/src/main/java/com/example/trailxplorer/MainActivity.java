package com.example.trailxplorer;

import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Timer:
    TimerHelper timer;

    // GPS:
    GpsHelper gps;

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

        gps = new GpsHelper(this, MainActivity.this, tv_uiInterface);

        timer = new TimerHelper((TextView) findViewById(R.id.timeRun));

        initMainButton();
    }

    //Initializes the Toolbar.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    private void initMainButton() {
        Button MainButton = (Button) findViewById(R.id.startBtn);

        MainButton.setText("start");
        MainButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                if (b.getText().equals("stop")) {
                    timer.stop();
                    gps.stop();
                    b.setText("start");
                } else {
                    timer.start();
                    gps.start();
                    b.setText("stop");
                }
            }
        });
    }

    private void initUiInterface() {
        tv_uiInterface.put("current_speed", (TextView) findViewById(R.id.curSpeed));
        tv_uiInterface.put("current_altitude", (TextView) findViewById(R.id.curAlt));
        tv_uiInterface.put("total_distance", (TextView) findViewById(R.id.totDist));
        tv_uiInterface.put("minimum_altitude", (TextView) findViewById(R.id.minAlt));
        tv_uiInterface.put("maximum_altitude", (TextView) findViewById(R.id.maxAlt));
    }
}
