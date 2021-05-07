package com.esi.navigator_22;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    String urlStations = "http://192.168.1.7:3000/stations";
    String urlChemin = "http://192.168.1.7:3000/polyline";
    private String myResponse;

    LocationManager locationManager;
    private String fournisseur;
    private MapView myMap;
    ScaleBarOverlay echelle;
    MyLocationNewOverlay mLocationOverlay;
//    Geocoder geocoder = new Geocoder(this, Locale.getDefault());

    ImageView currentPosition;

    Station a = new Station();
    GeoPoint currentLocation = new GeoPoint(35.2023025901554, -0.6302970012564838);
    GeoPoint point = new GeoPoint(0.0, 0.0);

    DbHelper database = new DbHelper(this);

    ArrayList<Station> stations = new ArrayList<>();
    ArrayList<GeoPoint> chemin = new ArrayList<>();
    int minZ = 2;
    int maxZ = 17;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myMap = findViewById(R.id.map);

        Runnable downloadMapToCache = () -> runOnUiThread(() -> {
            myMap.setTileSource(TileSourceFactory.HIKEBIKEMAP);
            CacheManager cacheManager = new CacheManager(myMap);
            BoundingBox bbox = new BoundingBox(35.2287, -0.6058, 35.1775, -0.6630);
            cacheManager.downloadAreaAsync(getApplicationContext(), bbox, minZ, maxZ);
        });
        Executors.newSingleThreadExecutor().execute(downloadMapToCache);

        currentPosition = findViewById(R.id.currentPosition);

        OkHttpClient client = new OkHttpClient();

        myMap.getController().setZoom(15.0);
        getLocation();
        myMap.getController().setCenter(currentLocation);

        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()), this.myMap);
        Drawable currentDraw = ResourcesCompat.getDrawable(getResources(), R.drawable.person, null);
        Bitmap currentIcon = null;
        if (currentDraw != null) {
            currentIcon = ((BitmapDrawable) currentDraw).getBitmap();
        }
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.setDirectionArrow(currentIcon, currentIcon);
        echelle = new ScaleBarOverlay(this.myMap);
        myMap.getOverlays().add(echelle);
        myMap.setMultiTouchControls(true);
        RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(this.myMap);
        mRotationGestureOverlay.setEnabled(true);
        myMap.setMultiTouchControls(true);
        myMap.getOverlays().add(mRotationGestureOverlay);
        myMap.getOverlays().add(mLocationOverlay);

        currentPosition.setOnClickListener(v -> {
            getLocation();
            myMap.getController().setCenter(currentLocation);
            Log.d("Offline", "Button clicked");
        });

        Request request = new Request.Builder()
                .url(urlChemin)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    fetchRoute(response);
                }
            }
        });

        request = new Request.Builder()
                .url(urlStations)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {

                    fetchAllStations(response);
                }
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        stations = database.getAllStations();
        Log.d("databaseTest", String.valueOf(stations.size()));
        for (int i = 0; i < stations.size(); i++) {
            addMarker(this, myMap, stations.get(i).coordonnees, stations.get(i).nomFr, stations.get(i).nomAr);
        }

        chemin = database.getAllPointsChemin();
        Log.d("databaseTest", String.valueOf(chemin.size()));
        tracerChemin(chemin, myMap);


        Log.d("databaseTest", database.getAllPointsChemin().toString());


    }

    private void fetchAllStations(Response response) throws IOException {
        myResponse = response.body().string();
        JSONArray jsonarray = null;
        try {
            jsonarray = new JSONArray(myResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < jsonarray.length(); i++) {
            JSONObject jsonobject = null;
            try {
                jsonobject = jsonarray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                a.nomFr = jsonobject.getString("nomFr");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                a.nomAr = jsonobject.getString("nomAr");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                point.setLatitude(jsonobject.getDouble("latitude"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                point.setLongitude(jsonobject.getDouble("longitude"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            a.coordonnees = point;
//            result.add(a);
            database.addStation(a);
//            addMarker(ctx, mapView, a.coordonnees, a.nomFr, a.nomAr);
        }
    }

    private void fetchRoute(Response response) throws IOException {
        ArrayList<GeoPoint> chemin = new ArrayList<>();
        myResponse = response.body().string();
        JSONArray jsonarray = null;
        try {
            jsonarray = new JSONArray(myResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < jsonarray.length(); i++) {
            JSONObject jsonobject = null;
            GeoPoint po = new GeoPoint(0.0, 0.0);
            try {
                jsonobject = jsonarray.getJSONObject(i);
                po.setLatitude(jsonobject.getDouble("latitude"));
                po.setLongitude(jsonobject.getDouble("longitude"));
                database.addPointChemin(po);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            chemin.add(point);
        }
    }

    private void tracerChemin(ArrayList<GeoPoint> chemin, MapView mapView) {
        Polyline line = new Polyline();
        line.setWidth(12);
//        line.getOutlinePaint();
        line.setColor(Color.rgb(230, 138, 0));
        line.setDensityMultiplier(0.1f);
        line.setPoints(chemin);
        line.setGeodesic(true);
        mapView.getOverlayManager().add(line);
    }

    public void addMarker(Context context, MapView map, GeoPoint position, String nomStation, String nomAr) {
        float[] distance = new float[1];
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), position.getLatitude(), position.getLongitude(), distance);
        Marker marker = new Marker(map);
        marker.setPosition(position);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setAlpha(1);
        marker.setIcon(context.getResources().getDrawable(R.drawable.ic_tramway));
        marker.setSnippet(nomStation + "\n " + " " + nomAr);
        marker.setPanToView(true);
        map.invalidate();
        map.getOverlays().add(marker);

        marker.setOnMarkerClickListener((marker1, map1) -> {
            map1.invalidate();
            ArrayList<GeoPoint> route = new ArrayList<>();
            route.add(currentLocation);
            route.add(new GeoPoint(marker1.getPosition()));
            OSRMRoadManager roadManager = new OSRMRoadManager(this, "22-Transport");
            roadManager.setMean(OSRMRoadManager.MEAN_BY_FOOT);
            Road road = roadManager.getRoad(route);
//            RoadManager roadManager1 = new GraphHopperRoadManager("9b8e0c01-5851-4b2d-9cc5-184a5a9f40c8", false);
//            roadManager1.addRequestOption("vehicle=foot");
//            Road road = roadManager1.getRoad(route);
            Log.d("Routing", road.mDuration / 60 + "mn | " + road.mLength);
            marker.setSnippet(nomStation + "\n " + " " + nomAr + "\n" + "Duration: " + road.mDuration / 60 + "minutes\nDistance: " + road.mLength + "km");
//            Toast.makeText(this, "Duration: "+road.mDuration/60+"minutes\nDistance: "+road.mLength+"km", Toast.LENGTH_LONG).show();
            Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
            map1.getOverlays().add(roadOverlay);
            map1.invalidate();

//            Toast.makeText(MainActivity.this,
//                    marker1.getSnippet() +
//                            "\n" + String.valueOf(distance[0]) + "m",
//                    Toast.LENGTH_SHORT).show();
            marker.showInfoWindow();
            map1.getController().setCenter(marker1.getPosition());
            map1.getController().setZoom(16);
            map1.invalidate();
            return false;
        });
    }

    void getLocation() {
        if (locationManager == null) {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

            Criteria criteres = new Criteria();
            criteres.setAccuracy(Criteria.ACCURACY_FINE);
            criteres.setAltitudeRequired(true);
            criteres.setBearingRequired(true);
            criteres.setSpeedRequired(true);
            criteres.setCostAllowed(true);
            criteres.setPowerRequirement(Criteria.POWER_MEDIUM);
            fournisseur = locationManager.getBestProvider(criteres, true);
        }
        if (fournisseur != null) {
            // dernière position connue
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("Offline", "no permissions !");

                return;
            }
            Location localisation = locationManager.getLastKnownLocation(fournisseur);
            if (localisation != null) {
                // on notifie la localisation
                ecouteurGPS.onLocationChanged(localisation);
                currentLocation.setLatitude(localisation.getLatitude());
                currentLocation.setLongitude(localisation.getLongitude());
            }

            Log.d("Offline", "Marche");
        } else {
            Log.d("status", "GPS desactive");
        }
    }

    LocationListener ecouteurGPS = new LocationListener() {

        @Override
        public void onLocationChanged(Location localisation) {
            String coordonnees = String.format("Latitude : %f - Longitude : %f\n", localisation.getLatitude(), localisation.getLongitude());
            myMap.getController().setCenter(new GeoPoint(localisation.getLatitude(), localisation.getLongitude()));
            myMap.invalidate();

            List<Address> adresses = null;
            try {
//                adresses = geocoder.getFromLocation(localisation.getLatitude(), localisation.getLongitude(), 1);
            } catch (IllegalArgumentException illegalArgumentException) {
                Log.e("GPS", "erreur " + coordonnees, illegalArgumentException);
            }

            if (adresses == null || adresses.size() == 0) {
                Log.e("GPS", "erreur aucune adresse !");
            } else {
                Address adresse = adresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<>();

                for (int i = 0; i <= adresse.getMaxAddressLineIndex(); i++) {
                    addressFragments.add(adresse.getAddressLine(i));
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {

        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        arreterLocalisation();
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    private void arreterLocalisation() {
        if (locationManager != null) {
            locationManager.removeUpdates(ecouteurGPS);
            ecouteurGPS = null;
        }
    }
}