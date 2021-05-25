package com.esi.navigator_22;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
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
import org.osmdroid.bonuspack.routing.GraphHopperRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
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
import java.util.Objects;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//import androidx.appcompat.app.AlertDialog;
//import org.osmdroid.bonuspack.routing.GraphHopperRoadManager;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    String urlStations = "http://192.168.1.5:3000/stations";
    String urlRouteSubway = "http://192.168.1.5:3000/subway";
    String urlRouteBus = "http://192.168.1.5:3000/bus";
    String urlStationsBus = "http://192.168.1.5:3000/stations_buses";
    private String myResponse;


    static MapView myMap;
    ScaleBarOverlay echelle;
    MyLocationNewOverlay mLocationOverlay;
    RotationGestureOverlay mRotationGestureOverlay;
    MapEventsOverlay mapEventsOverlay;

    ImageView currentPosition, reset;
    LinearLayout menu_linear;
    ImageView subway, bus3, bus3bis, bus11, bus16, bus17, bus25, bus27, arrow_down, arrow_up,bus_22;

    Station stationSubway = new Station();
    StationBus stationBus = new StationBus();
    public GeoPoint defaultLocation = new GeoPoint(35.19115853846664, -0.6298066051152207);
    static GeoPoint currentLocation = new GeoPoint(0.0, 0.0);
    GeoPoint point = new GeoPoint(0.0, 0.0);

    DbHelper database = DbHelper.getInstance(this);
    static ArrayList<Station> stationsSubway = new ArrayList<>();
    static ArrayList<StationBus> stationsBus = new ArrayList<>();
    static ArrayList<StationBus> stationsBusByNumber = new ArrayList<>();
    ArrayList<GeoPoint> chemin = new ArrayList<>();
    double minZ = 13.0;
    double maxZ = 19.0;
    DrawerLayout drawerLayout;
    LinearLayout scroll_menu;
    ActionBarDrawerToggle toggle;
    int[] ids = new int[22];
    int[] couleurs;

    double distanceTo, timeTo;
    boolean drawn = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ids = new int[]{R.id.station1, R.id.station2, R.id.station3, R.id.station4, R.id.station5, R.id.station6, R.id.station7, R.id.station8, R.id.station9,
                R.id.station10, R.id.station11, R.id.station12, R.id.station13, R.id.station14, R.id.station15, R.id.station16, R.id.station17, R.id.station18, R.id.station19, R.id.station20, R.id.station21, R.id.station22};
        couleurs = new int[]{getApplicationContext().getResources().getColor(R.color.black),
                getApplicationContext().getResources().getColor(R.color.red),
                getApplicationContext().getResources().getColor(R.color.green),
                getApplicationContext().getResources().getColor(R.color.blue),
                getApplicationContext().getResources().getColor(R.color.yellow),
                getApplicationContext().getResources().getColor(R.color.dark_blue)};

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stationsSubway = database.getAllStationsSubway();
        stationsBus = database.getAllStationsBus();


        myMap = findViewById(R.id.map);
        currentPosition = findViewById(R.id.currentPosition);
        reset = findViewById(R.id.reset);
        bus3 = findViewById(R.id.bus_3);
        bus3bis = findViewById(R.id.bus_3bis);
        bus11 = findViewById(R.id.bus_11);
        bus16 = findViewById(R.id.bus_16);
        bus17 = findViewById(R.id.bus_17);
        bus_22= findViewById(R.id.bus_22);
        bus25 = findViewById(R.id.bus_25);
        bus27 = findViewById(R.id.bus_27);
        subway = findViewById(R.id.subway);
        scroll_menu = findViewById(R.id.stations_menu);
        menu_linear = findViewById(R.id.menu_linear);
        arrow_down = findViewById(R.id.arrow_down);
        arrow_up = findViewById(R.id.arrow_up);
        drawerLayout = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);

        OkHttpClient client = new OkHttpClient();


        Animation anim = new AlphaAnimation(0.60f, 1.0f);
        anim.setDuration(500); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        currentPosition.startAnimation(anim);

        arrow_down.setOnClickListener(v -> {
            menu_linear.setVisibility(View.VISIBLE);
            arrow_down.setVisibility(View.INVISIBLE);
            arrow_up.setVisibility(View.VISIBLE);
        });

        arrow_up.setOnClickListener(v -> {
            menu_linear.setVisibility(View.INVISIBLE);
            arrow_up.setVisibility(View.INVISIBLE);
            arrow_down.setVisibility(View.VISIBLE);
        });

        setNavigationViewListener();

        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.search));
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
            BoundingBox bbox = new BoundingBox(35.23286, -0.540047, 35.128473, -0.708618);
