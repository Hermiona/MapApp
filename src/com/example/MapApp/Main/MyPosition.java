package com.example.MapApp.Main;

/**
 * Created by respect on 3/14/14.
 */
public class MyPosition implements Point{

    public static float latitude;
    public static float longitude;

    @Override
    public float getLatitude() {
        return latitude;
    }

    @Override
    public float getLongitude() {
        return longitude;
    }
}
