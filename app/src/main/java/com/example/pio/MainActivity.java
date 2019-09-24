package com.example.pio;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.MatrixCursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import java.util.ArrayList;
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
        LocationListener, GoogleMap.OnMapClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();


    private int driverid;


    private double current_lat;
    private double current_lng;

    private ArrayList<Double> latitude, longitude;



    private SuggestionAdapter mSuggestAdapter;


    private String duration, distance;

    private Float start_rotation = 0.15f;


    private PointProfileBean pointProfileBean = new PointProfileBean();



    /**
     * Keeps track of the selected marker.
     */
    private Marker mSelectedMarker, busLocation;

    private LatLng bus_location;

    private LocationRequest mLocationRequest = null;



    private Polyline mPolyline;

    private List<PointProfileBean> pointProfileData;

    private ArrayList<Integer> mListSuggestionInt = new ArrayList<Integer>();

    private static final long INTERVAL = 1;
    private static final long FASTEST_INTERVAL = 1;

    private static final String FINE_LOCATION = permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 17f;



    private MatrixCursor mCursor;


    private SearchView searchBar;

    private Menu menu;

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


        latitude = new ArrayList<Double>();
        longitude = new ArrayList<Double>();

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

        createLocationRequest();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Handler handler = new Handler();
                handler.post(new Runnable() {
                    public void run() {
                        //DO SOME ACTIONS HERE , THIS ACTIONS WILL WILL EXECUTE AFTER 5 SECONDS...
                        handler.postDelayed(this, 5000);
                        fetchPointProfilesData();
                    }
                });
            }
        });

       /* if(latitude.size() == 5 & longitude.size() == 5){
            latitude.remove(5);
            longitude.remove(5);
        }*/



        /*findViewById(R.id.ic_gps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    if (mPolyline.isVisible()) {
                        mPolyline.remove();
                        try {
                            mSelectedMarker.remove();
                        } catch (NullPointerException e) {
                            Toast.makeText(MainActivity.this, "no marker selected", Toast.LENGTH_LONG).show();
                        }

                    }
                } catch (NullPointerException e) {
                    Toast.makeText(MainActivity.this, "no polyline is displayed.", Toast.LENGTH_LONG).show();

                }
            }
        });*/


        hideSoftKeyboard();


    }

    private void startSuggestion(final String search){

        if(mListSuggestionInt != null){

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                   /* searchBar.setSuggestionsAdapter(null);
                    mSuggestAdapter = null;*/

                    mCursor = new MatrixCursor(new String[] {"_id","text"});

                    Object[] mTempData = new Object[]{0, "default"};

                    int size = mListSuggestionInt.size();

//                    mCursor.close();

                    for(int i=0; i < size; i++){
                        mTempData[0] = i;
                        mTempData[1] = mListSuggestionInt.get(i);
                        mCursor.addRow(mTempData);
                    }

                    mSuggestAdapter = new SuggestionAdapter(getApplicationContext(), mCursor, mListSuggestionInt);

                    searchBar.setSuggestionsAdapter(mSuggestAdapter);



                }
            });

        }





    }

    public void fetchPointProfilesData() {

//        Log.d(TAG, "Calling Method : retro()");


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


                        driverid = pointProfileData.get(i).getVehicle_id();

                        current_lat = pointProfileData.get(i).getCurrent_lat();
                        current_lng = pointProfileData.get(i).getCurrent_lng();





                        bus_location = new LatLng(current_lat, current_lng);








                        /*busLocation = mMap.addMarker(new MarkerOptions().title("SW Bus")
                        .position(bus_location)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.muetbusx1))
                        );*/

                        if (busLocation != null) {

                            moveVechile(busLocation, bus_location);
                            rotateMarker(busLocation, busLocation.getRotation(), start_rotation);
                        }






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


    private void addMarkerAndRoute() {

        BitmapDescriptor checkPoststart_ic = BitmapDescriptorFactory.fromResource(R.drawable.icons1);


        mMap.addMarker(new MarkerOptions().flat(true).icon(checkPoststart_ic).position(CheckPostData.CHECK_POST_SW).title("SW Bus Point")).setSnippet("Click here");


        mMap.addMarker(new MarkerOptions().icon(checkPoststart_ic).flat(true).position(CheckPostData.CHECK_POST_START).title("Check post Start")).setSnippet("Click here");





        BitmapDescriptor checkPostEnd_ic = BitmapDescriptorFactory.fromResource(R.drawable.icons2);
        mMap.addMarker(new MarkerOptions().position(CheckPostData.CHECK_POST_END).flat(true).title("Check Post End").icon(checkPostEnd_ic)).setSnippet("Click here");

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

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchBar = (SearchView) menu.findItem(R.id.searchbar).getActionView();
        searchBar.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchBar.setSubmitButtonEnabled(true);
        searchBar.setQueryHint(Html.fromHtml("<font color = #ffffff>"+getResources().getString(R.string.place_autocomplete_search_hint)+"</font>"));






        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                if (query.equals(Integer.toString(driverid))) {


                    addMarkerAndRoute();

                }


                Log.d(TAG, "pointProfileBean.getDriver_id() : " + driverid);
                Log.d(TAG, query);


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, newText);
                startSuggestion(newText);
                return false;
            }
        });

        searchBar.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {

                Log.d(MainActivity.class.getSimpleName(), "onSuggestionSelect : position:" + position);


                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {

                if (mListSuggestionInt != null && mListSuggestionInt.size() > 0) {
                    searchBar.setQuery(Integer.toString(mListSuggestionInt.get(position)), false);
                    if(mListSuggestionInt.get(position).equals(driverid)){
                        addMarkerAndRoute();
                    }
                }
                return false;



            }
        });

        searchBar.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                if (mPolyline.isVisible()) {
                    mPolyline.remove();
                    try {
                        mSelectedMarker.remove();
                    } catch (NullPointerException e) {
                        Toast.makeText(MainActivity.this, "no marker selected", Toast.LENGTH_LONG).show();
                    }
                    /*sw_bus_point.remove();
                    checkPostEnd.remove();
                    checkPostStart.remove();*/
                }

                return false;
            }
        });



        this.menu = menu;


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

            getApplicationContext().startActivity(new Intent(this, MainPageActivity.class));


        } else if (id == R.id.nav_display_map) {
            getApplicationContext().startActivity(new Intent(this, DisplayMapActivity.class));

        } else if (id == R.id.nav_display_route) {
            getApplicationContext().startActivity(new Intent(this, MainActivity.class));

        }/* else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

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
        moveCamera(new LatLng(25.4050, 68.2608), 16.5f, "Default Location");
        mMap.setOnMarkerClickListener(this);
        mMap.setMyLocationEnabled(true);

        mMap.getUiSettings().setMapToolbarEnabled(false);


        mMap.setOnInfoWindowClickListener(this);

        mMap.setOnMapClickListener(this);

        busLocation = mMap.addMarker(new MarkerOptions().title("SW Shutter")
                .position(CheckPostData.CHECK_POST_SW)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.minibus_xhdpi_40px)));

        busLocation.setFlat(true);


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

       /* if (marker.equals(mSelectedMarker)) {
            // The showing info window has already been closed - that's the first thing to happen
            // when any marker is clicked.
            // Return true to indicate we have consumed the event and that we do not want the
            // the default behavior to occur (which is for the camera to move such that the
            // marker is centered and for the marker's info window to open, if it has one).
            mSelectedMarker = null;
            return true;
        }*/


        mSelectedMarker = marker;

        if (mSelectedMarker.getTitle().equalsIgnoreCase("SW Shutter")) {

//            Log.d(TAG, "SW Bus Marker selected");

            addMarkerAndRoute();

           /* String url = getUrl(checkPostStart.getPosition(), checkPostEnd.getPosition(),"driving");
            new FetchURL(this).execute(url,"driving");*/


        }
        else if (mSelectedMarker.getTitle().equalsIgnoreCase("SW Bus Point")) {
//            Log.d(TAG, "SW Bus Point Marker selected");

//            sw_bus_point = mSelectedMarker;

//            Log.d(TAG, "SW Bus Point Marker selected: sw_bus_point marker title: "+sw_bus_point.getTitle());

            mSelectedMarker.setTitle("SW Bus Point");
            mSelectedMarker.setSnippet("Click here");
            mSelectedMarker.setVisible(true);
            new GeoTask(this).execute(getDistanceURl(bus_location, mSelectedMarker.getPosition()));


        }
        else if (mSelectedMarker.getTitle().equalsIgnoreCase("Check post Start")) {
//            Log.d(TAG, "Check post Start Marker selected");
//            checkPostStart = mSelectedMarker;

            mSelectedMarker.setTitle("Check post Start");
            mSelectedMarker.setSnippet("Click here");
            mSelectedMarker.setVisible(true);
            new GeoTask(this).execute(getDistanceURl(bus_location, mSelectedMarker.getPosition()));



        }
        else if (mSelectedMarker.getTitle().equalsIgnoreCase("Check Post End")) {
//            Log.d(TAG, "Check Post End Marker selected");
//            checkPostEnd = mSelectedMarker;


            mSelectedMarker.setTitle("Check Post End");
            mSelectedMarker.setSnippet("Click here");
            mSelectedMarker.setVisible(true);
            new GeoTask(this).execute(getDistanceURl(bus_location, mSelectedMarker.getPosition()));



        }






        return false;
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.d(TAG, "onInfoWindowClick: called");

        mSelectedMarker = marker;

        if (mSelectedMarker.getTitle().equalsIgnoreCase("SW Bus Point")) {
            mSelectedMarker.setVisible(true);
//            sw_bus_point = mSelectedMarker;

            new GeoTask(this).execute(getDistanceURl(bus_location, mSelectedMarker.getPosition()));
            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));

            mSelectedMarker.showInfoWindow();




        }
        if (mSelectedMarker.getTitle().equalsIgnoreCase("Check post Start")) {
            mSelectedMarker.setVisible(true);
//            checkPostStart = mSelectedMarker;

            new GeoTask(this).execute(getDistanceURl(bus_location, mSelectedMarker.getPosition()));


            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));


            mSelectedMarker.showInfoWindow();

        }
        if (mSelectedMarker.getTitle().equalsIgnoreCase("Check Post End")) {
            mSelectedMarker.setVisible(true);
//            checkPostEnd = mSelectedMarker;


            new GeoTask(this).execute(getDistanceURl(bus_location, mSelectedMarker.getPosition()));
            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));


            mSelectedMarker.showInfoWindow();
        }


    }

    @Override
    public void setDouble(String min) {
        String res[] = min.split(",");
        duration = res[0];
        distance = res[1];

        mSelectedMarker.setSnippet("duration : " + duration + "\ndistance: " + distance);


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

    }

    protected void stopLocationUpdates() {

        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.e(TAG, "Location update stopped .......................");
    }

    public void moveVechile(final Marker myMarker, final LatLng finalPosition) {

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
                        startPosition.latitude * (1 - t) + (finalPosition.latitude) * t,
                        startPosition.longitude * (1 - t) + (finalPosition.longitude) * t);
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
    public void onMapClick(LatLng latLng) {

        // Any showing info window closes when the map is clicked.
        // Clear the currently selected marker.
        mSelectedMarker = null;

    }


}