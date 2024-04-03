package com.example.mapthreadsapp;

import static java.lang.System.nanoTime;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

public class Region {
    private String name;
    private double latitude;
    private double longitude;
    private String id;
    private long timestamp;
    static private int counter = 1;

    public Region() {}

    public Region(double latitude, double longitude) {
        this.name = "Regiao " + counter++;
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
        this.timestamp = nanoTime();
    }

    public String getName() {
        return name;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    public Boolean isNear(Region region) {

        float[] results = new float[1];
        Location.distanceBetween(this.latitude, this.longitude, region.getLatitude(), region.getLongitude(), results);
        return results[0] < 30;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("latitude", latitude);
        map.put("longitude", longitude);
        map.put("timestamp", timestamp);
        return map;
    }
}
