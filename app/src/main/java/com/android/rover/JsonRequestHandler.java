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

    public String convertPairValueToString(ContentValues params){
        StringBuilder stringBuilder= new StringBuilder();
        boolean firstPair=true;

        for(Map.Entry<String, Object> entry : params.valueSet()) {
            if (firstPair) {
                firstPair = false;
            }
            else
                stringBuilder.append("&");
            try{
                stringBuilder.append(URLEncoder.encode(entry.getKey(),"UTF-8"));//name
                stringBuilder.append("=");
                stringBuilder.append(URLEncoder.encode(entry.getValue().toString(),"UTF-8")); //value

            }
            catch (Exception exception){
                exception.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }



    public String jsonStringFromServer(String urlForServer, ContentValues params, String method) {
        StringBuilder stringBuilder = new StringBuilder();
        String paramStringSentToServer;
        DataOutputStream dataOutputStream;
        try {
            URL url = new URL(urlForServer);
            httpURLConnection = (HttpURLConnection) url.openConnection();


        } catch (Exception e) {
            e.printStackTrace();
        }
        try{
            httpURLConnection.setRequestMethod(method);
            httpURLConnection.setReadTimeout(10000);//in millisecond
            httpURLConnection.setConnectTimeout(15000);

            if(params!=null) {
                // paramStringSentToServer=
                httpURLConnection.setDoOutput(true);

            }
            httpURLConnection.connect();
            if(params!=null) {
                dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
                //dataOutputStream.writeBytes(paramStringSentToServer);
            }
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
    public JSONArray jsonArrayFromServer(String urlForServer, ContentValues params, String method){
        JSONArray jsonArray=null;
        try {
            jsonArray = new JSONArray(jsonStringFromServer(urlForServer, params, method));
        }catch(Exception e){
            e.printStackTrace();
        }
        return jsonArray;
    }
    public Integer getResponseOnly(String urlForServer, String params, String method){
        JSONArray jsonArray=null;
        StringBuilder stringBuilder = new StringBuilder();
        String paramStringSentToServer;
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
            paramStringSentToServer=params;
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();
            if(params!=null) {
                dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
                dataOutputStream.writeBytes(paramStringSentToServer);
            }
        }catch(Exception e){e.printStackTrace();
        }
        try{
            httpURLConnectionResponseCode =httpURLConnection.getResponseCode();

        }catch (Exception e){e.printStackTrace();}
        finally {
            if(httpURLConnection!=null)
                httpURLConnection.disconnect();
        }

        return httpURLConnectionResponseCode;
    }
}
