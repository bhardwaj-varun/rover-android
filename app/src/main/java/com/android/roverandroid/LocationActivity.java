package com.android.roverandroid;

import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        SensorEventListener {

    private final static String TAG = "LocationActivityTAG";
    private TextView tvLocation,tvX,tvY,tvZ;
    private SwitchCompat switchCompatButton;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Sensor mySensor;
    private SensorManager SM;
    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    boolean isMoving=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        //creating an object of GoogleApiCLient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        //Create our Sensor
        SM=(SensorManager)getSystemService(SENSOR_SERVICE);
        //Accelerometer Sensor
        mySensor=SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //defining textview and toggle Button
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        tvX=(TextView) findViewById(R.id.tvX);
        tvY=(TextView) findViewById(R.id.tvY);
        tvZ=(TextView) findViewById(R.id.tvZ);
        switchCompatButton = (SwitchCompat) findViewById(R.id.switchCompatButton);
        //toggle button on CheckedChangeListener
        switchCompatButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    //connect google api client
                    mGoogleApiClient.connect();
                } else {
                    //disconnect google api client
                    mGoogleApiClient.disconnect();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //connect google api
    }

    @Override
    protected void onStop() {
        super.onStop();
        //disconnect google api
    }
    @Override
    protected void onResume(){
        super.onResume();
        //Register Sensor Listener
        SM.registerListener(this,mySensor,SensorManager.SENSOR_DELAY_FASTEST);
    }
    @Override
    protected void onPause(){
        super.onPause();
        //unregister Senser Listener
        SM.unregisterListener(this);
    }

    /**
     * This Method handles the event when GoogleApiClient is connected
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); //update location every second

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
        Log.e(TAG,"GoogleApiClient connection has been suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG,"GoogleApiClient connection has failed");
    }

    /**
     * When location change is detected
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG,location.toString());
        tvLocation.setText(location.toString());
    }

    /**
     * Handles event when accelerometer value is changed
     * @param event
     */

    @Override
    public void onSensorChanged(SensorEvent event) {
        tvX.setText("X : "+String.valueOf(event.values[0]));
        tvY.setText("Y : "+String.valueOf(event.values[1]));
        tvZ.setText("Z : "+String.valueOf(event.values[2]));
        Log.e(TAG,"X : "+ event.values[0]+" Y : "+event.values[1]+" Z : "+event.values[2]);
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            mGravity = event.values.clone();
            // Shake detection
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float)Math.sqrt(x * x + y * y + z*z);
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            mAccel=(float)round(mAccel,3);

            if(Math.abs(mAccel) > 1){
                //avgSpeedText.setText(Float.toString(Math.abs(mAccel)));
                isMoving=true;

            }
            else if(Math.abs(mAccel)<0.003 && Math.abs(mAccel)>0){
                isMoving=false;
                // avgSpeedText.setText(Float.toString(Math.abs(mAccel)));
            }

        }
        Log.e("Moving : "," "+isMoving);

    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //Method for accelerometer accuracyChanged event .Not in Use
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
}
