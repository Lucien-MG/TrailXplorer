package com.example.trailxplorer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SavedActivity extends AppCompatActivity {

    private PopupMenu popupSaved;

    private ListView savedList;
    private SimpleAdapter adapter;
    private HashMap<String, String> item;
    private List<HashMap<String, String>> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Setting the style depending on the activation of the night mode.
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkAppTheme);
        } else {
            setTheme(R.style.LightAppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        //Initializing the Toolbar.
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //Creating the up button.
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size);
        ab.setDisplayHomeAsUpEnabled(true);

        initList();
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
            popupSaved = new PopupMenu(this, findViewById(R.id.my_toolbar));
            MenuInflater inflater = popupSaved.getMenuInflater();
            inflater.inflate(R.menu.popup_menusaved, popupSaved.getMenu());

            //Handles click on the popup menu.
            popupSaved.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.newRun:
                            Intent intent = new Intent(SavedActivity.this, MainActivity.class);
                            startActivity(intent);
                            return true;
                        case R.id.options:
                            Intent intent2 = new Intent(SavedActivity.this, OptionsActivity.class);
                            startActivity(intent2);
                            return true;
                        default:
                            return false;
                    }
                }
            });

            popupSaved.show();
        }
        return super.onOptionsItemSelected(item);
    }

    //Handles the activity pause, to dismiss the popup.
    @Override
    protected void onPause() {
        super.onPause();

        if (popupSaved != null) {
            popupSaved.dismiss();
        }
    }

    //Handles the activity destroy, to dismiss the popup.
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (popupSaved != null) {
            popupSaved.dismiss();
        }
    }

    //Initializes the listView.
    protected void initList() {
        //Initializing the HashMap and the list.
        item = new HashMap<>();
        list = new ArrayList<>();

        //Filling the Hashmap and the list.
        item.put("title", "Test1");
        item.put("time", "Time: 00:00:00");
        item.put("distance", "Distance: 0 km");
        list.add(item);

        //Initializing the list view.
        savedList = findViewById(R.id.listView);
        adapter = new SimpleAdapter(this, list, R.layout.activity_listview,
                new String[] {"title", "time", "distance"}, new int[] {R.id.savedList, R.id.savedListTime, R.id.savedListDistance});
        savedList.setAdapter(adapter);

        //In case of a click on a note, open the savedRunActivity, with the corresponding data.
        savedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SavedActivity.this, SavedRunActivity.class);

                startActivity(intent);
            }
        });

        //In case of long click on a note, show an alert dialog to ask confirmation and delete the note form the database and from the list.
        savedList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return onLongListItemClick(view, position, id);
            }

            protected boolean onLongListItemClick(View v, final int pos, long id) {
                AlertDialog.Builder builder;

                //Setting the style depending on the activation of the night mode.
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                    builder = new AlertDialog.Builder(SavedActivity.this, R.style.DarkAlertDialog);
                }
                else {
                    builder = new AlertDialog.Builder(SavedActivity.this, R.style.LightAlertDialog);
                }

                //Setting the alert dialog.
                builder.setTitle("Delete");
                builder.setMessage("Are you sure ?");

                //Setting the buttons of the alert dialog.
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Delete the run.
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });

                //Creating the alert dialog.
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
            }
        });
    }
}
