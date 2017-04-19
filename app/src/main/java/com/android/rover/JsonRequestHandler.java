package com.android.rover;

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by root on 16/4/17.
 */

public class JsonRequestHandler {

    static HttpURLConnection httpURLConnection;
    private int httpURLConnectionResponseCode;
    private String TAG="JsonRequestHandler";
    private String string;
    public JSONObject jsonObjectFromServer(String urlForServer, ContentValues params, String method) {
        StringBuilder stringBuilder = new StringBuilder();
        JSONObject jsonObject = null;
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
            jsonObject= new JSONObject(stringBuilder.toString());

        }catch (Exception e){e.printStackTrace();}
        finally {
            if(httpURLConnection!=null)
                httpURLConnection.disconnect();
        }
        return jsonObject;
    }

}
