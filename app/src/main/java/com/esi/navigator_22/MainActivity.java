package com.esi.navigator_22;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.bonuspack.routing.GraphHopperRoadManager;
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
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    //    private static MapView myMap;
    String urlStations = "http://192.168.1.7:3000/stations";
    String urlChemin = "http://192.168.1.7:3000/polyline";
    private String myResponse;
    int numberOfOverlays = 1;

    LocationManager locationManager;
    private MapView myMap;
    ScaleBarOverlay echelle;
    MyLocationNewOverlay mLocationOverlay;
//    Geocoder geocoder = new Geocoder(this, Locale.getDefault());

    ImageView currentPosition;

    Station a = new Station();
    GeoPoint defaultPosition = new GeoPoint(35.2023025901554, -0.6302970012564838);
    GeoPoint currentLocation = new GeoPoint(0.0, 0.0);
    GeoPoint point = new GeoPoint(0.0, 0.0);

    DbHelper database = new DbHelper(this);

    ArrayList<Station> stations = new ArrayList<>();
    ArrayList<GeoPoint> chemin = new ArrayList<>();
    ArrayList<Polyline> roads = new ArrayList<>();
    public ArrayList<GeoPoint> history = new ArrayList<>();
    int minZ = 2;
    int maxZ = 17;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OkHttpClient client = new OkHttpClient();
        myMap = findViewById(R.id.map);
        currentPosition = findViewById(R.id.currentPosition);
        myMap.getController().setCenter(new GeoPoint(35.2023025901554, -0.6302970012564838));

        Runnable downloadMapToCache = () -> runOnUiThread(() -> {
            myMap.setTileSource(TileSourceFactory.HIKEBIKEMAP);
            CacheManager cacheManager = new CacheManager(myMap);
            BoundingBox bbox = new BoundingBox(35.2287, -0.6058, 35.1775, -0.6630);
            cacheManager.downloadAreaAsync(getApplicationContext(), bbox, minZ, maxZ);
        });
        Executors.newSingleThreadExecutor().execute(downloadMapToCache);


        myMap.getController().setZoom(15.0);

        if ((ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED)) {
            Log.d("LogGps", "Permissions granted");
        } else {
            // You can directly ask for the permission.
            Toast.makeText(this, "Localisation requise", Toast.LENGTH_LONG).show();
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1
            );
//            getLocation();
            Log.d("LogGps", "Permission check");
        }


        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()), this.myMap);
        Drawable currentDraw = ResourcesCompat.getDrawable(getResources(), R.drawable.person, null);
        Bitmap currentIcon = null;
        if (currentDraw != null) {
            currentIcon = ((BitmapDrawable) currentDraw).getBitmap();
        }
        mLocationOverlay.setDirectionArrow(currentIcon, currentIcon);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.getMyLocation();
        myMap.getOverlays().add(mLocationOverlay);
        numberOfOverlays++;
//        history.add(mLocationOverlay.getMyLocation());


        echelle = new ScaleBarOverlay(this.myMap);
        myMap.getOverlays().add(echelle);
        numberOfOverlays++;
        myMap.setMultiTouchControls(true);
        RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(this.myMap);
        mRotationGestureOverlay.setEnabled(true);
        myMap.setMultiTouchControls(true);
        myMap.getOverlays().add(mRotationGestureOverlay);
        numberOfOverlays++;


