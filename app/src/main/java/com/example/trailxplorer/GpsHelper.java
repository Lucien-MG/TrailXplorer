package com.example.trailxplorer;

// Location import:

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;
import android.widget.Toast;

public class GpsHelper {
    // private fields of the classprivate
    private TextView tv_lat, tv_long;
    private LocationManager lm;
    private int RequestAnswer = 0;

    private Context ActivityContext;
    private Activity A_Activity;

    public GpsHelper(Context context, Activity activity, TextView latitudeView, TextView longitudeView) {
        ActivityContext = context;
        A_Activity = activity;

        lm = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

        tv_lat = latitudeView;
        tv_long = longitudeView;

        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, RequestAnswer);

        Toast t = Toast.makeText(ActivityContext, "Begin GPS", Toast.LENGTH_LONG);
        t.show();
    }

    // public method that will add a location listener to the location manager
    public LocationManager addLocationListener() {
        if (ActivityCompat.checkSelfPermission(ActivityContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ActivityContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast t = Toast.makeText(ActivityContext, "No authorization", Toast.LENGTH_LONG);
            t.show();
            return null;
        }

        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast t = Toast.makeText(ActivityContext, "GPS enable", Toast.LENGTH_LONG);
            t.show();
        }

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // the location of the device has changed so update the
                // textviews to reflect this
                tv_lat.setText("Latitude: " + location.getLatitude());
                tv_long.setText("Longitude: " + location.getLongitude());
                Toast t = Toast.makeText(ActivityContext, "test: " + location.getLatitude(), Toast.LENGTH_LONG);
                t.show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                // if GPS has been disabled then update the textviews to reflect
                // this
                if (provider == LocationManager.GPS_PROVIDER) {
                    tv_lat.setText("Latitude");
                    tv_long.setText("Longitude");
                }
            }

            @Override
            public void onProviderEnabled(String provider) {
                // if there is a last known location then set it on the
                // textviews
                if (provider == LocationManager.GPS_PROVIDER) {
                    if (ActivityCompat.checkSelfPermission(ActivityContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(ActivityContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        Toast t = Toast.makeText(ActivityContext, "No authorization", Toast.LENGTH_LONG);
                        t.show();
                        return;
                    }

                    Location l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (l != null) {
                        tv_lat.setText("Latitude: " + l.getLatitude());
                        tv_long.setText("Longitude: " + l.getLongitude());
                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}
        });

        return lm;
    }
}
