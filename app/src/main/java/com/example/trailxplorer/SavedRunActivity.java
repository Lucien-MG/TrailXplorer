package com.example.trailxplorer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class SavedRunActivity extends AppCompatActivity {

    private long id;
    private GpsHelper gps;

    Map<String, TextView> tv_uiInterface = new HashMap<String, TextView>();

    private TextView timerun;
    private TextView totDist;
    private TextView aveSpeed;
    private TextView minAlt;
    private TextView aveAlt;
    private TextView maxAlt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Setting the style depending on the activation of the night mode.
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkAppTheme);
            GraphView.setNight(true);
        }
        else {
            setTheme(R.style.LightAppTheme);
            GraphView.setNight(false);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savedrun);

        //Initializing the Toolbar.
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //Creating the up button.
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        //Get the id of the run.
        id = convertID(getIntent().getStringExtra("id"));

        //Load the gps object from the database.
        initUiInterface();

        gps = new GpsHelper(this, tv_uiInterface, "");
        gps.loadFromDataBase(id);

        //Filling the textViews.
        gps.printFromSave();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    private void initUiInterface() {
        tv_uiInterface.put("timerun", (TextView) findViewById(R.id.timeRun));
        tv_uiInterface.put("totDist", (TextView) findViewById(R.id.totDist));
        tv_uiInterface.put("aveSpeed", (TextView) findViewById(R.id.aveSpeed));
        tv_uiInterface.put("minAlt", (TextView) findViewById(R.id.minAlt));
        tv_uiInterface.put("maxAlt", (TextView) findViewById(R.id.maxAlt));
        tv_uiInterface.put("aveAlt", (TextView) findViewById(R.id.aveAlt));
    }

    private long convertID(String id) {
        long longID = 0;
        int pos = 0;

        while (pos < id.length()) {
            longID += longID * 10 + (id.charAt(pos)-48);
            pos += 1;
        }

        return longID;
    }
}
