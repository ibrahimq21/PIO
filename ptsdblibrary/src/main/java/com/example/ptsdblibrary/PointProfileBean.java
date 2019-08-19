package com.example.ptsdblibrary;

import java.io.Serializable;

public class PointProfileBean implements Serializable {

    private Double lat = null;
    private Double lng = null;

    public PointProfileBean(){}

    public PointProfileBean(Double latitude, Double longitude){

        this.lat = latitude;
        this.lng = longitude;

    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
