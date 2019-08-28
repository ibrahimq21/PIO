package com.example.ptsdblibrary;

import java.io.Serializable;

public class PointProfileBean implements Serializable {

    private Double lat = null;
    private Double lng = null;
    private String driverid = null;

    public PointProfileBean(){}

    public PointProfileBean(Double latitude, Double longitude, String driverid){

        this.lat = latitude;
        this.lng = longitude;
        this.driverid = driverid;

    }


    public String getDriverid() {
        return driverid;
    }

    public void setDriverid(String driverid) {
        this.driverid = driverid;
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
