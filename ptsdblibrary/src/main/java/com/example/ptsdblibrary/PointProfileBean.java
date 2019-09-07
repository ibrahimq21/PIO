package com.example.ptsdblibrary;



import java.io.Serializable;

public class PointProfileBean implements Serializable {




    private int point_id;
    private int vehicle_id;
    private String device_id;
    private int driver_id;
    private String area;
    private String route;
    private double current_lat;
    private double current_lng;
    private String online_timestamp;
    private int tourmode;
    private int reversemode;

    public PointProfileBean(int point_id, int vehicle_id, String device_id, int driver_id, String area, String route, double current_lat, double current_lng, String online_timestamp, int tourmode, int reversemode) {
        this.point_id = point_id;
        this.vehicle_id = vehicle_id;
        this.device_id = device_id;
        this.driver_id = driver_id;
        this.area = area;
        this.route = route;
        this.current_lat = current_lat;
        this.current_lng = current_lng;
        this.online_timestamp = online_timestamp;
        this.tourmode = tourmode;
        this.reversemode = reversemode;
    }

    public PointProfileBean(){}


    public int getPoint_id() {
        return point_id;
    }

    public void setPoint_id(int point_id) {
        this.point_id = point_id;
    }

    public int getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(int vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public int getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(int driver_id) {
        this.driver_id = driver_id;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public double getCurrent_lat() {
        return current_lat;
    }

    public void setCurrent_lat(double current_lat) {
        this.current_lat = current_lat;
    }

    public double getCurrent_lng() {
        return current_lng;
    }

    public void setCurrent_lng(double current_lng) {
        this.current_lng = current_lng;
    }

    public String getOnline_timestamp() {
        return online_timestamp;
    }

    public void setOnline_timestamp(String online_timestamp) {
        this.online_timestamp = online_timestamp;
    }

    public int getTourmode() {
        return tourmode;
    }

    public void setTourmode(int tourmode) {
        this.tourmode = tourmode;
    }

    public int getReversemode() {
        return reversemode;
    }

    public void setReversemode(int reversemode) {
        this.reversemode = reversemode;
    }
}
