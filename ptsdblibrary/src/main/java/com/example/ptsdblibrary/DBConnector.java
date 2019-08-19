package com.example.ptsdblibrary;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.WindowManager;


import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DBConnector extends AsyncTask<String, Void, Void> {

    private static final String TAG = "DBConnector";



    private double lat;
    private double lng;
    private GoogleMap mMap;

    private JSONObject jo;
    private static final float DEFAULT_ZOOM = 16.5f;
    private ResultSet rs;

    private PointProfileBean pointProfileBean = new PointProfileBean();

    private ArrayList<PointProfileBean> additems;


    public DBConnector() {

    }

    @Override
    protected Void doInBackground(String... strings) {



        dbConfigPHP("http://10.0.2.2/afnan/fetchPointdet.php");
        return null;
    }

    public void checkConnection(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://192.168.1.102:3306/infiblve_pts","infiblve","afnan1234");

            Statement stmt = con.createStatement();

            if(stmt.isClosed()){
                Log.d(TAG, "Connection is Closed.");
            }else{
                Log.d(TAG, "Connection is Open.");

            }

            rs = stmt.executeQuery("select current_lat from infiblve_pts.points_profiles where reverse_mode = 0");

            while(rs.next()){
                lat = rs.getDouble("current_lat");
                Log.d(TAG, "Drivers Latitude value :"+lat);

            }





            stmt.close();
            con.close();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }









    public void dbConfigPHP(String link){

        try {
            URL url = new URL(link);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpClient client = new DefaultHttpClient();
        HttpGet req = new HttpGet();
        try {
            req.setURI(new URI(link));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        try {
            HttpResponse res = client.execute(req);
            BufferedReader in = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));

            String out = in.readLine();

            JSONArray ja = new JSONArray(out);

            for(int i=0 ; i < ja.length(); i++){

                jo = ja.getJSONObject(i);
                lat = jo.getDouble("current_lat");
                lng = jo.getDouble("current_lng");

               pointProfileBean.setLng(lng);

               pointProfileBean.setLat(lat);

                Log.d(TAG, "current_lng  :"+pointProfileBean.getLng()+"\n"+"current_lat :"+pointProfileBean.getLat());

            }

            moveCamera(new LatLng(pointProfileBean.getLat(), pointProfileBean.getLng()), DEFAULT_ZOOM, "Driver Location");

            Log.d(TAG, "current_lng  :"+pointProfileBean.getLng()+"\n"+"current_lat :"+pointProfileBean.getLat());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private void moveCamera(LatLng latLng, float defaultZoom, String my_location) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, defaultZoom));

        if (!my_location.equals("Driver Location")) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(my_location);
            mMap.addMarker(options);
        }

//        hideSoftKeyboard();
    }



   /* private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }*/





}
