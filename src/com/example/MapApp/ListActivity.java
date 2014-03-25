package com.example.MapApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.example.MapApp.Main.XmlReader;
import com.example.MapApp.PrayerPlace.PrayerPlace;

import java.util.ArrayList;

/**
 * Created by respect on 3/17/14.
 */
public class ListActivity extends Activity {

    private static int placeNumber;
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
//                    insertPlaceRow(malePlacesTable, tempPrayerPlace);
                    insertPlace(malePlacesTable, tempPrayerPlace, i);
                    break;
                case FEMALE:
                    insertPlaceRow(femalePlacesTable, tempPrayerPlace);
                    break;
                case JOINT:
//                    insertPlaceRow(malePlacesTable, tempPrayerPlace);
                    insertPlaceRow(femalePlacesTable, tempPrayerPlace);
            }

            insertPlaceRow(allPlacesTable, tempPrayerPlace);
        }
        registerForContextMenu(host);
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
//        return super.onContextItemSelected(item);
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        //  info.position will give the index of selected item
//        int IndexSelected=info.position;
        if(item.getTitle().equals(getString(R.string.list_item_context_menu_show_on_map)))
        {
            MainActivity.prayerPlaceFromList = prayerPlaceArrayList.get(placeNumber);
            startActivity(new Intent(this, MapActivity.class));
            // Code to execute when clicked on This Item
        }
        else
        {
            return false;
        }
        return true;
    }

    private void insertPlace(TableLayout malePlacesTable, PrayerPlace prayerPlace, int placeNumber) {
        TextView textView = new TextView(this);
        textView.setTextSize(getResources().getDimension(R.dimen.list_item_size));
        textView.setTextColor(getResources().getColor(R.color.list_item_color));
        textView.setText(prayerPlace.getPlaceTypeString(getResources()) + " - " + prayerPlace.name + "\n" + prayerPlace.address + "\n" + prayerPlace.description + "\n" + prayerPlace.getPlaceGenderString(getResources()));
        textView.setSingleLine(false);
        textView.setId(placeNumber);
        int ss = textView.getId();
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListActivity.placeNumber = view.getId();
                view.showContextMenu();
            }
        });
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                view.showContextMenu();
                return true;
            }
        });
        malePlacesTable.addView(textView);
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
