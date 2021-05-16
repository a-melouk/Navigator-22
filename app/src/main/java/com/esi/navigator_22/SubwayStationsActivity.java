package com.esi.navigator_22;

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

import java.util.ArrayList;

public class SubwayStationsActivity extends AppCompatActivity {
    GeoPoint currentLocation;
    ListView listView;
    TextView loading;
    MyLocationNewOverlay myLocationNewOverlay;
    ArrayList<StationDetails> test = new ArrayList<>();
    double distanceTo;
    double timeTo;
    Thread t1;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subway_stations);
        Bundle b = getIntent().getExtras();
        double currentLocationLatitude = b.getDouble("currentLocationLatitude");
        double currentLocationLongitude = b.getDouble("currentLocationLongitude");
        currentLocation = new GeoPoint(currentLocationLatitude, currentLocationLongitude);
        myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()), MainActivity.myMap);
        myLocationNewOverlay.enableMyLocation();

        ArrayList<StationDetails> stationDetails = new ArrayList<>();

        listView = findViewById(R.id.listSubway);
        TextView loading = findViewById(R.id.whileLoading);


        StationAdapter stationAdapter = new StationAdapter(this, R.layout.list_row, stationDetails);


//        setContentView(R.layout.activity_subway_stations);

        t1 = new Thread(() -> {

//            MainActivity.stations.size()
            for (int i = 0; i < MainActivity.stations.size(); i++) {

                StationDetails stationDetails1 = new StationDetails();
                stationDetails1.nomAr = MainActivity.stations.get(i).nomAr;
                stationDetails1.nomFr = MainActivity.stations.get(i).nomFr;
                getDistanceOnlineOnVehicle(MainActivity.stations.get(i).coordonnees);
                stationDetails1.distanceTo = distanceTo;
                stationDetails1.timeTo = timeTo;

                stationDetails.add(stationDetails1);
                sort(stationDetails);

//                setContentView(R.layout.loading);

            }

            runOnUiThread(() -> {
                loading.setVisibility(View.INVISIBLE);
                listView.setAdapter(stationAdapter);
                Log.d("allStations", "done!");
            });

        });
        t1.start();
//        listView.setAdapter(stationAdapter);
//        setContentView(R.layout.loading);

        DbHelper.getInstance(this);
        Log.d("allStationsDatabase", String.valueOf(DbHelper.getInstance(this).getAllStations().size()));

    }


    private void getDistanceOnlineOnFoot(GeoPoint geoPoint) {
        ArrayList<GeoPoint> roadPoints = new ArrayList<>();
        roadPoints.add(geoPoint);
        roadPoints.add((currentLocation));
        OSRMRoadManager roadManager = new OSRMRoadManager(getApplicationContext(), "22-Transport");
        roadManager.setMean(OSRMRoadManager.MEAN_BY_FOOT);
        Road road = roadManager.getRoad(roadPoints);

//        RoadManager roadManager1 = new GraphHopperRoadManager("484e2932-b8a9-4bfa-a760-d3f32f84e347", false);
//        roadManager1.addRequestOption("vehicle=foot");
//        Road road = roadManager1.getRoad(roadPoints);
//        RoadManager roadManager2 = new GraphHopperRoadManager("484e2932-b8a9-4bfa-a760-d3f32f84e347", false);
//        roadManager2.addRequestOption("vehicle=car");
//        Road road2 = roadManager2.getRoad(roadPoints);
        distanceTo = road.mLength;
        timeTo = road.mDuration / 60;
    }

    private void getDistanceOnlineOnVehicle(GeoPoint geoPoint) {
        ArrayList<GeoPoint> roadPoints = new ArrayList<>();
        roadPoints.add(geoPoint);
        roadPoints.add((currentLocation));
//        OSRMRoadManager roadManager = new OSRMRoadManager(getApplicationContext(), "22-Transport");
//        roadManager.setMean(OSRMRoadManager.MEAN_BY_CAR);
//        Road road = roadManager.getRoad(roadPoints);

//        RoadManager roadManager1 = new GraphHopperRoadManager("484e2932-b8a9-4bfa-a760-d3f32f84e347", false);
//        roadManager1.addRequestOption("vehicle=foot");
//        Road road = roadManager1.getRoad(roadPoints);
        RoadManager roadManager2 = new GraphHopperRoadManager("484e2932-b8a9-4bfa-a760-d3f32f84e347", false);
        roadManager2.addRequestOption("vehicle=car");
        Road road = roadManager2.getRoad(roadPoints);
        distanceTo = road.mLength;
        timeTo = road.mDuration / 60;
    }

    /*private double getDistanceOffline(GeoPoint currentLocation, GeoPoint targetedLocation) {
        float[] distance = new float[2];
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), targetedLocation.getLatitude(), targetedLocation.getLongitude(), distance);
        return distance[0];
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    void sort(ArrayList<StationDetails> stationDetails) {
        StationDetails temp;
        for (int i = 0; i < stationDetails.size() - 1; i++)
            for (int j = i; j < stationDetails.size(); j++) {
                if (stationDetails.get(i).timeTo > stationDetails.get(j).timeTo) {
                    temp = stationDetails.get(i);
                    stationDetails.set(i, stationDetails.get(j));
                    stationDetails.set(j, temp);
                }
            }
    }

    void closestStations(ArrayList<StationDetails> stationDetails, int number) {
        for (int i = 0; i < number; i++) {
            stationDetails.get(i);
        }
    }
}