package com.example.MapApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MenuActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        ListView menuList = (ListView)findViewById(R.id.ListView_Menu);

        String[] menuListItems = {
                getResources().getString(R.string.menu_item_map),
                getResources().getString(R.string.menu_item_list),
                getResources().getString(R.string.menu_item_settings),
                getResources().getString(R.string.menu_item_help),
                getResources().getString(R.string.menu_item_quit)
        };

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.menu_item, menuListItems);
        menuList.setAdapter(arrayAdapter);

        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = (TextView)view;
                String clickedItemText = textView.getText().toString();
                if(clickedItemText.equalsIgnoreCase(getResources().getString(R.string.menu_item_map))){
                    startActivity(new Intent(MenuActivity.this, MapActivity.class));
                } else if(clickedItemText.equalsIgnoreCase(getResources().getString(R.string.menu_item_list))){
                    startActivity(new Intent(MenuActivity.this, ListActivity.class));
                } else if(clickedItemText.equalsIgnoreCase(getResources().getString(R.string.menu_item_settings))){
                    startActivity(new Intent(MenuActivity.this, SettingsActivity.class));
                } else if (clickedItemText.equalsIgnoreCase(getResources().getString(R.string.menu_item_quit))){
                    finish();
                }
            }
        });
    }
}