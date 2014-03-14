package com.example.MapApp.Main;

/**
 * Created by respect on 3/14/14.
 */
public class MyPosition implements Point{

    public static double latitude;
    public static double longitude;

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }
}
