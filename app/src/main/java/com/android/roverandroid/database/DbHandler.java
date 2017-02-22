package com.android.roverandroid.database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.TextView;

import com.android.roverandroid.database.LocationContract.LocationEntry;
/**
 * Class DbHandler handles CRUD operations
 * Extends Activity Class to obtain ApplicationContext
 * Created by root on 21/2/17.
 */

public class DbHandler extends  Activity{

    private LocationDbHelper locationDbHelper;
    private Context context;
    public DbHandler(Context context){
        this.context=context;
    };

    public void insertCurrentLocation(double latitude,double longitude,int accuracy){
        locationDbHelper= new LocationDbHelper(context);
        SQLiteDatabase db=locationDbHelper.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(LocationEntry.COLUMN_LAT,latitude);
        values.put(LocationEntry.COLUMN_LONG,longitude);
        values.put(LocationEntry.COLUMN_ACCURACY,accuracy);
        try{
        long rowno=db.insert(LocationEntry.TABLE_NAME,null,values);
        Log.e("DbHelper","Row no : " +rowno);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.close();
        }
    }
    public void getAllLocations(){
        locationDbHelper= new LocationDbHelper(context);
        SQLiteDatabase db= locationDbHelper.getReadableDatabase();
        String SELECT_ALL_ROWS="SELECT * FROM "+LocationEntry.TABLE_NAME+";";
        Cursor cursor = db.rawQuery(SELECT_ALL_ROWS, null);
        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            int noOfRows=cursor.getCount();
            Log.e("Cursor","No of Rows : "+noOfRows);
            cursor.moveToFirst();
            for(int i=0;i<cursor.getCount();i++, cursor.moveToNext()){
                int _id=cursor.getInt(cursor.getColumnIndex(LocationEntry._ID));
                double latitude=cursor.getDouble(cursor.getColumnIndex(LocationEntry.COLUMN_LAT));
                double longitude=cursor.getDouble(cursor.getColumnIndex(LocationEntry.COLUMN_LONG));
                int accuracy=cursor.getInt(cursor.getColumnIndex(LocationEntry.COLUMN_ACCURACY));
                Log.e("row id : "+ _id," Lat: "+latitude+" Long: "+longitude+" Accuracy :"+accuracy);
            }

        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
            db.close();
        }

    }
    public void runQuery(){
        locationDbHelper= new LocationDbHelper(context);
        SQLiteDatabase db= locationDbHelper.getReadableDatabase();
        try{

        String query="delete  from " + LocationEntry.TABLE_NAME + " ;";
        db.execSQL(query);}
        catch (Exception e){
            e.getMessage();
        }finally {
            db.close();
        }
    }

}
