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
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//import androidx.appcompat.app.AlertDialog;
//import org.osmdroid.bonuspack.routing.GraphHopperRoadManager;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    String urlStations = "http://192.168.1.9:3000/stations_sba";
    String urlRouteSubway = "http://192.168.1.9:3000/subway";
    String urlRouteBus = "http://192.168.1.9:3000/bus";
    String urlCorrespondance = "http://192.168.1.9:3000/correspondance";
    private String myResponse;


    static MapView myMap;
    ScaleBarOverlay echelle;
    MyLocationNewOverlay mLocationOverlay;
    RotationGestureOverlay mRotationGestureOverlay;
    MapEventsOverlay mapEventsOverlay;

    ImageView currentPosition, reset;
    LinearLayout menu_linear;
    ImageView subway, bus3, bus3bis, bus11, bus16, bus17, bus25, bus27, arrow_down, arrow_up, bus_22;
    ImageView walk, car;

    Station station = new Station();
    public GeoPoint defaultLocation = new GeoPoint(35.19115853846664, -0.6298066051152207);
    static GeoPoint currentLocation = new GeoPoint(0.0, 0.0);
    GeoPoint point = new GeoPoint(0.0, 0.0);

    DbHelper database = DbHelper.getInstance(this);
    static ArrayList<Station> stationsSubway = new ArrayList<>();
    static ArrayList<Station> stationsBus = new ArrayList<>();
    static ArrayList<RouteBus> routeBus = new ArrayList<>();
    static ArrayList<Station> stationsBus3 = new ArrayList<>();
    static ArrayList<Station> stationsBus3bis = new ArrayList<>();
    static ArrayList<Station> stationsBus11 = new ArrayList<>();
    static ArrayList<Station> stationsBus16 = new ArrayList<>();
    static ArrayList<Station> stationsBus17 = new ArrayList<>();
    static ArrayList<Station> stationsBus22 = new ArrayList<>();
    static ArrayList<Station> stationsBus25 = new ArrayList<>();
    static ArrayList<Station> stationsBus27 = new ArrayList<>();
    ArrayList<GeoPoint> chemin = new ArrayList<>();
    ArrayList<RouteBus> cheminBus = new ArrayList<>();
    double minZ = 13.0;
    double maxZ = 19.0;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    int[] ids_tramway = new int[22];
    int[] ids_bus3 = new int[15];
    int[] ids_bus3bis = new int[9];
    int[] ids_bus11 = new int[14];
    int[] ids_bus16 = new int[16];
    int[] ids_bus17 = new int[12];
    int[] ids_bus22 = new int[8];
    int[] ids_bus25 = new int[10];
    int[] ids_bus27 = new int[7];
    int[] couleurs;
    int bus3_click = 1, bus3bis_click = 1, bus11_click = 1, bus16_click = 1, bus17_click = 1, bus22_click = 1, bus25_click = 1,
            bus27_click = 1, tramway_click = 1;
    static ArrayList<CustomOverlay> customOverlays = new ArrayList<>();

    double distanceTo, timeTo;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ids_tramway = new int[]{R.id.station1, R.id.station2, R.id.station3, R.id.station4, R.id.station5, R.id.station6, R.id.station7, R.id.station8, R.id.station9,
                R.id.station10, R.id.station11, R.id.station12, R.id.station13, R.id.station14, R.id.station15, R.id.station16, R.id.station17, R.id.station18,
                R.id.station19, R.id.station20, R.id.station21, R.id.station22};
        ids_bus3 = new int[]{R.id.station_3_1, R.id.station_3_2, R.id.station_3_3, R.id.station_3_4, R.id.station_3_5, R.id.station_3_6, R.id.station_3_7, R.id.station_3_8,
                R.id.station_3_9,
                R.id.station_3_10, R.id.station_3_11, R.id.station_3_12, R.id.station_3_13, R.id.station_3_14, R.id.station_3_15};
        ids_bus3bis = new int[]{R.id.station_3bis_1, R.id.station_3bis_2, R.id.station_3bis_3, R.id.station_3bis_4, R.id.station_3bis_5, R.id.station_3bis_6, R.id.station_3bis_7,
                R.id.station_3bis_8, R.id.station_3bis_9};
        ids_bus11 = new int[]{R.id.station_11_1, R.id.station_11_2, R.id.station_11_3, R.id.station_11_4, R.id.station_11_5, R.id.station_11_6, R.id.station_11_7, R.id.station_11_8,
                R.id.station_11_9,
                R.id.station_11_10, R.id.station_11_11, R.id.station_11_12, R.id.station_11_13, R.id.station_11_14};

        ids_bus16 = new int[]{R.id.station_16_1, R.id.station_16_2, R.id.station_16_3, R.id.station_16_4, R.id.station_16_5, R.id.station_16_6, R.id.station_16_7, R.id.station_16_8,
                R.id.station_16_9,
                R.id.station_16_10, R.id.station_16_11, R.id.station_16_12, R.id.station_16_13, R.id.station_16_14, R.id.station_16_15, R.id.station_16_15};

        ids_bus17 = new int[]{R.id.station_17_1, R.id.station_17_2, R.id.station_17_3, R.id.station_17_4, R.id.station_17_5, R.id.station_17_6, R.id.station_17_7, R.id.station_17_8,
                R.id.station_17_9,
                R.id.station_17_10, R.id.station_17_11, R.id.station_17_12};

        ids_bus22 = new int[]{R.id.station_22_1, R.id.station_22_2, R.id.station_22_3, R.id.station_22_4, R.id.station_22_5, R.id.station_22_6, R.id.station_22_7, R.id.station_22_8,};

        ids_bus25 = new int[]{R.id.station_25_1, R.id.station_25_2, R.id.station_25_3, R.id.station_25_4, R.id.station_25_5, R.id.station_25_6, R.id.station_25_7, R.id.station_25_8,
                R.id.station_25_9,
                R.id.station_25_10};

        ids_bus27 = new int[]{R.id.station_27_1, R.id.station_27_2, R.id.station_27_3, R.id.station_27_4, R.id.station_27_5, R.id.station_27_6, R.id.station_27_7};

        couleurs = new int[]{getApplicationContext().getResources().getColor(R.color.black),
                getApplicationContext().getResources().getColor(R.color.red),
                getApplicationContext().getResources().getColor(R.color.green),
                getApplicationContext().getResources().getColor(R.color.blue),
                getApplicationContext().getResources().getColor(R.color.yellow),
                getApplicationContext().getResources().getColor(R.color.dark_blue)};


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myMap = findViewById(R.id.map);
        myMap.setUseDataConnection(false);
        currentPosition = findViewById(R.id.currentPosition);
        reset = findViewById(R.id.reset);
        bus3 = findViewById(R.id.bus_3);
        bus3bis = findViewById(R.id.bus_3bis);
        bus11 = findViewById(R.id.bus_11);
        bus16 = findViewById(R.id.bus_16);
        bus17 = findViewById(R.id.bus_17);
        bus_22 = findViewById(R.id.bus_22);
        bus25 = findViewById(R.id.bus_25);
        bus27 = findViewById(R.id.bus_27);
        subway = findViewById(R.id.subway);
        car = findViewById(R.id.car);
        walk = findViewById(R.id.walk);

