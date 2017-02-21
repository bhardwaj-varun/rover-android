package com.android.roverandroid.database;

import android.app.Activity;

/**
 * Created by root on 21/2/17.
 */

public class DbHandler extends Activity {
    private LocationDbHelper locationDbHelper;
    public DbHandler(){
        locationDbHelper= new LocationDbHelper(getApplicationContext());

    };
    public void insertCurrentLocation(){
        

    }

}
