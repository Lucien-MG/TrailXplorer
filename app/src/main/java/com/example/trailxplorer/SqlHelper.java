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
            + "AVE_SPEED integer,"
            + "TT_DISTANCE integer,"
            + "MIN_ALT integer,"
            + "MAX_ALT integer,"
            + "AVE_ALT integer,"
            + "NAME string,"
            + "TIME string,"
            + "ALL_SPEED string"
            + ")";

    private static final String drop_table = "drop table GPSdataBase";

    public void deleteSingleNote(String id) {
        SQLiteDatabase sdb = this.getWritableDatabase();
        sdb.delete("GPSdataBase", "ID= '" + id + "'", null);
    }
}
