package com.example.ptsdblibrary;


import android.os.AsyncTask;
import android.util.Log;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnector extends AsyncTask<String, Void, Void> {

    private static final String TAG = "DBConnector";


    public DBConnector() {

    }

    @Override
    protected Void doInBackground(String... strings) {

        checkConnection();
        return null;
    }

    public void checkConnection(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://192.168.0.101:3306/infiblve_pts","infiblve","afnan1234");

            Statement stmt = con.createStatement();

            if(stmt.isClosed()){
                Log.d(TAG, "Connection is Closed.");
            }else{
                Log.d(TAG, "Connection is Open.");

            }

            stmt.close();
            con.close();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
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
