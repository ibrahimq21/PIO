package com.example.pio;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.ptsdblibrary.PointProfileBean;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


public class DistanceActivity extends BaseActivity implements OnMapReadyCallback, TaskLoadedCallback {


    private Marker checkPost;
    private Marker busLocation;
    private Polyline mPolyline;
    private static final float DEFAULT_ZOOM = 16.5f;
    private PointProfileBean pointProfileBean = new PointProfileBean();

    private static String TAG = "DistanceActivity";

    /*@Override
    protected int getLayoutId() {
        return R.layout.activity_distance;
    }*/


    BitmapDescriptor busIcon = BitmapDescriptorFactory.fromResource(R.drawable.icons1);

    @Override
    protected void start() {


        pointProfileBean = (PointProfileBean) getIntent().getSerializableExtra("point_profile");

        Log.d(TAG, "start: clicked Bus icon Bus Lat and Lng" + pointProfileBean.getLat() + " and " + pointProfileBean.getLng());


        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(CheckPostData.CHECK_POST_SW_LAT, CheckPostData.CHECK_POST_SW_LNG), DEFAULT_ZOOM));


        checkPost = getMap().addMarker(new MarkerOptions().position(CheckPostData.CHECK_POST_START).draggable(false).title("Check Post"));
        busLocation = getMap().addMarker(new MarkerOptions().position(new LatLng(pointProfileBean.getLat(), pointProfileBean.getLng())).draggable(false).title("Bus Location").icon(busIcon));






        String url = getUrl(checkPost.getPosition(), busLocation.getPosition(), "driving");
        new FetchURL(this).execute(url, "driving");

    }

    private String getDistanceURl(LatLng origin, LatLng destination){

        String str_origin = "origins=" + origin.latitude + "," + origin.longitude;

//        Destination of route

        String str_dest = "destinations=" + destination.latitude + "," + destination.longitude;

        String unit = "units=imperial";

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


    private String formatNumber(double distance) {
        String unit = "m";
        if (distance < 1) {
            distance *= 1000;
            unit = "mm";
        } else if (distance > 1000) {
            distance /= 1000;
            unit = "km";
        }

        return String.format("%4.3f%s", distance, unit);
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
    public void onTaskDone(Object... values) {
        if (mPolyline != null)
            mPolyline.remove();
        mPolyline = getMap().addPolyline((PolylineOptions) values[0]);
    }
}
