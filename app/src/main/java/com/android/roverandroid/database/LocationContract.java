package com.android.roverandroid.database;

import android.provider.BaseColumns;

/**
 * Created by root on 21/2/17.
 */

public final class LocationContract {
    private LocationContract(){};
    public final static class LocationEntry implements BaseColumns{

        public final static String TABLE_NAME="location";
        public final static String _ID=BaseColumns._ID;
        public final static String COLUMN_LAT="latitude";
        public final static String COLUMN_LONG="longitude";
        public final static String COLUMN_ACCURACY="accuracy";



    }

}
