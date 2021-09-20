package com.esi.navigator_22;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.osmdroid.bonuspack.routing.GraphHopperRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class NthTramwayStationsActivity extends AppCompatActivity {
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
        barProgressDialog.setTitle("Recupérations des 7 stations les plus proches ...");
        barProgressDialog.setMessage("En cours ...");
        barProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(7);
        barProgressDialog.show();
        barProgressDialog.setCancelable(false);

        ArrayList<StationDetails> plusProchesOffline = new ArrayList<>();
        ArrayList<StationDetails> plusProchesOnline = new ArrayList<>();
        t1 = new Thread(() -> {
            plusProchesOffline.clear();
            for (int i = 0; i < MainActivity.stationsTramway.size(); i++) {
                StationDetails stationDetails = new StationDetails();
                stationDetails.type = MainActivity.stationsTramway.get(i).type;
                stationDetails.nomFr = MainActivity.stationsTramway.get(i).nomFr;
                stationDetails.numero = MainActivity.stationsTramway.get(i).numero;
                stationDetails.coordonnees = MainActivity.stationsTramway.get(i).coordonnees;
                stationDetails.distanceTo = getDistanceOffline(stationDetails.coordonnees, currentLocation);
                stationDetails.timeTo = getDistanceOffline(stationDetails.coordonnees, currentLocation) / 3.8;
                plusProchesOffline.add(stationDetails);
            }
            sort(plusProchesOffline, "distance");
            plusProchesOnline.clear();
            for (int i = 0; i < 7; i++) {

                StationDetails availableStation = new StationDetails();
                availableStation.type = plusProchesOffline.get(i).type;
                availableStation.nomFr = plusProchesOffline.get(i).nomFr;
                getRouteOnlineOnFoot(plusProchesOffline.get(i).coordonnees);
                availableStation.distanceTo = distanceTo;
                availableStation.timeTo = timeTo;
                plusProchesOnline.add(availableStation);
                barProgressDialog.incrementProgressBy(1);
                if (i == 6) {
                    barProgressDialog.dismiss();
                }
            }

            runOnUiThread(() -> {
                sort(plusProchesOnline, "time");
                Log.d("LesPlusProches",plusProchesOnline.size()+"");
                for (int i = 0; i < 7; i++)
                    myList.add(plusProchesOnline.get(i));
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
            timeTo = getDistanceOffline(currentLocation, geoPoint)/0.4;
        } else {
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