package com.example.trailxplorer;

import android.Manifest;
import android.app.Activity;

// Content import:
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;

// Location import:
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

// Widget import:
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

// Java import:
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class GpsHelper {
    // Name:
    public String name;

    // Location provider:
    private String locationProvider;

    // Location:
    private LocationManager lm;
    private LocationListener ll;
    private Location la;
    private Location lb;

    // Stats and data:
    public long speed = 0;
    public long TotalDistance = 0;
    public long CurAltitude;
    public long MinAltitude;
    public long MaxAltitude;
    public int averageSpeed = 0;
    public int averageAltitude = 0;
    public int nbPoint = 0;
    public String time = "00:00:00";
    public ArrayList<Long> dataSpeed;
    public ArrayList<Long> dataAltitude;
    public ArrayList<String> dataDate;
    public ArrayList<Location> dataLocation;

    // Contain message send by gps:
    private Toast toast;

    // Contain activity context:
    private Context ActivityContext;

    // UI connection:
    private Map<String, TextView> tv_uiInterface;

    public GpsHelper(Context context, Map<String, TextView> uiInterface, String name) {
        this.name = name;

        // Affect Location provider:
        locationProvider = LocationManager.NETWORK_PROVIDER;

        // Get application context:
        ActivityContext = context;

        // Get interface:
        tv_uiInterface = uiInterface;

        // Min altitude init:
        MinAltitude = Long.MAX_VALUE;

        // Init data structure:
        dataSpeed = new ArrayList<Long>();
        dataAltitude = new ArrayList<Long>();
        dataDate = new ArrayList<String>();
        dataLocation = new ArrayList<Location>();
    }

    // private method that will add a location listener to the location manager
    private void addLocationListener() {
        if (ActivityCompat.checkSelfPermission(ActivityContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ActivityContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            toast = Toast.makeText(ActivityContext, "The application has not the permission to use GPS.", Toast.LENGTH_LONG);
            toast.show();

            return;
        }

        if (!lm.isProviderEnabled(locationProvider)) {
            toast = Toast.makeText(ActivityContext, "WARNING: Location Disable", Toast.LENGTH_LONG);
            toast.show();
        }

        ll = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                // the location of the device has changed so update the TextViews to reflect this.
                updateAltitude(location);
                updateDistanceAndSpeed(location);
                updateUI();

                dataDate.add(Calendar.getInstance().getTime().toString());

                nbPoint += 1;
            }

            @Override
            public void onProviderDisabled(String provider) {
                // if GPS has been disabled then update the TextViews to reflect
                if (provider == locationProvider) {
                    //tv_lat.setText("Latitude");
                    //tv_long.setText("Longitude");
                }
            }

            @Override
            public void onProviderEnabled(String provider) {
                // if there is a last known location then set it on the Text View:
                if (provider == locationProvider) {
                    if (ActivityCompat.checkSelfPermission(ActivityContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(ActivityContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        toast = Toast.makeText(ActivityContext, "The application has not the permission to use GPS.", Toast.LENGTH_LONG);
                        toast.show();

                        return;
                    }

                    Location location = lm.getLastKnownLocation(locationProvider);

                    if (location != null)
                        updateUI();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        };

        lm.requestLocationUpdates(locationProvider, 5000, 0, ll);
    }

    public void start() {
        // Init location manager:
        lm = (LocationManager) ActivityContext.getSystemService(ActivityContext.LOCATION_SERVICE);
        this.addLocationListener();
    }

    public void pause() {
        lm.removeUpdates(ll);
    }

    public void startAgain() {
    }

    public void stop() {
        // Calculate average speed:
        averageSpeed = 0;
        for(long tmpSpeed: dataSpeed)
            averageSpeed += tmpSpeed;

        if (dataSpeed.size() != 0)
            averageSpeed = averageSpeed / dataSpeed.size();

        // Calculate average speed:
        averageAltitude = 0;
        for(long tmpSpeed: dataAltitude)
            averageAltitude += tmpSpeed;

        if (dataAltitude.size() != 0)
            averageAltitude = averageAltitude / dataAltitude.size();

        lm.removeUpdates(ll);
        lm = null;
    }

    public void printFromSave() {
        tv_uiInterface.get("timerun").setText(this.time);
        tv_uiInterface.get("totDist").setText(this.TotalDistance + " km" + " m");
        tv_uiInterface.get("aveSpeed").setText(this.speed + " km/h");
        tv_uiInterface.get("minAlt").setText(this.MinAltitude + " m");
        tv_uiInterface.get("maxAlt").setText(this.averageAltitude + " m");
        tv_uiInterface.get("aveAlt").setText(this.MaxAltitude + " m");
    }

    public void updateUI() {
        tv_uiInterface.get("current_speed").setText(speed + " km/h");
        tv_uiInterface.get("current_altitude").setText(CurAltitude + " m");
        tv_uiInterface.get("minimum_altitude").setText(MinAltitude + " m");
        tv_uiInterface.get("maximum_altitude").setText(MaxAltitude + " m");
        tv_uiInterface.get("total_distance").setText(String.format("%.2f km", (float) TotalDistance / 1000));
    }

    private void updateDistanceAndSpeed(Location location) {
        lb = new Location(locationProvider);
        lb.setLatitude(location.getLatitude());
        lb.setLongitude(location.getLongitude());

        if (la != null) {
            TotalDistance += la.distanceTo(lb);
            speed = (long)la.distanceTo(lb) / 5;
        }

        la = lb;

        // Update database:
        dataSpeed.add(speed);
        dataLocation.add(lb);
    }

    private void updateAltitude(Location location) {
        CurAltitude = (long)location.getAltitude();

        if (location.getAltitude() < MinAltitude)
            MinAltitude = (long) location.getAltitude();

        if (location.getAltitude() > MaxAltitude)
            MaxAltitude = (long) location.getAltitude();

        dataAltitude.add(CurAltitude);
    }

    public long saveInDataBase(String timeRan) {
        SqlHelper dataBase = new SqlHelper(ActivityContext, "GPSdataBase", null, 1);
        SQLiteDatabase sdb = dataBase.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("AVE_SPEED", averageSpeed);
        cv.put("TT_DISTANCE", TotalDistance);
        cv.put("MIN_ALT", MinAltitude);
        cv.put("MAX_ALT", MaxAltitude);
        cv.put("AVE_ALT", averageAltitude);
        cv.put("NAME", name);
        cv.put("TIME", timeRan);

        return sdb.insert("GPSdataBase", null, cv);
    }

    public void loadFromDataBase(long id) {
        Log.e("test", "loadFromDataBase: " + id);

        SqlHelper dataBase = new SqlHelper(ActivityContext, "GPSdataBase", null, 1);
        SQLiteDatabase sdb = dataBase.getWritableDatabase();

        // name of the table to query
        String table_name = "GPSdataBase";
        // the columns that we wish to retrieve from the tables
        String[] columns = {"ID", "AVE_SPEED", "TT_DISTANCE", "MIN_ALT", "MAX_ALT", "AVE_ALT", "NAME", "TIME"};
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
        for(int i = 0; i < c.getCount(); i++) {
            if (c.getLong(0) == id) {
                Log.e("test", "Found");
                this.averageAltitude = c.getInt(1);
                this.TotalDistance = c.getInt(2);
                this.MinAltitude = c.getInt(3);
                this.MaxAltitude = c.getInt(4);
                this.averageAltitude = c.getInt(5);
                this.name = c.getString(6);
                break;
            }
            Log.e("test", "Move");
            c.moveToNext();
        }
    }
}
