package com.esi.navigator_22;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.bonuspack.routing.GraphHopperRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class AllNearTramwayStationsActivity extends AppCompatActivity {
    GeoPoint currentLocation;
    ListView listView;
    MyLocationNewOverlay myLocationNewOverlay;
    double distanceTo;
    int timeTo;
    Thread t1;
    NumberFormat df;
    DbHelper database = DbHelper.getInstance(this);

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tramway_stations);
        String format = "0.00";
        df = new DecimalFormat(format);
        Bundle b = getIntent().getExtras();
        double currentLocationLatitude = b.getDouble("currentLocationLatitude");
        double currentLocationLongitude = b.getDouble("currentLocationLongitude");
        currentLocation = new GeoPoint(currentLocationLatitude, currentLocationLongitude);
        myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()), MainActivity.myMap);
        myLocationNewOverlay.enableMyLocation();

        ArrayList<StationDetails> myList = new ArrayList<>();

        listView = findViewById(R.id.listTramway);
        TextView loading = findViewById(R.id.whileLoading);

        StationAdapter stationAdapter = new StationAdapter(this, R.layout.row_list_stations, myList);


//        setContentView(R.layout.activity_subway_stations);
        ProgressDialog barProgressDialog = new ProgressDialog(this);

        barProgressDialog.setTitle("Recupérations des stations les plus proches ...");
        barProgressDialog.setMessage("Download in progress ...");
        barProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(MainActivity.stationsTramway.size());
        barProgressDialog.show();
        barProgressDialog.setCancelable(true);
        t1 = new Thread(() -> {
//            Log.d("databaseDelete13", String.valueOf(database.getAllNearestSubStationsSortedByDistance().size()));
            database.deleteAllNearestTramwayStation();
//            Log.d("databaseDelete14", String.valueOf(database.getAllNearestSubStationsSortedByDistance().size()));

//            Log.d("databaseDelete5", String.valueOf(getDistanceOffline(new GeoPoint(35.2065503, -0.6191647), new GeoPoint(0.0, 0.0))));

//            MainActivity.stations.size()
            for (int i = 0; i < MainActivity.stationsTramway.size(); i++) {
                Log.d("TestAllNearest", i + "");
                barProgressDialog.incrementProgressBy(1);
                StationDetails availableStation = new StationDetails();
                availableStation.type = MainActivity.stationsTramway.get(i).type;
                availableStation.nomFr = MainActivity.stationsTramway.get(i).nomFr;
                availableStation.numero = MainActivity.stationsTramway.get(i).numero;
                getRouteOnlineOnFoot(MainActivity.stationsTramway.get(i).coordonnees);
                availableStation.distanceTo = distanceTo;
                availableStation.timeTo = timeTo;
                Log.d("allstation13", MainActivity.stationsTramway.get(i).nomFr + " | " + getDistanceOffline(currentLocation, MainActivity.stationsTramway.get(i).coordonnees));
                Log.d("allstation14", MainActivity.stationsTramway.get(i).nomFr + " | " + availableStation.distanceTo);
                database.addNearStation(availableStation);

                Log.d("progressBar11", String.valueOf(barProgressDialog.getProgress()));
                Log.d("progressBar12", String.valueOf(i));

            }

            runOnUiThread(() -> {
                for (int i = 0; i < MainActivity.stationsTramway.size(); i++) {
                    myList.add(database.getAllNearestSubStationsSortedByTime().get(i));
                    Log.d("Distance online", myList.get(i).nomFr + " | " + myList.get(i).distanceTo + " | " + myList.get(i).timeTo);
                    if (barProgressDialog.getProgress() == MainActivity.stationsTramway.size())
                        barProgressDialog.dismiss();

                }
                loading.setVisibility(View.INVISIBLE);
                listView.setAdapter(stationAdapter);
            });

        });
        t1.start();
    }


    private void getRouteOnlineOnFoot(GeoPoint geoPoint) {
        ArrayList<GeoPoint> roadPoints = new ArrayList<>();
        roadPoints.add((currentLocation));
        roadPoints.add(geoPoint);

//        OSRMRoadManager roadManager = new OSRMRoadManager(getApplicationContext(), "22-Transport");
//        roadManager.setMean(OSRMRoadManager.MEAN_BY_FOOT);
//        Road road = roadManager.getRoad(roadPoints);
        RoadManager roadManager1 = new GraphHopperRoadManager("484e2932-b8a9-4bfa-a760-d3f32f84e347", false);
        roadManager1.addRequestOption("vehicle=foot");
        Road road = roadManager1.getRoad(roadPoints);

        if (road.mLength == 0) {
            distanceTo = getDistanceOffline(currentLocation, geoPoint);

            Log.d("allStation", "Unavailable " + getDistanceOffline(currentLocation, geoPoint));
            timeTo = 99999;
        } else {
            Log.d("allStation", "Available " + getDistanceOffline(currentLocation, geoPoint));
            distanceTo = road.mLength;
            Log.d("Formatting", String.valueOf(distanceTo));
            timeTo = (int) Math.round(road.mDuration / 60);
        }

    }


    private void getRouteOnlineOnVehicle(GeoPoint geoPoint) {
        ArrayList<GeoPoint> roadPoints = new ArrayList<>();
        roadPoints.add(geoPoint);
        roadPoints.add((currentLocation));

        OSRMRoadManager roadManager = new OSRMRoadManager(getApplicationContext(), "22-Transport");
        roadManager.setMean(OSRMRoadManager.MEAN_BY_CAR);
        Road road = roadManager.getRoad(roadPoints);
//        RoadManager roadManager2 = new GraphHopperRoadManager("484e2932-b8a9-4bfa-a760-d3f32f84e347", false);
//        roadManager2.addRequestOption("vehicle=car");
//        Road road = roadManager2.getRoad(roadPoints);

        distanceTo = road.mLength;
        timeTo = (int) Math.round(road.mDuration / 60);
    }


    private double getDistanceOffline(GeoPoint currentLocation, GeoPoint targetedLocation) {
        float[] distance = new float[2];
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), targetedLocation.getLatitude(), targetedLocation.getLongitude(), distance);
        return distance[0] / 1000;
    }


}