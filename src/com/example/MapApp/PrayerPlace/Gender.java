package com.example.MapApp.PrayerPlace;

/**
 * Created by respect on 3/14/14.
 */
public enum Gender {
    MALE(1), FEMALE(2), JOINT(3), UNDEFINED(4);

    private int value;

    private Gender(int value) {
        this.value = value;
    }
}
