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
import java.util.Map;

public class GpsHelper {
    // private fields of the class
    private TextView tv_lat, tv_long;
    private LocationManager lm;
    private int RequestAnswer;

    // Contain message send by gps:
    private Toast toast;

    // Contain activity context:
    private Context ActivityContext;
    private Activity A_Activity;

    // UI connection:
    private Map<String, TextView> tv_uiInterface;

    public GpsHelper(Context context, Activity activity, Map<String, TextView> uiInterface) {
        // Get application context:
        ActivityContext = context;
        A_Activity = activity;

        // Init location manager:
        lm = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

        // List of permissions needed:
        String[] perms = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                      Manifest.permission.ACCESS_FINE_LOCATION,
                                      Manifest.permission.INTERNET};

        // Request Permission to acces to the GPS:
        ActivityCompat.requestPermissions(activity, perms, RequestAnswer);

        // Get interface:
        tv_uiInterface = uiInterface;
    }

    // public method that will add a location listener to the location manager
    public LocationManager addLocationListener() {
        if (ActivityCompat.checkSelfPermission(ActivityContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ActivityContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            toast = Toast.makeText(ActivityContext, "The application has not the permission to use GPS.", Toast.LENGTH_LONG);
            toast.show();

            return null;
        }

        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            toast = Toast.makeText(ActivityContext, "WARNING: GPS Disable", Toast.LENGTH_LONG);
            toast.show();
        }

        if (!lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            toast = Toast.makeText(ActivityContext, "WARNING: NETWORK Location Disable", Toast.LENGTH_LONG);
            toast.show();
        }

        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                // the location of the device has changed so update the
                // textviews to reflect this
                toast = Toast.makeText(ActivityContext, "test: " + location.getLatitude(), Toast.LENGTH_LONG);
                toast.show();

                updateUI(location);
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
                        updateUI(location);
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
                        updateUI(location);
                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        });

        return lm;
    }

    private void updateUI(Location location) {
        //tv_uiInterface.get("time_run").setText(location.getSpeed() + " km/h");
        tv_uiInterface.get("current_speed").setText((int)location.getSpeed() + " km/h");
        tv_uiInterface.get("current_altitude").setText((int)location.getAltitude() + " m");
    }
}
