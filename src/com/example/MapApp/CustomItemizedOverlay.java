package com.example.MapApp;

import android.content.Context;
import android.graphics.Point;
import android.view.MotionEvent;
import com.google.common.collect.Lists;
import org.osmdroid.api.IMapView;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.List;

/**
 * Created by respect on 3/12/14.
 */
public class CustomItemizedOverlay extends ItemizedIconOverlay {


    public CustomItemizedOverlay(Context context, List list) {
        super(context, list, null);
    }

    static float latitude, longitude;

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event, MapView mapView) {
        float coordinateX = event.getX();
        float coordinateY = event.getY();
        MapView.Projection projection = mapView.getProjection();
        GeoPoint tappedGeoPoint = (GeoPoint) projection.fromPixels(coordinateX, coordinateY);
        latitude = (float)tappedGeoPoint.getLatitude();
        longitude = (float)tappedGeoPoint.getLongitude();
        MyActivity.mMap.getOverlays().clear();
        MyActivity.mMap.callOnClick();
        return true;//super.onSingleTapConfirmed(event, mapView);
    }
}
