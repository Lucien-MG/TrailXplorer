package com.example.trailxplorer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlHelper extends SQLiteOpenHelper {

    public SqlHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // overridden method that is called when the database is to be created
    public void onCreate(SQLiteDatabase db) {
        // create the database
        db.execSQL(create_table);
    }

    public void onUpgrade(SQLiteDatabase db, int version_old, int version_new) {
        // drop the tables and recreate them
        db.execSQL(drop_table);
        db.execSQL(create_table);
    }

    private static final String create_table = "create table GPSdataBase("
            + "ID integer primary key autoincrement, "
            + "AVE_SPEED int,"
            + "TT_DISTANCE int,"
            + "MIN_ALT int,"
            + "MAX_ALT int,"
            + "AVE_ALT int,"
            + "TIME string"
            + ")";

    private static final String drop_table = "drop table test";
}
