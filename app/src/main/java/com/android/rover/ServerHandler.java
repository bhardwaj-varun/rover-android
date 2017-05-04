package com.android.rover;

import android.content.ContentValues;
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
        try{
        if(jsonObject.has("id"))
         return Integer.valueOf(jsonObject.getInt("id"));

        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }
    public Integer getPostStatus(String params){
        //String string = " [{\"latitude\": 25.34,\"longitude\": 36.65,\"paramStringSentToServer=params;accuracy\": 16,\"dateTime\": \"2017-04-19 11:15:56\"}]";

        jsonRequestHandler=new JsonRequestHandler();
        return jsonRequestHandler.getResponseOnly(ServerUrls.all,params,"POST");

    }
}
