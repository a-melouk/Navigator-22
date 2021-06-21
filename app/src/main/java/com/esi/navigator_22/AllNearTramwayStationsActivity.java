package com.esi.navigator_22;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.bonuspack.routing.GraphHopperRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class AllNearTramwayStationsActivity extends AppCompatActivity {
    GeoPoint currentLocation;
    ListView listView;
    double distanceTo, timeTo;
    Thread t1;
    ArrayList<StationDetails> myList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tramway_stations);
        Bundle b = getIntent().getExtras();
        double currentLocationLatitude = b.getDouble("currentLocationLatitude");
        double currentLocationLongitude = b.getDouble("currentLocationLongitude");
        currentLocation = new GeoPoint(currentLocationLatitude, currentLocationLongitude);

        myList = new ArrayList<>();

        listView = findViewById(R.id.listTramway);
        TextView loading = findViewById(R.id.whileLoading);

        StationAdapter stationAdapter = new StationAdapter(this, R.layout.row_list_stations, myList);


//        setContentView(R.layout.activity_subway_stations);
        ProgressDialog barProgressDialog = new ProgressDialog(this);
        barProgressDialog.setTitle("Recupérations des stations ...");
        barProgressDialog.setMessage("Download in progress ...");
        barProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(MainActivity.stationsTramway.size());
        barProgressDialog.show();
        barProgressDialog.setCancelable(false);

        ArrayList<StationDetails> plusProchesOnline = new ArrayList<>();
        t1 = new Thread(() -> {

            for (int i = 0; i < MainActivity.stationsTramway.size(); i++) {

                StationDetails availableStation = new StationDetails();
                availableStation.nomFr = MainActivity.stationsTramway.get(i).nomFr;
                availableStation.numero = MainActivity.stationsTramway.get(i).numero;
                availableStation.type = MainActivity.stationsTramway.get(i).type;
                availableStation.coordonnees = MainActivity.stationsTramway.get(i).coordonnees;
                getRouteOnlineOnFoot(MainActivity.stationsTramway.get(i).coordonnees);
                availableStation.distanceTo = distanceTo;
                availableStation.timeTo = timeTo;
                plusProchesOnline.add(availableStation);
                Log.d("LesPlusProches1", plusProchesOnline.toString());
                barProgressDialog.incrementProgressBy(1);
                if (i == 21) {
                    barProgressDialog.dismiss();
                }
            }

            runOnUiThread(() -> {
                sort(plusProchesOnline, "time");
                Log.d("LesPlusProches2", plusProchesOnline.toString());
                myList.addAll(plusProchesOnline);
                loading.setVisibility(View.INVISIBLE);
                listView.setAdapter(stationAdapter);
            });
        });
        t1.start();
    }

    private void sort(ArrayList<StationDetails> plusProches, String criteria) {

        StationDetails temp;
        if (criteria.equals("distance"))
            for (int i = 0; i < plusProches.size() - 1; i++)
                for (int j = i; j < plusProches.size(); j++) {
                    if (plusProches.get(i).distanceTo > plusProches.get(j).distanceTo) {
                        temp = plusProches.get(i);
                        plusProches.set(i, plusProches.get(j));
                        plusProches.set(j, temp);
                    }
                }
        else if (criteria.equals("time"))
            for (int i = 0; i < plusProches.size() - 1; i++)
                for (int j = i; j < plusProches.size(); j++) {
                    if (plusProches.get(i).timeTo > plusProches.get(j).timeTo) {
                        temp = plusProches.get(i);
                        plusProches.set(i, plusProches.get(j));
                        plusProches.set(j, temp);
                    }
                }
    }

    private void getRouteOnlineOnFoot(GeoPoint geoPoint) {
        ArrayList<GeoPoint> roadPoints = new ArrayList<>();
        roadPoints.add((currentLocation));
        roadPoints.add(geoPoint);

        RoadManager roadManager = new GraphHopperRoadManager(MainActivity.graphhopperkey, false);
        roadManager.addRequestOption("vehicle=foot");
        Road road = roadManager.getRoad(roadPoints);
        if (road.mLength == 0) {
            distanceTo = getDistanceOffline(currentLocation, geoPoint);
            timeTo = getDistanceOffline(currentLocation, geoPoint) / 0.4;
        } else {
            distanceTo = road.mLength;
            timeTo = road.mDuration / 60;
        }
    }

    private double getDistanceOffline(GeoPoint currentLocation, GeoPoint targetedLocation) {
        float[] distance = new float[2];
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), targetedLocation.getLatitude(), targetedLocation.getLongitude(), distance);
        return distance[0] / 1000;
    }

}