package com.example.MapApp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.*;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.example.MapApp.Main.MyPosition;
import com.example.MapApp.Main.PointCalculates;
import com.example.MapApp.Main.XmlReader;
import com.example.MapApp.PrayerPlace.PrayerPlace;
import com.example.MapApp.PrayerPlace.Type;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.MarkerInfoWindow;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;

public class MapActivity extends Activity {

    public static MapView map;
    public ArrayList<PrayerPlace> prayerPlaceArrayList;
    boolean gpsNavigation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        gpsNavigation = false;

        createMapViewAndSetParameters();
        readPointsFromXml();

        PrayerPlace prayerPlaceFromList = MainActivity.prayerPlaceFromList;
        MainActivity.prayerPlaceFromList = null;

        if(prayerPlaceFromList == null){
            setStartPointPosition(42.8800, 74.6100);
            drawMarkersOnMap(prayerPlaceArrayList);
        } else{
            CustomMarker markerFromList = generateMarkerFromPrayerPlaceObject(prayerPlaceFromList);
            map.getOverlays().add(markerFromList);
            markerFromList.setInfoWindow(new MarkerInfoWindow(R.layout.bonuspack_bubble, map));
            setStartPointPosition(prayerPlaceFromList.latitude, prayerPlaceFromList.longitude);
            map.getController().setZoom(15);
            markerFromList.showInfoWindow();
        }