//            cacheManager.downloadAreaAsync(this, bbox, 12, 17);
            cacheManager.downloadAreaAsyncNoUI(this, bbox, 12, 17, new CacheManager.CacheManagerCallback() {

                @Override
                public void onTaskComplete() {
                    Log.d("DownloadMap", "Finished");
                }

                @Override
                public void updateProgress(int progress, int currentZoomLevel, int zoomMin, int zoomMax) {
                    Log.d("DownloadMap", "Updated " + progress);
                }

                @Override
                public void downloadStarted() {
                    Log.d("DownloadMap", "Started");

                }

                @Override
                public void setPossibleTilesInArea(int total) {
                    Log.d("DownloadMap", "Fixed " + total);

                }

                @Override
                public void onTaskFailed(int errors) {
                    Log.d("DownloadMap", "Failed");
                }
            });
        });
        Executors.newFixedThreadPool(4).execute(downloadMapToCache);


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
        mapEventsOverlay = new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                InfoWindow.closeAllInfoWindowsOn(myMap);
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                InfoWindow.closeAllInfoWindowsOn(myMap);
                clearMap();
                return false;
            }
        });
        myMap.getOverlays().add(mapEventsOverlay);

        echelle = new ScaleBarOverlay(myMap);
        myMap.getOverlays().add(echelle);
        myMap.setMultiTouchControls(true);
        mRotationGestureOverlay = new RotationGestureOverlay(myMap);
        mRotationGestureOverlay.setEnabled(true);
        myMap.setMultiTouchControls(true);

        myMap.getOverlays().add(mRotationGestureOverlay);