//        currentPosition.setOnClickListener(v -> {
////            currentLocation = history.get(history.size()-1);
//            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
//            Criteria criteres = new Criteria();
//            criteres.setAccuracy(Criteria.ACCURACY_FINE);
//            criteres.setAltitudeRequired(true);
//            criteres.setBearingRequired(true);
//            criteres.setSpeedRequired(true);
//            criteres.setCostAllowed(true);
//            criteres.setPowerRequirement(Criteria.POWER_MEDIUM);
//            fournisseur = locationManager.getBestProvider(criteres, true);
////            Location l = new Location(GPS_PROVIDER);
////            ecouteurGPS.onLocationChanged(locationManager.getLastKnownLocation(fournisseur));
////            ecouteurGPS.onLocationChanged(locationManager.getLastKnownLocation(GPS_PROVIDER));
////            currentLocation = history.get(history.size());
//            currentLocation = mLocationOverlay.getMyLocation();
//            if (currentLocation != null) {
//                Log.d("LogGps1", "Button clicked and current location = " + currentLocation.toString());
//            } else {
//                currentLocation.setLatitude(locationManager.getLastKnownLocation(fournisseur).getLatitude());
//                currentLocation.setLongitude(locationManager.getLastKnownLocation(fournisseur).getLongitude());
//                Log.d("LogGps","Using the provider : "+fournisseur);
//                Log.d("LogGps2", "Button clicked and current location = " + currentLocation.toString());
//            }
//            myMap.getController().setCenter(currentLocation);
//            myMap.getController().setZoom(16.0);
//        });

        currentPosition.setOnClickListener(v -> {

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

        chemin = database.getAllPointsChemin();
        Log.d("LogDatabase", String.valueOf(chemin.size()));
        tracerChemin(chemin, myMap);

        stations = database.getAllStations();
        Log.d("LogDatabase", String.valueOf(stations.size()));
        for (int i = 0; i < stations.size(); i++) {
            addMarker(this, myMap, stations.get(i).coordonnees, stations.get(i).nomFr, stations.get(i).nomAr);
            numberOfOverlays++;
        }


        Log.d("LogDatabase", database.getAllPointsChemin().toString());

    }

    void getLocation() {

    }

    LocationListener ecouteurGPS = new LocationListener() {


//        @Override
//        public void onLocationChanged(Location localisation) {
//            GeoPoint current = new GeoPoint(0.0, 0.0);
////            current = mLocationOverlay.getMyLocation();
//            current.setLatitude(localisation.getLatitude());
//            current.setLongitude(localisation.getLongitude());
//            Log.d("LogGps", "Marche et currentlocation2 = " + String.valueOf(current));
//            myMap.getController().setCenter(current);
//            myMap.getController().setZoom(16.0);
//            Log.d("LogGps", "onLocationChanged-252");
//            myMap.invalidate();
//
////            List<Address> adresses = null;
////            try {
//////                adresses = geocoder.getFromLocation(localisation.getLatitude(), localisation.getLongitude(), 1);
////            } catch (IllegalArgumentException illegalArgumentException) {
////                Log.e("GpsLog", "erreur " + coordonnees, illegalArgumentException);
////            }
////
////            if (adresses == null || adresses.size() == 0) {
////                Log.e("GpsLog", "erreur aucune adresse !");
////            } else {
////                Address adresse = adresses.get(0);
////                ArrayList<String> addressFragments = new ArrayList<>();
////
////                for (int i = 0; i <= adresse.getMaxAddressLineIndex(); i++) {
////                    addressFragments.add(adresse.getAddressLine(i));
////                }
////            }
//        }


        @Override
        public void onLocationChanged(@NonNull Location location) {
            currentLocation = new GeoPoint(location);
            getLocation();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
//            getLocation();
            Log.d("LogGps", "Gps status changed");
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            Log.d("LogGps", "Gps disabled");
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
            Log.d("LogGps", "Gps enabled");
//            getLocation();
        }
    };


    protected void onDestroy() {
        super.onDestroy();
        arreterLocalisation();
    }

    public void onResume() {
        super.onResume();
        mLocationOverlay.enableMyLocation();
//        getLocation();
    }

    public void onPause() {
        super.onPause();
        mLocationOverlay.disableMyLocation();
//        getLocation();
    }

    private void arreterLocalisation() {
        if (locationManager != null) {
            locationManager.removeUpdates(ecouteurGPS);
            ecouteurGPS = null;
        }
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
                assert jsonobject != null;
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

    @SuppressLint("UseCompatLoadingForDrawables")
    public void addMarker(Context context, MapView mapMarker, GeoPoint positionMarker, String nomFrMarker, String nomArMarker) {
        float[] distance = new float[1];
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), positionMarker.getLatitude(), positionMarker.getLongitude(), distance);
        Marker marker = new Marker(mapMarker);
        marker.setPosition(positionMarker);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setAlpha(1);
        marker.setIcon(context.getResources().getDrawable(R.drawable.ic_tramway));
//        marker.setSnippet(nomFrMarker + "\n " + " " + nomArMarker);
        marker.setTitle(nomFrMarker + " " + nomArMarker);
        marker.setPanToView(true);
        mapMarker.invalidate();
        mapMarker.getOverlays().add(marker);

        marker.setOnMarkerClickListener((marker1, mapView) -> {
            if (mapView.getOverlays().size() > numberOfOverlays) {
                mapView.getOverlays().remove(mapView.getOverlays().get(numberOfOverlays));
            }
            ArrayList<GeoPoint> roadPoints = new ArrayList<>();
            roadPoints.add(marker1.getPosition());
            roadPoints.add(mLocationOverlay.getMyLocation());
            OSRMRoadManager roadManager = new OSRMRoadManager(getApplicationContext(), "22-Transport");
            roadManager.setMean(OSRMRoadManager.MEAN_BY_FOOT);
            Road road = roadManager.getRoad(roadPoints);
            Polyline route = RoadManager.buildRoadOverlay(road);
//            OSRMRoadManager roadManager = new OSRMRoadManager(this, "22-Transport");
//            roadManager.setMean(OSRMRoadManager.MEAN_BY_FOOT);
//            Road road = roadManager.getRoad(route);
//            RoadManager roadManager1 = new GraphHopperRoadManager("9b8e0c01-5851-4b2d-9cc5-184a5a9f40c8", false);
//            roadManager1.addRequestOption("vehicle=foot");
//            Road road = roadManager1.getRoad(route);

            roads.add(route);
            mapView.getOverlays().add(roads.get(roads.size() - 1));
            marker1.setSnippet("Temps restant pour arriver الوقت اللازم للوصول دقيقة" + road.mDuration / 60 + " minutes");
            marker1.showInfoWindow();
            mapView.getController().setCenter(marker1.getPosition());
            mapView.getController().setZoom(16.0);
            return true;
        });

//        marker.setOnMarkerClickListener((marker1, map1) -> {

//            OSRMRoadManager roadManager = new OSRMRoadManager(this, "22-Transport");
//            roadManager.setMean(OSRMRoadManager.MEAN_BY_FOOT);
//            Road road = roadManager.getRoad(route);
//            RoadManager roadManager1 = new GraphHopperRoadManager("9b8e0c01-5851-4b2d-9cc5-184a5a9f40c8", false);
//            roadManager1.addRequestOption("vehicle=foot");
//            Road road = roadManager1.getRoad(route);
    }


}