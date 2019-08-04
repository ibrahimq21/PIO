package com.example.ptsdblibrary;

public class PointProfilePOJOClass {


    int point_id;
    int driver_id;
    int device_id;
    int vehicle_id;
    String area;

    String route;


    double current_log, current_lat;

    String timestamp_online;

    boolean tour_mode, reverse_mode;


    public PointProfilePOJOClass() {
    }

    public PointProfilePOJOClass(int point_id, int driver_id, int device_id, int vehicle_id, String area, String route, double current_log, double current_lat, String timestamp_online, boolean tour_mode, boolean reverse_mode) {
        this.point_id = point_id;
        this.driver_id = driver_id;
        this.device_id = device_id;
        this.vehicle_id = vehicle_id;
        this.area = area;
        this.route = route;
        this.current_log = current_log;
        this.current_lat = current_lat;
        this.timestamp_online = timestamp_online;
        this.tour_mode = tour_mode;
        this.reverse_mode = reverse_mode;
    }

    public PointProfilePOJOClass(double current_log, double current_lat) {
        this.current_log = current_log;
        this.current_lat = current_lat;
    }



    public int getPoint_id() {
        return point_id;
    }

    public void setPoint_id(int point_id) {
        this.point_id = point_id;
    }

    public int getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(int driver_id) {
        this.driver_id = driver_id;
    }

    public int getDevice_id() {
        return device_id;
    }

    public void setDevice_id(int device_id) {
        this.device_id = device_id;
    }

    public int getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(int vehicle_id) {
        this.vehicle_id = vehicle_id;
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

    public double getCurrent_log() {
        return current_log;
    }

    public void setCurrent_log(double current_log) {
        this.current_log = current_log;
    }

    public double getCurrent_lat() {
        return current_lat;
    }

    public void setCurrent_lat(double current_lat) {
        this.current_lat = current_lat;
    }

    public String getTimestamp_online() {
        return timestamp_online;
    }

    public void setTimestamp_online(String timestamp_online) {
        this.timestamp_online = timestamp_online;
    }

    public boolean isTour_mode() {
        return tour_mode;
    }

    public void setTour_mode(boolean tour_mode) {
        this.tour_mode = tour_mode;
    }

    public boolean isReverse_mode() {
        return reverse_mode;
    }

    public void setReverse_mode(boolean reverse_mode) {
        this.reverse_mode = reverse_mode;
    }







}
