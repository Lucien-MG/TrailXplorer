package com.example.trailxplorer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GpxHelper {

    private Activity activity;
    private Context context;

    private int RequestAnswer;

    private String dirName = "GPStracks";
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

    public String openFile(String fileName) {
        String content = null;

        try {
            File file = new File(directory.getAbsolutePath() + "/" + fileName);

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
            //toast = Toast.makeText(context, "Can't read file", Toast.LENGTH_LONG);
            //toast.show();
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
                else {
                    toast = Toast.makeText(context, "Create " + dir.getAbsolutePath(), Toast.LENGTH_LONG);
                    toast.show();
                }
            }

            toast = Toast.makeText(context, "Create " + dir.getAbsolutePath(), Toast.LENGTH_LONG);
            toast.show();

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
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

}
