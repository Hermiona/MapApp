package com.example.MapApp.Main;

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
    public XmlReader(XmlPullParser xmlResourceFile){
        this.xmlResourceFile = xmlResourceFile;
        prayerPlaceList = new ArrayList<PrayerPlace>();
    }

    private ArrayList<PrayerPlace> prayerPlaceList;
    public XmlPullParser xmlResourceFile;

    public ArrayList<PrayerPlace> getPrayerPlaceList(){
        return prayerPlaceList;
    }

    private void doXppNextWhileStartNeedTag(XmlPullParser xpp, String needTag) throws IOException, XmlPullParserException {
        while(!(xpp.getEventType() == XmlPullParser.START_TAG && xpp.getName().equals(needTag))){
            xpp.next();
        }
    }

    public void readPrayerPlaceListFromXML() throws IOException, XmlPullParserException {
        XmlPullParser xpp = xmlResourceFile;
        int eventType = xpp.getEventType();
        int i = 0;
        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            if(eventType == XmlPullParser.START_TAG && xpp.getName().equals("point"))
            {
                try{
                    PrayerPlace prayerPlace = new PrayerPlace();

                    doXppNextWhileStartNeedTag(xpp, "type");
                    String typeString = getTextOfXppTag(xpp);
                    prayerPlace.prayerPlaceType = getPrayerPlaceType(typeString);

                    doXppNextWhileStartNeedTag(xpp, "gender");
                    String genderString = getTextOfXppTag(xpp);
                    prayerPlace.prayerPlaceGender = getPrayerPlaceGender(genderString);

                    doXppNextWhileStartNeedTag(xpp, "name");
                    String nameString = getTextOfXppTag(xpp);
                    prayerPlace.name = nameString;

                    doXppNextWhileStartNeedTag(xpp, "description");
                    String descriptionString = getTextOfXppTag(xpp);
                    prayerPlace.description = descriptionString;

                    doXppNextWhileStartNeedTag(xpp, "address");
                    String addressString = getTextOfXppTag(xpp);
                    prayerPlace.address = addressString;

                    doXppNextWhileStartNeedTag(xpp, "latitude");
                    String latitudeString = getTextOfXppTag(xpp);
                    prayerPlace.latitude = Double.parseDouble(latitudeString);

                    doXppNextWhileStartNeedTag(xpp, "longitude");
                    String longitudeString = getTextOfXppTag(xpp);
                    prayerPlace.longitude = Double.parseDouble(longitudeString);

//                    doXppNextWhileStartNeedTag(xpp, "point");

                    prayerPlaceList.add(prayerPlace);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            eventType = xpp.next();
        }
    }

    private String getTextOfXppTag(XmlPullParser xpp){
        String temp = "";
        try{
            xpp.next();
            temp = xpp.getText().toString().trim();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return temp;
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
}
