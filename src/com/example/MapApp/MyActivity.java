package com.example.MapApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.res.XmlResourceParser;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapView;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    public static MapView mMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        final RelativeLayout relativeLayout = new RelativeLayout(this); // разметка
        mMap = new MapView(this, 256); // наша карта

        //разрешаем встроенные кнопки изменения масштаба

        mMap.setBuiltInZoomControls(true);
        // добавим карту в разметку
        relativeLayout.addView(mMap, new RelativeLayout.LayoutParams(MapView.LayoutParams.FILL_PARENT,
                MapView.LayoutParams.FILL_PARENT));
        setContentView(relativeLayout);
        mMap.setUseDataConnection(false);

        mMap.setTileSource(TileSourceFactory.MAPNIK);
        mMap.getController().setZoom(12);

        GeoPoint p = new GeoPoint(42.8946, 74.6079);
        mMap.getController().animateTo(p);

        ArrayList items = new ArrayList();
//        items.add(new OverlayItem("Казань", "Татарстан", new GeoPoint(42.8946, 74.6079)));
//        items.add(new OverlayItem("Мечеть Ош рынок", "Бишкек", new GeoPoint(42.87923, 74.57251)));
//        items.add(new OverlayItem("намазкана BetaStores", "Бишкек", new GeoPoint(42.87611, 74.59225)));
//        items.add(new OverlayItem("ie","Central Mosque","this is description", new GeoPoint(42.86939, 74.62152)));

        try {
            items = getEventsFromAnXML();
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //
        CustomItemizedOverlay MyLocationOverlay = new CustomItemizedOverlay(this, items);

        mMap.getOverlays().add(MyLocationOverlay);

        mMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    float[][] geoPoints = getGeoPointsFromXML();
                    float[] absPoints = new float[10];
                    int placeNumber = 0;
                    float minPlace = 0;
                    for (int i = 0; i < geoPoints.length; i++){
                        absPoints[i] = Math.abs(geoPoints[i][0] - CustomItemizedOverlay.latitude) + Math.abs(geoPoints[i][1] - CustomItemizedOverlay.longitude);
                        if(minPlace == 0 || minPlace > absPoints[i]){
                            minPlace = absPoints[i];
                            placeNumber = i;
                        }
                    }
                    ArrayList nearPlace = new ArrayList();
                    nearPlace.add(new OverlayItem("", "", new GeoPoint(geoPoints[placeNumber][0], geoPoints[placeNumber][1])));
                    //nearPlace.add(new OverlayItem("", "", new GeoPoint(CustomItemizedOverlay.latitude, CustomItemizedOverlay.longitude)));
                    //CustomItemizedOverlay MyLocationOverlay = new CustomItemizedOverlay(null, nearPlace);
                    addPoint(nearPlace);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void addPoint(List items){
        mMap.getOverlays().add(new CustomItemizedOverlay(this, items));
    }

    private ArrayList getEventsFromAnXML()
            throws XmlPullParserException, IOException
    {
        ArrayList items = new ArrayList();

        Double pointx = 0.0;
        Double pointy = 0.0;
        XmlResourceParser xpp = getResources().getXml(R.xml.geopoints);
        xpp.next();
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            if(eventType == XmlPullParser.START_TAG)
            {
                String xpp_name = xpp.getName();
                if("pointx".hashCode() == xpp_name.hashCode()){
                    xpp.next();
                    String str_pointx = xpp.getText().trim();
                    pointx = Double.parseDouble(str_pointx);
                }
                else if("pointy".hashCode() == xpp_name.hashCode()){
                    xpp.next();
                    pointy = Double.parseDouble(xpp.getText());

                    items.add(new OverlayItem("some title", "some snippet", new GeoPoint(pointx, pointy)));
                }
            }
            eventType = xpp.next();
        }
        return items;
    }

    public void getNearestPoints(float latitude, float longitude){
        MyActivity.mMap.getOverlays().clear();
    }

    float[][] getGeoPointsFromXML() throws IOException, XmlPullParserException
    {
        float[][] items = new float[10][2];

        XmlResourceParser xpp = getResources().getXml(R.xml.geopoints);
        xpp.next();
        int eventType = xpp.getEventType();
        int i = 0;
        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            if(eventType == XmlPullParser.START_TAG)
            {
                String xpp_name = xpp.getName();
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

class CustomItemizedOverlay extends ItemizedIconOverlay{

    public CustomItemizedOverlay(Context context, List list) {
        super(context, list, null);
    }

    @Override
    protected OverlayItem createItem(int i) {
        return super.createItem(i);
    }

    @Override
    public int size() {
        return super.size();
    }

    static float latitude, longitude;

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event, MapView mapView) {
        float coordinateX = event.getX();
        float coordinateY = event.getY();
        MapView.Projection projection = mapView.getProjection();
        GeoPoint tappedGeoPoint = (GeoPoint) projection.fromPixels(coordinateX, coordinateY);
        latitude = (float)tappedGeoPoint.getLatitudeE6()/1000000;
        longitude = (float)tappedGeoPoint.getLongitudeE6()/1000000;
        MyActivity.mMap.getOverlays().clear();
        MyActivity.mMap.callOnClick();

        return true;//super.onSingleTapConfirmed(event, mapView);
    }

    @Override
    public boolean onSnapToItem(int i, int i2, Point point, IMapView iMapView) {
        return super.onSnapToItem(i, i2, point, iMapView);
    }
}