package com.android.rover.database;

import android.provider.BaseColumns;

/**
 * Class contains Table name and column names as constants
 * Created by root on 21/2/17.
 */
public final class LocationContract {
    /**
     * Private constructor as it's final class
     */
    private LocationContract(){};
    /**
     * Subclass implementing BaseColumns Interface for ID
     * Contains Constants
     */
    public final static class LocationEntry implements BaseColumns{

        public final static String TABLE_NAME="location";   //table name
        public final static String _ID=BaseColumns._ID;     //column id
        public final static String COLUMN_LAT="latitude";   //column latitude
        public final static String COLUMN_LONG="longitude"; //column longitude
        public final static String COLUMN_ACCURACY="accuracy"; //column accuracy
        public final static String COLUMN_DATETIME="dateTime"; //column dateTIme
    }
}
