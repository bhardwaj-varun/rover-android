package com.android.rover;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by root on 18/4/17.
 */

public class ConnectivityReceiver extends BroadcastReceiver {
    public static ConnectivityReceiverListener connectivityReceiverListener;

    public interface ConnectivityReceiverListener{
        void onNetworkConnectionChanged(boolean isConnected);
    }
    public ConnectivityReceiver()
    {
        super();
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork =cm.getActiveNetworkInfo();
        boolean isConnected =activeNetwork !=null && activeNetwork.isConnectedOrConnecting();

        if(connectivityReceiverListener !=null){
            connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
        }
    }
    public  static boolean isConnected(){
        ConnectivityManager cm=(ConnectivityManager) ConnectionApplication
                .getInstance()
                .getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork=cm.getActiveNetworkInfo();
        return activeNetwork!=null && activeNetwork.isConnectedOrConnecting();
    }
}
