package com.example.MapApp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.MapApp.Main.XmlReader;
import com.example.MapApp.PrayerPlace.PrayerPlace;

import java.util.ArrayList;

/**
 * Created by respect on 3/17/14.
 */
public class ListActivity extends Activity {

    ArrayList<PrayerPlace> prayerPlaceArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        allPrayerPlacesTab.setIndicator(getResources().getString(R.string.all_places),
                getResources().getDrawable(android.R.drawable.star_on));
        allPrayerPlacesTab.setContent(R.id.ScrollViewAllPlaces);
        host.addTab(allPrayerPlacesTab);

        TabHost.TabSpec malePrayerPlacesTab = host.newTabSpec("malePrayerPlacesTab");
        malePrayerPlacesTab.setIndicator(getResources().getString(R.string.male_places),
                getResources().getDrawable(android.R.drawable.star_on));
        malePrayerPlacesTab.setContent(R.id.ScrollViewMalePlaces);
        host.addTab(malePrayerPlacesTab);

        TabHost.TabSpec femalePrayerPlacesTab = host.newTabSpec("femalePrayerPlacesTab");
        femalePrayerPlacesTab.setIndicator(getResources().getString(R.string.female_places),
                getResources().getDrawable(android.R.drawable.star_on));
        femalePrayerPlacesTab.setContent(R.id.ScrollViewFemalePlaces);
        host.addTab(femalePrayerPlacesTab);

        host.setCurrentTabByTag("allTab");

        TableLayout allPlacesTable = (TableLayout) findViewById(R.id.TableLayout_AllPlaces);
        TableLayout malePlacesTable = (TableLayout) findViewById(R.id.TableLayout_MalePlaces);
        TableLayout femalePlacesTable = (TableLayout) findViewById(R.id.TableLayout_FemalePlaces);

        initializeHeaderRow(allPlacesTable);
        initializeHeaderRow(malePlacesTable);
        initializeHeaderRow(femalePlacesTable);

        for(int i = 0; i < prayerPlaceArrayList.size(); i++){
            PrayerPlace tempPrayerPlace = prayerPlaceArrayList.get(i);

            switch (tempPrayerPlace.prayerPlaceGender){
                case MALE:
                    insertPlaceRow(malePlacesTable, tempPrayerPlace);
                    break;
                case FEMALE:
                    insertPlaceRow(femalePlacesTable, tempPrayerPlace);
                    break;
                case JOINT:
                    insertPlaceRow(malePlacesTable, tempPrayerPlace);
                    insertPlaceRow(femalePlacesTable, tempPrayerPlace);
            }

            insertPlaceRow(allPlacesTable, tempPrayerPlace);
        }
    }

    private void initializeHeaderRow(TableLayout placesTable) {
        // Create the Table header row
        TableRow headerRow = new TableRow(this);
        int textColor = getResources().getColor(R.color.logo_color);
        float textSize = getResources().getDimension(R.dimen.help_text_size);
        addTextToRowWithValues(headerRow, getResources().getString(R.string.name), textColor, textSize);
        addTextToRowWithValues(headerRow, getResources().getString(R.string.address), textColor, textSize);
        addTextToRowWithValues(headerRow, getResources().getString(R.string.type), textColor, textSize);
        placesTable.addView(headerRow);
    }

    private void insertPlaceRow(final TableLayout scoreTable, PrayerPlace prayerPlace) {
        final TableRow newRow = new TableRow(ListActivity.this);

        int textColor = getResources().getColor(R.color.title_color);
        float textSize = getResources().getDimension(R.dimen.help_text_size);

        addTextToRowWithValues(newRow, prayerPlace.name, textColor, textSize);
        addTextToRowWithValues(newRow, prayerPlace.description, textColor, textSize);
        addTextToRowWithValues(newRow, prayerPlace.address, textColor, textSize);
        scoreTable.addView(newRow);
    }

    private void addTextToRowWithValues(final TableRow tableRow, String text, int textColor, float textSize) {
        TextView textView = new TextView(this);
        textView.setTextSize(textSize);
        textView.setTextColor(textColor);
        textView.setText(text);
        tableRow.addView(textView);
    }
}
