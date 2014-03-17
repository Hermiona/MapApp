package com.example.MapApp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import com.example.MapApp.Main.MyPosition;
import com.example.MapApp.Main.PointCalculates;
import com.example.MapApp.Main.XmlReader;
import com.example.MapApp.PrayerPlace.Gender;
import com.example.MapApp.PrayerPlace.PrayerPlace;
import com.example.MapApp.PrayerPlace.Type;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.MarkerInfoWindow;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;

public class MapActivity extends Activity {

    public static MapView map;
    public ArrayList<PrayerPlace> prayerPlaceArrayList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        createMapViewAndSetParameters();
        setStartPointPosition();

        XmlReader xmlReader = new XmlReader(getResources().getXml(R.xml.geopoints));
        try {
            xmlReader.readPrayerPlaceListFromXML();
            prayerPlaceArrayList = xmlReader.getPrayerPlaceList();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        drawMarkersOnMap(prayerPlaceArrayList);
        map.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.map_options, menu);
        menu.findItem(R.id.help_menu_item).setIntent(
                new Intent(this, HelpActivity.class));
        menu.findItem(R.id.settings_menu_item).setIntent(
                new Intent(this, SettingsActivity.class));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        startActivity(item.getIntent());
        return true;
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

        map.setTileSource(TileSourceFactory.MAPNIK);//using OpenStreetMaps
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
        tempMarker.setIcon(getMarkerIconFromTypeAndGender(prayerPlace));
        tempMarker.setTitle(prayerPlace.name);
        tempMarker.setSubDescription(prayerPlace.description);
        return tempMarker;
    }

    public class CustomMarker extends Marker {
        public CustomMarker(MapView mapView) {
            super(mapView);
        }

        @Override
        public void setOnMarkerClickListener(OnMarkerClickListener listener) {
            super.setOnMarkerClickListener(listener);
            nearPrayerPlaceMarker.showInfoWindow();
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event, MapView mapView) {
            float coordinateX = event.getX();
            float coordinateY = event.getY();
            MapView.Projection projection = mapView.getProjection();
            GeoPoint tappedGeoPoint = (GeoPoint) projection.fromPixels(coordinateX, coordinateY);
            MyPosition.latitude = tappedGeoPoint.getLatitude();
            MyPosition.longitude = tappedGeoPoint.getLongitude();

            map.getOverlays().clear();

            drawNearestPoint();
            drawMyPosition();

            map.invalidate();

            return true;
        }
        Marker nearPrayerPlaceMarker;
        public void drawNearestPoint(){
            PointCalculates pointCalculates = new PointCalculates(prayerPlaceArrayList);
            pointCalculates.sortPointsFromMyPosition();
            ArrayList<PrayerPlace> sortedPoints = pointCalculates.prayerPlaceArrayList;
            PrayerPlace nearPrayerPlace = sortedPoints.get(0);
            nearPrayerPlaceMarker = generateMarkerFromPrayerPlaceObject(nearPrayerPlace);
            map.getOverlays().add(nearPrayerPlaceMarker);

            nearPrayerPlaceMarker.setInfoWindow(new MarkerInfoWindow(R.layout.bonuspack_bubble, map));
            nearPrayerPlaceMarker.showInfoWindow();

        }

        public void drawMyPosition(){
            Marker tempMarker = new CustomMarker(map);
            tempMarker.setPosition(new GeoPoint(MyPosition.latitude, MyPosition.longitude));
            map.getOverlays().add(tempMarker);
        }
    }

    public Drawable getMarkerIconFromTypeAndGender(PrayerPlace prayerPlace){
        Drawable drawableIcon = null;
        String ss = "";
        int iconId = R.drawable.marker_undefined;
        switch (prayerPlace.prayerPlaceGender){
            case MALE:
                iconId = (prayerPlace.prayerPlaceType == Type.MOSQUE)?R.drawable.mosque_male:R.drawable.prayerroom_male;
                break;
            case FEMALE:
                iconId = (prayerPlace.prayerPlaceType == Type.MOSQUE)?R.drawable.mosque_female:R.drawable.prayerroom_female;
                break;
            case JOINT:
                iconId = (prayerPlace.prayerPlaceType == Type.MOSQUE)?R.drawable.mosque_joint1:R.drawable.prayerroom_joint;
                break;
            case UNDEFINED:
                break;
        }
        drawableIcon = getResources().getDrawable(iconId);
        return drawableIcon;
    }
}