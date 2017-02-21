package com.android.roverandroid.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.android.roverandroid.database.LocationContract.LocationEntry;

/**
 * LocationDbHelper Class Extending SQLiteOpenHelper
 * for Database connection management
 * Created by root on 21/2/17.
 */

public class LocationDbHelper extends SQLiteOpenHelper {
    /**
     *  Name of the database file
     */
    private static final String DATABASE_NAME="location.db";
    /**
     * Database version. If you change the database scheme,you must incement the database version.
     */
    private static final  int DATABASE_VERSION=1;

    /**
     * Construct in format of super class constructor
     * Needs
     * @param context
     * for handling connection
     */
    public LocationDbHelper(Context context){
        /**
         * calling super class constructor with
         * application context,Database name,CursorFactory,Database version
         */
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    };
    @Override
    public void onCreate(SQLiteDatabase db) {
        //create an SQL statement in String Form
        String SQL_CREATE_LOCATION_TABLE="CREATE TABLE "+ LocationEntry.TABLE_NAME + " ( "
                +LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + LocationEntry.COLUMN_LAT +" DOUBLE PRECISION NOT NULL , "
                + LocationEntry.COLUMN_LONG + " DOUBLE PRECISION NOT NULL , "
                + LocationEntry.COLUMN_ACCURACY +" INTEGER NOT NULL ); ";

        //executing create table statement on db
        db.execSQL(SQL_CREATE_LOCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        /**Code for database handling when app upgrades
         * we can handle versions of database here
         */

    }
}
