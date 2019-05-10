package com.example.trailxplorer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Timer:
    TimerHelper timer;

    // GPX:
    GpxHelper gpx;

    // GPS:
    GpsHelper gps;

    // All text view useful for GPS:
    Map<String, TextView> tv_uiInterface = new HashMap<String, TextView>();

    // Context:
    Context appContext;

    // Permission answer:
    private int RequestAnswer;

    //Popup Menu.
    private PopupMenu popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Setting the style depending on the activation of the night mode.
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            setTheme(R.style.DarkAppTheme);
        else
            setTheme(R.style.LightAppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askPermissions();

        //Initializing the Toolbar.
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //Creating the up button.
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size);
        ab.setDisplayHomeAsUpEnabled(true);

        initUiInterface();

        gpx = new GpxHelper(MainActivity.this, this);

        timer = new TimerHelper((TextView) findViewById(R.id.timeRun));

        initMainButton();
        appContext = this;
    }

    //Initializes the Toolbar.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    //Handles clicks on the toolbar buttons.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            popup = new PopupMenu(this, findViewById(R.id.my_toolbar));
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.popup_menu, popup.getMenu());

            //Handles click on the popup menu.
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                 public boolean onMenuItemClick(MenuItem item) {
                     switch(item.getItemId()) {
                         case R.id.savedRun:
                             Intent intent = new Intent(MainActivity.this, SavedActivity.class);
                             startActivity(intent);
                             return true;
                         case R.id.options:
                             Intent intent2 = new Intent(MainActivity.this, OptionsActivity.class);
                             startActivity(intent2);
                             return true;
                         default:
                             return false;
                     }
                 }
             });

            popup.show();
        }
        return super.onOptionsItemSelected(item);
    }

    //Handles the activity pause, to dismiss the popup.
    @Override
    protected void onPause() {
        super.onPause();

        if (popup != null) {
            popup.dismiss();
        }
    }

    //Handles the activity destroy, to dismiss the popup.
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (popup != null) {
            popup.dismiss();
        }
    }

    private void initMainButton() {
        Button MainButton = (Button) findViewById(R.id.startBtn);

        MainButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                if (b.getText().equals("Stop")) {
                    gps.stop();
                    gpx.saveDataInGpx(gps, gps.name);
                    gps.saveInDataBase(timer.getTime());
                    timer.stop();
                    gps = null;
                    b.setText("Start");
                } else {
                    gps = new GpsHelper(appContext, tv_uiInterface, Calendar.getInstance().getTime().toString());
                    timer.start();
                    gps.start();
                    b.setText("Stop");
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

    private void askPermissions() {
        // List of permissions needed:
        String[] perms = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET};

        // Request Permission to acces to the GPS:
        ActivityCompat.requestPermissions(this, perms, RequestAnswer);
    }
}
