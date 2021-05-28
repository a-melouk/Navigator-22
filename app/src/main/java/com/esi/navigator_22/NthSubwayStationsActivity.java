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

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

public class NthSubwayStationsActivity extends AppCompatActivity {
    GeoPoint currentLocation;
    ListView listView;
    MyLocationNewOverlay myLocationNewOverlay;
    double distanceTo;
    double timeTo;
    Thread t1;
    DbHelper database = DbHelper.getInstance(this);

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

        ArrayList<StationDetails> myList = new ArrayList<>();

        listView = findViewById(R.id.listSubway);
        TextView loading = findViewById(R.id.whileLoading);

        StationAdapter stationAdapter = new StationAdapter(this, R.layout.list_row, myList);


//        setContentView(R.layout.activity_subway_stations);
        ProgressDialog barProgressDialog = new ProgressDialog(this);

        barProgressDialog.setTitle("Recupérations des 5 stations les plus proches ...");
        barProgressDialog.setMessage("Download in progress ...");
        barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(5);
        barProgressDialog.show();
        barProgressDialog.setCancelable(false);
        t1 = new Thread(() -> {
            database.deleteAllNearestSubwayStation();
//            MainActivity.stations.size()
            ArrayList<StationDetails> plusProches = new ArrayList<>();
            for (int i = 0; i < MainActivity.stationsSubway.size(); i++) {
                double distanceOffline = 0.0;
                StationDetails stationDetails = new StationDetails();
                stationDetails.type = MainActivity.stationsSubway.get(i).type;
                stationDetails.nomFr = MainActivity.stationsSubway.get(i).nomFr;
                stationDetails.numero = MainActivity.stationsSubway.get(i).numero;
                stationDetails.coordonnees = MainActivity.stationsSubway.get(i).coordonnees;
                distanceOffline = getDistanceOffline(stationDetails.coordonnees, currentLocation);
                stationDetails.distanceTo = distanceOffline;
                plusProches.add(stationDetails);
            }
            sort(plusProches);
            for (int i = 0; i < 7; i++) {

            Log.d("allStation11",plusProches.get(i).nomFr);
            }
            for (int i = 0; i < 7; i++) {

                StationDetails availableStation = new StationDetails();
                availableStation.type = plusProches.get(i).type;
                availableStation.nomFr = plusProches.get(i).nomFr;
                getRouteOnlineOnFoot(plusProches.get(i).coordonnees);
                availableStation.distanceTo = distanceTo;
                availableStation.timeTo = timeTo;
                Log.d("allStation12",availableStation.nomFr+" "+availableStation.distanceTo);
                database.addNearStation(availableStation);
                barProgressDialog.incrementProgressBy(1);
                if (i == 6) {
                    barProgressDialog.dismiss();
                }


            }

            runOnUiThread(() -> {
                copyToMyList(5, myList);
                loading.setVisibility(View.INVISIBLE);
                listView.setAdapter(stationAdapter);
            });

        });
        t1.start();
    }

    private void sort(ArrayList<StationDetails> plusProches) {
        StationDetails temp;
        for (int i = 0; i < plusProches.size() - 1; i++)
            for (int j = i; j < plusProches.size(); j++) {
                if (plusProches.get(i).distanceTo > plusProches.get(j).distanceTo) {
                    temp = plusProches.get(i);
                    plusProches.set(i, plusProches.get(j));
                    plusProches.set(j, temp);
                }
            }

    }

    private void copyToMyList(int number, ArrayList<StationDetails> list) {
//        ArrayList<StationDetails> temp = database.getNthNearestSubStationsSortedByDistance(number);
//        for (int i = 0; i < number; i++)
//            list.add(temp.get(i));
    }

    private void getRouteOnlineOnFoot(GeoPoint geoPoint) {
        ArrayList<GeoPoint> roadPoints = new ArrayList<>();
        roadPoints.add((currentLocation));

        roadPoints.add(geoPoint);

        OSRMRoadManager roadManager = new OSRMRoadManager(getApplicationContext(), "22-Transport");
        roadManager.setMean(OSRMRoadManager.MEAN_BY_FOOT);
        Road road = roadManager.getRoad(roadPoints);

//        RoadManager roadManager1 = new GraphHopperRoadManager("484e2932-b8a9-4bfa-a760-d3f32f84e347", false);
//        roadManager1.addRequestOption("vehicle=foot");
//        Road road = roadManager1.getRoad(roadPoints);
        if (road.mLength == 0) {
            distanceTo = getDistanceOffline(currentLocation, geoPoint);
//            Log.d("allStation10", "Unavailable " + getDistanceOffline(currentLocation, geoPoint));
            timeTo = 99999.0;
        } else {
//            Log.d("allStation10", "Available " + getDistanceOffline(currentLocation, geoPoint));
            distanceTo = road.mLength;
            timeTo = road.mDuration / 60;
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
        timeTo = road.mDuration / 60;
    }

    private double getDistanceOffline(GeoPoint currentLocation, GeoPoint targetedLocation) {
        float[] distance = new float[2];
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), targetedLocation.getLatitude(), targetedLocation.getLongitude(), distance);
        return distance[0] / 1000;
    }

}