package com.android.rover;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
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

import java.util.Timer;
import java.util.TimerTask;

public class LocationActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        SensorEventListener,
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
    private boolean isConnected=false;
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
        //get sensor services
        SM=(SensorManager)getSystemService(SENSOR_SERVICE);
        //Accelerometer Sensor
        mySensor=SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SM.registerListener(this,mySensor,SensorManager.SENSOR_DELAY_FASTEST);
        //Database handler
        DbHandler dbHandler=new DbHandler(this);
        dbHandler.getAllLocations();
        //register broadcast receiver
        this.registerReceiver(this.broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        //calling asyncloader repeatedly
        callLoaders();
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        Log.e(TAG, "Google Api On create Connected");
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity= intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,false);
            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            boolean isFailOver = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER,false);

            ConnectivityManager connectivityManager =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo currentNetworkInfo = connectivityManager.getActiveNetworkInfo();
            NetworkInfo otherNetworkInfo = (NetworkInfo)intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
            if(currentNetworkInfo!=null && currentNetworkInfo.isConnected()){
                isConnected=true;
                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Network connected");
            }else{
                isConnected=false;
                Log.e(TAG, "Network disconnected");
                Toast.makeText(getApplicationContext(), "Not connected "+reason, Toast.LENGTH_LONG).show();
            }
        }
    };

    public void callLoaders(){
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doServerwork =  new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            if(isConnected){
                                getSupportLoaderManager().restartLoader(0,null,LocationActivity.this).forceLoad();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        timer.schedule(doServerwork,0,20000); //every 20-secs
    }
    @Override
    protected void onDestroy(){
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        Log.e(TAG, "Google Api On destroy Connection disconnected");
        this.unregisterReceiver(broadcastReceiver);
        SM.unregisterListener(this);
        super.onDestroy();
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
        tvLatitude.setText(String.valueOf(latitude));
        tvLongitude.setText(String.valueOf(longitude));
        tvAccuracy.setText(String.valueOf(accuracy));
        tvAltitude.setText(String.valueOf(altitude));

        if(isMoving){
            if(isLocationChanged()){
                Log.e(TAG,"Location changed : "+location.toString());
                if(accuracy <= 16.0) { //accuracy less then equal to  16 meters
                    Log.e("Accuracy <=  16 :", String.valueOf(accuracy));
                    DbHandler dbHandler = new DbHandler(this);
                    dbHandler.insertCurrentLocation(latitude, longitude, accuracy);
                    dbHandler.getAllLocations();
                }
                else{
                    Log.e("Accuracy > 16 :", String.valueOf(accuracy));
                }

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
    public  android.support.v4.content.Loader<Integer> onCreateLoader(int id, Bundle args) {
        switch (id){
            case 0: return new LocationLoader(this,id,null);
            case 1: return new LocationLoader(this,id,jsonString);
        }
        return null;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Integer> loader, Integer data) {
        Log.e(TAG,"Received Data is "+ data.toString()+"Loader Id : "+loader.getId());
        if(loader.getId()==0) {
            DbHandler dbHandler = new DbHandler(this);
            jsonString=dbHandler.fetchingNewData(data);
            Log.e(TAG,"Length = "+jsonString.length());
            Log.e("Json data for server : " , jsonString);
            if(jsonString.length()>2) //if jsonstring is not empty array i.e "[]"
                getSupportLoaderManager().restartLoader(1,null,this).forceLoad();
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Integer> loader) {
        Log.e(TAG,"Reset Called ");
    }

}
