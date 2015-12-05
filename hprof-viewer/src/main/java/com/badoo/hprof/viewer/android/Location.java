package com.badoo.hprof.viewer.android;

/**
 * Model for an Android Location
 *
 * Created by Erik Andre on 05/12/15.
 */
public class Location {

    private final String provider;
    private final double latitude;
    private final double longitude;
    private final float accuracy;
    private final long time;

    public Location(String provider, double latitude, double longitude, float accuracy, long time) {
        this.provider = provider;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.time = time;
    }

    public String getProvider() {
        return provider;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public long getTime() {
        return time;
    }

}
