package com.example.trailxplorer;

// Manifest:
import android.Manifest;

// Import Content:
import android.content.Context;
import android.content.Intent;

// Import DataBase:
import android.database.sqlite.SQLiteDatabase;

// Import Os:
import android.os.Bundle;

// Import Support:
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

// Import View:
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

// Import Widget:
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

// Import Java utils:
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
        // Save context in variable:
        appContext = this;

        // Access data base to get application parameters:
        AppDataBase dataBase = new AppDataBase(appContext, "trailXplorerData", null, 1);
        SQLiteDatabase sdb = dataBase.getWritableDatabase();

        //Setting the style depending on the activation of the night mode.
        if (dataBase.getState(sdb,1)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            setTheme(R.style.DarkAppTheme);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            setTheme(R.style.LightAppTheme);
        }

        // Close database:
        sdb.close();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ask applications permissions
        askPermissions();

        //Initializing the Toolbar.
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //Creating the up button.
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size);
        ab.setDisplayHomeAsUpEnabled(true);

        // Get text view for the gps:
        initUiInterface();

        // Init gps
        gpx = new GpxHelper(MainActivity.this, this);

        // Init timer
        timer = new TimerHelper((TextView) findViewById(R.id.timeRun));

        // Affect button function:
        initMainButton();
        initPauseButton();
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

            //Handles click on the popup menu items.
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

        if (popup != null)
            popup.dismiss();
    }

    //Handles the activity destroy, to dismiss the popup.
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (popup != null)
            popup.dismiss();
    }

    //Initializes the main button, used to start and stop the gps.
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
                    b.setText(R.string.start_btn);

                    Intent intent = new Intent(MainActivity.this, SavedActivity.class);
                    intent.putExtra("FromStopBtn", true);
                    startActivity(intent);
                } else {
                    gps = new GpsHelper(appContext, tv_uiInterface, Calendar.getInstance().getTime().toString());
                    timer.start();
                    gps.start();
                    b.setText(R.string.stop_btn);
                }
            }
        });
    }

    //Initializes the main button, used to pause the gps.
    private void initPauseButton() {
        Button PauseButton = (Button) findViewById(R.id.pauseBtn);

        PauseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                if (gps != null) {
                    if (b.getText().equals("Pause")) {
                        gps.pause();
                        timer.stop();
                        b.setText(R.string.continue_btn);
                    } else {
                        timer.startAgain();
                        gps.startAgain();
                        b.setText(R.string.pause_btn);
                    }
                }
            }
        });
    }

    //Initializes the HashMap, containing the main activity textViews.
    private void initUiInterface() {
        tv_uiInterface.put("current_speed", (TextView) findViewById(R.id.curSpeed));
        tv_uiInterface.put("current_altitude", (TextView) findViewById(R.id.curAlt));
        tv_uiInterface.put("total_distance", (TextView) findViewById(R.id.totDist));
        tv_uiInterface.put("minimum_altitude", (TextView) findViewById(R.id.minAlt));
        tv_uiInterface.put("maximum_altitude", (TextView) findViewById(R.id.maxAlt));
    }

    //Asks for the permissions required for the app to work.
    private void askPermissions() {
        // List of permissions needed:
        String[] perms = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET,
        Manifest.permission.CONTROL_LOCATION_UPDATES};

        // Request Permission to access to the GPS:
        ActivityCompat.requestPermissions(this, perms, RequestAnswer);
    }
}
