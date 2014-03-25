package com.example.MapApp.PrayerPlace;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import com.example.MapApp.Main.MyPosition;
import com.example.MapApp.Main.Point;
import com.example.MapApp.R;

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

    public String getPlaceTypeString(Resources resources){
        String placeTypeString = "";
        switch (prayerPlaceType){
            case MOSQUE:
                placeTypeString = resources.getString(R.string.type_mosque_text);
                break;
            case PRAYERROOM:
                placeTypeString = resources.getString(R.string.type_prayerroom_text);
                break;
        }
        return placeTypeString;
    }

    public String getPlaceGenderString(Resources resources){
        String genderText = "";
        switch (prayerPlaceGender){
            case MALE:
                genderText = resources.getString(R.string.gender_male_text);
                break;
            case FEMALE:
                genderText = resources.getString(R.string.gender_female_text);
                break;
            case JOINT:
                genderText = resources.getString(R.string.gender_joint_text);
                break;
        }
        return genderText;
    }

    public Drawable getMarkerIconFromTypeAndGender(Resources resources){
        Drawable drawableIcon = null;
        int iconId = R.drawable.marker_undefined;
        switch (prayerPlaceGender){
            case MALE:
                iconId = (prayerPlaceType == Type.MOSQUE)?R.drawable.mosque_male:R.drawable.prayerroom_male;
                break;
            case FEMALE:
                iconId = (prayerPlaceType == Type.MOSQUE)?R.drawable.mosque_female:R.drawable.prayerroom_female;
                break;
            case JOINT:
                iconId = (prayerPlaceType == Type.MOSQUE)?R.drawable.mosque_joint1:R.drawable.prayerroom_joint;
                break;
            case UNDEFINED:
                break;
        }
        drawableIcon = resources.getDrawable(iconId);
        return drawableIcon;
    }
}
