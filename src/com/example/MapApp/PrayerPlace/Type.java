package com.example.MapApp.PrayerPlace;

/**
 * Created by respect on 3/14/14.
 */
public enum Type {
    MOSQUE(1),
    PRAYERROOM(2),
    UNDEFINED(3);

    private int value;

    private Type(int value) {
        this.value = value;
    }
}
