package com.example.trailxplorer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Switch;

public class OptionsActivity extends AppCompatActivity {

    private Switch nightMode;
    private Switch useNetwork;

    private PopupMenu popupOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Setting the style depending on the activation of the night mode.
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkAppTheme);
        }
        else {
            setTheme(R.style.LightAppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        //Initializing the Toolbar.
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //Creating the up button.
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size);
        ab.setDisplayHomeAsUpEnabled(true);

        //Setting the nightMode Switch and enabling it if necessary.
        nightMode = findViewById(R.id.nightModeSwitch);
        nightMode.setChecked(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);

        nightMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }

                Intent intent = new Intent(getApplicationContext(), OptionsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        useNetwork = findViewById(R.id.gpsModeSwitch);
        useNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

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
            popupOptions = new PopupMenu(this, findViewById(R.id.my_toolbar));
            MenuInflater inflater = popupOptions.getMenuInflater();
            inflater.inflate(R.menu.popup_menuoptions, popupOptions.getMenu());

            //Handles click on the popup menu.
            popupOptions.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.newRun:
                            Intent intent = new Intent(OptionsActivity.this, MainActivity.class);
                            startActivity(intent);
                            return true;
                        case R.id.savedRun:
                            Intent intent2 = new Intent(OptionsActivity.this, SavedActivity.class);
                            startActivity(intent2);
                            return true;
                        default:
                            return false;
                    }
                }
            });

            popupOptions.show();
        }
        return super.onOptionsItemSelected(item);
    }

    //Handles the activity pause, to dismiss the popup.
    @Override
    protected void onPause() {
        super.onPause();

        if (popupOptions != null) {
            popupOptions.dismiss();
        }
    }

    //Handles the activity destroy, to dismiss the popup.
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (popupOptions != null) {
            popupOptions.dismiss();
        }
    }
}