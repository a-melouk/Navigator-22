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
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    String urlStations = "http://192.168.1.15:3000/stations";
    String urlChemin = "http://192.168.1.15:3000/polyline";
    private String myResponse;
    int numberOfOverlays = 1;


    static MapView myMap;
    ScaleBarOverlay echelle;
    MyLocationNewOverlay mLocationOverlay;

    ImageView currentPosition;
    ImageView closestStation;

    Station a = new Station();
    public GeoPoint defaultLocation = new GeoPoint(35.19115853846664, -0.6298066051152207);
    static GeoPoint currentLocation = new GeoPoint(0.0, 0.0);
    GeoPoint point = new GeoPoint(0.0, 0.0);

    DbHelper database = DbHelper.getInstance(this);
    static ArrayList<Station> stations = new ArrayList<>();
    ArrayList<GeoPoint> chemin = new ArrayList<>();
    double minZ = 14.0;
    double maxZ = 19.0;
    DrawerLayout drawerLayout;
    LinearLayout scroll_menu;
    ActionBarDrawerToggle toggle;
    int boucle = 1000500;
    int[] ids = new int[22];

    double distanceTo, timeTo;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ids = new int[]{R.id.station1, R.id.station2, R.id.station3, R.id.station4, R.id.station5, R.id.station6, R.id.station7, R.id.station8, R.id.station9,
                R.id.station10, R.id.station11, R.id.station12, R.id.station13, R.id.station14, R.id.station15, R.id.station16, R.id.station17, R.id.station18, R.id.station19, R.id.station20, R.id.station21, R.id.station22};
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myMap = findViewById(R.id.map);
        currentPosition = findViewById(R.id.currentPosition);
        closestStation = findViewById(R.id.current);
        scroll_menu = findViewById(R.id.stations_menu);
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
        myMap.setMinZoomLevel(minZ);
        myMap.setMaxZoomLevel(maxZ);
        myMap.getController().setZoom(15.0);
        myMap.setTileSource(TileSourceFactory.HIKEBIKEMAP);
