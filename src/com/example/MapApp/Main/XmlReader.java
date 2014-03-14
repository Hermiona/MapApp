package com.example.MapApp.Main;

import android.content.res.XmlResourceParser;
import com.example.MapApp.PrayerPlace.Gender;
import com.example.MapApp.PrayerPlace.PrayerPlace;
import com.example.MapApp.PrayerPlace.Type;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by respect on 3/14/14.
 */
public class XmlReader {
    public XmlReader(XmlResourceParser xmlResourceFile){
        this.xmlResourceFile = xmlResourceFile;
        prayerPlaceList = new ArrayList<PrayerPlace>();
    }

    private ArrayList<PrayerPlace> prayerPlaceList;
    private XmlResourceParser xmlResourceFile;
    private int geoPointsCount;

    public ArrayList<PrayerPlace> getPrayerPlaceList(){
        return prayerPlaceList;
    }

    private void doXppNext(XmlResourceParser xpp, int howMuch) throws IOException, XmlPullParserException {
        for(int i = 0; i < howMuch; i++){
            xpp.next();
        }
    }

    public void readPrayerPlaceListFromXML() throws IOException, XmlPullParserException {
        XmlResourceParser xpp = xmlResourceFile;
        int eventType = xpp.getEventType();
        int i = 0;
        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            if(eventType == XmlPullParser.START_TAG && xpp.getName().equals("point"))
            {
                PrayerPlace prayerPlace = new PrayerPlace();

                doXppNext(xpp, 2);
                String typeString = xpp.getText().trim();
                prayerPlace.prayerPlaceType = getPrayerPlaceType(typeString);

                doXppNext(xpp, 3);
                String genderString = xpp.getText().trim();
                prayerPlace.prayerPlaceGender = getPrayerPlaceGender(genderString);

                doXppNext(xpp, 3);
                String nameString = xpp.getText().trim();
                prayerPlace.name = nameString;

                doXppNext(xpp, 3);
                String descriptionString = xpp.getText().trim();
                prayerPlace.description = descriptionString;

                doXppNext(xpp, 3);
                String latitudeString = xpp.getText().trim();
                prayerPlace.latitude = Double.parseDouble(latitudeString);

                doXppNext(xpp, 3);
                String longitudeString = xpp.getText().trim();
                prayerPlace.longitude = Double.parseDouble(longitudeString);

                doXppNext(xpp, 1);
                eventType = xpp.next();

                prayerPlaceList.add(prayerPlace);
            }
            eventType = xpp.next();
        }
    }

    private Gender getPrayerPlaceGender(String genderString) {
        Gender prayerPlaceGender;
        if(genderString.equals("male")){
          prayerPlaceGender = Gender.MALE;
        } else if(genderString.equals("female")){
            prayerPlaceGender = Gender.FEMALE;
        } else if(genderString.equals("joint")){
            prayerPlaceGender = Gender.JOINT;
        } else {
            prayerPlaceGender = Gender.UNDEFINED;
        }
        return prayerPlaceGender;
    }

    private Type getPrayerPlaceType(String typeString) {
        Type prayerPlaceType;
        if (typeString.equals("mosque")) {
            prayerPlaceType = Type.MOSQUE;
        } else if (typeString.equals("prayerroom")) {
            prayerPlaceType = Type.PRAYERROOM;
        } else {
            prayerPlaceType = Type.UNDEFINED;
        }
        return prayerPlaceType;
    }

    private int setXmlGeoPointsCount () throws XmlPullParserException {
        XmlResourceParser xpp = xmlResourceFile;
        int eventType = xpp.getEventType();
        geoPointsCount = 0;
        while(eventType != XmlPullParser.END_DOCUMENT){
            if(eventType == XmlPullParser.START_TAG)
            {
                String xpp_name = xpp.getName();
                if("point".hashCode() == xpp_name.hashCode()){
                    geoPointsCount++;
                }
            }
        }
        return geoPointsCount;
    }

    public int getXmlGeoPointsCount() throws XmlPullParserException {
        if(geoPointsCount == 0)
            setXmlGeoPointsCount();
        return geoPointsCount;
    }
}
