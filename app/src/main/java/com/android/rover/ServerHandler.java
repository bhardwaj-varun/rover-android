package com.android.rover;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by root on 19/4/17.
 */

public class ServerHandler implements ServerUrls {

    JsonRequestHandler jsonRequestHandler;
    String TAG="ServerHandler";
    public Integer getLastID(){
        jsonRequestHandler= new JsonRequestHandler();
        JSONObject jsonObject;

        jsonObject=jsonRequestHandler.jsonObjectFromServer(ServerUrls.lastID,null,"GET");
        Log.e(TAG,"Recieved String is "+ jsonObject.toString());
        String str =jsonObject.toString();
        Log.e(TAG,"returned integer is "+str.substring(str.indexOf("{\"id\":")+6,str.indexOf(",\"latitude\"")));
        return Integer.valueOf(str.substring(str.indexOf("{\"id\":")+6,str.indexOf(",\"latitude\"")));
    }
}