//        setMapOfflineSource();
        Runnable downloadMapToCache = () -> runOnUiThread(() -> {
            CacheManager cacheManager = new CacheManager(myMap);
            BoundingBox bbox = new BoundingBox(35.2287, -0.6058, 35.1775, -0.6630);
            cacheManager.downloadAreaAsync(this, bbox, 12, 17);
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
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1
            );
//            getLocation();
            Log.d("LogGps", "Permission check");
        }


        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()), myMap);
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

        echelle = new ScaleBarOverlay(myMap);
        myMap.getOverlays().add(echelle);
        numberOfOverlays++;
        myMap.setMultiTouchControls(true);
        RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(myMap);
        mRotationGestureOverlay.setEnabled(true);
        myMap.setMultiTouchControls(true);

        myMap.getOverlays().add(mRotationGestureOverlay);
        numberOfOverlays++;
        addMarker(this, myMap, new GeoPoint(35.19181984486152, -0.6367524076104305));
        numberOfOverlays++;


        currentPosition.setOnClickListener(v -> {
            getLocation();
            myMap.getController().setCenter(currentLocation);
            myMap.getController().setZoom(16.0);
        });

        closestStation.setOnClickListener(v -> {
            getLocation();
            myMap.getController().setCenter(currentLocation);
            myMap.getController().setZoom(16.0);
            travelPlanner();
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
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
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

        stations = database.getAllSubwayStations();
        Log.d("LogDatabaseStation", String.valueOf(stations.size()));
        for (int i = 0; i < stations.size(); i++) {
            addStation(this, myMap, stations.get(i).coordonnees, stations.get(i).nomFr, stations.get(i).nomAr);
            numberOfOverlays++;

        }
        Log.d("DatabaseSingleton1", String.valueOf(database.getAllSubwayStations().size()));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        ArrayList<Station> stations = database.getAllSubwayStations();
        SubMenu tramwaySubMenu = menu.addSubMenu("Stations de Tramway");
        SubMenu busSubMenu = menu.addSubMenu("Stations de bus");

        for (int i = 0; i < 7; i++) {
            busSubMenu.addSubMenu("Ligne " + i);
        }
        for (int i = 0; i < stations.size(); i++) {
            tramwaySubMenu.add(R.id.subway_stations, ids[i], 1, stations.get(i).nomFr + " " + stations.get(i).nomAr);
        }
//        int i = 0;
//        tramwaySubMenu.add(R.id.subway_stations, R.id.station1, 1, stations.get(0).nomFr + " " + stations.get(0).nomAr);
//        tramwaySubMenu.add(R.id.subway_stations, R.id.station2, 1, stations.get(1).nomFr + " " + stations.get(1).nomAr);
//        tramwaySubMenu.add(R.id.subway_stations, R.id.station3, 1, stations.get(2).nomFr + " " + stations.get(2).nomAr);
//        tramwaySubMenu.add(R.id.subway_stations, R.id.station4, 1, stations.get(3).nomFr + " " + stations.get(3).nomAr);
//        tramwaySubMenu.add(R.id.subway_stations, R.id.station5, 1, stations.get(4).nomFr + " " + stations.get(4).nomAr);
//        tramwaySubMenu.add(R.id.subway_stations, R.id.station6, 1, stations.get(5).nomFr + " " + stations.get(5).nomAr);
//        tramwaySubMenu.add(R.id.subway_stations, R.id.station7, 1, stations.get(6).nomFr + " " + stations.get(6).nomAr);
//        tramwaySubMenu.add(R.id.subway_stations, R.id.station8, 1, stations.get(7).nomFr + " " + stations.get(7).nomAr);
//        tramwaySubMenu.add(R.id.subway_stations, R.id.station9, 1, stations.get(8).nomFr + " " + stations.get(8).nomAr);
//        tramwaySubMenu.add(R.id.subway_stations, R.id.station10, 1, stations.get(9).nomFr + " " + stations.get(9).nomAr);
//        tramwaySubMenu.add(R.id.subway_stations, R.id.station11, 1, stations.get(10).nomFr + " " + stations.get(10).nomAr);
//        tramwaySubMenu.add(R.id.subway_stations, R.id.station12, 1, stations.get(11).nomFr + " " + stations.get(11).nomAr);
//        tramwaySubMenu.add(R.id.subway_stations, R.id.station13, 1, stations.get(12).nomFr + " " + stations.get(12).nomAr);
//        tramwaySubMenu.add(R.id.subway_stations, R.id.station14, 1, stations.get(13).nomFr + " " + stations.get(13).nomAr);
//        tramwaySubMenu.add(R.id.subway_stations, R.id.station15, 1, stations.get(14).nomFr + " " + stations.get(14).nomAr);
//        tramwaySubMenu.add(R.id.subway_stations, R.id.station16, 1, stations.get(15).nomFr + " " + stations.get(15).nomAr);
//        tramwaySubMenu.add(R.id.subway_stations, R.id.station17, 1, stations.get(16).nomFr + " " + stations.get(16).nomAr);
//        tramwaySubMenu.add(R.id.subway_stations, R.id.station18, 1, stations.get(17).nomFr + " " + stations.get(17).nomAr);
//        tramwaySubMenu.add(R.id.subway_stations, R.id.station19, 1, stations.get(18).nomFr + " " + stations.get(18).nomAr);
//        tramwaySubMenu.add(R.id.subway_stations, R.id.station20, 1, stations.get(19).nomFr + " " + stations.get(19).nomAr);
//        tramwaySubMenu.add(R.id.subway_stations, R.id.station21, 1, stations.get(20).nomFr + " " + stations.get(20).nomAr);
//        tramwaySubMenu.add(R.id.subway_stations, R.id.station22, 1, stations.get(21).nomFr + " " + stations.get(21).nomAr);


        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.stations_sba, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d("ItemSeelected", stations.get(0).nomFr);
        getLocation();
        switch (item.getItemId()) {
            case R.id.station1:
                Log.d("ItemSelected1", String.valueOf(item.getTitle().toString().equals(stations.get(0).nomFr + " " + stations.get(0).nomAr)));
                cleanMap(myMap);
                drawRouteOnlineOnFoot(currentLocation, stations.get(0).coordonnees);
                numberOfOverlays++;
                break;
            case R.id.station2:
                cleanMap(myMap);
                Log.d("ItemSelected2", String.valueOf(item.getTitle().toString().equals(stations.get(1).nomFr + " " + stations.get(1).nomAr)));
                drawRouteOnlineOnFoot(currentLocation, stations.get(1).coordonnees);
                numberOfOverlays++;
                break;
            case R.id.station3:
                cleanMap(myMap);
                Log.d("ItemSelected3", String.valueOf(item.getTitle().toString().equals(stations.get(2).nomFr + " " + stations.get(2).nomAr)));
                drawRouteOnlineOnFoot(currentLocation, stations.get(2).coordonnees);
                numberOfOverlays++;
                break;
            case R.id.station4:
                cleanMap(myMap);
                Log.d("ItemSelected4", String.valueOf(item.getTitle().toString().equals(stations.get(3).nomFr + " " + stations.get(3).nomAr)));
                drawRouteOnlineOnFoot(currentLocation, stations.get(3).coordonnees);
                numberOfOverlays++;
                break;
            case R.id.station5:
                Log.d("ItemSelected5", String.valueOf(item.getTitle().toString().equals(stations.get(4).nomFr + " " + stations.get(4).nomAr)));
                cleanMap(myMap);
                drawRouteOnlineOnFoot(currentLocation, stations.get(4).coordonnees);
                numberOfOverlays++;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getLocation() {
        if (mLocationOverlay.getMyLocation() != null) {
            Log.d("DatabaseSingleton1", "fiha");
            currentLocation = mLocationOverlay.getMyLocation();
        } else {
            Log.d("DatabaseSingleton1", "mafihech");
            currentLocation = defaultLocation;
            Toast.makeText(getApplicationContext(),
                    "Using default location, consider enabling the GPS and restarting the app",
                    Toast.LENGTH_SHORT).show();
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

    public void addMarker(Context context, MapView mapMarker, GeoPoint positionMarker) {
        float[] distance = new float[1];
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), positionMarker.getLatitude(), positionMarker.getLongitude(), distance);
        Marker marker = new Marker(mapMarker);
        marker.setPosition(positionMarker);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setAlpha(1);
//        marker.setIcon(context.getResources().getDrawable(R.drawable.ic_tramway));
        marker.setSnippet("Custom station");
//        marker.setSnippet(nomFrMarker + "\n " + " " + nomArMarker);
        marker.setPanToView(true);
        mapMarker.invalidate();
        mapMarker.getOverlays().add(marker);


        marker.setOnMarkerClickListener((marker1, mapView) -> {
            tracerRoute(marker1, mapView);
            marker1.setSnippet(tracerRoute(marker1, mapView));
            marker1.setInfoWindow(new InfoWindow(R.layout.custom_bubble, myMap) {
                @Override
                public void onOpen(Object item) {
                    InfoWindow.closeAllInfoWindowsOn(myMap);
                    TextView station = mView.findViewById(R.id.nomStation);
                    station.setText(marker1.getTitle() + "\n" + marker1.getSnippet());
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
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void addStation(Context context, MapView mapMarker, GeoPoint positionMarker, String nomFrMarker, String nomArMarker) {
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
            marker1.setSnippet(tracerRoute(marker1, mapView));
            marker1.setInfoWindow(new InfoWindow(R.layout.custom_bubble, myMap) {
                @Override
                public void onOpen(Object item) {
                    InfoWindow.closeAllInfoWindowsOn(myMap);
                    TextView station = mView.findViewById(R.id.nomStation);
                    station.setText(marker1.getTitle() + "\n" + marker1.getSnippet());
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
    }

    Bundle send = new Bundle();

    @Override
    public boolean onNavigationItemSelected(@NotNull MenuItem item) {
        getLocation();
        if (item.getItemId() == R.id.allSubwayStations) {
            Intent intent = new Intent(MainActivity.this, AllNearSubwayStationsActivity.class);
            send.putDouble("currentLocationLatitude", currentLocation.getLatitude());
            send.putDouble("currentLocationLongitude", currentLocation.getLongitude());
            intent.putExtras(send);
            MainActivity.this.startActivity(intent);
        } else if (item.getItemId() == R.id.closestStations) {
            Intent intent = new Intent(MainActivity.this, NthSubwayStationsActivity.class);
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
        cleanMap(mapView);
        ArrayList<GeoPoint> roadPoints = new ArrayList<>();
        getLocation();
        roadPoints.add((currentLocation));
        roadPoints.add(marker.getPosition());

        OSRMRoadManager roadManager = new OSRMRoadManager(getApplicationContext(), "22-Transport");
        roadManager.setMean(OSRMRoadManager.MEAN_BY_FOOT);
        Road road = roadManager.getRoad(roadPoints);
//        RoadManager roadManager = new GraphHopperRoadManager("9b8e0c01-5851-4b2d-9cc5-184a5a9f40c8", false);
//        roadManager.addRequestOption("vehicle=foot");
//        Road road = roadManager.getRoad(route);

        Polyline route = RoadManager.buildRoadOverlay(road);
        mapView.getOverlays().add(route);

        String duration = format(road.mDuration / 60);
        String dist = format(road.mLength);
        String distanceTo = "km " + dist + " كم";
        String timeTo = "minutes " + duration + " دقيقة";
        return distanceTo + "\n" + timeTo;

    }

    String tracerShortestRoute(Marker marker, MapView mapView) {
        if (mapView.getOverlays().size() > numberOfOverlays) {
            mapView.getOverlays().remove(mapView.getOverlays().get(numberOfOverlays));
        }
        ArrayList<GeoPoint> roadPoints = new ArrayList<>();
        getLocation();
        roadPoints.add((currentLocation));

        roadPoints.add(marker.getPosition());

        OSRMRoadManager roadManager = new OSRMRoadManager(getApplicationContext(), "22-Transport");
        roadManager.setMean(OSRMRoadManager.MEAN_BY_FOOT);
        Road road = roadManager.getRoad(roadPoints);
//        RoadManager roadManager = new GraphHopperRoadManager("9b8e0c01-5851-4b2d-9cc5-184a5a9f40c8", false);
//        roadManager.addRequestOption("vehicle=foot");
//        Road road = roadManager.getRoad(route);

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


    //new GeoPoint(35.193098292023045, -0.6314308284717288)

    private void travelPlanner() {
        GeoPoint destinationStation = new GeoPoint(35.19181984486152, -0.6367524076104305);
        StationDetails closestSubwayStationGetOn;
        StationDetails closestSubwayStationGetOff;
        GeoPoint temp = new GeoPoint(0.0, 0.0);
        ArrayList<StationDetails> stationss = new ArrayList<>();

        ArrayList<GeoPoint> points = new ArrayList<>();
        points.add(currentLocation);
        database.deleteAllNearestSubwayStation();
        for (int i = 0; i < MainActivity.stations.size(); i++) {
            StationDetails availableStation = new StationDetails();
            availableStation.nomAr = MainActivity.stations.get(i).nomAr;
            availableStation.nomFr = MainActivity.stations.get(i).nomFr;
            availableStation.coordonnees = stations.get(i).coordonnees;
            getRouteOnlineOnFootDetails(currentLocation, MainActivity.stations.get(i).coordonnees);
            availableStation.distanceTo = distanceTo;
            availableStation.timeTo = timeTo;
            stationss.add(availableStation);
            sort(stationss);
            database.addNearStation(availableStation);
        }
        closestSubwayStationGetOn = stationss.get(0);
        Log.d("TravelPlanner11", closestSubwayStationGetOn.toString());
//        temp.setLatitude(closestSubwayStationGetOn.coordonnees.getLatitude());
//        temp.setLongitude(closestSubwayStationGetOn.coordonnees.getLongitude());
//        points.add(temp);
        stationss.clear();
        for (int i = 0; i < MainActivity.stations.size(); i++) {
            database.deleteAllNearestSubwayStation();

            StationDetails availableStation = new StationDetails();
            availableStation.nomAr = MainActivity.stations.get(i).nomAr;
            availableStation.nomFr = MainActivity.stations.get(i).nomFr;
            getRouteOnlineOnFootDetails(destinationStation, MainActivity.stations.get(i).coordonnees);
            availableStation.coordonnees = stations.get(i).coordonnees;
            availableStation.distanceTo = distanceTo;
            availableStation.timeTo = timeTo;
            stationss.add(availableStation);
            sort(stationss);
        }
        closestSubwayStationGetOff = stationss.get(0);

        Log.d("TravelPlanner12", closestSubwayStationGetOff.toString());
        drawRouteOnlineOnFoot(currentLocation, closestSubwayStationGetOn.coordonnees);
        numberOfOverlays++;
        drawRouteOnlineOnFoot(closestSubwayStationGetOff.coordonnees, destinationStation);
        numberOfOverlays++;

//        temp.setLatitude(closestSubwayStationGetOff.coordonnees.getLatitude());
//        temp.setLongitude(closestSubwayStationGetOff.coordonnees.getLongitude());
//        points.add(temp);
        points.add(destinationStation);

        Log.d("TravelPlanner", points.toString());
        Log.d("TestClosest", String.valueOf(closestSubwayStationGetOn));
    }


    private double getDistanceOffline(GeoPoint currentLocation, GeoPoint targetedLocation) {
        float[] distance = new float[2];
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(),
                targetedLocation.getLatitude(), targetedLocation.getLongitude(), distance);
        return distance[0] / 1000;
    }


    private void getRouteOnlineOnFootDetails(GeoPoint start, GeoPoint end) {
        ArrayList<GeoPoint> roadPoints = new ArrayList<>();
        getLocation();
        roadPoints.add((start));
        roadPoints.add(end);

        OSRMRoadManager roadManager = new OSRMRoadManager(getApplicationContext(), "22-Transport");
        roadManager.setMean(OSRMRoadManager.MEAN_BY_FOOT);
        Road road = roadManager.getRoad(roadPoints);
//        RoadManager roadManager1 = new GraphHopperRoadManager("484e2932-b8a9-4bfa-a760-d3f32f84e347", false);
//        roadManager1.addRequestOption("vehicle=foot");
//        Road road = roadManager1.getRoad(roadPoints);
        if (road.mLength == 0) {
            distanceTo = getDistanceOffline(currentLocation, end);
            Log.d("allStation", "Unavailable " + getDistanceOffline(currentLocation, end));
            timeTo = 99999.0;
        } else {
            Log.d("allStation", "Available " + getDistanceOffline(currentLocation, end));
            distanceTo = road.mLength;
            timeTo = road.mDuration / 60;
        }
    }


    private void drawRouteOnlineOnFoot(GeoPoint start, GeoPoint end) {
        ArrayList<GeoPoint> roadPoints = new ArrayList<>();
        roadPoints.add((start));
        roadPoints.add(end);

        OSRMRoadManager roadManager = new OSRMRoadManager(getApplicationContext(), "22-Transport");
        roadManager.setMean(OSRMRoadManager.MEAN_BY_FOOT);
        Road road = roadManager.getRoad(roadPoints);

        Polyline route = RoadManager.buildRoadOverlay(road);
        myMap.getOverlays().add(route);

//        RoadManager roadManager1 = new GraphHopperRoadManager("484e2932-b8a9-4bfa-a760-d3f32f84e347", false);
//        roadManager1.addRequestOption("vehicle=foot");
//        Road road = roadManager1.getRoad(roadPoints);

    }

    void sort(ArrayList<StationDetails> stationDetails) {
        StationDetails temp;
        for (int i = 0; i < stationDetails.size() - 1; i++)
            for (int j = i; j < stationDetails.size(); j++) {
                if (stationDetails.get(i).distanceTo > stationDetails.get(j).distanceTo) {
                    temp = stationDetails.get(i);
                    stationDetails.set(i, stationDetails.get(j));
                    stationDetails.set(j, temp);
                }
            }
    }

    void cleanMap(MapView mapView) {
        if (mapView.getOverlays().size() > numberOfOverlays) {
            mapView.getOverlays().remove(mapView.getOverlays().get(numberOfOverlays));
        }
    }
}