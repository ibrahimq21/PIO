package com.example.pio;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import com.example.ptsdblibrary.PointProfileBean;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;


import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.navigation.NavigationView;

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


public class MainActivity extends AppCompatActivity implements GeoTask.Geo, NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback ,GoogleMap.OnMarkerClickListener, TaskLoadedCallback,GoogleMap.OnInfoWindowClickListener{

    private static final String TAG = "MainActivity";



    private JSONObject jo;

    private double lat;
    private double lng;

    private String duration, distance;


    private PointProfileBean pointProfileBean = new PointProfileBean();

    private Marker checkPostStart;
    private Marker checkPostEnd;
    private Marker busLocation;
    private Marker sw_bus_point;


    private Polyline mPolyline;

    private static final String FINE_LOCATION = permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 17f;


    //widgets

    private ImageView mGps;
    private ImageView mBuss;

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient = null;


    protected int getLayoutId() {
        return R.layout.activity_main;
    }



    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        getLocationPermission();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);


        Toolbar toolbar = findViewById(R.id.toolbar);
        SearchView searchBar = findViewById(R.id.searchbar);



        toolbar.setNavigationContentDescription(R.layout.search_bar);

        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                if(query.equals("1101")){






                  getRouteK();

                }



                Log.d(TAG,query);


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });




//        hideSoftKeyboard();




    }






    private void getRouteK(){


        sw_bus_point = mMap.addMarker(new MarkerOptions().position(CheckPostData.CHECK_POST_SW).title("SW Bus Point"));
        sw_bus_point.setSnippet("Click here");

        moveCamera(sw_bus_point.getPosition(),15f,"");

        checkPostStart = mMap.addMarker(new MarkerOptions().position(CheckPostData.CHECK_POST_START).title("Check post Start"));

        BitmapDescriptor checkPoststart_ic = BitmapDescriptorFactory.fromResource(R.drawable.icons1);

        checkPostStart.setIcon(checkPoststart_ic);

        checkPostStart.setVisible(true);
        checkPostStart.setSnippet("Click here");



        checkPostEnd = mMap.addMarker(new MarkerOptions().position(CheckPostData.CHECK_POST_END).title("Check Post End"));

        BitmapDescriptor checkPostEnd_ic = BitmapDescriptorFactory.fromResource(R.drawable.icons2);

        checkPostEnd.setIcon(checkPostEnd_ic);

        checkPostEnd.setVisible(true);

        checkPostEnd.setTag(0);

        checkPostEnd.setSnippet("Click here");

        if (mPolyline != null)
            mPolyline.remove();
        mPolyline = mMap.addPolyline(new PolylineOptions()
                .add(   CheckPostData.CHECK_POST_END,
                        CheckPostData.CHECK_POST_FACULTY_ROAD_22,
                        CheckPostData.CHECK_POST_FACULTY_ROAD_23,
                        CheckPostData.CHECK_POST_FACULTY_ROAD,
                        CheckPostData.CHECK_POST_FACULTY_ROAD_1,
                        CheckPostData.CHECK_POST_FACULTY_ROAD_2,
                        CheckPostData.CHECK_POST_FACULTY_ROAD_3,
                        CheckPostData.CHECK_POST_FACULTY_ROAD_4,
                        CheckPostData.CHECK_POST_FACULTY_ROAD_5,
                        CheckPostData.CHECK_POST_FACULTY_ROAD_6,
                        CheckPostData.CHECK_POST_FACULTY_ROAD_7,
                        CheckPostData.CHECK_POST_FACULTY_ROAD_8,
                        CheckPostData.CHECK_POST_FACULTY_ROAD_9,
                        CheckPostData.CHECK_POST_FACULTY_ROAD_10,
                        CheckPostData.CHECK_POST_FACULTY_ROAD_11,
                        CheckPostData.CHECK_POST_FACULTY_ROAD_12,
                        CheckPostData.CHECK_POST_FACULTY_ROAD_13,
                        CheckPostData.CHECK_POST_FACULTY_ROAD_14,
                        CheckPostData.CHECK_POST_FACULTY_ROAD_15,
                        CheckPostData.CHECK_POST_FACULTY_ROAD_16,
                        CheckPostData.CHECK_POST_FACULTY_ROAD_17,
                        CheckPostData.CHECK_POST_FACULTY_ROAD_18,
                        CheckPostData.CHECK_POST_FACULTY_ROAD_19,
                        CheckPostData.CHECK_POST_FACULTY_ROAD_20,
                        CheckPostData.CHECK_POST_FACULTY_ROAD_21,
                        CheckPostData.CHECK_POST_START));
    }


    private void initMap() {

        Log.d(TAG, "initMap Called");

       ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);


    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {permission.ACCESS_FINE_LOCATION,
                permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(this);
            mGoogleApiClient.disconnect();
        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public static int getResponseCode(String urlString) throws MalformedURLException, IOException {
        URL u = new URL(urlString);
        HttpURLConnection huc = (HttpURLConnection) u.openConnection();
        huc.setRequestMethod("GET");
        huc.connect();
        Log.d(TAG, "Responce Code :"+huc.getResponseCode());
        return huc.getResponseCode();
    }




    private void getDriverLocation() throws MalformedURLException, JSONException, IOException{

        /*This method will get the location of driver by getting the latitute
         and longitude from the database and then put this in moveCamera method*/


        String link = "http://10.0.2.2/afnan/fetchPointdet.php";


        if (getResponseCode(link) != 200) {

            moveCamera(CheckPostData.CHECK_POST_SW,15f,"");
            Toast.makeText(this, "Cannot find Bus location. Try Search route", Toast.LENGTH_SHORT).show();

        }else{
            URL url = new URL(link);
        }




        HttpClient client = new DefaultHttpClient();
        HttpGet req = new HttpGet();
        try {
            req.setURI(new URI(link));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


            HttpResponse res = client.execute(req);
            BufferedReader in = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));

            String out = in.readLine();

            JSONArray ja = new JSONArray(out);

            for (int i = 0; i < ja.length(); i++) {

                jo = ja.getJSONObject(i);
                lat = jo.getDouble("current_lat");
                lng = jo.getDouble("current_lng");

                pointProfileBean.setLng(lng);

                pointProfileBean.setLat(lat);

                //Log.d(TAG, "current_lng  :"+pointProfileBean.getLng()+"\n"+"current_lat :"+pointProfileBean.getLat());

                BitmapDescriptor busIcon = BitmapDescriptorFactory.fromResource(R.drawable.muetbusx1);



                MarkerOptions options = new MarkerOptions().position(new LatLng(pointProfileBean.getLat(), pointProfileBean.getLng())).title("Bus Location").icon(busIcon);




               busLocation = mMap.addMarker(options);
            }

//            Log.d(TAG, "IOException " + e.getMessage());

//            Log.d(TAG, "JSONException " + e.getMessage());


        Log.d(TAG, "getDriverLocation: getting the drivers devices current location Latitude: " + pointProfileBean.getLat() + "\n Longitude: " + pointProfileBean.getLng());



        moveCamera(new LatLng(25.4050,68.2608),16.5f,"Default Location");






        /*Intent intent = new Intent(this, DistanceActivity.class);
        intent.putExtra("point_profile", pointProfileBean);
        startActivity(intent);*/


    }

    /*private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "My Location");

                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MainActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }*/


    private void moveCamera(LatLng latLng, float defaultZoom, String location) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, defaultZoom));









        hideSoftKeyboard();
    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawer_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");

        if(mMap != null){
            return;
        }
        this.mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

        try {
            getDriverLocation();
        } catch (JSONException e) {
            Log.d(TAG, "JSONException: "+e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "IOException: "+e.getMessage());
        }
        mMap.setOnInfoWindowClickListener(this);


    }

    private String getDistanceURl(LatLng origin, LatLng destination){

        String str_origin = "origins=" + origin.latitude + "," + origin.longitude;

//        Destination of route

        String str_dest = "destinations=" + destination.latitude + "," + destination.longitude;

        String unit = "units=metric";

        String parameter = unit+"&"+str_origin + "&" + str_dest;



        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/distancematrix/"+output+"?"+parameter+"&key="+getString(R.string.google_maps_key);
        return url;
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {


//        origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

//        Destination of route

        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

//        Mode

        String mode = "mode=" + directionMode;

        String parameter = str_origin + "&" + str_dest + "&" + mode;

        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameter + "&key=" + getString(R.string.google_maps_key);

        return url;
    }




    @Override
    public boolean onMarkerClick(Marker marker) {

        Log.d(TAG,"onMarkerClick: called");
        if(marker.equals(busLocation)){



            getRouteK();




           /* String url = getUrl(checkPostStart.getPosition(), checkPostEnd.getPosition(),"driving");
            new FetchURL(this).execute(url,"driving");*/

        }
        if(marker.equals(sw_bus_point)){
            new GeoTask(this).execute(getDistanceURl(busLocation.getPosition(), sw_bus_point.getPosition()));
        }
        if(marker.equals(checkPostStart)){
            new GeoTask(this).execute(getDistanceURl(busLocation.getPosition(), checkPostStart.getPosition()));

        }
        if(marker.equals(checkPostEnd)){
            new GeoTask(this).execute(getDistanceURl(busLocation.getPosition(), checkPostEnd.getPosition()));

        }


        return false;
    }



    @Override
    public void onTaskDone(Object... values) {

        if (mPolyline != null)
            mPolyline.remove();
        mPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.d(TAG,"onInfoWindowClick: called");
        if(marker.equals(sw_bus_point)){
            sw_bus_point.setTitle("SW Bus Point");
            new GeoTask(this).execute(getDistanceURl(busLocation.getPosition(), sw_bus_point.getPosition()));
            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));

            marker.showInfoWindow();




        }
        if(marker.equals(checkPostStart)){
            checkPostStart.setTitle("Check Post Start Faculty Road Route");
            new GeoTask(this).execute(getDistanceURl(busLocation.getPosition(), checkPostStart.getPosition()));


            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));


            marker.showInfoWindow();

        }
        if(marker.equals(checkPostEnd)){
            checkPostEnd.setTitle("Check Post End Faculty Road Route");

            new GeoTask(this).execute(getDistanceURl(busLocation.getPosition(), checkPostEnd.getPosition()));
            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));


            marker.showInfoWindow();
        }






    }

    @Override
    public void setDouble(String min) {
        String res[]=min.split(",");
        duration = res[0];
        distance = res[1];

        sw_bus_point.setSnippet("duration : "+duration+"\ndistance: "+distance);
        checkPostStart.setSnippet("duration : "+duration+"\ndistance: "+distance);
        checkPostEnd.setSnippet("duration : "+duration+"\ndistance: "+distance);

        Log.d(TAG, "duration : "+duration+"\ndistance: "+distance);

    }
}