//        addMarker(myMap, new GeoPoint(35.19181984486152, -0.6367524076104305));


        currentPosition.setOnClickListener(v -> {
            getLocation();
            myMap.getController().setCenter(currentLocation);
            myMap.getController().setZoom(16.0);
        });

        subway.setOnClickListener(v -> {
            getLocation();
            chemin = database.getAllPointsSub();
            tracerCheminSubway(chemin, myMap);
            addStationsSubway();
        });

        bus3.setOnClickListener(v -> {
            chemin = database.getAllPointsBusByNumber("A03");
            tracerCheminBus(chemin, myMap, 255, 0, 0);
            stationsBusByNumber = database.getAllStationsBusByNumber("A03");
            addStationsBus(stationsBusByNumber);
            Log.d("Ligne : ", "A03");
        });
        bus3bis.setOnClickListener(v -> {
            chemin = database.getAllPointsBusByNumber("A03 bis");
            tracerCheminBus(chemin, myMap, 255, 0, 0);
            stationsBusByNumber = database.getAllStationsBusByNumber("A03 bis");
            addStationsBus(stationsBusByNumber);
            Log.d("Ligne : ", "A03 bis");
        });
        bus11.setOnClickListener(v -> {
            chemin = database.getAllPointsBusByNumber("A11");
            tracerCheminBus(chemin, myMap, 0, 0, 0);
            stationsBusByNumber = database.getAllStationsBusByNumber("A11");
            addStationsBus(stationsBusByNumber);
            Log.d("Ligne : ", "A11");
        });
        bus16.setOnClickListener(v -> {
            chemin = database.getAllPointsBusByNumber("A16");
            tracerCheminBus(chemin, myMap, 0, 0, 255);
            stationsBusByNumber = database.getAllStationsBusByNumber("A16");
            addStationsBus(stationsBusByNumber);
            Log.d("Ligne : ", "A16");
        });
        bus17.setOnClickListener(v -> {
            chemin = database.getAllPointsBusByNumber("A17");
            tracerCheminBus(chemin, myMap, 0, 255, 0);
            stationsBusByNumber = database.getAllStationsBusByNumber("A17");
            addStationsBus(stationsBusByNumber);
            Log.d("Ligne : ", "A17");
        });

        bus_22.setOnClickListener(v -> {
            chemin=database.getAllPointsBusByNumber("A22");
            tracerCheminBus(chemin, myMap, 105,105,105);
            stationsBusByNumber = database.getAllStationsBusByNumber("A22");
            addStationsBus(stationsBusByNumber);
            Log.d("Ligne : ", "A22");
        });
        bus25.setOnClickListener(v -> {
            chemin = database.getAllPointsBusByNumber("A25");
            tracerCheminBus(chemin, myMap, 255, 0, 255);
            stationsBusByNumber = database.getAllStationsBusByNumber("A25");
            addStationsBus(stationsBusByNumber);
            Log.d("Ligne : ", "A25");
        });
        bus27.setOnClickListener(v -> {
            chemin = database.getAllPointsBusByNumber("A27");
            tracerCheminBus(chemin, myMap, 0, 255, 255);
            stationsBusByNumber = database.getAllStationsBusByNumber("A27");
            addStationsBus(stationsBusByNumber);
            Log.d("Ligne : ", "A27");
        });


        Request request = new Request.Builder()
                .url(urlRouteSubway)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    fetchRouteSubway(response);
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

        request = new Request.Builder()
                .url(urlRouteBus)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    fetchRouteBus(response);
                }
            }
        });

        request = new Request.Builder()
                .url(urlStationsBus)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    fetchAllStationsBus(response);

                }
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Log.d("OverlaysNumber", String.valueOf(myMap.getOverlays().size()));

        reset.setOnClickListener(v -> clearMap());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        ArrayList<Station> stations = database.getAllStationsSubway();
        SubMenu tramwaySubMenu = menu.addSubMenu("Stations de Tramway");
        SubMenu busSubMenu = menu.addSubMenu("Stations de bus");

        for (int i = 0; i < 7; i++) {
            busSubMenu.addSubMenu("Ligne " + i);
        }
        for (int i = 0; i < stations.size(); i++) {
            tramwaySubMenu.add(R.id.subway_stations, ids[i], 1, stations.get(i).nomFr + " " + stations.get(i).nomAr);
        }

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.stations_sba, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        getLocation();
        for (int i = 0; i < stationsSubway.size(); i++) {
            if (item.getItemId() == ids[i]) {
                int random = (int) Math.round(Math.random() * 5);
                Log.d("Couleurs", String.valueOf(random));
                fetchRoute(currentLocation, stationsSubway.get(i).coordonnees, true);
                addMarker(myMap, stationsSubway.get(i).coordonnees, stationsSubway.get(i).nomFr + " " + stationsSubway.get(i).nomAr);
                myMap.getController().setCenter(stationsSubway.get(i).coordonnees);
                myMap.getController().setZoom(17.0);
            }
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
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    private void arreterLocalisation() {
        mLocationOverlay.disableMyLocation();
        mLocationOverlay.disableFollowLocation();
    }


    //Fetching
    private void fetchAllStations(Response response) throws IOException {
        myResponse = Objects.requireNonNull(response.body()).string();
        JSONArray jsonarray = null;
        try {
            jsonarray = new JSONArray(myResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < Objects.requireNonNull(jsonarray).length(); i++) {
            JSONObject jsonobject = null;
            try {
                jsonobject = jsonarray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                assert jsonobject != null;
                stationSubway.nomFr = jsonobject.getString("nomFr");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                stationSubway.nomAr = jsonobject.getString("nomAr");
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
            stationSubway.coordonnees = point;
            database.addStationSubway(stationSubway);
        }
    }

    private void fetchAllStationsBus(Response response) throws IOException {
        myResponse = Objects.requireNonNull(response.body()).string();
        JSONArray jsonarray = null;
        try {
            jsonarray = new JSONArray(myResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < Objects.requireNonNull(jsonarray).length(); i++) {
            JSONObject jsonobject = null;
            try {
                jsonobject = jsonarray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                assert jsonobject != null;
                stationBus.nomFr = jsonobject.getString("nomFr");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                stationBus.numLigne = jsonobject.getString("numero");
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
            stationBus.coordonnees = point;
            Log.d("StationBus", stationBus.toString());
            database.addStationBus(stationBus);
        }
    }

    private void fetchRouteSubway(Response response) throws IOException {
        myResponse = response.body().string();
        JSONArray jsonarray = null;
        try {
            jsonarray = new JSONArray(myResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < Objects.requireNonNull(jsonarray).length(); i++) {
            JSONObject jsonobject;
            GeoPoint po = new GeoPoint(0.0, 0.0);
            try {
                jsonobject = jsonarray.getJSONObject(i);
                po.setLatitude(jsonobject.getDouble("latitude"));
                po.setLongitude(jsonobject.getDouble("longitude"));
                database.addPointSub(po);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void fetchRouteBus(Response response) throws IOException {
        myResponse = Objects.requireNonNull(response.body()).string();
        JSONArray jsonarray = null;
        try {
            jsonarray = new JSONArray(myResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < Objects.requireNonNull(jsonarray).length(); i++) {
            JSONObject jsonobject = null;
            GeoPoint po = new GeoPoint(0.0, 0.0);
            RouteBus pointBus = new RouteBus(new GeoPoint(0.0, 0.0), "Ligne de bus");
            try {
                jsonobject = jsonarray.getJSONObject(i);
                po.setLatitude(jsonobject.getDouble("latitude"));
                po.setLongitude(jsonobject.getDouble("longitude"));
                pointBus.numLigne = (jsonobject.getString("numero"));
                Log.d("BusBus", jsonobject.toString());
                pointBus.coordinates.setLatitude(po.getLatitude());
                pointBus.coordinates.setLongitude(po.getLongitude());
                database.addPointBus(pointBus);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    //Adding Overlays
    private void addStationsSubway() {

        for (int i = 0; i < stationsSubway.size(); i++) {
            addStationSubway(myMap, stationsSubway.get(i).coordonnees, stationsSubway.get(i).nomFr, stationsSubway.get(i).nomAr);
        }
    }

    private void addStationsBus(ArrayList<StationBus> stationsBus) {
        for (int i = 0; i < stationsBus.size(); i++) {
            addStationBus(myMap, stationsBus.get(i).coordonnees, stationsBus.get(i).nomFr, stationsBus.get(i).numLigne);
        }

    }

    private void tracerCheminSubway(ArrayList<GeoPoint> chemin, MapView mapView) {
        Polyline line = new Polyline();
        line.setWidth(8);
//        line.getOutlinePaint();
        line.setColor(Color.rgb(230, 138, 0));
        line.setDensityMultiplier(0.1f);
        line.setPoints(chemin);
        line.setGeodesic(true);
        mapView.getOverlayManager().add(line);

    }

    private void tracerCheminBus(ArrayList<GeoPoint> chemin, MapView mapView, int red, int green, int blue) {
        Polyline line = new Polyline();
        line.setWidth(10);
//        line.getOutlinePaint();
        line.setColor(Color.rgb(red, green, blue));
        line.setDensityMultiplier(0.5f);
        line.setPoints(chemin);
//        line.setGeodesic(true);
        mapView.getOverlayManager().add(line);
        mapView.invalidate();

    }

    public void addMarker(MapView mapMarker, GeoPoint positionMarker, String nom) {
        float[] distance = new float[1];
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), positionMarker.getLatitude(), positionMarker.getLongitude(), distance);
        Marker marker = new Marker(mapMarker);
        marker.setPosition(positionMarker);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setAlpha(0.8f);
        marker.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.pin_tramway));
//        marker.setIcon(context.getResources().getDrawable(R.drawable.ic_tramway));

//        marker.setSnippet(nomFrMarker + "\n " + " " + nomArMarker);
        marker.setPanToView(true);
        mapMarker.invalidate();
        mapMarker.getOverlays().add(marker);
        getLocation();
        marker.setSnippet(nom + "\n" + fetchRoute(currentLocation, marker.getPosition(), true));
        marker.setInfoWindow(new InfoWindow(R.layout.custom_bubble, myMap) {
            @Override
            public void onOpen(Object item) {
                TextView station = mView.findViewById(R.id.nomStation);
                station.setText(marker.getSnippet());
            }

            @Override
            public void onClose() {
                InfoWindow.closeAllInfoWindowsOn(myMap);
            }

        });
//            marker.showInfoWindow();
        marker.showInfoWindow();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void addStationSubway(MapView mapMarker, GeoPoint positionMarker, String nomFrMarker, String nomArMarker) {
        float[] distance = new float[1];
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), positionMarker.getLatitude(), positionMarker.getLongitude(), distance);
        Marker marker = new Marker(mapMarker);
        marker.setPosition(positionMarker);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setAlpha(0.8f);
        marker.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.pin_tramway));
//        marker.setSnippet(nomFrMarker + "\n " + " " + nomArMarker);
        marker.setTitle(nomFrMarker + " " + nomArMarker);
        marker.setPanToView(true);
        mapMarker.invalidate();
        mapMarker.getOverlays().add(marker);


        marker.setOnMarkerClickListener((marker1, mapView) -> {
            getLocation();
            marker1.setSnippet(fetchRoute(currentLocation, marker1.getPosition(), true));
            marker1.setInfoWindow(new InfoWindow(R.layout.custom_bubble, myMap) {
                @Override
                public void onOpen(Object item) {
                    InfoWindow.closeAllInfoWindowsOn(myMap);
                    TextView station = mView.findViewById(R.id.nomStation);
                    station.setText(marker1.getTitle() + "\n" + marker1.getSnippet());
                }

                @Override
                public void onClose() {
                    InfoWindow.closeAllInfoWindowsOn(myMap);
                }

            });
//            marker1.showInfoWindow();
            marker1.showInfoWindow();
            mapView.getController().setCenter(marker1.getPosition());
            mapView.getController().setZoom(16.0);
            return true;
        });
    }

    public void addStationBus(MapView mapMarker, GeoPoint positionMarker, String nomFr, String numLigne) {
        Marker marker = new Marker(mapMarker);
        marker.setPosition(positionMarker);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setAlpha(0.8f);
        marker.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.pin_bus));
//        marker.setSnippet(nomFrMarker + "\n " + " " + nomArMarker);
        marker.setTitle(nomFr + " " + numLigne);
        marker.setPanToView(true);
        mapMarker.invalidate();
        mapMarker.getOverlays().add(marker);


        marker.setOnMarkerClickListener((marker1, mapView) -> {
//            tracerRoute(marker1.getPosition(), mapView, true);
            getLocation();
            marker1.setSnippet(fetchRoute(currentLocation,marker1.getPosition(), true));
//            marker1.setSnippet(nomFr+" "+numLigne);

            marker1.setInfoWindow(new InfoWindow(R.layout.custom_bubble, myMap) {
                @Override
                public void onOpen(Object item) {
                    InfoWindow.closeAllInfoWindowsOn(myMap);
                    TextView station = mView.findViewById(R.id.nomStation);
//                    station.setText(marker1.getTitle() + "\n" + marker1.getSnippet());
                    station.setText(marker1.getTitle());
                }

                @Override
                public void onClose() {
                    InfoWindow.closeAllInfoWindowsOn(myMap);
                }

            });
//            marker1.showInfoWindow();
            marker1.showInfoWindow();
            mapView.getController().setCenter(marker1.getPosition());
            mapView.getController().setZoom(17.0);
            return true;
        });
    }


    String fetchRoute(GeoPoint start, GeoPoint end, boolean draw) {
        ArrayList<GeoPoint> roadPoints = new ArrayList<>();
        getLocation();
        roadPoints.add((start));
        roadPoints.add(end);

//        OSRMRoadManager roadManager = new OSRMRoadManager(getApplicationContext(), "22-Transport");
//        roadManager.setMean(OSRMRoadManager.MEAN_BY_FOOT);
//        Road road = roadManager.getRoad(roadPoints);
//        RoadManager roadManager = new MapQuestRoadManager("jmQfNVRjCrl8jiDLW1QNO5hTkWuyv5mm");
//        roadManager.addRequestOption("routeType=pedestrian");
//        Road road = roadManager.getRoad(roadPoints);
        RoadManager roadManager = new GraphHopperRoadManager("484e2932-b8a9-4bfa-a760-d3f32f84e347", false);
        roadManager.addRequestOption("vehicle=foot");
        Road road = roadManager.getRoad(roadPoints);

        if (road.mLength == 0) {
            distanceTo = getDistanceOffline(start, end);
            timeTo = 99999.0;
            Log.d("FetchRoute","indisponible");
            return "Route indisponible";
        }
        if (draw == true) {
            int random = (int) Math.round(Math.random() * 5);
            Polyline route = RoadManager.buildRoadOverlay(road, couleurs[random], 8.0f);
            myMap.getOverlays().add(route);
        }
        String duration = format(road.mDuration / 60);
        String dist = format(road.mLength);
        String distanceTo = "km " + dist + " كم";
        String timeTo = "minutes " + duration + " دقيقة";
        Log.d("FetchRoute","disponible");
        return distanceTo + "\n" + timeTo;

    }


    String tracerShortestRoute(Marker marker, MapView mapView) {
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

/*    private void travelPlanner() {
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
    }*/

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


    private void drawRouteOnlineOnFoot(GeoPoint start, GeoPoint end, int color) {
        ArrayList<GeoPoint> roadPoints = new ArrayList<>();
        roadPoints.add((start));
        roadPoints.add(end);
        OSRMRoadManager roadManager = new OSRMRoadManager(getApplicationContext(), "22-Transport");
        roadManager.setMean(OSRMRoadManager.MEAN_BY_FOOT);
        Road road = roadManager.getRoad(roadPoints);

        Polyline route = RoadManager.buildRoadOverlay(road, color, 8.0f);
        Log.d("couleurs", String.valueOf(color));
        route.setDensityMultiplier(15.0f);
//        route.setColor(getApplicationContext().getResources().getColor(R.color.red));


        myMap.getOverlays().add(route);

//        RoadManager roadManager1 = new GraphHopperRoadManager("484e2932-b8a9-4bfa-a760-d3f32f84e347", false);
//        roadManager1.addRequestOption("vehicle=foot");
//        Road road = roadManager1.getRoad(roadPoints);

    }


    //Sort ArrayList by Distance
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

    //Menu Navigation
    private void setNavigationViewListener() {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NotNull MenuItem item) {
        getLocation();
        Bundle send = new Bundle();
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


    //Clean Map from all markers and polylines
    private void clearMap() {
        myMap.getOverlays().clear();
        myMap.getOverlays().add(mLocationOverlay);
        myMap.getOverlays().add(mRotationGestureOverlay);
        myMap.getOverlays().add(echelle);
        myMap.getOverlays().add(mapEventsOverlay);
    }

    //Formatting the values to #.##
    static String format(double number) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(number);
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
}