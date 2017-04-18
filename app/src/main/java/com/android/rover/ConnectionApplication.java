package com.android.rover;

import android.app.Application;

/**
 * Created by root on 18/4/17.
 */

public class ConnectionApplication extends Application {
    private static ConnectionApplication mInstance;

    @Override
    public void onCreate(){
        super.onCreate();
        mInstance = this;
    }
    public static synchronized ConnectionApplication getInstance(){
        return mInstance;
    }
    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener){
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
