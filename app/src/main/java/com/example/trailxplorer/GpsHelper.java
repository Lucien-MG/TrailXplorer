package com.example.trailxplorer;

import android.Manifest;
import android.app.Activity;

// Content import:
import android.content.Context;
import android.content.pm.PackageManager;

// Location import:
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

// Widget import:
import android.widget.TextView;
import android.widget.Toast;

// Java import:
import java.util.ArrayList;
import java.util.Map;

public class GpsHelper {
    // private fields of the class
    private TextView tv_lat, tv_long;

    // Location:
    private LocationManager lm;
    private LocationListener ll;
    private Location la;
    private Location lb;

    // Stats:
    private long speed = 0;
    private long TotalDistance;
    private long CurAltitude;
    private long MinAltitude;
    private long MaxAltitude;

    // Data:
    public ArrayList<Long> dataSpeed;
    public ArrayList<Location> dataLocation;
    public int averageSpeed;
    public int nbPoint = 0;

    // Contain message send by gps:
    private Toast toast;

    // Contain activity context:
    private Context ActivityContext;

    // UI connection:
    private Map<String, TextView> tv_uiInterface;

    public GpsHelper(Context context, Map<String, TextView> uiInterface) {
        // Get application context:
        ActivityContext = context;

        // Get interface:
        tv_uiInterface = uiInterface;

        // Min altitude init:
        MinAltitude = Long.MAX_VALUE;

        // Init data structure:
        dataSpeed = new ArrayList<Long>();
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

        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            toast = Toast.makeText(ActivityContext, "WARNING: GPS Disable", Toast.LENGTH_LONG);
            toast.show();
        }

        if (!lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            toast = Toast.makeText(ActivityContext, "WARNING: NETWORK Location Disable", Toast.LENGTH_LONG);
            toast.show();
        }

        ll = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                // the location of the device has changed so update the TextViews to reflect this.
                updateAltitude(location);
                updateDistanceAndSpeed(location);
                updateUI();

                nbPoint += 1;
            }

            @Override
            public void onProviderDisabled(String provider) {
                // if GPS has been disabled then update the TextViews to reflect
                if (provider == LocationManager.GPS_PROVIDER) {
                    //tv_lat.setText("Latitude");
                    //tv_long.setText("Longitude");
                }

                if (provider == LocationManager.NETWORK_PROVIDER) {

                }
            }

            @Override
            public void onProviderEnabled(String provider) {
                // if there is a last known location then set it on the Text View:
                if (provider == LocationManager.GPS_PROVIDER) {
                    if (ActivityCompat.checkSelfPermission(ActivityContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(ActivityContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        toast = Toast.makeText(ActivityContext, "The application has not the permission to use GPS.", Toast.LENGTH_LONG);
                        toast.show();

                        return;
                    }

                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (location != null) {
                        updateUI();
                    }
                }

                if (provider == LocationManager.NETWORK_PROVIDER) {
                    if (ActivityCompat.checkSelfPermission(ActivityContext, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(ActivityContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        toast = Toast.makeText(ActivityContext, "The application has not the permission to use Location.", Toast.LENGTH_LONG);
                        toast.show();

                        return;
                    }

                    Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    if (location != null) {
                        updateUI();
                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        };

        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, ll);
    }

    public void start() {
        // Init location manager:
        lm = (LocationManager) ActivityContext.getSystemService(ActivityContext.LOCATION_SERVICE);
        this.addLocationListener();
    }

    public void stop() {
        averageSpeed = 0;

        for(long tmpSpeed: dataSpeed)
            averageSpeed += tmpSpeed;

        averageSpeed = averageSpeed / dataSpeed.size();

        lm.removeUpdates(ll);
        lm = null;
    }

    public void updateUI() {
        tv_uiInterface.get("current_speed").setText(speed + " km/h");
        tv_uiInterface.get("current_altitude").setText(CurAltitude + " m");
        tv_uiInterface.get("minimum_altitude").setText(MinAltitude + " m");
        tv_uiInterface.get("maximum_altitude").setText(MaxAltitude + " m");
        tv_uiInterface.get("total_distance").setText(String.format("%.2f km", (float) TotalDistance / 1000));
    }

    private void updateDistanceAndSpeed(Location location) {
        lb = new Location(LocationManager.NETWORK_PROVIDER);
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
    }
}
