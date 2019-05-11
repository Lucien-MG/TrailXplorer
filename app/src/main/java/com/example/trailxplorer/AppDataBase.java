package com.example.trailxplorer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppDataBase extends SQLiteOpenHelper {

    public AppDataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
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

    public boolean getState(SQLiteDatabase db, int mode) {
        try {
            // name of the table to query
            String table_name = "trailXplorerData";
            // the columns that we wish to retrieve from the tables
            String[] columns = {"ID", "NIGHT_MODE", "ECONOMY_MODE", "NETWORK_MODE"};
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

            Cursor c = db.query(table_name, columns, where, where_args, group_by, having, order_by);
            c.moveToFirst();

            return c.getInt(mode) == 1;
        }
        catch (Exception e) {
            //this.onCreate(db);
        }

        return false;
    }

    private static final String create_table = "create table trailXplorerData("
            + "ID integer primary key autoincrement, "
            + "NIGHT_MODE integer,"
            + "ECONOMY_MODE integer,"
            + "NETWORK_MODE integer"
            + ")";

    private static final String drop_table = "drop table trailXplorerData";

    public void delete() {
        SQLiteDatabase sdb = this.getWritableDatabase();
        sdb.delete("trailXplorerData", null, null);
    }
}
