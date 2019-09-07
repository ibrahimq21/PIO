package com.example.pio;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import com.example.ptsdblibrary.PointProfileBean;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements GeoTask.Geo,
        NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, VehicleLocationActivity.VehicleLocationHelper {

    private static final String TAG = "MainActivity";


    private int driverid;


    private double current_lat;
    private double current_lng;

    private static final int REQUEST_CHECK_SETTINGS = 25;

    private boolean PRE_ALERT_FLAG = true;

    private String duration, distance;

    private Float start_rotation = 0.5f;


    private PointProfileBean pointProfileBean = new PointProfileBean();

    private Marker checkPostStart;
    private Marker checkPostEnd;
    private Marker busLocation;
    private Marker sw_bus_point;

    private LocationRequest mLocationRequest = null;
    private Location mCurrentLocation = null;


    private Polyline mPolyline;

    private List<PointProfileBean> pointProfileData;

    private static final long INTERVAL = 1;
    private static final long FASTEST_INTERVAL = 1;

    private static final String FINE_LOCATION = permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 17f;


    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager manager;


    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        getLocationPermission();


        Toolbar toolbar = findViewById(R.id.toolbar);
        SearchView searchBar = findViewById(R.id.searchbar);


        toolbar.setNavigationContentDescription(R.layout.search_bar);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this::onConnectionFailed)
                .build();

        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        retro();


        findViewById(R.id.ic_gps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPolyline.isVisible()) {
                    mPolyline.remove();
                    sw_bus_point.remove();
                    checkPostEnd.remove();
                    checkPostStart.remove();
                }
            }
        });




        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                if (query.equals(Integer.toString(driverid))) {


                    getRouteK();

                }


                Log.d(TAG, "pointProfileBean.getDriver_id() : " + driverid);
                Log.d(TAG, query);


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


