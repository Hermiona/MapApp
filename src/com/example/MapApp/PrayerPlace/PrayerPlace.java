package com.example.MapApp.PrayerPlace;

import com.example.MapApp.Main.MyPosition;
import com.example.MapApp.Main.Point;

/**
 * Created by respect on 3/14/14.
 */
public class PrayerPlace implements Point, Comparable {

    public String name;
    public String description;
    public Type prayerPlaceType;
    public Gender prayerPlaceGender;
    public double latitude;
    public double longitude;
    public String address;

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    @Override
    public int compareTo(Object obj) {
        PrayerPlace temp = (PrayerPlace)obj;
        double thisDistance = Math.abs(MyPosition.latitude - this.latitude) + Math.abs(MyPosition.longitude - this.longitude);
        double tempDistance = Math.abs(MyPosition.latitude - temp.latitude) + Math.abs(MyPosition.longitude - temp.longitude);
        if(thisDistance > tempDistance){
            return 1;
        }
        else if (thisDistance < tempDistance){
            return -1;
        }
        return 0;
    }
}
