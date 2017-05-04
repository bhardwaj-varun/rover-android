package com.android.rover;

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Objects;

/**
 * Created by root on 16/4/17.
 */

public class JsonRequestHandler {

    static HttpURLConnection httpURLConnection;
    private int httpURLConnectionResponseCode;
    private String TAG="JsonRequestHandler";
    private String string;

    public String jsonStringFromServer(String urlForServer, ContentValues params, String method) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL url = new URL(urlForServer);
            httpURLConnection = (HttpURLConnection) url.openConnection();


        } catch (Exception e) {
            e.printStackTrace();
        }
        try{
            httpURLConnection.setRequestMethod(method);
            httpURLConnection.setReadTimeout(30000);//in millisecond
            httpURLConnection.setConnectTimeout(30000);

            if(params!=null) {

                httpURLConnection.setDoOutput(true);

            }
            httpURLConnection.connect();

        }catch(Exception e){e.printStackTrace();
        }
        try{
            httpURLConnectionResponseCode =httpURLConnection.getResponseCode();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            while((string=bufferedReader.readLine())!=null){
                stringBuilder.append(string+"\n");
            }
            bufferedReader.close();
            Log.e(TAG,"Json Object from Server"+ stringBuilder.toString());

        }catch (Exception e){e.printStackTrace();}
        finally {
            if(httpURLConnection!=null)
                httpURLConnection.disconnect();
        }
        return stringBuilder.toString();
    }

    public JSONObject jsonObjectFromServer(String urlForServer, ContentValues params, String method){
        JSONObject jsonObject=null;
        try {
          jsonObject = new JSONObject(jsonStringFromServer(urlForServer, params, method));
        }catch(Exception e){
            e.printStackTrace();
        }
        return jsonObject;
    }

    public Integer getResponseOnly(String urlForServer, String params, String method){
        DataOutputStream dataOutputStream;
        try {
            URL url = new URL(urlForServer);
            httpURLConnection = (HttpURLConnection) url.openConnection();
        }catch(Exception e){
            e.printStackTrace();
        }
        try{
            httpURLConnection.setRequestMethod(method);
            httpURLConnection.setReadTimeout(10000);//in millisecond
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();
            if(params!=null) {
                dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
                dataOutputStream.writeBytes(params);
            }
        }catch(Exception e){e.printStackTrace();
        }
        try{
            httpURLConnectionResponseCode =httpURLConnection.getResponseCode();
            Log.e("Response  code : ",String.valueOf(httpURLConnectionResponseCode));
        }catch (Exception e){e.printStackTrace();}
        finally {
            if(httpURLConnection!=null)
                httpURLConnection.disconnect();
        }

        return httpURLConnectionResponseCode;
    }
}
