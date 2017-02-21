package com.android.roverandroid.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.android.roverandroid.database.LocationContract.LocationEntry;

/**
 * Created by root on 21/2/17.
 */

public class LocationDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="location.db";
    private static final  int DATABASE_VERSION=1;


    public LocationDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    };
    @Override
    public void onCreate(SQLiteDatabase db) {
                //creating table
        String SQL_CREATE_LOCATION_TABLE="CREATE TABLE "+ LocationEntry.TABLE_NAME + " ( "
                +LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + LocationEntry.COLUMN_LAT +" DOUBLE PRECISION NOT NULL , "
                + LocationEntry.COLUMN_LONG + "DOUBLE PRECISION NOT NULL , "
                + LocationEntry.COLUMN_ACCURACY +"INTEGER NOT NULL ";

        db.execSQL(SQL_CREATE_LOCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //not in use now
    }
}
