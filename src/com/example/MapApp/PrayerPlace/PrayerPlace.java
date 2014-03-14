package com.example.MapApp.PrayerPlace;

import com.example.MapApp.CustomItemizedOverlay;
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
    public float latitude;
    public float longitude;

    @Override
    public float getLatitude() {
        return latitude;
    }

    @Override
    public float getLongitude() {
        return longitude;
    }

    @Override
    public int compareTo(Object obj) {
        PrayerPlace temp = (PrayerPlace)obj;
        float thisDistance = Math.abs(MyPosition.latitude - this.latitude) + Math.abs(MyPosition.longitude - this.longitude);
        float tempDistance = Math.abs(MyPosition.latitude - temp.latitude) + Math.abs(MyPosition.longitude - temp.longitude);
        if(thisDistance > tempDistance){
            return 1;
        }
        else if (thisDistance < tempDistance){
            return -1;
        }
        return 0;
    }
}
