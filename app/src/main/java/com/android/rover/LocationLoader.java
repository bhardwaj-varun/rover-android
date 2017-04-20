package com.android.rover;


import android.content.Context;

/**
 * Created by root on 20/4/17.
 */

public class LocationLoader extends android.support.v4.content.AsyncTaskLoader{
    ServerHandler serverHandler;
    public LocationLoader(Context context) {
        super(context);
    }

    @Override
    public Integer loadInBackground() {
       serverHandler=new ServerHandler();
        Integer data=serverHandler.getLastID();
        return data ;
    }
}
