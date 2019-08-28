package com.example.pio;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class PointProfileTask extends AsyncTask<String,Void, String> {

    private static final String TAG = "PointProfileTask";

    private Context mContext;

    private PointProfilehelper helper;

    public PointProfileTask(Context mContext) {
        this.mContext = mContext;
        helper = (PointProfilehelper) mContext;

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        helper.setDriverId(s);


    }

    @Override
    protected String doInBackground(String... strings) {
        String driverid ;
        try {
            URL url = new URL("http://10.0.2.2/afnan/fetchPointdet.php");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            int statuscode = con.getResponseCode();
            if (statuscode == HttpURLConnection.HTTP_OK) {

                HttpClient client = new DefaultHttpClient();
                HttpGet req = new HttpGet();
                req.setURI(new URI("http://10.0.2.2/afnan/fetchPointdet.php"));

                HttpResponse res = client.execute(req);
                BufferedReader in = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));

                String out = in.readLine();

                JSONArray ja = new JSONArray(out);

                for (int i = 0; i < ja.length(); i++) {

                    JSONObject jo = ja.getJSONObject(i);

                    driverid = jo.getString("vehicle_id");

                    return driverid;






                }


            }

        } catch(MalformedURLException e){
            Log.d(TAG, "MalformedURLException : "+e.getMessage());
        }catch(IOException e){
            Log.d(TAG, "IOException : "+e.getMessage());
        }catch(URISyntaxException e){
            Log.d(TAG, "URISyntaxException : "+e.getMessage());
        }catch(JSONException e){
            Log.d(TAG, "JSONException : "+e.getMessage());
        }
        return null;

    }
}