//        scroll_menu = findViewById(R.id.stations_menu);
        menu_linear = findViewById(R.id.menu_linear);
        arrow_down = findViewById(R.id.arrow_down);
        arrow_up = findViewById(R.id.arrow_up);
        drawerLayout = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);

        OkHttpClient client = new OkHttpClient();


        Animation anim = new AlphaAnimation(0.8f, 1.0f);
        anim.setDuration(1000); //You can manage the blinking time with this parameter
        anim.setStartOffset(0);
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
        Executors.newFixedThreadPool(1).execute(downloadMapToCache);


        if ((ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED)) {
            Log.d("LogGps", "Permissions granted");
        } else {
            Toast.makeText(this, "Localisation requise", Toast.LENGTH_LONG).show();
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1
            );
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
        myMap.getOverlays().add(mRotationGestureOverlay);


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
                Log.d("Nouvelle station1", "fail");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    fetchAllStations(response);
                    Log.d("Nouvelle station2", "success");
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
                .url(urlCorrespondance)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    fetchRouteCorrespondance(response);
                }
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        stationsSubway = database.getAllTramwayStations();
        stationsBus = database.getAllBusStations();
        routeBus = database.getAllPointsBus();
        stationsBus3 = searchBusStationByNumber("A03");
        stationsBus3bis = searchBusStationByNumber("A03 bis");
        stationsBus11 = searchBusStationByNumber("A11");
        stationsBus16 = searchBusStationByNumber("A16");
        stationsBus17 = searchBusStationByNumber("A17");
        stationsBus22 = searchBusStationByNumber("A22");
        stationsBus25 = searchBusStationByNumber("A25");
        stationsBus27 = searchBusStationByNumber("A27");

        currentPosition.setOnClickListener(v -> {
            getLocation();
            myMap.getController().setCenter(currentLocation);
            myMap.getController().setZoom(16.0);
        });

        subway.setOnClickListener(v -> {
            Log.d("MapOverlays", myMap.getOverlays().size() + "");
            int i = tramway_click;
            if (i == 1) {
                chemin = database.getAllPointsSub();
                tracerCheminSubway(chemin, myMap);
                chemin = database.getAllPointsCorrespondance();
                tracerCorrespondance(chemin, myMap);
                addStationsSubway();
                tramway_click++;
            } else {
                for (int k = 0; k < customOverlays.size(); k++)
                    if (customOverlays.get(k).name.equals("tramway") || customOverlays.get(k).name.equals("correspondance"))
                        myMap.getOverlays().remove(customOverlays.get(k).overlayItem);
                myMap.invalidate();
                tramway_click--;
            }
        });


        bus3.setOnClickListener(v -> {
            int i = bus3_click;
            Log.d("MapOverlays", myMap.getOverlays().size() + "");
            if (i == 1) {
                cheminBus = searchBusRouteByNumber("A03");
                addBus(cheminBus, myMap, 255, 0, 0, "A03");
                bus3_click++;
            } else {
                for (int k = 0; k < customOverlays.size(); k++)
                    if (customOverlays.get(k).name.equals("A03"))
                        myMap.getOverlays().remove(customOverlays.get(k).overlayItem);
                myMap.invalidate();
                bus3_click--;
            }
        });

        bus3bis.setOnClickListener(v -> {
            int i = bus3bis_click;
            Log.d("MapOverlays", myMap.getOverlays().size() + "");
            if (i == 1) {
                cheminBus = searchBusRouteByNumber("A03 bis");
                addBus(cheminBus, myMap, 255, 0, 0, "A03 bis");
                bus3bis_click++;
            } else {
                for (int k = 0; k < customOverlays.size(); k++)
                    if (customOverlays.get(k).name.equals("A03 bis"))
                        myMap.getOverlays().remove(customOverlays.get(k).overlayItem);
                myMap.invalidate();
                bus3bis_click--;
            }
        });

        bus11.setOnClickListener(v -> {
            int i = bus11_click;
            Log.d("MapOverlays", myMap.getOverlays().size() + "");
            if (i == 1) {
                cheminBus = searchBusRouteByNumber("A11");
                addBus(cheminBus, myMap, 0, 0, 0, "A11");
                bus11_click++;
            } else {
                for (int k = 0; k < customOverlays.size(); k++)
                    if (customOverlays.get(k).name.equals("A11"))
                        myMap.getOverlays().remove(customOverlays.get(k).overlayItem);
                myMap.invalidate();
                bus11_click--;
            }
        });


        bus16.setOnClickListener(v -> {
            int i = bus16_click;
            Log.d("MapOverlays", myMap.getOverlays().size() + "");
            if (i == 1) {
                cheminBus = searchBusRouteByNumber("A16");
                addBus(cheminBus, myMap, 0, 0, 255, "A16");
                bus16_click++;
            } else {
                for (int k = 0; k < customOverlays.size(); k++)
                    if (customOverlays.get(k).name.equals("A16"))
                        myMap.getOverlays().remove(customOverlays.get(k).overlayItem);
                myMap.invalidate();
                bus16_click--;
            }
        });

        bus17.setOnClickListener(v -> {
            int i = bus17_click;
            Log.d("MapOverlays", myMap.getOverlays().size() + "");
            if (i == 1) {
                cheminBus = searchBusRouteByNumber("A17");
                Log.d("A17", chemin.size() + "");
                addBus(cheminBus, myMap, 0, 255, 0, "A17");
                bus17_click++;
            } else {
                for (int k = 0; k < customOverlays.size(); k++)
                    if (customOverlays.get(k).name.equals("A17"))
                        myMap.getOverlays().remove(customOverlays.get(k).overlayItem);
                myMap.invalidate();
                bus17_click--;
            }
        });

        bus_22.setOnClickListener(v -> {
            int i = bus22_click;
            Log.d("MapOverlays", myMap.getOverlays().size() + "");
            if (i == 1) {
                cheminBus = searchBusRouteByNumber("A22");
                addBus(cheminBus, myMap, 105, 105, 105, "A22");
                bus22_click++;
            } else {
                for (int k = 0; k < customOverlays.size(); k++)
                    if (customOverlays.get(k).name.equals("A22"))
                        myMap.getOverlays().remove(customOverlays.get(k).overlayItem);
                myMap.invalidate();
                bus22_click--;
            }
        });

        bus25.setOnClickListener(v -> {
            int i = bus25_click;
            Log.d("MapOverlays", myMap.getOverlays().size() + "");
            if (i == 1) {
                cheminBus = searchBusRouteByNumber("A25");
                addBus(cheminBus, myMap, 255, 0, 255, "A25");
                bus25_click++;
            } else {
                for (int k = 0; k < customOverlays.size(); k++)
                    if (customOverlays.get(k).name.equals("A25"))
                        myMap.getOverlays().remove(customOverlays.get(k).overlayItem);
                myMap.invalidate();
                bus25_click--;
            }
        });

        bus27.setOnClickListener(v -> {
            int i = bus27_click;
            Log.d("MapOverlays", myMap.getOverlays().size() + "");
            if (i == 1) {
                cheminBus = searchBusRouteByNumber("A27");
                addBus(cheminBus, myMap, 0, 255, 255, "A27");
                bus27_click++;
            } else {
                for (int k = 0; k < customOverlays.size(); k++)
                    if (customOverlays.get(k).name.equals("A27"))
                        myMap.getOverlays().remove(customOverlays.get(k).overlayItem);
                myMap.invalidate();
                bus27_click--;
            }
        });

        reset.setOnClickListener(v -> {
            Log.d("MapOverlays", myMap.getOverlays().size() + "");
            clearMap();
        });

        walk.setOnClickListener(v -> {
            walk.setImageResource(R.drawable.walk_enabled);
            car.setImageResource(R.drawable.car);

            walk.setSelected(true);
            car.setSelected(false);
        });
        car.setOnClickListener(v -> {
            car.setImageResource(R.drawable.car_enabled);
            walk.setImageResource(R.drawable.walk);

            car.setSelected(true);
            walk.setSelected(false);
        });


    }


