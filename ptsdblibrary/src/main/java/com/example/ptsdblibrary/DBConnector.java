package com.example.ptsdblibrary;


import android.os.AsyncTask;
import android.util.Log;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

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

public class DBConnector extends AsyncTask<String, Void, Void> {

    private static final String TAG = "DBConnector";

    private String lat;
    private String lng;

    private ResultSet rs;


    public DBConnector() {

    }

    @Override
    protected Void doInBackground(String... strings) {

        dbConfigPHP("");
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
                lat = rs.getString("current_lat");
                Log.d(TAG, "Drivers Latitude value :"+lat);

            }
            new PointProfilePOJOClass(Double.parseDouble(lat),Double.parseDouble(lng));




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
        } catch (IOException e) {
            e.printStackTrace();
        }



    }




  /*  @Override
    protected String doInBackground(String... strings) {


        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/infiblve_pts","infiblve","afnan1234");

            Statement stmt = con.createStatement();

            if(stmt.isClosed()){
                Log.d(TAG, "Connection is Closed.");
            }else{
                Log.d(TAG, "Connection is Open.");

            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return null;
    }*/
}
