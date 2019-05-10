package com.example.trailxplorer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

public class SavedRunActivity extends AppCompatActivity {

    private long id;
    private GpsHelper gps;

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
        id = getIntent().getLongExtra("id", 0);

        //Load the gps object from the database.
        gps = new GpsHelper(this, )
        gps.loadInDataBase(id);

        //Initializing the textViews.
        timerun = findViewById(R.id.timeRun);
        totDist = findViewById(R.id.totDist);
        aveSpeed = findViewById(R.id.aveSpeed);
        minAlt = findViewById(R.id.minAlt);
        aveAlt = findViewById(R.id.aveAlt);
        maxAlt = findViewById(R.id.maxAlt);

        //Filling the textViews.
        timerun.setText(gps.time);
        totDist.setText(gps.TotalDistance + " km");
        aveSpeed.setText(gps.speed + " km/h");
        minAlt.setText(gps.MinAltitude + " m");
        aveAlt.setText(gps.averageAltitude + " m");
        maxAlt.setText(gps.MaxAltitude + " m");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
}
