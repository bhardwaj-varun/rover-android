package com.android.rover;


import android.content.Context;
import android.util.Log;

/**
 * Created by root on 20/4/17.
 */

public class LocationLoader extends android.support.v4.content.AsyncTaskLoader{
    private ServerHandler serverHandler;
    private int id;
    private String params;
    public LocationLoader(Context context, int id,String params) {
        super(context);
        this.id=id;
        this.params=params;
    }

    @Override
    public Integer loadInBackground() {
        if(id==0) {
            serverHandler = new ServerHandler();
            Integer data = serverHandler.getLastID();
            return data;
        }
        else if(id==1){//id==1 for post requet
            serverHandler= new ServerHandler();
            Log.e("Json data for server : " , params);

        }
        return 0;
    }
}
