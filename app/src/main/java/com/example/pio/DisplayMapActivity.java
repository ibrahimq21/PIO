package com.example.pio;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

public class DisplayMapActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private static final long INTERVAL = 1;
    private static final long FASTEST_INTERVAL = 1;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private LocationRequest mLocationRequest = null;
    private Boolean mLocationPermissionsGranted = false;
    private Menu menu;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_uni_map);


       /* Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);*/

        getLocationPermission();
        createLocationRequest();


        hideSoftKeyboard();


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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.drawer_main, menu);

        this.menu = menu;


        return true;
    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {


        if (this.googleMap != null) {
            return;
        }
        this.googleMap = googleMap;


        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        this.googleMap.setMyLocationEnabled(true);

        this.googleMap.getUiSettings().setMapToolbarEnabled(false);


        this.googleMap.setBuildingsEnabled(true);

        try {

            boolean success = this.googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_map_retro));
            if (!success) {
                Log.d(DisplayMapActivity.class.getSimpleName(), "Style Parsing failed");
            }
        } catch (Resources.NotFoundException e) {

            Log.d(DisplayMapActivity.class.getSimpleName(), "Resources.NotFoundException : " + e.getMessage());
        }

        makeMarker(R.drawable.marker_lightblue_35px, "Administration Block MUET", CheckPostData.ADMIN_BLOCK_MUET);
        makeMarker(R.drawable.marker_black_35px, "MUET STC", CheckPostData.MUET_STC);
        makeMarker(R.drawable.marker_green_35px, "HBL ATM", CheckPostData.HBL_ATM);
        makeMarker(R.drawable.marker_noisegreen_35px, "MUET Cricket Ground", CheckPostData.MUET_CRICKET_GROUND);
        makeMarker(R.drawable.marker_orange_35px, "MUET Telecommunication Dept", CheckPostData.DEPT_ENGR_TELE);
        makeMarker(R.drawable.marker_silver_35px, "MUET Library Info", CheckPostData.MUET_LIB_INFO);
        makeMarker(R.drawable.marker_notorange_35px, "Mosque", CheckPostData.MOSQ_MUET);
        makeMarker(R.drawable.marker_notred_35px, "MUET Software Engineering Dept", CheckPostData.DEPT_SW);
        makeMarker(R.drawable.marker_pink_35px, "MUET Main Gate", CheckPostData.MAIN_GATE);




        moveCamera(new LatLng(25.4050, 68.2608), 16.5f);


    }

    private void moveCamera(LatLng latLng, float v) {

        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, v));
    }

    private void getLocationPermission() {
        Log.d(DisplayMapActivity.class.getSimpleName(), "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.uni_map)).getMapAsync(DisplayMapActivity.this);

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

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            getApplicationContext().startActivity(new Intent(this, MainPageActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();


        } else if (id == R.id.nav_display_map) {
            getApplicationContext().startActivity(new Intent(this, DisplayMapActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();

        } else if (id == R.id.nav_display_route) {
            getApplicationContext().startActivity(new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void makeMarker(int color, String title, LatLng latLng) {

        this.googleMap.addMarker(new MarkerOptions()
                .icon((BitmapDescriptorFactory
                        .fromResource(color)))
                .title(title)
                .position(latLng));

    }
}
