package com.esi.navigator_22;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

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
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//import androidx.appcompat.app.AlertDialog;
//import org.osmdroid.bonuspack.routing.GraphHopperRoadManager;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    String urlStations = "http://192.168.1.7:3000/stations";
    String urlChemin = "http://192.168.1.7:3000/polyline";
    private String myResponse;
    int numberOfOverlays = 1;

    static MapView myMap;
    ScaleBarOverlay echelle;
    MyLocationNewOverlay mLocationOverlay;

    ImageView currentPosition;

    Station a = new Station();
    public GeoPoint defaultLocation = new GeoPoint(35.19115853846664, -0.6298066051152207);
    static GeoPoint currentLocation = new GeoPoint(0.0, 0.0);
    GeoPoint point = new GeoPoint(0.0, 0.0);

    DbHelper database = DbHelper.getInstance(this);
    static ArrayList<Station> stations = new ArrayList<>();
    ArrayList<GeoPoint> chemin = new ArrayList<>();
    ArrayList<Polyline> roads = new ArrayList<>();
    public ArrayList<GeoPoint> history = new ArrayList<>();
    int minZ = 2;
    int maxZ = 17;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myMap = findViewById(R.id.map);
        currentPosition = findViewById(R.id.currentPosition);
        OkHttpClient client = new OkHttpClient();

        setNavigationViewListener();
        drawerLayout = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_draw_open, R.string.navigation_draw_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();


        myMap.getController().setCenter(new GeoPoint(35.2023025901554, -0.6302970012564838));
        myMap.getController().setZoom(15.0);

        Runnable downloadMapToCache = () -> runOnUiThread(() -> {
            myMap.setTileSource(TileSourceFactory.HIKEBIKEMAP);
            CacheManager cacheManager = new CacheManager(myMap);
            BoundingBox bbox = new BoundingBox(35.2287, -0.6058, 35.1775, -0.6630);
            cacheManager.downloadAreaAsync(getApplicationContext(), bbox, minZ, maxZ);
        });
        Executors.newSingleThreadExecutor().execute(downloadMapToCache);


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


        currentPosition.setOnClickListener(v -> {
            getLocation();
            myMap.getController().setCenter(currentLocation);
            myMap.getController().setZoom(16.0);
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


/*        float[] distance = new float[1];
        Location.distanceBetween(35.21141638069786,-0.6279895164997695, 35.21534793928003, -0.6310374089929073, distance);
        Log.d("DetailsA", String.valueOf(distance[0]));*/


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        chemin = database.getAllPointsChemin();
        Log.d("LogDatabaseChemin", String.valueOf(chemin.size()));
        tracerChemin(chemin, myMap);

        stations = database.getAllStations();
        Log.d("LogDatabaseStation", String.valueOf(stations.size()));
        for (int i = 0; i < stations.size(); i++) {
            addMarker(this, myMap, stations.get(i).coordonnees, stations.get(i).nomFr, stations.get(i).nomAr);
            numberOfOverlays++;
        }
        Log.d("DatabaseSingleton1", String.valueOf(database.getAllStations().size()));
    }

    public void getLocation() {
        if (mLocationOverlay.getMyLocation() != null)
            currentLocation = mLocationOverlay.getMyLocation();
        else {
            currentLocation = defaultLocation;
            Toast.makeText(getApplicationContext(), "Using default location, consider enabling the GPS and restarting the app", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("LogGps", "onDestroy");
        arreterLocalisation();
    }

    @Override
    public void onResume() {
        super.onResume();
        myMap.onResume();
        Log.d("LogGps", "onResume");
        mLocationOverlay.enableMyLocation();
//        getLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
        myMap.onPause();
        Log.d("LogGps", "onPause");
//        mLocationOverlay.disableMyLocation();
//        getLocation();
    }

    @Override
    public void onBackPressed() {
        Log.d("LogGps", "Back button pressed");
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Fermer l'application")
                .setMessage("Voulez-vous vraiment fermer l'application?")
                .setPositiveButton("Oui", (dialog, which) -> super.onBackPressed())
                .setNegativeButton("Non", null)
                .show();
    }

    private void arreterLocalisation() {
        mLocationOverlay.disableMyLocation();
        mLocationOverlay.disableFollowLocation();
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
//        line.setGeodesic(true);
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
            tracerRoute(marker1, mapView);
//            OSRMRoadManager roadManager = new OSRMRoadManager(this, "22-Transport");
//            roadManager.setMean(OSRMRoadManager.MEAN_BY_FOOT);
//            Road road = roadManager.getRoad(route);
//            RoadManager roadManager1 = new GraphHopperRoadManager("9b8e0c01-5851-4b2d-9cc5-184a5a9f40c8", false);
//            roadManager1.addRequestOption("vehicle=foot");
//            Road road = roadManager1.getRoad(route);
//            String distanceTo = "Distance pour arriver المسافة اللازمة للوصول كم" + road.mLength + " km";
//            String timeTo = "Temps nécessaire الوقت اللازم للوصول دقيقة" + road.mDuration / 60 + " minutes";;

            marker1.setSnippet(tracerRoute(marker1, mapView));
//            marker1.getSnippet().set
//            marker1.showInfoWindow();

            marker1.setInfoWindow(new InfoWindow(R.layout.custom_bubble, myMap) {
                @Override
                public void onOpen(Object item) {
                    InfoWindow.closeAllInfoWindowsOn(myMap);
                    TextView station = mView.findViewById(R.id.nomStation);
                    station.setText(marker1.getTitle() + "\n" + marker1.getSnippet());
//                    TextView details = (TextView) mView.findViewById(R.id.route);
//                    details.setText(marker1.getSnippet());
                }

                @Override
                public void onClose() {

                }
            });
            marker1.showInfoWindow();
            mapView.getController().setCenter(marker1.getPosition());
            mapView.getController().setZoom(16.0);
            return true;
        });

//        marker.setOnMarkerClickListener((marker1, map1) -> {

//            OSRMRoadManager roadManager = new OSRMRoadManager(this, "22-Transport");
//            roadManager.setMean(OSRMRoadManager.MEAN_BY_FOOT);
//            Road road = roadManager.getRoad(route);
//            RoadManager roadManager1 = new GraphHopperRoadManager("484e2932-b8a9-4bfa-a760-d3f32f84e347", false);
//            roadManager1.addRequestOption("vehicle=foot");
//            Road road = roadManager1.getRoad(route);
    }

    Bundle send = new Bundle();
    Bundle get = new Bundle();
    Boolean boolean2;

    @Override
    public boolean onNavigationItemSelected(@NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.allSubwayStations:
                Intent intent = new Intent(MainActivity.this, SubwayStationsActivity.class);
                getLocation();
                send.putDouble("currentLocationLatitude", currentLocation.getLatitude());
                send.putDouble("currentLocationLongitude", currentLocation.getLongitude());
                intent.putExtras(send);
                MainActivity.this.startActivity(intent);
        }

        return true;
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    String tracerRoute(Marker marker, MapView mapView) {
        if (mapView.getOverlays().size() > numberOfOverlays) {
            mapView.getOverlays().remove(mapView.getOverlays().get(numberOfOverlays));
        }
        ArrayList<GeoPoint> roadPoints = new ArrayList<>();
        roadPoints.add(marker.getPosition());
//            roadPoints.add(mLocationOverlay.getMyLocation());
        getLocation();
        roadPoints.add((currentLocation));
        OSRMRoadManager roadManager = new OSRMRoadManager(getApplicationContext(), "22-Transport");
        roadManager.setMean(OSRMRoadManager.MEAN_BY_FOOT);
        Road road = roadManager.getRoad(roadPoints);
//        RoadManager roadManager1 = new GraphHopperRoadManager("9b8e0c01-5851-4b2d-9cc5-184a5a9f40c8", false);
//        roadManager1.addRequestOption("vehicle=foot");
//        Road road = roadManager1.getRoad(route);
        Polyline route = RoadManager.buildRoadOverlay(road);
        mapView.getOverlays().add(route);


        String duration = format(road.mDuration / 60);
        String dist = format(road.mLength);
        String distanceTo = "km " + dist + " كم";
        String timeTo = "minutes " + duration + " دقيقة";
        return distanceTo + "\n" + timeTo;

    }

    static String format(double number) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(number);
    }


}