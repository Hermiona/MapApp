package com.example.MapApp;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import com.example.MapApp.Main.XmlReader;
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }


        ArrayList items = new ArrayList();
//        items.add(new OverlayItem("Казань", "Татарстан", new GeoPoint(42.8946, 74.6079)));
//        items.add(new OverlayItem("Мечеть Ош рынок", "Бишкек", new GeoPoint(42.87923, 74.57251)));
//        items.add(new OverlayItem("намазкана BetaStores", "Бишкек", new GeoPoint(42.87611, 74.59225)));
//        items.add(new OverlayItem("ie","Central Mosque","this is description", new GeoPoint(42.86939, 74.62152)));

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

    private ArrayList getOverlayItemArrayListFromArrayFloat(float[][] geoPoints)
    {
        ArrayList items = new ArrayList();
        for(int i = 0; i < geoPoints.length; i++){
            items.add(new OverlayItem("Казань", "Татарстан", new GeoPoint(geoPoints[i][0], geoPoints[i][1])));
        }
        return items;
    }

    public void getNearestPoints(float latitude, float longitude){
        MyActivity.map.getOverlays().clear();
    }

    float[][] getGeoPointsFromXML() throws IOException, XmlPullParserException
    {
        float[][] items = new float[XMLGeoPointsCount][2];

        XmlResourceParser xpp = getResources().getXml(R.xml.geopoints);
        xpp.next();
        int eventType = xpp.getEventType();
        int i = 0;
        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            if(eventType == XmlPullParser.START_TAG)
            {
                String xpp_name = xpp.getName();
                if(xpp_name.equals("point")){
                    String pt = xpp.getText();
                    xpp.next();
                    String pnt = xpp.getText();
                    xpp.next();
                    String pnt2 = xpp.getText();
                }
                if("pointx".hashCode() == xpp_name.hashCode()){
                    xpp.next();
                    String str_pointx = xpp.getText().trim();
                    items[i][0] = Float.parseFloat(str_pointx);
                }
                else if("pointy".hashCode() == xpp_name.hashCode()){
                    xpp.next();
                    items[i][1] = Float.parseFloat(xpp.getText());
                    i++;
                }
            }
            eventType = xpp.next();
        }
        return items;
    }


}