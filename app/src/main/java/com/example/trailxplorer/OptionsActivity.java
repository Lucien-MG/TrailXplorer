package com.example.trailxplorer;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    private Switch economyMode;
    private Switch useNetwork;

    private PopupMenu popupOptions;
    private Context ActivityContext;

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

        ActivityContext = this;

        //Initializing the Toolbar.
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //Creating the up button.
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size);
        ab.setDisplayHomeAsUpEnabled(true);

        //Setting the nightMode Switch and enabling it if necessary.
        nightMode = findViewById(R.id.nightModeSwitch);
        economyMode = findViewById(R.id.ecoModeSwitch);
        useNetwork = findViewById(R.id.gpsModeSwitch);

        setBtnState();

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
                updateDataBase();
            }
        });

        useNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDataBase();
            }
        });

        economyMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDataBase();
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

    private void setBtnState() {
        try {
            AppDataBase dataBase = new AppDataBase(ActivityContext, "trailXplorerData", null, 1);
            SQLiteDatabase sdb = dataBase.getWritableDatabase();

            // name of the table to query
            String table_name = "trailXplorerData";
            // the columns that we wish to retrieve from the tables
            String[] columns = {"ID", "NIGHT_MODE", "ECONOMY_MODE", "NETWORK_MODE"};
            // where clause of the query. DO NOT WRITE WHERE IN THIS
            String where = null;
            // arguments to provide to the where clauseString
            String where_args[] = null;
            // group by clause of the query. DO NOT WRITE GROUP BY IN THIS
            String group_by = null;
            // having clause of the query. DO NOT WRITE HAVING IN THIS
            String having = null;
            // order by clause of the query. DO NOT WRITE ORDER BY IN THIS
            String order_by = null;

            Cursor c = sdb.query(table_name, columns, where, where_args, group_by, having, order_by);
            c.moveToFirst();

            nightMode.setChecked(c.getInt(1) == 1);
            economyMode.setChecked(c.getInt(2) == 1);
            useNetwork.setChecked(c.getInt(3) == 1);

            sdb.close();
        }catch (Exception e) {
            AppDataBase dataBase = new AppDataBase(ActivityContext, "trailXplorerData", null, 1);
            SQLiteDatabase sdb = dataBase.getWritableDatabase();
            ContentValues cv = new ContentValues();

            cv.put("NIGHT_MODE", boolToInt(nightMode.isChecked()));
            cv.put("ECONOMY_MODE", boolToInt(economyMode.isChecked()));
            cv.put("NETWORK_MODE", boolToInt(useNetwork.isChecked()));

            dataBase.onCreate(sdb);
            sdb.insert("trailXplorerData", null, cv);
            sdb.close();
        }
    }

    private void updateDataBase() {
        AppDataBase dataBase = new AppDataBase(ActivityContext, "trailXplorerData", null, 1);
        SQLiteDatabase sdb = dataBase.getWritableDatabase();

        dataBase.delete();

        ContentValues cv = new ContentValues();

        cv.put("NIGHT_MODE", nightMode.isChecked());
        cv.put("ECONOMY_MODE", economyMode.isChecked());
        cv.put("NETWORK_MODE", useNetwork.isChecked());

        sdb.insert("trailXplorerData", null, cv);
        sdb.close();
    }

    private int boolToInt(boolean b) {
        if (b)
            return 1;
        else
            return 0;
    }
}