package com.example.trailxplorer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
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
import java.util.ArrayList;
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

                strGpx += "<ele>" + gps.dataSpeed.get(i) + "</ele>\n";

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

    private String getWord(String s, int index) {
        String word = new String();
        while (index < s.length() && (s.charAt(index) == ' ' && s.charAt(index) == '\n'))
            index += 1;

        while (index < s.length() && s.charAt(index) != ' ' && s.charAt(index) != '\n' && s.charAt(index) != '>') {
            word += s.charAt(index);
            index += 1;
        }

        return word;
    }

    private long getNumber(String s, int index) {
        long nb = 0;

        while (s.charAt(index) < 40 && s.charAt(index) > 29) {
            nb = nb * 10 + (long) (s.charAt(index) - 30);
            index += 1;
        }

        return nb;
    }

    private void parseToken(GpsHelper gps, String token) {
        int index = 0;
        String tk = getWord(token, index);

        switch (tk) {
            case "trk":

                break;

            case "trkpt":

                break;

            case "ele":
                gps.dataSpeed.add(getNumber(token, 4));
                break;
        }
    }

    private void parseGpx(GpsHelper gps, String file) {
        String[] tokens = file.split("<");
        for (int i=0; i < tokens.length; i++)
            Log.e(TAG, "Token: " + tokens[i]);

        for (int i=1; i < tokens.length; i++)
            parseToken(gps, tokens[i]);
    }

    public GpsHelper loadGpx(Context context, Map<String, TextView> uiInterface, String fileName) {
        GpsHelper gps = new GpsHelper(context, uiInterface);
        String content = readFile(fileName);
        Log.e(TAG, "loadGpx: " + content);
        parseGpx(gps, content);
        gps.updateUI();
        Log.e(TAG, "loadGpx: " + gps.dataSpeed.get(0));
        return gps;
    }

    public String readFile(String fileName) {
        String content = null;

        try {
            File file = new File(directory.getAbsolutePath() + "/" + fileName + ".gpx");

            if (!file.exists())
                file.createNewFile();

            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();

            while ((content = bufferedReader.readLine()) != null) {
                stringBuilder.append(content + System.getProperty("line.separator"));
            }

            fileInputStream.close();
            content = stringBuilder.toString();

            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            toast = Toast.makeText(context, "File not found", Toast.LENGTH_LONG);
            toast.show();
        }
        catch(IOException ex) {
            toast = Toast.makeText(context, "Can't read file", Toast.LENGTH_LONG);
            toast.show();
        }

        return content;
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
