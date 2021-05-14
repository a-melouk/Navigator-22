package com.esi.navigator_22;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;

public class SubwayStationsActivity extends AppCompatActivity {
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subway_stations);

        listView = findViewById(R.id.listSubway);
        Log.d("LogGpsSecondActivity", String.valueOf(MainActivity.stations.size()));

        StationAdapter stationAdapter = new StationAdapter(this,R.layout.list_row,MainActivity.stations);
        listView.setAdapter(stationAdapter);


    }
}