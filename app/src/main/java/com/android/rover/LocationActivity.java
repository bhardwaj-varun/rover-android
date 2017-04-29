package com.android.rover;

import android.support.v4.app.LoaderManager;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.rover.database.DbHandler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        SensorEventListener,
        ConnectivityReceiver.ConnectivityReceiverListener,
        LoaderManager.LoaderCallbacks<Integer>{

    private final static String TAG = "LocationActivityTAG";
    String jsonString;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private TextView tvLatitude,tvLongitude,tvAccuracy,tvAltitude,tvX,tvY,tvZ,tvIsMoving;
    private Sensor mySensor;
    private SensorManager SM;
    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    boolean isMoving=false;
    private double lastLatitude,latitude,lastLongitude,longitude,altitude,vel,bear,et;
    private int accuracy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        tvLatitude = (TextView) findViewById(R.id.tvLatitude) ;
        tvLongitude = (TextView) findViewById(R.id.tvLongitude);
        tvAccuracy = (TextView) findViewById(R.id.tvAccuracy);
        tvAltitude = (TextView) findViewById(R.id.tvAltitude);
        tvX=(TextView) findViewById(R.id.tvX);
        tvY=(TextView) findViewById(R.id.tvY);
        tvZ=(TextView) findViewById(R.id.tvZ);
        tvIsMoving = (TextView) findViewById(R.id.tvIsMoving);
        buildGoogleApiClient();
        //Create our Sensor
        SM=(SensorManager)getSystemService(SENSOR_SERVICE);
        //Accelerometer Sensor
        mySensor=SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        DbHandler dbHandler=new DbHandler(this);
        dbHandler.getAllLocations();
        getSupportLoaderManager().initLoader(0,null,this).forceLoad();

        //(android.support.v4.app.LoaderManager.LoaderCallbacks<Integer>)

    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        Log.e(TAG, "Google Api On start Connected");
        Toast.makeText(this, "Google Api On start Connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        Log.e(TAG, "Google Api On stop Connection disconnected");
        Toast.makeText(this, "Google Api On stop Connection disconnected", Toast.LENGTH_SHORT).show();
        super.onStop();
    }
    @Override
    protected void onResume(){
        super.onResume();
        //Register Sensor Listener
        SM.registerListener(this,mySensor,SensorManager.SENSOR_DELAY_FASTEST);
        ConnectionApplication.getInstance().setConnectivityListener(this);
    }
    @Override
    protected void onPause(){
        //unregister Senser Listener
        SM.unregisterListener(this);
        super.onPause();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(100);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG,"Google Api Connection Suspended");
        Toast.makeText(this,"Google Api Connection Suspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG,"Google Api Connection Failed");
        Toast.makeText(this,"Google Api Connection Failed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG,location.toString());
        lastLatitude=latitude;
        lastLongitude=longitude;
        latitude=location.getLatitude();
        longitude=location.getLongitude();
        altitude=location.getAltitude();
        accuracy=(int)location.getAccuracy();
        et=location.getElapsedRealtimeNanos();
        vel=location.getSpeed();
        bear=location.getBearing();
        if(location.getExtras()!=null)
            Log.e(TAG,"Location Extras" +location.getExtras().toString());
        tvLatitude.setText(String.valueOf(latitude));
        tvLongitude.setText(String.valueOf(longitude));
        tvAccuracy.setText(String.valueOf(accuracy));
        tvAltitude.setText(String.valueOf(altitude));

        if(isMoving){
            if(isLocationChanged()){
                Log.e(TAG,"Location changed : "+location.toString());
                DbHandler dbHandler=new DbHandler(this);
                dbHandler.insertCurrentLocation(latitude,longitude,accuracy);
                dbHandler.getAllLocations();
            }
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        tvX.setText("X : "+String.valueOf(event.values[0]));
        tvY.setText("Y : "+String.valueOf(event.values[1]));
        tvZ.setText("Z : "+String.valueOf(event.values[2]));
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            mGravity =event.values.clone();
            //shake detection
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float)Math.sqrt(x*x+y*y+z*z);
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel*0.9f + delta;
            mAccel = (float)round(mAccel,3);
            if(Math.abs(mAccel) > 1){
                isMoving = true;
            }
            else if(Math.abs(mAccel) < 0.003 && Math.abs(mAccel)>0){
                isMoving = false;
            }
        }
        tvIsMoving.setText("IsMoving:"+isMoving);
    }

    /**
     *Static method for rounding
     * @param value
     * @param places
     * @return
     */
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    /**
     * This method finds out if the location is actually changed
     * by taking abs differences of latitude and longitude with there old values
     * @return boolean
     */
    public boolean isLocationChanged(){

        if(Math.abs(latitude-lastLatitude)>0 || Math.abs(longitude-lastLongitude) >0)
            return true;
        else
            return false;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        Toast.makeText(this,"Connected : "+isConnected,Toast.LENGTH_SHORT).show();
        Log.e(TAG,"Connection Status : "+isConnected);
    }


    @Override
    public  android.support.v4.content.Loader<Integer> onCreateLoader(int id, Bundle args) {

        switch (id){
            case 0: return new LocationLoader(this,id,null);
            case 1: return new LocationLoader(this,id,jsonString);
        }

        return null;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Integer> loader, Integer data) {

        Log.e(TAG,"Recieved Data is "+ data.toString()+"Loader Id : "+loader.getId());
        if(loader.getId()==0) {
            DbHandler dbHandler = new DbHandler(this);
             jsonString=dbHandler.fetchingNewData(data);
            getSupportLoaderManager().initLoader(1,null,this).forceLoad();
        }
        else if(loader.getId()==1){

        }

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Integer> loader) {
        Log.e(TAG,"Reset Called ");
    }


}
