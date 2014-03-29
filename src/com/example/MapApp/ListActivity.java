package com.example.MapApp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.example.MapApp.Main.XmlReader;
import com.example.MapApp.PrayerPlace.PrayerPlace;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by respect on 3/17/14.
 */
public class ListActivity extends Activity {

    private static int placeNumber;
    ArrayList<PrayerPlace> prayerPlaceArrayList;
    TableLayout allPlacesTable;
    int mProgressCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.list);

        XmlReader xmlReader = new XmlReader(getResources().getXml(R.xml.geopoints));
        try {
            xmlReader.readPrayerPlaceListFromXML();
            prayerPlaceArrayList = xmlReader.getPrayerPlaceList();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        TabHost host = (TabHost)findViewById(R.id.TabHost1);
        host.setup();

        TabHost.TabSpec allPrayerPlacesTab = host.newTabSpec("allPrayerPlacesTab");
        allPrayerPlacesTab.setIndicator(getResources().getString(R.string.list_all_places),
                getResources().getDrawable(android.R.drawable.star_on));
        allPrayerPlacesTab.setContent(R.id.ScrollViewAllPlaces);
        host.addTab(allPrayerPlacesTab);

        TabHost.TabSpec malePrayerPlacesTab = host.newTabSpec("malePrayerPlacesTab");
        malePrayerPlacesTab.setIndicator(getResources().getString(R.string.list_male_places_text),
                getResources().getDrawable(android.R.drawable.star_on));
        malePrayerPlacesTab.setContent(R.id.ScrollViewMalePlaces);
        host.addTab(malePrayerPlacesTab);

        TabHost.TabSpec femalePrayerPlacesTab = host.newTabSpec("femalePrayerPlacesTab");
        femalePrayerPlacesTab.setIndicator(getResources().getString(R.string.list_female_places_text),
                getResources().getDrawable(android.R.drawable.star_on));
        femalePrayerPlacesTab.setContent(R.id.ScrollViewFemalePlaces);
        host.addTab(femalePrayerPlacesTab);

        host.setCurrentTabByTag("allPrayerPlacesTab");

        allPlacesTable = (TableLayout) findViewById(R.id.TableLayout_AllPlaces);
        TableLayout malePlacesTable = (TableLayout) findViewById(R.id.TableLayout_MalePlaces);
        TableLayout femalePlacesTable = (TableLayout) findViewById(R.id.TableLayout_FemalePlaces);

//        initializeHeaderRow(allPlacesTable);
//        initializeHeaderRow(malePlacesTable);
//        initializeHeaderRow(femalePlacesTable);


        for(int i = 0; i < prayerPlaceArrayList.size(); i++){
            PrayerPlace tempPrayerPlace = prayerPlaceArrayList.get(i);

            switch (tempPrayerPlace.prayerPlaceGender){
                case MALE:
//                    insertPlaceRow(malePlacesTable, tempPrayerPlace);
                    insertPlace(malePlacesTable, tempPrayerPlace, i);
                    break;
                case FEMALE:
//                    insertPlaceRow(femalePlacesTable, tempPrayerPlace);
                    insertPlace(femalePlacesTable, tempPrayerPlace, i);
                    break;
                case JOINT:
//                    insertPlaceRow(malePlacesTable, tempPrayerPlace);
//                    insertPlaceRow(femalePlacesTable, tempPrayerPlace);
                    insertPlace(malePlacesTable, tempPrayerPlace, i);
                    insertPlace(femalePlacesTable, tempPrayerPlace, i);
            }

//            insertPlaceRow(allPlacesTable, tempPrayerPlace);
            insertPlace(allPlacesTable, tempPrayerPlace, i);
        }
        registerForContextMenu(host);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(getString(R.string.options_menu_get_data_from_server));
        return true;
    }
    ProgressDialog pleaseWaitDialog;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getTitle().equals(getString(R.string.options_menu_get_data_from_server))){
//            TableLayout allPlacesTable = (TableLayout) findViewById(R.id.TableLayout_AllPlaces);
//
//            DataDownloaderTask dataDownloader = new DataDownloaderTask();
//            dataDownloader.execute(TRIVIA_SERVER_POINTS, allPlacesTable);
        }
        return true;
    }

    public static final String TRIVIA_SERVER_BASE = "http://better.hol.es/points/";
    public static final String TRIVIA_SERVER_POINTS = TRIVIA_SERVER_BASE + "prayer_rooms.xml";


    public class DataDownloaderTask extends AsyncTask<Object, String, Boolean>{

        TableLayout table;
        ArrayList<PrayerPlace> prayerPlaceArrayListRemoteServer;

        private static final String DEBUG_TAG = "ScoreDownloaderTask";

        @Override
        protected Boolean doInBackground(Object... params) {
            boolean result = false;
            String pathToPoints = (String) params[0];
            table = (TableLayout) params[1];

            XmlPullParser xmlPullParser = null;
            XmlReader points = null;
            URL xmlUrl = null;
            try {
                xmlUrl = new URL(pathToPoints);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                xmlPullParser = XmlPullParserFactory.newInstance().newPullParser();
                try {
                    xmlPullParser.setInput(xmlUrl.openStream(), null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            points = new XmlReader(xmlPullParser);
            if (points != null) {
                try {
                    points.readPrayerPlaceListFromXML();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                prayerPlaceArrayListRemoteServer = points.getPrayerPlaceList();
                processScores(prayerPlaceArrayListRemoteServer);
            }
            return result;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            Log.i(DEBUG_TAG, "onPostExecute");
            mProgressCounter--;
            if (mProgressCounter <= 0) {
                mProgressCounter = 0;
                ListActivity.this.setProgressBarIndeterminateVisibility(false);
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (values.length == 1) {
                int Value = Integer.parseInt(values[0]);
                insertPlace(table, prayerPlaceArrayListRemoteServer.get(Value), Value);
            } else {
                final TableRow newRow = new TableRow(ListActivity.this);
                TextView noResults = new TextView(ListActivity.this);
                noResults.setText(getResources().getString(R.string.no_scores));
                newRow.addView(noResults);
                table.addView(newRow);
            }
        }

        private void processScores(ArrayList<PrayerPlace> prayerPlaceArrayList) {
            for(int i = 0; i < prayerPlaceArrayList.size(); i++){
                publishProgress(String.valueOf(i));
            }
        }

        @Override
        protected void onPreExecute() {
            mProgressCounter++;
            ListActivity.this.setProgressBarIndeterminateVisibility(true);
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
//        menu.setHeaderTitle("the context man");
        int id = v.getId();
        menu.add(0, id, 0, getString(R.string.list_item_context_menu_show_on_map));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(getString(R.string.list_item_context_menu_show_on_map)))
        {
            MainActivity.prayerPlaceFromList = prayerPlaceArrayList.get(placeNumber);
            startActivity(new Intent(this, MapActivity.class));
        }
        else
        {
            return false;
        }
        return true;
    }

    private void insertPlace(TableLayout table, PrayerPlace prayerPlace, int placeNumber) {
        RelativeLayout relativeLayout = new RelativeLayout(this);
        ImageView imageViewPlaceIcon = new ImageView(this);
        imageViewPlaceIcon.setImageDrawable(prayerPlace.getMarkerIconFromTypeAndGender(getResources()));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        imageViewPlaceIcon.setLayoutParams(layoutParams);

        ImageView imageViewDivider = new ImageView(this);
        RelativeLayout.LayoutParams layoutParamsForDivider = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, 2);
        layoutParamsForDivider.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        imageViewDivider.setBackgroundColor(getResources().getColor(R.color.divider_backgroud_color));
        imageViewDivider.setLayoutParams(layoutParamsForDivider);

        TextView textView = new TextView(this);
        textView.setTextSize(getResources().getDimension(R.dimen.list_item_size));
        textView.setTextColor(getResources().getColor(R.color.list_item_color));
        textView.setText(getPrayerPlaceToStringAllParams(prayerPlace));
        textView.setSingleLine(false);
        textView.setId(placeNumber);

        relativeLayout.setId(placeNumber);

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListActivity.placeNumber = view.getId();
                view.showContextMenu();
            }
        });

        relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ListActivity.placeNumber = view.getId();
                view.showContextMenu();
                return true;
            }
        });
        relativeLayout.addView(imageViewPlaceIcon);
        relativeLayout.addView(textView);
        relativeLayout.addView(imageViewDivider);
        table.addView(relativeLayout);
    }

    private String getPrayerPlaceToStringAllParams(PrayerPlace prayerPlace){
        String prayerPlaceString = "";
        prayerPlaceString += prayerPlace.getPlaceTypeString(getResources()) + " - " + prayerPlace.name + "\n";
        prayerPlaceString += prayerPlace.address + "\n";
        prayerPlaceString += prayerPlace.description;
        return prayerPlaceString;
    }

    private void initializeHeaderRow(TableLayout placesTable) {
        TableRow headerRow = new TableRow(this);
        int textColor = getResources().getColor(R.color.list_header_color);
        float textSize = getResources().getDimension(R.dimen.list_header_size);
        addTextToRowWithValues(headerRow, getResources().getString(R.string.list_place_name), textColor, textSize);
        addTextToRowWithValues(headerRow, getResources().getString(R.string.list_place_address), textColor, textSize);
        addTextToRowWithValues(headerRow, getResources().getString(R.string.list_place_type), textColor, textSize);
        placesTable.addView(headerRow);
    }

    private void insertPlaceRow(TableLayout placesTable, PrayerPlace prayerPlace) {
        TableRow newRow = new TableRow(ListActivity.this);

        int textColor = getResources().getColor(R.color.list_item_color);
        float textSize = getResources().getDimension(R.dimen.list_item_size);

        addTextToRowWithValues(newRow, prayerPlace.name, textColor, textSize);
        addTextToRowWithValues(newRow, prayerPlace.address, textColor, textSize);
        addTextToRowWithValues(newRow, prayerPlace.getPlaceGenderString(getResources()), textColor, textSize);
        placesTable.addView(newRow);
    }

    private void addTextToRowWithValues(final TableRow tableRow, String text, int textColor, float textSize) {
        TextView textView = new TextView(this);
        textView.setTextSize(textSize);
        textView.setTextColor(textColor);
        textView.setText(text);
        tableRow.addView(textView);
    }
}
