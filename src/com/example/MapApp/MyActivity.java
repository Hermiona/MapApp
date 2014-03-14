package com.example.MapApp;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import com.example.MapApp.Main.MyPosition;
import com.example.MapApp.Main.PointCalculates;
import com.example.MapApp.Main.XmlReader;
import com.example.MapApp.PrayerPlace.Gender;
import com.example.MapApp.PrayerPlace.PrayerPlace;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.*;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
//    for example:
//        items.add(new OverlayItem("Казань", "Татарстан", new GeoPoint(42.8946, 74.6079)));
//        items.add(new OverlayItem("Мечеть Ош рынок", "Бишкек", new GeoPoint(42.87923, 74.57251)));
//        items.add(new OverlayItem("намазкана BetaStores", "Бишкек", new GeoPoint(42.87611, 74.59225)));
//        items.add(new OverlayItem("ie","Central Mosque","this is description", new GeoPoint(42.86939, 74.62152)));


    public static MapView map;
    public final int XMLGeoPointsCount = 5;
    public static float[][] geoPoints;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        createMapViewAndSetParameters();
        setStartPointPosition();

        XmlReader xmlReader = new XmlReader(getResources().getXml(R.xml.geopoints));
        try {
            xmlReader.readPrayerPlaceListFromXML();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        drawMarkersOnMap(xmlReader.getPrayerPlaceList());
        map.invalidate();

        MyPosition.latitude = (float)42.86990;
        MyPosition.longitude = (float)74.62200;

        PointCalculates pointCalculates = new PointCalculates(xmlReader.getPrayerPlaceList());
        ArrayList<PrayerPlace> sortedPrayerPlaceArrayList = pointCalculates.sortPointsFromMyPosition();

/*
        ArrayList items = new ArrayList();

        try {
            geoPoints = getGeoPointsFromXML();
            items = getOverlayItemArrayListFromArrayFloat(geoPoints);
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //
        CustomItemizedOverlay MyLocationOverlay = new CustomItemizedOverlay(this, items);


        map.getOverlays().add(MyLocationOverlay);
        */

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float[] absPoints = new float[XMLGeoPointsCount];
                int placeNumber = 0;
                float minPlace = 0;
                for (int i = 0; i < geoPoints.length; i++) {
                    absPoints[i] = Math.abs(geoPoints[i][0] - CustomItemizedOverlay.latitude) + Math.abs(geoPoints[i][1] - CustomItemizedOverlay.longitude);
                    if (minPlace == 0 || minPlace > absPoints[i]) {
                        minPlace = absPoints[i];
                        placeNumber = i;
                    }
                }
                ArrayList nearPlace = new ArrayList();

                GeoPoint nearPoint = new GeoPoint(geoPoints[placeNumber][0], geoPoints[placeNumber][1]);
                GeoPoint myPosition = new GeoPoint(CustomItemizedOverlay.latitude, CustomItemizedOverlay.longitude);


                nearPlace.add(new OverlayItem("", "", nearPoint));
                nearPlace.add(new OverlayItem("", "", myPosition));
                map.getOverlays().add(new CustomItemizedOverlay(map.getContext(), nearPlace));
                map.invalidate();
            }
        });
    }

    public void createMapViewAndSetParameters(){
        final RelativeLayout relativeLayout = new RelativeLayout(this); // разметка
        map = new MapView(this, 256); // наша карта

        //разрешаем встроенные кнопки изменения масштаба

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        // добавим карту в разметку
        relativeLayout.addView(map, new RelativeLayout.LayoutParams(MapView.LayoutParams.FILL_PARENT,
                MapView.LayoutParams.FILL_PARENT));
        setContentView(relativeLayout);
        map.setUseDataConnection(false);

        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getController().setZoom(12);



    }

    public void setStartPointPosition(){

        GeoPoint startPoint = new GeoPoint(42.8800, 74.6100);
        map.getController().setCenter(startPoint);

        final Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(startMarker);

        startMarker.setIcon(getResources().getDrawable(R.drawable.marker_destination));
        startMarker.setTitle("Start point");
        map.invalidate();
    }

    public void drawMarkersOnMap(ArrayList<PrayerPlace> prayerPlaces){
        for(int i = 0; i < prayerPlaces.size(); i++){
            Marker tempMarker = new Marker(map);
            PrayerPlace tempPrayerPlace = prayerPlaces.get(i);
            tempMarker.setPosition(new GeoPoint(tempPrayerPlace.latitude, tempPrayerPlace.longitude));
            tempMarker.setIcon(getMarkerIconFromGender(tempPrayerPlace.prayerPlaceGender));
            tempMarker.setTitle(tempPrayerPlace.name);
            tempMarker.setSubDescription(tempPrayerPlace.description);
            map.getOverlays().add(tempMarker);
        }
    }

    public Drawable getMarkerIconFromGender(Gender prayerPlaceGender){
        Drawable drawableIcon = null;
        switch (prayerPlaceGender){
            case MALE:
                drawableIcon = getResources().getDrawable(R.drawable.marker_male);
                break;
            case FEMALE:
                drawableIcon = getResources().getDrawable(R.drawable.marker_female);
                break;
            case JOINT:
                drawableIcon = getResources().getDrawable(R.drawable.marker_joint);
                break;
        }
        return drawableIcon;
    }
}