        map.invalidate();

//        final ImageView gps_icon = (ImageView)findViewById(R.id.gps_icon);
////        gps_icon.setImageDrawable(getResources().getDrawable(R.drawable.gps_off));
//        gps_icon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                gps_icon.setImageDrawable(getResources().getDrawable(R.drawable.gps_on));
//            }
//        });


    }

    void readPointsFromXml(){
        XmlReader xmlReader = new XmlReader(getResources().getXml(R.xml.geopoints));
        try {
            xmlReader.readPrayerPlaceListFromXML();
            prayerPlaceArrayList = xmlReader.getPrayerPlaceList();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.map_options, menu);
        menu.add(getString(R.string.options_menu_clear_map));
        menu.add(getString(R.string.options_menu_show_all_markers));
        menu.findItem(R.id.help_menu_item).setIntent(
                new Intent(this, HelpActivity.class));
        menu.findItem(R.id.settings_menu_item).setIntent(
                new Intent(this, SettingsActivity.class));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getTitle().equals(getString(R.string.options_menu_clear_map))){
            hideInfoWindowOfAllCustomMarker();
            map.getOverlays().clear();
            drawMyPosition();
            map.invalidate();
        } else if(item.getTitle().equals(getString(R.string.options_menu_show_all_markers))){
            drawMarkersOnMap(prayerPlaceArrayList);
        } else {
            startActivity(item.getIntent());
        }
        return true;
    }

    void hideInfoWindowOfAllCustomMarker(){
        int overlaysCount = map.getOverlays().size();
        for(int i = 0; i < overlaysCount; i++){
            Overlay overlay = map.getOverlays().get(i);
            if(overlay instanceof CustomMarker){
                CustomMarker marker = (CustomMarker)overlay;
                marker.hideInfoWindow();
            }
        }
    }

    public void createMapViewAndSetParameters(){
        final ImageView gpsIcon = new ImageView(this);
        gpsIcon.setImageDrawable((gpsNavigation)?getResources().getDrawable(R.drawable.gps_on):getResources().getDrawable(R.drawable.gps_off));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        gpsIcon.setPadding(10, 0, 0, 10);
        gpsIcon.setMinimumWidth(50);
        gpsIcon.setMinimumHeight(50);
        gpsIcon.setLayoutParams(layoutParams);
        gpsIcon.setOnClickListener(new View.OnClickListener() {
            LocationManager mLocManager;
            LocationListener mlocListener;
            @Override
            public void onClick(View view) {
                gpsNavigation = !gpsNavigation;
                gpsIcon.setImageDrawable((gpsNavigation)?getResources().getDrawable(R.drawable.gps_on):getResources().getDrawable(R.drawable.gps_off));
                if(gpsNavigation){
                    mLocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                    mlocListener = new MapAppLocationListener();
                    mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
                    mLocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);
                } else {
                    mLocManager.removeUpdates(mlocListener);
                    mLocManager = null;
                }
            }
        });

        final RelativeLayout relativeLayout = new RelativeLayout(this); // разметка
        map = new MapView(this, 256); // наша карта

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        // добавим карту в разметку
        relativeLayout.addView(map, new RelativeLayout.LayoutParams(MapView.LayoutParams.FILL_PARENT,
                MapView.LayoutParams.FILL_PARENT));
        relativeLayout.addView(gpsIcon);
        setContentView(relativeLayout);
        map.setUseDataConnection(true);
        map.setClickable(true);

        map.setTileSource(TileSourceFactory.MAPNIK);//using OpenStreetMaps
        map.getController().setZoom(12);
        map.setMaxZoomLevel(20);
        map.setMinZoomLevel(12);
    }

    public void setStartPointPosition(double latittude, double longitude){
        GeoPoint startPoint = new GeoPoint(latittude, longitude);
        map.getController().setCenter(startPoint);
    }

    public void drawMarkersOnMap(ArrayList<PrayerPlace> prayerPlaces){
        for(int i = 0; i < prayerPlaces.size(); i++){
            CustomMarker tempMarker = generateMarkerFromPrayerPlaceObject(prayerPlaces.get(i));
            map.getOverlays().add(tempMarker);
        }
    }

    public CustomMarker generateMarkerFromPrayerPlaceObject(PrayerPlace prayerPlace){
        CustomMarker tempMarker = new CustomMarker(map);
        tempMarker.setPosition(new GeoPoint(prayerPlace.latitude, prayerPlace.longitude));
        tempMarker.setIcon(getMarkerIconFromTypeAndGender(prayerPlace));
        tempMarker.setTitle(prayerPlace.name);
        tempMarker.setSubDescription(prayerPlace.address + "\n" + prayerPlace.description);
        return tempMarker;
    }

    public class CustomMarker extends Marker {
        public CustomMarker(MapView mapView) {
            super(mapView);
        }

        @Override
        public void setOnMarkerClickListener(OnMarkerClickListener listener) {
            super.setOnMarkerClickListener(listener);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event, MapView mapView) {
            float coordinateX = event.getX();
            float coordinateY = event.getY();
            MapView.Projection projection = mapView.getProjection();
            GeoPoint tappedGeoPoint = (GeoPoint) projection.fromPixels(coordinateX, coordinateY);
            drawNearestPointFromMyPosition(tappedGeoPoint);
            return true;
        }



    }

    private void drawNearestPointFromMyPosition(GeoPoint myPosition){
        MyPosition.latitude = myPosition.getLatitude();
        MyPosition.longitude = myPosition.getLongitude();

        hideInfoWindowOfAllCustomMarker();
        map.getOverlays().clear();

        drawNearestPoint();
        drawMyPosition();

        map.invalidate();
    }

    CustomMarker nearPrayerPlaceMarker;
    public void drawNearestPoint(){
        PointCalculates pointCalculates = new PointCalculates(prayerPlaceArrayList);
        pointCalculates.sortPointsFromMyPosition();
        ArrayList<PrayerPlace> sortedPoints = pointCalculates.prayerPlaceArrayList;
        PrayerPlace nearPrayerPlace = sortedPoints.get(0);
        nearPrayerPlaceMarker = generateMarkerFromPrayerPlaceObject(nearPrayerPlace);
        map.getOverlays().add(nearPrayerPlaceMarker);

        nearPrayerPlaceMarker.setInfoWindow(new MarkerInfoWindow(R.layout.bonuspack_bubble, map));
        nearPrayerPlaceMarker.showInfoWindow();

        nearPrayerPlaceMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                String ss = "hi msn";
                return true;
            }
        });

        nearPrayerPlaceMarker.setDraggable(true);
    }

    public void drawMyPosition(){
        CustomMarker myPositionMarker = new CustomMarker(map);
        myPositionMarker.setDraggable(true);
        myPositionMarker.setTitle("My position");
        myPositionMarker.setPosition(new GeoPoint(MyPosition.latitude, MyPosition.longitude));
        map.getOverlays().add(myPositionMarker);
        myPositionMarker.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                drawNearestPointFromMyPosition(marker.getPosition());
            }

            @Override
            public void onMarkerDragStart(Marker marker) {

            }
        });
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




    public class MapAppLocationListener implements LocationListener {


        @Override
        public void onLocationChanged(Location loc) {
             MyPosition.latitude = loc.getLatitude();
            MyPosition.longitude = loc.getLongitude();

            String Text =
            "Latitude = " + loc.getLatitude() + "\n" +
            "Longitude = " + loc.getLongitude();

            Toast.makeText( getApplicationContext(),
                    Text,
                    Toast.LENGTH_SHORT).show();

            if(!gpsNavigation)
                return;

            drawMyPosition();
            drawNearestPointFromMyPosition(MyPosition.getMyPositionGeoPoint());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

            Toast.makeText(getApplicationContext(),
                    "Gps status changed",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String s) {

            Toast.makeText(getApplicationContext(),
                    "Gps Enabled",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String s) {

            Toast.makeText(getApplicationContext(),
                    "Gps Disabled",
                    Toast.LENGTH_SHORT).show();
        }
    }
}