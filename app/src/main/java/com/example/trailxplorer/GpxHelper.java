package com.example.trailxplorer;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class GpxHelper {

    private Activity activity;
    private Context context;

    private int RequestAnswer;

    private String dirName = "GPStracks/";
    private File directory;
    private File actualFile;
    private File[] listFiles;

    // Contain message send by gpx:
    private Toast toast;

    public GpxHelper(Activity act, Context ctx) {
        activity = act;
        context = ctx;

        directory = getPublicStorageDir();
        listFiles = directory.listFiles();
    }

    private String buildGpx(GpsHelper gps, String fileName) {
        String strGpx = new String();

        strGpx += "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n";
        strGpx += "<gpx version=\"1.1\">\n";

        // Track:
        strGpx += "<trk>\n";

        strGpx += "<name>" + fileName + "</name>\n";

        strGpx += "<trkseg>\n";

        for(int i=0; i < gps.nbPoint; i++) {
            strGpx += "<trkpt" + " lat=\"" + gps.dataLocation.get(i).getLatitude()
                          + "\" lon=\"" + gps.dataLocation.get(i).getLongitude() + "\">\n";

            strGpx += "<ele>" + gps.dataAltitude.get(i) + "</ele>\n";

            strGpx += "<time>" + gps.dataDate.get(i) + "</time>\n";

            strGpx += "</trkpt>\n";
        }

        strGpx += "</trkseg>\n";

        strGpx += "</trk>\n";

        strGpx += "</gpx>";

        return strGpx;
    }

    private void writeToFile(String data,String path) {
        try {
            File file = new File(path);

            if (!file.exists())
                file.createNewFile();

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public void saveDataInGpx(GpsHelper gps, String fileName) {
        String gpxSave = buildGpx(gps, fileName);
        String path = directory.getAbsolutePath() + "/" + fileName + ".gpx";
        writeToFile(gpxSave, path);
    }
    
    private File getPublicStorageDir() {
        // Get the directory from the user's public directory.
        if(isExternalStorageReadable() && isExternalStorageWritable()) {
            File dir = new File(Environment.getExternalStorageDirectory(), dirName);
            if (!dir.exists()) {
                if (!dir.mkdirs()){
                    toast = Toast.makeText(context, "Can't create" + dir.getAbsolutePath(), Toast.LENGTH_LONG);
                    toast.show();
                }
            }

            return dir;
        }
        else {
            toast = Toast.makeText(context, "Can't access to external storage", Toast.LENGTH_LONG);
            toast.show();

            return null;
        }
    }

    /* Checks if external storage is available to at least write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

}
