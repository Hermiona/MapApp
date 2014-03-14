package com.example.MapApp.Main;

import com.example.MapApp.PrayerPlace.PrayerPlace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by respect on 3/14/14.
 */
public class PointCalculates {

    public static ArrayList<PrayerPlace> prayerPlaceArrayList;

    public PointCalculates(ArrayList arrayList){
        this.prayerPlaceArrayList = arrayList;
    }

    public static ArrayList sortPointsFromMyPosition(){
        Collections.sort(prayerPlaceArrayList);
        return prayerPlaceArrayList;
    }

}
