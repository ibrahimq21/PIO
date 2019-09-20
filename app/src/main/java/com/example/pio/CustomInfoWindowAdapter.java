package com.example.pio;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private static final String TAG = "CustomInfoWindowAdapter";
    private final View mWindow;
    private Context mContext;

    public CustomInfoWindowAdapter(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);

    }

    private void rendowWindowText(Marker marker, View view){
        Log.d(TAG,"CustomInfoWindowAdapter: called");
        String title = marker.getTitle();
        TextView tvTitle = (TextView) view.findViewById(R.id.title);
        TextView tvSnippet = (TextView) view.findViewById(R.id.snippet);

        if(!title.equals("")){
            tvTitle.setText(title);
        }



        if(marker.isInfoWindowShown()){
            String snippet = marker.getSnippet();

            if(!snippet.equals("")){
                tvSnippet.setText(""+snippet);
            }
        }else{

            tvSnippet.setText(marker.getSnippet());
        }



    }


    @Override
    public View getInfoWindow(Marker marker) {
        rendowWindowText(marker, mWindow);

        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        rendowWindowText(marker, mWindow);
        return mWindow;
    }
}
