package com.example.MapApp;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import com.example.MapApp.Main.MyPosition;
import com.example.MapApp.Main.PointCalculates;
import com.example.MapApp.Main.XmlReader;
import com.example.MapApp.PrayerPlace.Gender;
import com.example.MapApp.PrayerPlace.PrayerPlace;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;

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

        PointCalculates.prayerPlaceArrayList = xmlReader.getPrayerPlaceList();
        PointCalculates.sortPointsFromMyPosition();
    }

    public void createMapViewAndSetParameters(){
        final RelativeLayout relativeLayout = new RelativeLayout(this); // разметка
        map = new MapView(this, 256); // наша карта

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
    }

    public void drawMarkersOnMap(ArrayList<PrayerPlace> prayerPlaces){
        for(int i = 0; i < prayerPlaces.size(); i++){
            Marker tempMarker = generateMarkerFromPrayerPlaceObject(prayerPlaces.get(i));
            map.getOverlays().add(tempMarker);
        }
    }

    public Marker generateMarkerFromPrayerPlaceObject(PrayerPlace prayerPlace){
        CustomMarker tempMarker = new CustomMarker(map);
        tempMarker.setPosition(new GeoPoint(prayerPlace.latitude, prayerPlace.longitude));
        tempMarker.setIcon(getMarkerIconFromGender(prayerPlace.prayerPlaceGender));
        tempMarker.setTitle(prayerPlace.name);
        tempMarker.setSubDescription(prayerPlace.description);
        return tempMarker;
    }

    public class CustomMarker extends Marker {
        public CustomMarker(MapView mapView) {
            super(mapView);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event, MapView mapView) {
            float coordinateX = event.getX();
            float coordinateY = event.getY();
            MapView.Projection projection = mapView.getProjection();
            GeoPoint tappedGeoPoint = (GeoPoint) projection.fromPixels(coordinateX, coordinateY);
            MyPosition.latitude = (float)tappedGeoPoint.getLatitude();
            MyPosition.longitude = (float)tappedGeoPoint.getLongitude();
            MyActivity.map.getOverlays().clear();
            PointCalculates.sortPointsFromMyPosition();
            map.getOverlays().add(generateMarkerFromPrayerPlaceObject(PointCalculates.prayerPlaceArrayList.get(0)));
            Marker tempMarker = new CustomMarker(map);
            tempMarker.setPosition(new GeoPoint(MyPosition.latitude, MyPosition.longitude));
            map.getOverlays().add(tempMarker);
            map.invalidate();
            return true;
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