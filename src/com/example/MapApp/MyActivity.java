package com.example.MapApp;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private MapView mMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        final RelativeLayout rl = new RelativeLayout(this); // разметка
        mMap = new MapView(this, 256); // наша карта

        //разрешаем встроенные кнопки изменения масштаба

        mMap.setBuiltInZoomControls(true);
        // добавим карту в разметку
        rl.addView( mMap, new RelativeLayout.LayoutParams(MapView.LayoutParams.FILL_PARENT,
                MapView.LayoutParams.FILL_PARENT));
        setContentView(rl);

        mMap.setTileSource(TileSourceFactory.MAPNIK);
        mMap.getController().setZoom(12);

        GeoPoint p = new GeoPoint(42.8946, 74.6079);
        mMap.getController().animateTo(p);

        ArrayList items = new ArrayList();
//        items.add(new OverlayItem("Казань", "Татарстан", new GeoPoint(42.8946, 74.6079)));
//        items.add(new OverlayItem("Мечеть Ош рынок", "Бишкек", new GeoPoint(42.87923, 74.57251)));
//        items.add(new OverlayItem("намазкана BetaStores", "Бишкек", new GeoPoint(42.87611, 74.59225)));
//        items.add(new OverlayItem("ie","Central Mosque","this is description", new GeoPoint(42.86939, 74.62152)));

        String stringXmlContent;
        try {
            items = getEventsFromAnXML(this);
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    //
    ItemizedIconOverlay MyLocationOverlay = new ItemizedIconOverlay(this, items, null);
    mMap.getOverlays().add(MyLocationOverlay);
}

    private ArrayList getEventsFromAnXML(Activity activity)
            throws XmlPullParserException, IOException
    {
        ArrayList items = new ArrayList();
        String[] overlay = new String[2];
        XmlResourceParser xpp = getResources().getXml(R.xml.geopoints);
        xpp.next();
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            if(eventType == XmlPullParser.START_TAG)
            {
                if("pointx" == xpp.getName()){
                    overlay[0] = xpp.getText();
                }
                else if("pointy" == xpp.getName()){
                    overlay[1] = xpp.getText();

                    items.add(new OverlayItem("some title", "some snippet", new GeoPoint(Integer.parseInt(overlay[0]), Integer.parseInt(overlay[1]))));
                }
            }
        }
        return items;
        //
    }
}