//        hideSoftKeyboard();


    }

    public void retro() {

        Log.d(TAG, "Calling Method : retro()");


        try {
            Retrofit retro = new Retrofit.Builder()
                    .baseUrl(ApiURL.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiService service = retro.create(ApiService.class);
            Call<List<PointProfileBean>> call = service.getPointProfilesDetails();

            call.enqueue(new Callback<List<PointProfileBean>>() {
                @Override
                public void onResponse(Call<List<PointProfileBean>> call, Response<List<PointProfileBean>> response) {


                    List<PointProfileBean> pointProfileData = response.body();

                    for (int i = 0; i < pointProfileData.size(); i++) {


                        Log.d(TAG, "JSON DATA " + pointProfileData.get(i).getRoute());
                        Log.d(TAG, "Vehicle id   " + pointProfileData.get(i).getVehicle_id());

                        driverid = pointProfileData.get(i).getVehicle_id();

                        current_lat = pointProfileData.get(i).getCurrent_lat();
                        current_lng = pointProfileData.get(i).getCurrent_lng();

                        busLocation = mMap.addMarker(new MarkerOptions().title("SW Bus")
                        .position(new LatLng(current_lat, current_lng))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.muetbusx1))
                        );


                    }


                }

                @Override
                public void onFailure(Call<List<PointProfileBean>> call, Throwable t) {
                    Log.d(TAG, "Retrofit2 Error: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "Retrofit2 Exception: " + e.getMessage());

        }


    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    private void getRouteK() {


        sw_bus_point = mMap.addMarker(new MarkerOptions().position(CheckPostData.CHECK_POST_SW).title("SW Bus Point"));
        sw_bus_point.setSnippet("Click here");

        moveCamera(sw_bus_point.getPosition(), 15f, "");

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
                .add(CheckPostData.CHECK_POST_END,
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

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(MainActivity.this);


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


    @Override
    protected void onResume() {
        super.onResume();
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdate();
        }
    }






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

        if (mMap != null) {
            return;
        }
        this.mMap = googleMap;
        mMap.setOnMarkerClickListener(this);


        if (checkSelfPermission(permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        mMap.getUiSettings().setMapToolbarEnabled(false);
        moveCamera(new LatLng(25.4050, 68.2608), 16.5f, "Default Location");


        mMap.setOnInfoWindowClickListener(this);


    }

    private String getDistanceURl(LatLng origin, LatLng destination) {

        String str_origin = "origins=" + origin.latitude + "," + origin.longitude;

//        Destination of route

        String str_dest = "destinations=" + destination.latitude + "," + destination.longitude;

        String unit = "units=metric";

        String parameter = unit + "&" + str_origin + "&" + str_dest;


        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/distancematrix/" + output + "?" + parameter + "&key=" + getString(R.string.google_maps_key);
        return url;
    }


    @Override
    public boolean onMarkerClick(Marker marker) {

        Log.d(TAG, "onMarkerClick: called");
        if (marker.equals(busLocation)) {

            getRouteK();

           /* String url = getUrl(checkPostStart.getPosition(), checkPostEnd.getPosition(),"driving");
            new FetchURL(this).execute(url,"driving");*/

        }
        if (marker.equals(sw_bus_point)) {
            sw_bus_point.setSnippet("Click here");
            new GeoTask(this).execute(getDistanceURl(busLocation.getPosition(), sw_bus_point.getPosition()));
        }
        if (marker.equals(checkPostStart)) {
            checkPostStart.setSnippet("Click here");
            new GeoTask(this).execute(getDistanceURl(busLocation.getPosition(), checkPostStart.getPosition()));

        }
        if (marker.equals(checkPostEnd)) {
            checkPostEnd.setSnippet("Click here");
            new GeoTask(this).execute(getDistanceURl(busLocation.getPosition(), checkPostEnd.getPosition()));

        }


        return false;
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.d(TAG, "onInfoWindowClick: called");
        if (marker.equals(sw_bus_point)) {
            sw_bus_point.setTitle("SW Bus Point");
            new GeoTask(this).execute(getDistanceURl(busLocation.getPosition(), sw_bus_point.getPosition()));
            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));

            sw_bus_point.showInfoWindow();


        }
        if (marker.equals(checkPostStart)) {
            checkPostStart.setTitle("Check Post Start Faculty Road Route");
            new GeoTask(this).execute(getDistanceURl(busLocation.getPosition(), checkPostStart.getPosition()));


            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));


            checkPostStart.showInfoWindow();

        }
        if (marker.equals(checkPostEnd)) {
            checkPostEnd.setTitle("Check Post End Faculty Road Route");

            new GeoTask(this).execute(getDistanceURl(busLocation.getPosition(), checkPostEnd.getPosition()));
            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));


            checkPostEnd.showInfoWindow();
        }


    }

    @Override
    public void setDouble(String min) {
        String res[] = min.split(",");
        duration = res[0];
        distance = res[1];

        sw_bus_point.setSnippet("duration : " + duration + "\ndistance: " + distance);
        checkPostStart.setSnippet("duration : " + duration + "\ndistance: " + distance);
        checkPostEnd.setSnippet("duration : " + duration + "\ndistance: " + distance);

        Log.d(TAG, "duration : " + duration + "\ndistance: " + distance);

    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdate();
    }

    protected void startLocationUpdate() {
        if (checkSelfPermission(permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (i == 1) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        moveVechile(busLocation, location);
        rotateMarker(busLocation, location.getBearing(), start_rotation);
    }

    protected void stopLocationUpdates() {

        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.e(TAG, "Location update stopped .......................");
    }

    public void moveVechile(final Marker myMarker, final Location finalPosition) {

        final LatLng startPosition = myMarker.getPosition();

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 3000;
        final boolean hideMarker = false;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                LatLng currentPosition = new LatLng(
                        startPosition.latitude * (1 - t) + (finalPosition.getLatitude()) * t,
                        startPosition.longitude * (1 - t) + (finalPosition.getLongitude()) * t);
                myMarker.setPosition(currentPosition);
                // myMarker.setRotation(finalPosition.getBearing());


                // Repeat till progress is completeelse
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                    // handler.postDelayed(this, 100);
                } else {
                    if (hideMarker) {
                        myMarker.setVisible(false);
                    } else {
                        myMarker.setVisible(true);
                    }
                }
            }
        });


    }

    public void rotateMarker(final Marker marker, final float toRotation, final float st) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = st;
        final long duration = 1555;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                float rot = t * toRotation + (1 - t) * startRotation;


                marker.setRotation(-rot > 180 ? rot / 2 : rot);
                start_rotation = -rot > 180 ? rot / 2 : rot;
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    public void makeMarker(int color, String title, LatLng latLng) {

        mMap.addMarker(new MarkerOptions()
                .icon((BitmapDescriptorFactory
                        .fromResource(color)))
                .title(title)
                .position(latLng));

    }

    @Override
    public void setVehicleCurrentLocation(Marker marker) {
        busLocation = marker;
    }
}