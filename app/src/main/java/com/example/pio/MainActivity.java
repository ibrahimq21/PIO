package com.example.pio;

import android.content.Intent;

import android.os.Bundle;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.libraries.places.compat.Places;


//import com.google.android.libraries.places.api.Places;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String apikey = getString(R.string.google_maps_key);

        if(apikey.isEmpty()){
            Toast.makeText(this, getString(R.string.error_api_key), Toast.LENGTH_LONG).show();
        }

    /*    if(!Places.isInitialized()){
            Places.initialize(getApplicationContext(), apikey);
        }*/

        init();

    }

    private void init(){
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);
    }



}