/*    private void bestChoiceOnTramway(GeoPoint currentLocation) {
        double timeBetweenStations = 150;
        Graph graph = new Graph();
        int nombreSommets = stationsSubway.size() + 1;
        int nombreAretes = stationsSubway.size() * 2 - 1;
        boolean oriente = false;
        graph = new Graph(oriente, nombreSommets, nombreAretes);
        ArrayList<StationDetails> stationDetails1 = database.getAllNearestSubStations();
        sort(stationDetails1);
        for (int i = 0; i < stationsSubway.size(); i++) {
            StationDetails station;
            station = stationDetails1.get(i);
            Log.d("dijikstra100", station.nomFr);
            graph.Ajouter(new Arete(Integer.valueOf(stationDetails1.get(i).numero + 0), Integer.valueOf(stationDetails1.get(i).numero) + 1, 0, timeBetweenStations, stationDetails1.get(i).nomFr));
            Log.d("dijikstra10", stationDetails1.get(15).nomFr);

        }
        for (int i = 0; i < stationsSubway.size(); i++) {
            graph.Ajouter(new Arete(stationsSubway.size(), i, stationDetails1.get(i).distanceTo, stationDetails1.get(i).timeTo, stationDetails1.get(i).nomFr));
        }
        Log.d("DijikstraEdited10", graph.toString());
        graph.Djiskra(22);

    }*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ArrayList<Station> ligne;
        SubMenu tramwaySubMenu = menu.addSubMenu(R.id.subway_stations, R.id.subway_stations, 1, "Stations de Tramway");
        SubMenu busSubMenu = menu.addSubMenu(R.id.bus_16, R.id.bus_16, 1, "Stations de bus");
        SubMenu ligne3 = busSubMenu.addSubMenu(R.id.bus_stations_3, R.id.bus_stations_3, 1, "Ligne 3");
        SubMenu ligne3bis = busSubMenu.addSubMenu(R.id.bus_stations_3_bis, R.id.bus_stations_3_bis, 1, "Ligne 3 bis");
        SubMenu ligne11 = busSubMenu.addSubMenu(R.id.bus_stations_11, R.id.bus_stations_11, 1, "Ligne 11");
        SubMenu ligne16 = busSubMenu.addSubMenu(R.id.bus_stations_16, R.id.bus_stations_16, 1, "Ligne 16");
        SubMenu ligne17 = busSubMenu.addSubMenu(R.id.bus_stations_17, R.id.bus_stations_17, 1, "Ligne 17");
        SubMenu ligne22 = busSubMenu.addSubMenu(R.id.bus_stations_22, R.id.bus_stations_22, 1, "Ligne 22");
        SubMenu ligne25 = busSubMenu.addSubMenu(R.id.bus_stations_25, R.id.bus_stations_25, 1, "Ligne 25");
        SubMenu ligne27 = busSubMenu.addSubMenu(R.id.bus_stations_27, R.id.bus_stations_27, 1, "Ligne 27");

        for (int i = 0; i < stationsSubway.size(); i++) {
            tramwaySubMenu.add(R.id.subway_stations, ids_tramway[i], 1, stationsSubway.get(i).nomFr);
        }
        ligne = searchBusStationByNumber("A03");
        for (int i = 0; i < ligne.size(); i++) {
            ligne3.add(R.id.bus_stations_3, ids_bus3[i], 1, ligne.get(i).nomFr);
        }
        ligne = searchBusStationByNumber("A03 bis");
        for (int i = 0; i < ligne.size(); i++) {
            ligne3bis.add(R.id.bus_stations_3_bis, ids_bus3bis[i], 1, ligne.get(i).nomFr);
        }
        ligne = searchBusStationByNumber("A11");
        for (int i = 0; i < ligne.size(); i++) {
            ligne11.add(R.id.bus_stations_11, ids_bus11[i], 1, ligne.get(i).nomFr);
        }
        ligne = searchBusStationByNumber("A16");
        for (int i = 0; i < ligne.size(); i++) {
            ligne16.add(R.id.bus_stations_16, ids_bus16[i], 1, ligne.get(i).nomFr);
        }
        ligne = searchBusStationByNumber("A17");
        for (int i = 0; i < ligne.size(); i++) {
            ligne17.add(R.id.bus_stations_17, ids_bus17[i], 1, ligne.get(i).nomFr);
        }
        ligne = searchBusStationByNumber("A22");
        for (int i = 0; i < ligne.size(); i++) {
            ligne22.add(R.id.bus_stations_22, ids_bus22[i], 1, ligne.get(i).nomFr);
        }
        ligne = searchBusStationByNumber("A25");
        for (int i = 0; i < ligne.size(); i++) {
            ligne25.add(R.id.bus_stations_25, ids_bus25[i], 1, ligne.get(i).nomFr);
        }
        ligne = searchBusStationByNumber("A27");
        for (int i = 0; i < ligne.size(); i++) {
            ligne27.add(R.id.bus_stations_27, ids_bus27[i], 1, ligne.get(i).nomFr);
        }

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.stations, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        getLocation();
        int i, verify, parcours;
        if (item.getGroupId() == R.id.subway_stations) {
            i = item.getItemId();
            verify = 1;
            parcours = 0;
            while (verify != 0 && parcours < stationsSubway.size()) {
                if (i == ids_tramway[parcours]) {
                    Log.d("MenuTramway11", String.valueOf(item.getTitle()));
//                    fetchRoute(currentLocation, stationsSubway.get(parcours).coordonnees, true);
                    addMarker(myMap, stationsSubway.get(parcours).coordonnees, stationsSubway.get(parcours).nomFr, "tramway");
                    verify = 0;
                } else {
                    Log.d("MenuTramway12", String.valueOf(parcours));
                    parcours++;

                }
            }
        } else if (item.getGroupId() == R.id.bus_stations_3) {
            verify = 1;
            parcours = 0;
            while (verify != 0 && parcours < stationsBus3.size()) {
                if (item.getItemId() == ids_bus3[parcours]) {
                    addMarker(myMap, stationsBus3.get(parcours).coordonnees, stationsBus3.get(parcours).nomFr, "bus");
                    verify = 0;
                } else {
                    parcours++;
                }
            }
        } else if (item.getGroupId() == R.id.bus_stations_3_bis) {
            verify = 1;
            parcours = 0;
            while (verify != 0 && parcours < stationsBus3bis.size()) {
                if (item.getItemId() == ids_bus3bis[parcours]) {
                    addMarker(myMap, stationsBus3bis.get(parcours).coordonnees, stationsBus3bis.get(parcours).nomFr, "bus");
                    verify = 0;
                } else {
                    parcours++;
                }
            }
        } else if (item.getGroupId() == R.id.bus_stations_11) {
            verify = 1;
            parcours = 0;
            while (verify != 0 && parcours < stationsBus11.size()) {
                if (item.getItemId() == ids_bus11[parcours]) {
                    addMarker(myMap, stationsBus11.get(parcours).coordonnees, stationsBus11.get(parcours).nomFr, "bus");
                    verify = 0;
                } else {
                    parcours++;
                }
            }
        } else if (item.getGroupId() == R.id.bus_stations_16) {
            verify = 1;
            parcours = 0;
            while (verify != 0 && parcours < stationsBus16.size()) {
                if (item.getItemId() == ids_bus16[parcours]) {
                    addMarker(myMap, stationsBus16.get(parcours).coordonnees, stationsBus16.get(parcours).nomFr, "bus");
                    verify = 0;
                } else {
                    parcours++;
                }
            }
        } else if (item.getGroupId() == R.id.bus_stations_17) {
            verify = 1;
            parcours = 0;
            while (verify != 0 && parcours < stationsBus17.size()) {
                if (item.getItemId() == ids_bus17[parcours]) {
                    addMarker(myMap, stationsBus17.get(parcours).coordonnees, stationsBus17.get(parcours).nomFr, "bus");
                    verify = 0;
                } else {
                    parcours++;
                }
            }
        } else if (item.getGroupId() == R.id.bus_stations_22) {
            verify = 1;
            parcours = 0;
            while (verify != 0 && parcours < stationsBus22.size()) {
                if (item.getItemId() == ids_bus22[parcours]) {
                    addMarker(myMap, stationsBus22.get(parcours).coordonnees, stationsBus22.get(parcours).nomFr, "bus");
                    verify = 0;
                } else {
                    parcours++;
                }
            }
        } else if (item.getGroupId() == R.id.bus_stations_25) {
            verify = 1;
            parcours = 0;
            while (verify != 0 && parcours < stationsBus25.size()) {
                if (item.getItemId() == ids_bus25[parcours]) {
                    addMarker(myMap, stationsBus25.get(parcours).coordonnees, stationsBus25.get(parcours).nomFr, "bus");
                    verify = 0;
                } else {
                    parcours++;
                }
            }
        } else if (item.getGroupId() == R.id.bus_stations_27) {
            verify = 1;
            parcours = 0;
            while (verify != 0 && parcours < stationsBus27.size()) {
                if (item.getItemId() == ids_bus27[parcours]) {
                    addMarker(myMap, stationsBus27.get(parcours).coordonnees, stationsBus27.get(parcours).nomFr, "bus");
                    verify = 0;
                } else {
                    parcours++;
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //Bus
    void addBusStationByNumber(String name) {
        ArrayList<Station> busStations = searchBusStationByNumber(name);
        for (int i = 0; i < busStations.size(); i++)
            addStationBus(myMap, busStations.get(i).coordonnees, busStations.get(i).nomFr, busStations.get(i).numero);
    }

    ArrayList<Station> searchBusStationByNumber(String name) {
        ArrayList<Station> result = new ArrayList<>();
        for (int i = 0; i < stationsBus.size(); i++) {
            if (stationsBus.get(i).numero.equals(name)) {
                result.add(stationsBus.get(i));
            } else {
            }
        }
        return result;
    }

    ArrayList<RouteBus> searchBusRouteByNumber(String name) {
        ArrayList<RouteBus> result = new ArrayList<>();
        for (int i = 0; i < routeBus.size(); i++) {
            if (routeBus.get(i).numLigne.equals(name)) {
                result.add(routeBus.get(i));
            } else {
            }
        }
        return result;
    }

    void addBus(ArrayList<RouteBus> chemin, MapView mapView, int red, int green, int blue, String numero) {
        tracerCheminBus(chemin, mapView, red, green, blue, numero);
        addBusStationByNumber(numero);

    }

    void addStationBus(MapView mapMarker, GeoPoint positionMarker, String nomFr, String numLigne) {
        Marker marker = new Marker(mapMarker);
        marker.setPosition(positionMarker);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setAlpha(0.8f);
        marker.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.pin_bus));
        marker.setTitle(nomFr + " " + numLigne);
        marker.setPanToView(true);
        myMap.invalidate();
        myMap.getOverlays().add(marker);
        customOverlays.add(customOverlays.size(), new CustomOverlay(numLigne, marker));


        marker.setOnMarkerClickListener((marker1, mapView) -> {
            getLocation();
            marker1.setSnippet(fetchRoute(currentLocation, marker1.getPosition(), true));

            marker1.setInfoWindow(new InfoWindow(R.layout.custom_bubble, myMap) {
                @Override
                public void onOpen(Object item) {
                    InfoWindow.closeAllInfoWindowsOn(myMap);
                    TextView station = mView.findViewById(R.id.nomStation);
//                    station.setText(marker1.getTitle() + "\n" + marker1.getSnippet());
                    station.setText(marker1.getTitle() + "\n" + marker1.getSnippet());
                }

                @Override
                public void onClose() {
                    InfoWindow.closeAllInfoWindowsOn(myMap);
                }

            });
            marker1.showInfoWindow();
            mapView.getController().setCenter(marker1.getPosition());
            mapView.getController().setZoom(17.0);
            return true;
        });
    }

    private void tracerCheminBus(ArrayList<RouteBus> chemin, MapView mapView, int red, int green, int blue, String numero) {
        Polyline line = new Polyline();
        line.setWidth(10);
        line.setColor(Color.rgb(red, green, blue));
        line.setDensityMultiplier(0.5f);
        ArrayList<GeoPoint> route = new ArrayList<>();
        for (int i = 0; i < chemin.size(); i++)
            route.add(chemin.get(i).coordinates);
        line.setPoints(route);
        mapView.getOverlayManager().add(line);
        customOverlays.add(customOverlays.size(), new CustomOverlay(numero, line));
        mapView.invalidate();
    }

    public void getLocation() {
        if (mLocationOverlay.getMyLocation() != null) {
            currentLocation = mLocationOverlay.getMyLocation();
        } else {
            currentLocation = defaultLocation;
            Toast.makeText(getApplicationContext(),
                    "Using default location, consider enabling the GPS and restarting the app", Toast.LENGTH_SHORT).show();
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
                station.nomFr = jsonobject.getString("nomFr");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                station.type = jsonobject.getString("type");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                station.numero = jsonobject.getString("numero");
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
            station.coordonnees = point;
            Log.d("Nouvelle station", station.toString());
            database.addStation(station);
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

    private void fetchRouteCorrespondance(Response response) throws IOException {
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
                database.addPointCorrespondance(po);
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
            JSONObject jsonobject;
            GeoPoint po = new GeoPoint(0.0, 0.0);
            RouteBus pointBus = new RouteBus(new GeoPoint(0.0, 0.0), "Ligne de bus");
            try {
                jsonobject = jsonarray.getJSONObject(i);
                po.setLatitude(jsonobject.getDouble("latitude"));
                po.setLongitude(jsonobject.getDouble("longitude"));
                pointBus.numLigne = (jsonobject.getString("numero"));
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
            addStationSubway(myMap, stationsSubway.get(i).coordonnees, stationsSubway.get(i).nomFr);
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
        customOverlays.add(customOverlays.size(), new CustomOverlay("tramway", line));
    }

    private void tracerCorrespondance(ArrayList<GeoPoint> chemin, MapView mapView) {
        Polyline line = new Polyline();
        line.setWidth(12);
//        line.getOutlinePaint();
        line.setColor(Color.rgb(0, 0, 0));
        line.setDensityMultiplier(0.9f);
        line.setPoints(chemin);
        line.setGeodesic(true);
        mapView.getOverlayManager().add(line);
        customOverlays.add(customOverlays.size(), new CustomOverlay("correspondance", line));
    }

    public void addMarker(MapView mapMarker, GeoPoint positionMarker, String nom, String type) {
        Marker marker = new Marker(mapMarker);
        marker.setPosition(positionMarker);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setAlpha(0.8f);
        if (type.equals("tramway"))
            marker.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.pin_tramway));
        else
            marker.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.pin_bus));
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
        marker.showInfoWindow();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void addStationSubway(MapView mapMarker, GeoPoint positionMarker, String nomFrMarker) {
        Marker marker = new Marker(mapMarker);
        marker.setPosition(positionMarker);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setAlpha(0.8f);
        marker.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.pin_tramway));
        marker.setTitle(nomFrMarker);
        marker.setPanToView(true);
        mapMarker.invalidate();
        mapMarker.getOverlays().add(marker);
        customOverlays.add(customOverlays.size(), new CustomOverlay("tramway", marker));

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
            marker1.showInfoWindow();
            mapView.getController().setCenter(marker1.getPosition());
            mapView.getController().setZoom(16.0);
            return true;
        });
    }

    String fetchRoute(GeoPoint start, GeoPoint end, boolean draw) {
        ArrayList<GeoPoint> roadPoints = new ArrayList<>();
        getLocation();
        roadPoints.add((start));
        roadPoints.add(end);
        OSRMRoadManager roadManager = new OSRMRoadManager(getApplicationContext(), "22-Transport");
        if (car.isSelected())
            roadManager.setMean(OSRMRoadManager.MEAN_BY_CAR);
        else roadManager.setMean(OSRMRoadManager.MEAN_BY_FOOT);
//        RoadManager roadManager = new MapQuestRoadManager("jmQfNVRjCrl8jiDLW1QNO5hTkWuyv5mm");
//        roadManager.addRequestOption("routeType=pedestrian");
//        Road road = roadManager.getRoad(roadPoints);
//        RoadManager roadManager = new GraphHopperRoadManager("484e2932-b8a9-4bfa-a760-d3f32f84e347", false);
//        roadManager.addRequestOption("vehicle=foot");
//        Road road = roadManager.getRoad(roadPoints);
        Road road = roadManager.getRoad(roadPoints);
        Polyline route;
        if (road.mLength == 0) {
            distanceTo = getDistanceOffline(start, end);
            timeTo = 99999.0;
            return "Route indisponible";
        }
        if (draw == true) {
            if (car.isSelected())
                route = RoadManager.buildRoadOverlay(road, getApplicationContext().getResources().getColor(R.color.taxi), 15.0f);
            else
                route = RoadManager.buildRoadOverlay(road, getApplicationContext().getResources().getColor(R.color.teal_700), 15.0f);

            myMap.getOverlays().add(route);
        }
        String duration = format(road.mDuration / 60);
        String dist = format(road.mLength);
        String distanceTo = "km " + dist + " كم";
        String timeTo = "minutes " + duration + " دقيقة";
        return distanceTo + "\n" + timeTo;

    }

    private double getDistanceOffline(GeoPoint currentLocation, GeoPoint targetedLocation) {
        float[] distance = new float[2];
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(),
                targetedLocation.getLatitude(), targetedLocation.getLongitude(), distance);
        return distance[0] / 1000;
    }


    //Menu Navigation
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
        myMap.invalidate();
        bus3bis_click = bus3_click = bus11_click = bus16_click = bus17_click = bus22_click = bus25_click = bus27_click = tramway_click = 1;
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