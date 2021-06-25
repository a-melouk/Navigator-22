package com.esi.navigator_22;

import android.Manifest;
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
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.esi.navigator_22.dijkstra.Edge;
import com.esi.navigator_22.dijkstra.Graph;
import com.esi.navigator_22.dijkstra.Vertex;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.bonuspack.routing.GraphHopperRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
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

import static org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM;
import static org.osmdroid.views.overlay.Marker.ANCHOR_CENTER;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    String adresse = "http://192.168.1.8:3000/";
    //    String adresse = "https://routing22.herokuapp.com/";
    String urlStations = adresse + "stations_sba";
    String urlRouteTramway = adresse + "subway";
    String urlRouteBus = adresse + "bus";
    String urlCorrespondance = adresse + "correspondance";
    String urlMatrice = adresse + "matrice";
    static String graphhopperkey = "b0bf12d1-16d3-4b69-a5dd-7b58508cc8e6";

    private String myResponse;

    static MapView myMap;
    ScaleBarOverlay echelle;
    MyLocationNewOverlay mLocationOverlay;
    RotationGestureOverlay mRotationGestureOverlay;
    MapEventsOverlay mapEventsOverlay;
    Marker draggableMarker, markerSource, markerDestination;

    ImageView currentPosition, reset;
    RelativeLayout menu_linear, navigationSearchViews;
    ImageView tramway, bus3, bus11, bus16, bus17, bus25, bus27, arrow_down, arrow_up, bus_22;
    ImageView walk, car, bus, tram;
    ImageView mean_walk, mean_car, mean_bus, mean_tram, the_best_time, the_best_distance, ok_marker;
    ImageButton close;
    Button start;
    ListView searchStations, navigationSource, navigationDestination;
    CardView choices_card;
    ScrollView scroll_steps;
    LinearLayout linear_steps;
    RelativeLayout choices_layout;
    ImageView close_steps;
    NavigationView navigationView;

    Station station = new Station();
    MatriceLine ligne = new MatriceLine(new Station("type", "nom", "numero",
            new GeoPoint(0.0, 0.0)), new Station("type", "nom", "numero",
            new GeoPoint(0.0, 0.0)), 0.0, 0.0);
    GeoPoint defaultLocation = new GeoPoint(35.19115853846664, -0.6298066051152207);
    static GeoPoint currentLocation = new GeoPoint(0.0, 0.0);
    GeoPoint srcCoord = new GeoPoint(0.0, 0.0);
    GeoPoint dstCoord = new GeoPoint(0.0, 0.0);
    String srcNumber = "", dstNumber = "";
    GeoPoint point = new GeoPoint(0.0, 0.0);

    DbHelper database = DbHelper.getInstance(this);
    static ArrayList<RouteBus> routeBus = new ArrayList<>();
    static ArrayList<GeoPoint> routeTramway = new ArrayList<>();
    static ArrayList<GeoPoint> routeCorrespondance = new ArrayList<>();
    static ArrayList<Station> allStations = new ArrayList<>();
    static ArrayList<Station> stationsTramway = new ArrayList<>();
    static ArrayList<Station> stationsBus = new ArrayList<>();
    static ArrayList<Station> stationsBus3 = new ArrayList<>();
    static ArrayList<Station> stationsBus3bis = new ArrayList<>();
    static ArrayList<Station> stationsBus11 = new ArrayList<>();
    static ArrayList<Station> stationsBus16 = new ArrayList<>();
    static ArrayList<Station> stationsBus17 = new ArrayList<>();
    static ArrayList<Station> stationsBus22 = new ArrayList<>();
    static ArrayList<Station> stationsBus25 = new ArrayList<>();
    static ArrayList<Station> stationsBus27 = new ArrayList<>();
    static ArrayList<MatriceLine> matrice = new ArrayList<>();
    static ArrayList<TramwayMatrixLine> tramwayMatrice = new ArrayList<>();
    ArrayList<GeoPoint> chemin = new ArrayList<>();
    ArrayList<RouteBus> cheminBus = new ArrayList<>();
    ArrayList<GeoPoint> bestRoute = new ArrayList<>();
    static ArrayList<CustomOverlay> customOverlays = new ArrayList<>();
    double minZ = 13.0;
    double maxZ = 19.0;
    double distanceTo, timeTo;
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
    int bus3_click = 1, bus11_click = 1, bus16_click = 1, bus17_click = 1, bus22_click = 1, bus25_click = 1,
            bus27_click = 1, tramway_click = 1;

    ArrayAdapter<String> arrayAdapter;
    OkHttpClient client = new OkHttpClient();
    Graph g = new Graph();
    Vertex current;
    Vertex source;
    Vertex destination;
    ArrayList<Vertex> path;
    private double cost;

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
        bus11 = findViewById(R.id.bus_11);
        bus16 = findViewById(R.id.bus_16);
        bus17 = findViewById(R.id.bus_17);
        bus_22 = findViewById(R.id.bus_22);
        bus25 = findViewById(R.id.bus_25);
        bus27 = findViewById(R.id.bus_27);
        tramway = findViewById(R.id.tramway);
        car = findViewById(R.id.car);
        walk = findViewById(R.id.walk);
//        bus = findViewById(R.id.bus);
//        tram = findViewById(R.id.tram);

        mean_walk = findViewById(R.id.mean_walk);
        mean_tram = findViewById(R.id.mean_tramway);
        mean_bus = findViewById(R.id.mean_bus);
        mean_car = findViewById(R.id.mean_car);
        the_best_time = findViewById(R.id.the_best_time);
        the_best_distance = findViewById(R.id.the_best_distance);
        ok_marker = findViewById(R.id.ok);
        start = findViewById(R.id.start);
        close = findViewById(R.id.close);
        menu_linear = findViewById(R.id.menu_linear);
        navigationSearchViews = findViewById(R.id.searchViews);
//        arrow_down = findViewById(R.id.arrow_down);
//        arrow_up = findViewById(R.id.arrow_up);
        drawerLayout = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        scroll_steps = findViewById(R.id.scroll_steps);
        linear_steps = findViewById(R.id.linear_steps);
        close_steps = findViewById(R.id.close_steps);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        Animation anim = new AlphaAnimation(0.8f, 1.0f);
        anim.setDuration(1000);
        anim.setStartOffset(0);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        currentPosition.startAnimation(anim);

        setNavigationViewListener();

        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_station));
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_draw_open, R.string.navigation_draw_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setToolbarNavigationClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
        toggle.setHomeAsUpIndicator(R.drawable.icon_hamburger);

        myMap.getController().setCenter(new GeoPoint(35.2023025901554, -0.6302970012564838));
        myMap.setMinZoomLevel(minZ);
        myMap.setMaxZoomLevel(maxZ);
        myMap.getController().setZoom(15.0);
        myMap.setTileSource(TileSourceFactory.HIKEBIKEMAP);
//        setMapOfflineSource();
        Runnable downloadMapToCache = () -> runOnUiThread(() -> {
            CacheManager cacheManager = new CacheManager(myMap);
            BoundingBox bbox = new BoundingBox(35.23286, -0.540047, 35.128473, -0.708618);
            cacheManager.downloadAreaAsyncNoUI(this, bbox, 12, 17, new CacheManager.CacheManagerCallback() {

                @Override
                public void onTaskComplete() {
                }

                @Override
                public void updateProgress(int progress, int currentZoomLevel, int zoomMin, int zoomMax) {
                }

                @Override
                public void downloadStarted() {
                }

                @Override
                public void setPossibleTilesInArea(int total) {
                }

                @Override
                public void onTaskFailed(int errors) {
                }
            });
        });
        Executors.newFixedThreadPool(1).execute(downloadMapToCache);

        if ((ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED)) {
        } else {
            Toast.makeText(this, "GPS requis", Toast.LENGTH_LONG).show();
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1
            );
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

//        getRouteTramway();
//        getRouteBus();
//        getRouteCorrespondance();
//        getStations();
//        getMatrice();
//        getBestRoute();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        allStations = database.getAllStations();
        stationsTramway = database.getAllTramwayStations();
        stationsBus = database.getAllBusStations();
        routeBus = database.getAllPointsBus();
        routeTramway = database.getAllPointsSub();
        routeCorrespondance = database.getAllPointsCorrespondance();
        matrice = database.getAllLines();
        tramwayMatrice = database.getAllTramwayLines();
        stationsBus3 = searchBusStationByNumber("A03_");
        stationsBus3bis = searchBusStationByNumber("A03bis_");
        stationsBus11 = searchBusStationByNumber("A11_");
        stationsBus16 = searchBusStationByNumber("A16_");
        stationsBus17 = searchBusStationByNumber("A17_");
        stationsBus22 = searchBusStationByNumber("A22_");
        stationsBus25 = searchBusStationByNumber("A25_");
        stationsBus27 = searchBusStationByNumber("A27_");
        Animation animationToVisible = AnimationUtils.loadAnimation(this, R.anim.to_visible);
        Animation animationToInvisible = AnimationUtils.loadAnimation(this, R.anim.to_invisible);

/*        arrow_down.setOnClickListener(v -> {

            menu_linear.startAnimation(animationToVisible);
            menu_linear.setVisibility(View.VISIBLE);
            arrow_down.setVisibility(View.INVISIBLE);
            arrow_up.setVisibility(View.VISIBLE);
        });
        arrow_up.setOnClickListener(v -> {
            menu_linear.startAnimation(animationToInvisible);
            menu_linear.setVisibility(View.INVISIBLE);
            arrow_up.setVisibility(View.INVISIBLE);
            arrow_down.setVisibility(View.VISIBLE);
        });*/
        currentPosition.setOnClickListener(v -> {
            getLocation();

            myMap.getController().setZoom(16.0);
            myMap.getController().setCenter(currentLocation);
        });

        tramway.setOnClickListener(v -> {
            int i = tramway_click;
            if (i == 1) {
                chemin = routeTramway;
                tracerCheminTramway(chemin, myMap);
                chemin = routeCorrespondance;
                tracerCorrespondance(chemin, myMap);
                addStationsTramway();
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
            if (i == 1) {
                cheminBus = searchBusRouteByNumber("A03");
                addBus(cheminBus, myMap, 255, 0, 0, "A03_");
                cheminBus = searchBusRouteByNumber("A03 bis");
                addBus(cheminBus, myMap, 255, 0, 0, "A03bis_");
                bus3_click++;
            } else {
                for (int k = 0; k < customOverlays.size(); k++) {
                    if (customOverlays.get(k).name.contains("A03"))
                        myMap.getOverlays().remove(customOverlays.get(k).overlayItem);
                    if (customOverlays.get(k).name.contains("A03 bis") || customOverlays.get(k).name.contains("A03bis_"))
                        myMap.getOverlays().remove(customOverlays.get(k).overlayItem);
                }
                myMap.invalidate();
                bus3_click--;
            }
        });
        bus11.setOnClickListener(v -> {
            int i = bus11_click;
            if (i == 1) {
                cheminBus = searchBusRouteByNumber("A11");
                addBus(cheminBus, myMap, 0, 0, 0, "A11_");
                bus11_click++;
            } else {
                for (int k = 0; k < customOverlays.size(); k++)
                    if (customOverlays.get(k).name.contains("A11"))
                        myMap.getOverlays().remove(customOverlays.get(k).overlayItem);
                myMap.invalidate();
                bus11_click--;
            }
        });
        bus16.setOnClickListener(v -> {
            int i = bus16_click;
            if (i == 1) {
                cheminBus = searchBusRouteByNumber("A16");
                addBus(cheminBus, myMap, 0, 0, 255, "A16");
                bus16_click++;
            } else {
                for (int k = 0; k < customOverlays.size(); k++)
                    if (customOverlays.get(k).name.contains("A16"))
                        myMap.getOverlays().remove(customOverlays.get(k).overlayItem);
                myMap.invalidate();
                bus16_click--;
            }
        });
        bus17.setOnClickListener(v -> {
            int i = bus17_click;
            if (i == 1) {
                cheminBus = searchBusRouteByNumber("A17");
                addBus(cheminBus, myMap, 0, 255, 0, "A17_");
                bus17_click++;
            } else {
                for (int k = 0; k < customOverlays.size(); k++)
                    if (customOverlays.get(k).name.contains("A17"))
                        myMap.getOverlays().remove(customOverlays.get(k).overlayItem);
                myMap.invalidate();
                bus17_click--;
            }
        });
        bus_22.setOnClickListener(v -> {
            int i = bus22_click;
            if (i == 1) {
                cheminBus = searchBusRouteByNumber("A22");
                addBus(cheminBus, myMap, 105, 105, 105, "A22_");
                bus22_click++;
            } else {
                for (int k = 0; k < customOverlays.size(); k++)
                    if (customOverlays.get(k).name.contains("A22"))
                        myMap.getOverlays().remove(customOverlays.get(k).overlayItem);
                myMap.invalidate();
                bus22_click--;
            }
        });
        bus25.setOnClickListener(v -> {
            int i = bus25_click;
            if (i == 1) {
                cheminBus = searchBusRouteByNumber("A25");
                addBus(cheminBus, myMap, 255, 0, 255, "A25_");
                bus25_click++;
            } else {
                for (int k = 0; k < customOverlays.size(); k++)
                    if (customOverlays.get(k).name.contains("A25"))
                        myMap.getOverlays().remove(customOverlays.get(k).overlayItem);
                myMap.invalidate();
                bus25_click--;
            }
        });
        bus27.setOnClickListener(v -> {
            int i = bus27_click;
            if (i == 1) {
                cheminBus = searchBusRouteByNumber("A27");
                addBus(cheminBus, myMap, 0, 255, 255, "A27_");
                bus27_click++;
            } else {
                for (int k = 0; k < customOverlays.size(); k++)
                    if (customOverlays.get(k).name.contains("A27"))
                        myMap.getOverlays().remove(customOverlays.get(k).overlayItem);
                myMap.invalidate();
                bus27_click--;
            }
        });
        reset.setOnClickListener(v -> clearMap());
        walk.setOnClickListener(v -> {
            walk.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.gradient_bottom));
            walk.setSelected(true);
            car.setSelected(false);
            car.setBackground(null);
        });
        car.setOnClickListener(v -> {
            car.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.gradient_bottom));
            car.setSelected(true);
            walk.setSelected(false);
            walk.setBackground(null);
        });

        mean_walk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mean_walk.setBackgroundResource(R.drawable.gradient_mean);
                mean_walk.setSelected(true);
                mean_tram.setBackground(null);
                mean_bus.setBackground(null);
                mean_car.setBackground(null);
                mean_tram.setSelected(false);
                mean_bus.setSelected(false);
                mean_car.setSelected(false);
                the_best_time.setSelected(false);
                the_best_time.setBackground(null);
                the_best_distance.setBackground(null);
                the_best_distance.setSelected(false);
            }
        });
        mean_tram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mean_tram.setBackgroundResource(R.drawable.gradient_mean);
                mean_tram.setSelected(true);
                mean_walk.setBackground(null);
                mean_bus.setBackground(null);
                mean_car.setBackground(null);
                mean_walk.setSelected(false);
                mean_bus.setSelected(false);
                mean_car.setSelected(false);
                the_best_time.setSelected(false);
                the_best_time.setBackground(null);
                the_best_distance.setBackground(null);
                the_best_distance.setSelected(false);
            }
        });
        mean_bus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mean_bus.setBackgroundResource(R.drawable.gradient_mean);
                mean_bus.setSelected(true);
                mean_tram.setBackground(null);
                mean_walk.setBackground(null);
                mean_car.setBackground(null);
                mean_walk.setSelected(false);
                mean_tram.setSelected(false);
                mean_car.setSelected(false);
                the_best_time.setSelected(false);
                the_best_time.setBackground(null);
                the_best_distance.setBackground(null);
                the_best_distance.setSelected(false);
            }
        });
        mean_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mean_car.setBackgroundResource(R.drawable.gradient_mean);
                mean_car.setSelected(true);
                mean_tram.setBackground(null);
                mean_bus.setBackground(null);
                mean_walk.setBackground(null);
                mean_walk.setSelected(false);
                mean_bus.setSelected(false);
                mean_tram.setSelected(false);
                the_best_time.setSelected(false);
                the_best_time.setBackground(null);
                the_best_distance.setBackground(null);
                the_best_distance.setSelected(false);
            }
        });
        the_best_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                the_best_time.setBackgroundResource(R.drawable.gradient_mean);
                the_best_time.setSelected(true);
                mean_tram.setBackground(null);
                mean_bus.setBackground(null);
                mean_walk.setBackground(null);
                mean_walk.setSelected(false);
                mean_bus.setSelected(false);
                mean_tram.setSelected(false);
                mean_car.setSelected(false);
                mean_car.setBackground(null);
                the_best_distance.setBackground(null);
                the_best_distance.setSelected(false);
            }
        });
        the_best_distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                the_best_distance.setBackgroundResource(R.drawable.gradient_mean);
                the_best_distance.setSelected(true);
                mean_tram.setBackground(null);
                mean_bus.setBackground(null);
                mean_walk.setBackground(null);
                mean_walk.setSelected(false);
                mean_bus.setSelected(false);
                mean_tram.setSelected(false);
                mean_car.setSelected(false);
                mean_car.setBackground(null);
                the_best_time.setSelected(false);
                the_best_time.setBackground(null);
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
                addSource(srcCoord, "Source");
                addDestination(dstCoord, "Destination");
                if (mean_walk.isSelected())
                    fetchRouteByMean(srcCoord, dstCoord, "walk", true);
                else if (mean_tram.isSelected())
                    navigation(srcCoord, dstCoord, "tramway", "time");
                else if (mean_bus.isSelected())
//                    navigation(srcCoord, dstCoord, removeFromStart(srcNumber),"distance");
                    navigation(srcCoord, dstCoord, "buses", "distance");
                else if (mean_car.isSelected())
                    fetchRouteByMean(srcCoord, dstCoord, "car", true);
                else if (the_best_time.isSelected())
                    navigation(srcCoord, dstCoord, "All", "time");
                else if (the_best_distance.isSelected())
                    navigation(srcCoord, dstCoord, "All", "distance");
            }
        });

        close_steps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scroll_steps.setVisibility(View.INVISIBLE);
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationSearchViews.setVisibility(View.INVISIBLE);
                navigationDestination.setVisibility(View.INVISIBLE);
                navigationSource.setVisibility(View.INVISIBLE);
            }
        });

        setUpList();
        initSearch();
        stationSource();
        stationDestination();
    }

    public void navigation(GeoPoint src, GeoPoint dst, String mean, String criteria) {
        path = new ArrayList<>();
        ArrayList<Station> result = new ArrayList<>();
        if (src.equals(dst))
            Toast.makeText(getApplicationContext(), "0", Toast.LENGTH_SHORT).show();
        else {
            if (criteria.equals("time")) {
                if (mean.toLowerCase().equals("tramway"))
                    result = tramwayNavigation(src, dst, "time");
                else if (mean.equals("buses")) result = busesNavigation(src, dst, "time");
                /*else if (mean.equals("All")) {

                    g.edges.clear();
                    g.getVertices().clear();

                    for (int i = 0; i < allStations.size(); i++)
                        g.addVertex(allStations.get(i).numero);

                    int walk = 0;
                    for (int compteur = 0; compteur < allStations.size(); compteur++)
                        for (int compteur2 = compteur; compteur2 < allStations.size(); compteur2++) {
                            g.addEdge(g.getVertex(compteur), g.getVertex(compteur2), (int) matrice.get(walk).time);
                            walk++;
                            if (walk == 6441) walk = 0;
                        }

                    ArrayList<Edge> temp = new ArrayList<>();
                    //                             gh  adn bhmd env drt  nima cmps fer  sog  adl  dji  wim  dai  hb  rad  mtr adda amr  4   jrdn sud
                    int[] tramwayTimes = new int[]{105, 94, 98, 224, 100, 100, 95, 200, 120, 110, 150, 145, 120, 122, 85, 103, 78, 87, 110, 130, 130};

                    for (int i = 0; i < g.edges.size(); i++)
                        if (isNumeric(g.edges.get(i).source.name) && isNumeric(g.edges.get(i).target.name)) {
                            int sourceName = Integer.parseInt(g.edges.get(i).source.name);
                            int targetName = Integer.parseInt(g.edges.get(i).target.name);
                            int diff = sourceName - targetName;
                            for (int j = 0; j < stationsTramway.size() - 1; j++)
                                if (sourceName == Integer.parseInt(stationsTramway.get(j).numero) && targetName == Integer.parseInt(stationsTramway.get(j + 1).numero) && Math.abs(diff) == 1)
                                    temp.add(new Edge(g.getVertices().get(j), g.getVertices().get(j + 1), tramwayTimes[j]));
                        }

                    for (int i = 0; i < temp.size(); i++)
                        g.addEdge(temp.get(i).source, temp.get(i).target, (int) temp.get(i).weight);

                    addBusNavigation(stationsBus3, 120);
                    addBusNavigation(stationsBus3bis, 100);
                    addBusNavigation(stationsBus11, 129);
                    addBusNavigation(stationsBus16, 113);
                    addBusNavigation(stationsBus17, 125);
                    addBusNavigation(stationsBus22, 188);
                    addBusNavigation(stationsBus25, 180);
                    addBusNavigation(stationsBus27, 270);

                    path = g.affichage(g, g.getVertex(srcNumber), g.getVertex(dstNumber));
                    cost = Math.round(g.cost(g, g.getVertex(srcNumber), g.getVertex(dstNumber)) / 60);
                    for (int k = 1; k < path.size(); k++)
                        for (int i = 0; i < allStations.size(); i++)
                            if (path.get(k).name.equals(allStations.get(i).numero))
                                result.add(allStations.get(i));
                }*/
                else if (mean.equals("All")) result = allNavigation(src,dst,"time");

            }
            //
            else if (criteria.equals("distance")) {
                if (mean.toLowerCase().equals("tramway"))
                    result = tramwayNavigation(src, dst, "distance");
                else if (mean.equals("buses")) result = busesNavigation(src, dst, "distance");
                /*else if (mean.equals("All")) {

                    g.edges.clear();
                    g.getVertices().clear();

                    for (int i = 0; i < allStations.size(); i++)
                        g.addVertex(allStations.get(i).numero);

                    int walk = 0;
                    for (int compteur = 0; compteur < allStations.size(); compteur++)
                        for (int compteur2 = compteur; compteur2 < allStations.size(); compteur2++) {
                            g.addEdge(g.getVertex(compteur), g.getVertex(compteur2), (int) matrice.get(walk).distance);
                            walk++;
                            if (walk == 6441) walk = 0;
                        }

                    ArrayList<Edge> temp = new ArrayList<>();

                    for (int i = 0; i < g.edges.size(); i++)
                        if (isNumeric(g.edges.get(i).source.name) && isNumeric(g.edges.get(i).target.name)) {
                            int sourceName = Integer.parseInt(g.edges.get(i).source.name);
                            int targetName = Integer.parseInt(g.edges.get(i).target.name);
                            int diff = sourceName - targetName;
                            for (int j = 0; j < stationsTramway.size() - 1; j++)
                                if (sourceName == Integer.parseInt(stationsTramway.get(j).numero) && targetName == Integer.parseInt(stationsTramway.get(j + 1).numero) && Math.abs(diff) == 1)
                                    temp.add(new Edge(g.getVertices().get(j), g.getVertices().get(j + 1), 0));
                        }

                    for (int i = 0; i < temp.size(); i++)
                        g.addEdge(temp.get(i).source, temp.get(i).target, (int) temp.get(i).weight);

                    addBusNavigation(stationsBus3, 0);
                    addBusNavigation(stationsBus3bis, 0);
                    addBusNavigation(stationsBus11, 0);
                    addBusNavigation(stationsBus16, 0);
                    addBusNavigation(stationsBus17, 0);
                    addBusNavigation(stationsBus22, 0);
                    addBusNavigation(stationsBus25, 0);
                    addBusNavigation(stationsBus27, 0);

                    path = g.affichage(g, g.getVertex(srcNumber), g.getVertex(dstNumber));
                    cost = Math.round(g.cost(g, g.getVertex(srcNumber), g.getVertex(dstNumber)));
                    for (int k = 1; k < path.size(); k++)
                        for (int i = 0; i < allStations.size(); i++)
                            if (path.get(k).name.equals(allStations.get(i).numero))
                                result.add(allStations.get(i));
                }*/
                else if (mean.equals("All")) result = allNavigation(src,dst,"distance");
            }
            for (int i=0;i<g.edges.size();i++) {
                Log.d("Path",g.edges.get(i)+"");
            }

            if (result.size() > 0) {

                if (mean.equals("tramway")) {
                    Log.d("Navigation_Tramway_URL", adresse + "result/tram/" + result.get(0).coordonnees.getLatitude() + "/" + result.get(0).coordonnees.getLongitude() + "/" +
                            result.get(result.size() - 1).coordonnees.getLatitude() + "/" + result.get(result.size() - 1).coordonnees.getLongitude());
                    getBestRoute(
                            adresse + "result/tram/" + result.get(0).coordonnees.getLatitude() + "/" + result.get(0).coordonnees.getLongitude() + "/" +
                                    result.get(result.size() - 1).coordonnees.getLatitude() + "/" + result.get(result.size() - 1).coordonnees.getLongitude(), 253, 127, 44);
                }//
                else if (result.get(0).type.equals("bus"))
                    if (mean.equals(removeFromStart(result.get(0).numero))) {
                        getBestRoute(
                                adresse + "result/" + result.get(0).numero + "/"
                                        + result.get(0).coordonnees.getLatitude() + "/" + result.get(0).coordonnees.getLongitude() + "/" +
                                        result.get(result.size() - 1).coordonnees.getLatitude() + "/" + result.get(result.size() - 1).coordonnees.getLongitude(), 59, 136, 195);
                        Log.d("Navigation_Bus_URL", adresse + "result/" + result.get(0).numero + "/"
                                + result.get(0).coordonnees.getLatitude() + "/" + result.get(0).coordonnees.getLongitude() + "/" +
                                result.get(result.size() - 1).coordonnees.getLatitude() + "/" + result.get(result.size() - 1).coordonnees.getLongitude());
                    } else if (mean.equals("buses")) {

                    } else if (mean.equals("All")) {

                    }
//            fetchRoute(src, result.get(0).coordonnees, true);
//            fetchRoute(dst, result.get(result.size() - 1).coordonnees, true);
//            addSource(src, "From current location to \n" + result.get(0).nomFr + ":\n" + (int) Math.round(g.cost(g, source, path.get(1))));

                //1ère station to Size()-1
                for (int i = 0; i < result.size() - 1; i++) {
                    if (result.get(i).type.equals("tramway")) {
                        addPin(result.get(i).coordonnees, result.get(i).nomFr + " to \n" + result.get(i + 1).nomFr + ":\n" + (int) Math.round(g.cost(g, path.get(i + 1), path.get(i + 2))), "tramway");

                    } else
                        addPin(result.get(i).coordonnees, result.get(i).nomFr + ", " + removeFromStart(result.get(i).numero) + " to \n" + result.get(i + 1).nomFr + ":\n" + (int) Math.round(g.cost(g, path.get(i + 1), path.get(i + 2))), "bus");
                }
                linear_steps.removeAllViewsInLayout();
                View card = View.inflate(this, R.layout.card_view, null);
                TextView criteria_text = card.findViewById(R.id.criteria);
                criteria_text.setText("Distance");
                TextView weight_text = card.findViewById(R.id.weight);
                weight_text.setText(cost + "");

                linear_steps.addView(card);


                if (result.get(result.size() - 1).type.equals("tramway"))
                    addPin(result.get(result.size() - 1).coordonnees, result.get(0).nomFr + " to \n" + result.get(result.size() - 1).nomFr + ":\n" + cost + " minutes", "tramway");
                else {
                    addPin(result.get(result.size() - 1).coordonnees, result.get(0).nomFr + " to \n" + result.get(result.size() - 1).nomFr + ":\n" + cost + " minutes", "bus");
                }


                //Dernière station to Destination
//            if (result.get(result.size() - 1).type.equals("tramway"))
//                addPin(result.get(result.size() - 1).coordonnees, result.get(result.size() - 1).nomFr + " to \n" + path.get(path.size() - 1).name + ":\n" +
//                        (int) Math.round(g.cost(g, path.get(path.size() - 2), path.get(path.size() - 1))), "tramway");
//            else
//                addPin(result.get(result.size() - 1).coordonnees, path.get(path.size() - 2).name + " to \n" + path.get(path.size() - 1).name + ":\n" +
//                        (int) Math.round(g.cost(g, path.get(path.size() - 2), path.get(path.size() - 1))), "bus");
//            addDestination(dst, "From current to destination: " + "\n" + (int) Math.round(cost / 60) + "minutes");
            }
            //
            else {
//            fetchRoute(src, dst, true);
                Log.d("Error", "No possible way with public transport");
            }

        }
    }

    ArrayList<Station> tramwayNavigation(GeoPoint src, GeoPoint dst, String criteria) {
        g.edges.clear();
        g.getVertices().clear();
        int distance = 0;
        int time = 0;
        current = new Vertex("current");
        source = new Vertex("source");
        destination = new Vertex("destination");
        ArrayList<Station> result = new ArrayList<>();
        if (criteria.equals("time")) {
//                                         gh  adn bhmd env drt  nima cmps fer  sog  adl  dji  wim  dai  hb  rad  mtr adda amr  4   jrdn sud
            int[] tramwayTimes = new int[]{105, 94, 98, 224, 100, 100, 95, 200, 120, 110, 150, 145, 120, 122, 85, 103, 78, 87, 110, 130, 130};

            //Initialisation
            for (int i = 0; i < stationsTramway.size(); i++)
                g.addVertex(stationsTramway.get(i).numero);
            for (int compteur = 0; compteur < stationsTramway.size() - 1; compteur++)
                g.addEdge(g.getVertices().get(compteur), g.getVertices().get(compteur + 1), tramwayTimes[compteur]);
            g.addEdge(g.getVertices().get(4), g.getVertices().get(11), 60);

            //Si Source est Current Location
            if (srcNumber.toLowerCase().equals("current")) {
                g.addVertex(current);
                for (int compteur = 0; compteur < stationsTramway.size(); compteur++) {
                    time = (int) Math.round((fetchTime(src, stationsTramway.get(compteur).coordonnees) * 60));
                    g.addEdge(current, g.getVertices().get(compteur), time);
                }
            }

            //Si Source = Custom marker on map
            if (srcNumber.toLowerCase().equals("source")) {
                g.addVertex(source);
                for (int compteur = 0; compteur < stationsTramway.size(); compteur++) {
                    time = (int) Math.round((fetchTime(src, stationsTramway.get(compteur).coordonnees) * 60));
                    g.addEdge(source, g.getVertices().get(compteur), time);
                }
            }

            //Si Destination est Current Location
            if (dstNumber.toLowerCase().equals("current")) {
                g.addVertex(current);
                for (int compteur = 0; compteur < stationsTramway.size(); compteur++) {
                    time = (int) Math.round((fetchTime(dst, stationsTramway.get(compteur).coordonnees) * 60));
                    g.addEdge(current, g.getVertices().get(compteur), time);
                }
            }

            //Si Destination = Custom marker on map
            if (dstNumber.toLowerCase().equals("destination")) {
                g.addVertex(destination);
                for (int compteur = 0; compteur < stationsTramway.size(); compteur++) {
                    time = (int) Math.round((fetchTime(dst, stationsTramway.get(compteur).coordonnees) * 60));
                    g.addEdge(destination, g.getVertices().get(compteur), time);
                }
            }

            if (g.getVertices().contains(g.getVertex(srcNumber)) && g.getVertices().contains(g.getVertex(dstNumber))) {
                path = g.affichage(g, g.getVertex(srcNumber), g.getVertex(dstNumber));
                cost = Math.round(g.cost(g, g.getVertex(srcNumber), g.getVertex(dstNumber)) / 60);
                for (int k = 1; k < path.size(); k++)
                    for (int i = 0; i < stationsTramway.size(); i++)
                        if (path.get(k).name.equals(stationsTramway.get(i).numero))
                            result.add(stationsTramway.get(i));
            } else
                Toast.makeText(getApplicationContext(), "Les stations sont pas du même moyen de transport", Toast.LENGTH_LONG).show();
        }
        //
        else if (criteria.equals("distance")) {
            //Initialisation du graphe avec {tous les sommets = stations de tramway} et {les arêtes entre chaque deux stations}
            for (int i = 0; i < stationsTramway.size(); i++)
                g.addVertex(stationsTramway.get(i).numero);
            for (int compteur = 0; compteur < stationsTramway.size() - 1; compteur++)
                g.addEdge(g.getVertices().get(compteur), g.getVertices().get(compteur + 1), 0);
            g.addEdge(g.getVertices().get(4), g.getVertices().get(11), 98);

            //Si Source est Current Location
            if (srcNumber.toLowerCase().equals("current")) {
                g.addVertex(current);
                for (int compteur = 0; compteur < stationsTramway.size(); compteur++) {
                    distance = (int) Math.round((fetchDistance(src, stationsTramway.get(compteur).coordonnees) * 1000));
                    g.addEdge(current, g.getVertices().get(compteur), distance);
                }
            }

            //Si Source = Custom marker on map
            if (srcNumber.toLowerCase().equals("source")) {
                g.addVertex(source);
                for (int compteur = 0; compteur < stationsTramway.size(); compteur++) {
                    distance = (int) Math.round((fetchDistance(src, stationsTramway.get(compteur).coordonnees) * 1000));
                    g.addEdge(source, g.getVertices().get(compteur), distance);
                }
            }

            //Si Destination est Current Location
            if (dstNumber.toLowerCase().equals("current")) {
                g.addVertex(current);
                for (int compteur = 0; compteur < stationsTramway.size(); compteur++) {
                    distance = (int) Math.round((fetchDistance(dst, stationsTramway.get(compteur).coordonnees) * 1000));
                    g.addEdge(current, g.getVertices().get(compteur), distance);
                }
            }

            //Si Destination = Custom marker on map
            if (dstNumber.toLowerCase().equals("destination")) {
                g.addVertex(destination);
                for (int compteur = 0; compteur < stationsTramway.size(); compteur++) {
                    distance = (int) Math.round((fetchDistance(dst, stationsTramway.get(compteur).coordonnees) * 1000));
                    g.addEdge(destination, g.getVertices().get(compteur), distance);
                }
            }

            if (g.getVertices().contains(g.getVertex(srcNumber)) && g.getVertices().contains(g.getVertex(dstNumber))) {

                path = g.affichage(g, g.getVertex(srcNumber), g.getVertex(dstNumber));
                cost = Math.round(g.cost(g, g.getVertex(srcNumber), g.getVertex(dstNumber)));
                for (int k = 1; k < path.size(); k++)
                    for (int i = 0; i < stationsTramway.size(); i++)
                        if (path.get(k).name.equals(stationsTramway.get(i).numero))
                            result.add(stationsTramway.get(i));
            } else
                Toast.makeText(getApplicationContext(), "Les stations sont pas du même moyen de transport", Toast.LENGTH_LONG).show();
        }
        return result;
    }

    ArrayList<Station> busesNavigation(GeoPoint src, GeoPoint dst, String criteria) {
        g.edges.clear();
        g.getVertices().clear();
        int distance = 0;
        int time = 0;
        int walk = 0;
        current = new Vertex("current");
        source = new Vertex("source");
        destination = new Vertex("destination");
        ArrayList<Station> result = new ArrayList<>();
        if (criteria.equals("time")) {

            //Initialisation
            for (int i = 0; i < stationsBus.size(); i++)
                g.addVertex(stationsBus.get(i).numero);

            for (int compteur = 0; compteur < allStations.size(); compteur++)
                for (int compteur2 = compteur; compteur2 < allStations.size(); compteur2++) {
                    if (matrice.get(walk).stationSource.type.equals("bus") && matrice.get(walk).stationDestination.type.equals("bus")) {
                        g.addEdge(g.getVertex(matrice.get(walk).stationSource.numero), g.getVertex(matrice.get(walk).stationDestination.numero), (int) matrice.get(walk).time);
                    }
                    walk++;
                    if (walk == 6441) walk = 0;
                }
            addBusNavigation(stationsBus3, 120);
            addBusNavigation(stationsBus3bis, 100);
            addBusNavigation(stationsBus11, 129);
            addBusNavigation(stationsBus16, 113);
            addBusNavigation(stationsBus17, 125);
            addBusNavigation(stationsBus22, 188);
            addBusNavigation(stationsBus25, 180);
            addBusNavigation(stationsBus27, 270);

            //Si Source est Current Location
            if (srcNumber.toLowerCase().equals("current")) {
                g.addVertex(current);
                for (int compteur = 0; compteur < stationsBus.size(); compteur++)
                    g.addEdge(current, g.getVertices().get(compteur), (int) Math.round(fetchTime(src, stationsBus.get(compteur).coordonnees) * 60));
            }

            //Si Source = Custom marker on map
            if (srcNumber.toLowerCase().equals("source")) {
                g.addVertex(source);
                for (int compteur = 0; compteur < stationsBus.size(); compteur++)
                    g.addEdge(source, g.getVertices().get(compteur), (int) Math.round(fetchTime(src, stationsBus.get(compteur).coordonnees) * 60));
            }

            //Si Destination est Current Location
            if (dstNumber.toLowerCase().equals("current")) {
                g.addVertex(current);
                for (int compteur = 0; compteur < stationsBus.size(); compteur++)
                    g.addEdge(current, g.getVertices().get(compteur), (int) Math.round(fetchTime(dst, stationsBus.get(compteur).coordonnees) * 60));
            }

            //Si Destination = Custom marker on map
            if (dstNumber.toLowerCase().equals("destination")) {
                g.addVertex(destination);
                for (int compteur = 0; compteur < stationsBus.size(); compteur++)
                    g.addEdge(destination, g.getVertices().get(compteur), (int) Math.round(fetchTime(dst, stationsBus.get(compteur).coordonnees) * 60));
            }

            if (g.getVertices().contains(g.getVertex(srcNumber)) && g.getVertices().contains(g.getVertex(dstNumber))) {
                path = g.affichage(g, g.getVertex(srcNumber), g.getVertex(dstNumber));
                cost = Math.round(g.cost(g, g.getVertex(srcNumber), g.getVertex(dstNumber)) / 60);
                for (int k = 1; k < path.size(); k++)
                    for (int i = 0; i < stationsBus.size(); i++)
                        if (path.get(k).name.equals(stationsBus.get(i).numero))
                            result.add(stationsBus.get(i));
            } else
                Toast.makeText(getApplicationContext(), "Les stations sont pas de la même ligne", Toast.LENGTH_LONG).show();
        }
        //
        else if (criteria.equals("distance")) {
            //Initialisation
            for (int i = 0; i < stationsBus.size(); i++)
                g.addVertex(stationsBus.get(i).numero);

            for (int compteur = 0; compteur < allStations.size(); compteur++)
                for (int compteur2 = compteur; compteur2 < allStations.size(); compteur2++) {
                    if (matrice.get(walk).stationSource.type.equals("bus") && matrice.get(walk).stationDestination.type.equals("bus")) {
                        g.addEdge(g.getVertex(matrice.get(walk).stationSource.numero), g.getVertex(matrice.get(walk).stationDestination.numero), (int) matrice.get(walk).distance);
                    }
                    walk++;
                    if (walk == 6441) walk = 0;
                }
            addBusNavigation(stationsBus3, 0);
            addBusNavigation(stationsBus3bis, 0);
            addBusNavigation(stationsBus11, 0);
            addBusNavigation(stationsBus16, 0);
            addBusNavigation(stationsBus17, 0);
            addBusNavigation(stationsBus22, 0);
            addBusNavigation(stationsBus25, 0);
            addBusNavigation(stationsBus27, 0);

            //Si Source est Current Location
            if (srcNumber.toLowerCase().equals("current")) {
                g.addVertex(current);
                for (int compteur = 0; compteur < stationsBus.size(); compteur++)
                    g.addEdge(current, g.getVertices().get(compteur), (int) Math.round(fetchDistance(src, stationsBus.get(compteur).coordonnees) * 1000));
            }

            //Si Source = Custom marker on map
            if (srcNumber.toLowerCase().equals("source")) {
                g.addVertex(source);
                for (int compteur = 0; compteur < stationsBus.size(); compteur++)
                    g.addEdge(source, g.getVertices().get(compteur), (int) Math.round(fetchDistance(src, stationsBus.get(compteur).coordonnees) * 1000));
            }

            //Si Destination est Current Location
            if (dstNumber.toLowerCase().equals("current")) {
                g.addVertex(current);
                for (int compteur = 0; compteur < stationsBus.size(); compteur++)
                    g.addEdge(current, g.getVertices().get(compteur), (int) Math.round(fetchDistance(dst, stationsBus.get(compteur).coordonnees) * 1000));
            }

            //Si Destination = Custom marker on map
            if (dstNumber.toLowerCase().equals("destination")) {
                g.addVertex(destination);
                for (int compteur = 0; compteur < stationsBus.size(); compteur++)
                    g.addEdge(destination, g.getVertices().get(compteur), (int) Math.round(fetchDistance(dst, stationsBus.get(compteur).coordonnees) * 1000));
            }

            if (g.getVertices().contains(g.getVertex(srcNumber)) && g.getVertices().contains(g.getVertex(dstNumber))) {
                path = g.affichage(g, g.getVertex(srcNumber), g.getVertex(dstNumber));
                cost = Math.round(g.cost(g, g.getVertex(srcNumber), g.getVertex(dstNumber)));
                for (int k = 1; k < path.size(); k++)
                    for (int i = 0; i < stationsBus.size(); i++)
                        if (path.get(k).name.equals(stationsBus.get(i).numero))
                            result.add(stationsBus.get(i));
            }
            //
            else
                Toast.makeText(getApplicationContext(), "Les stations sont pas de la même ligne", Toast.LENGTH_LONG).show();
        }
        return result;
    }

    ArrayList<Station> allNavigation(GeoPoint src, GeoPoint dst, String criteria) {
        g.edges.clear();
        g.getVertices().clear();
        int distance = 0;
        int time = 0;
        int walk = 0;
        current = new Vertex("current");
        source = new Vertex("source");
        destination = new Vertex("destination");
        ArrayList<Station> result = new ArrayList<>();

        if (criteria.equals("time")) {
            for (int i = 0; i < allStations.size(); i++)
                g.addVertex(allStations.get(i).numero);
            for (int compteur = 0; compteur < allStations.size(); compteur++)
                for (int compteur2 = compteur; compteur2 < allStations.size(); compteur2++) {
                    g.addEdge(g.getVertex(compteur), g.getVertex(compteur2), (int) matrice.get(walk).time);
                    walk++;
                    if (walk == 6441) walk = 0;
                }

            ArrayList<Edge> temp = new ArrayList<>();
            //                             gh  adn bhmd env drt  nima cmps fer  sog  adl  dji  wim  dai  hb  rad  mtr adda amr  4   jrdn sud
            int[] tramwayTimes = new int[]{105, 94, 98, 224, 100, 100, 95, 200, 120, 110, 150, 145, 120, 122, 85, 103, 78, 87, 110, 130, 130};

            for (int i = 0; i < g.edges.size(); i++)
                if (isNumeric(g.edges.get(i).source.name) && isNumeric(g.edges.get(i).target.name)) {
                    int sourceName = Integer.parseInt(g.edges.get(i).source.name);
                    int targetName = Integer.parseInt(g.edges.get(i).target.name);
                    int diff = sourceName - targetName;
                    for (int j = 0; j < stationsTramway.size() - 1; j++)
                        if (sourceName == Integer.parseInt(stationsTramway.get(j).numero) && targetName == Integer.parseInt(stationsTramway.get(j + 1).numero) && Math.abs(diff) == 1)
                            temp.add(new Edge(g.getVertices().get(j), g.getVertices().get(j + 1), tramwayTimes[j]));
                }

            for (int i = 0; i < temp.size(); i++)
                g.addEdge(temp.get(i).source, temp.get(i).target, (int) temp.get(i).weight);

            addBusNavigation(stationsBus3, 120);
            addBusNavigation(stationsBus3bis, 100);
            addBusNavigation(stationsBus11, 129);
            addBusNavigation(stationsBus16, 113);
            addBusNavigation(stationsBus17, 125);
            addBusNavigation(stationsBus22, 188);
            addBusNavigation(stationsBus25, 180);
            addBusNavigation(stationsBus27, 270);


            //Si Source est Current Location
            if (srcNumber.toLowerCase().equals("current")) {
                g.addVertex(current);
                for (int compteur = 0; compteur < allStations.size(); compteur++)
                    g.addEdge(current, g.getVertices().get(compteur), (int) Math.round(fetchTime(src, allStations.get(compteur).coordonnees) * 60));
            }

            //Si Source = Custom marker on map
            if (srcNumber.toLowerCase().equals("source")) {
                g.addVertex(source);
                for (int compteur = 0; compteur < allStations.size(); compteur++)
                    g.addEdge(source, g.getVertices().get(compteur), (int) Math.round(fetchTime(src, allStations.get(compteur).coordonnees) * 60));
            }

            //Si Destination est Current Location
            if (dstNumber.toLowerCase().equals("current")) {
                g.addVertex(current);
                for (int compteur = 0; compteur < allStations.size(); compteur++)
                    g.addEdge(current, g.getVertices().get(compteur), (int) Math.round(fetchTime(dst, allStations.get(compteur).coordonnees) * 60));
            }

            //Si Destination = Custom marker on map
            if (dstNumber.toLowerCase().equals("destination")) {
                g.addVertex(destination);
                for (int compteur = 0; compteur < allStations.size(); compteur++)
                    g.addEdge(destination, g.getVertices().get(compteur), (int) Math.round(fetchTime(dst, allStations.get(compteur).coordonnees) * 60));
            }

            if (g.getVertices().contains(g.getVertex(srcNumber)) && g.getVertices().contains(g.getVertex(dstNumber))) {
                path = g.affichage(g, g.getVertex(srcNumber), g.getVertex(dstNumber));
                cost = Math.round(g.cost(g, g.getVertex(srcNumber), g.getVertex(dstNumber)) / 60);
                for (int k = 1; k < path.size(); k++)
                    for (int i = 0; i < allStations.size(); i++)
                        if (path.get(k).name.equals(allStations.get(i).numero))
                            result.add(allStations.get(i));
            }
        }
        //
        else if (criteria.equals("distance")) {
            for (int i = 0; i < allStations.size(); i++)
                g.addVertex(allStations.get(i).numero);

            for (int compteur = 0; compteur < allStations.size(); compteur++)
                for (int compteur2 = compteur; compteur2 < allStations.size(); compteur2++) {
                    g.addEdge(g.getVertex(compteur), g.getVertex(compteur2), (int) matrice.get(walk).distance);
                    walk++;
                    if (walk == 6441) walk = 0;
                }

            ArrayList<Edge> temp = new ArrayList<>();
            for (int i = 0; i < g.edges.size(); i++)
                if (isNumeric(g.edges.get(i).source.name) && isNumeric(g.edges.get(i).target.name)) {
                    int sourceName = Integer.parseInt(g.edges.get(i).source.name);
                    int targetName = Integer.parseInt(g.edges.get(i).target.name);
                    int diff = sourceName - targetName;
                    for (int j = 0; j < stationsTramway.size() - 1; j++)
                        if (sourceName == Integer.parseInt(stationsTramway.get(j).numero) && targetName == Integer.parseInt(stationsTramway.get(j + 1).numero) && Math.abs(diff) == 1)
                            temp.add(new Edge(g.getVertices().get(j), g.getVertices().get(j + 1), 0));
                }

            for (int i = 0; i < temp.size(); i++)
                g.addEdge(temp.get(i).source, temp.get(i).target, (int) temp.get(i).weight);

            addBusNavigation(stationsBus3, 0);
            addBusNavigation(stationsBus3bis, 0);
            addBusNavigation(stationsBus11, 0);
            addBusNavigation(stationsBus16, 0);
            addBusNavigation(stationsBus17, 0);
            addBusNavigation(stationsBus22, 0);
            addBusNavigation(stationsBus25, 0);
            addBusNavigation(stationsBus27, 0);


            //Si Source est Current Location
            if (srcNumber.toLowerCase().equals("current")) {
                g.addVertex(current);
                for (int compteur = 0; compteur < allStations.size(); compteur++)
                    g.addEdge(current, g.getVertices().get(compteur), (int) Math.round(fetchDistance(src, allStations.get(compteur).coordonnees) * 1000));
            }

            //Si Source = Custom marker on map
            if (srcNumber.toLowerCase().equals("source")) {
                g.addVertex(source);
                for (int compteur = 0; compteur < allStations.size(); compteur++)
                    g.addEdge(source, g.getVertices().get(compteur), (int) Math.round(fetchDistance(src, allStations.get(compteur).coordonnees) * 1000));
            }

            //Si Destination est Current Location
            if (dstNumber.toLowerCase().equals("current")) {
                g.addVertex(current);
                for (int compteur = 0; compteur < allStations.size(); compteur++)
                    g.addEdge(current, g.getVertices().get(compteur), (int) Math.round(fetchDistance(dst, allStations.get(compteur).coordonnees) * 1000));
            }

            //Si Destination = Custom marker on map
            if (dstNumber.toLowerCase().equals("destination")) {
                g.addVertex(destination);
                for (int compteur = 0; compteur < allStations.size(); compteur++)
                    g.addEdge(destination, g.getVertices().get(compteur), (int) Math.round(fetchDistance(dst, allStations.get(compteur).coordonnees) * 1000));
            }


            if (g.getVertices().contains(g.getVertex(srcNumber)) && g.getVertices().contains(g.getVertex(dstNumber))) {
                path = g.affichage(g, g.getVertex(srcNumber), g.getVertex(dstNumber));
                cost = Math.round(g.cost(g, g.getVertex(srcNumber), g.getVertex(dstNumber)));
                for (int k = 1; k < path.size(); k++)
                    for (int i = 0; i < allStations.size(); i++)
                        if (path.get(k).name.equals(allStations.get(i).numero))
                            result.add(allStations.get(i));
            }

        }
        return result;
    }

    ArrayList<Station> bus3Navigation(GeoPoint src, GeoPoint dst, String criteria) {
        g.getVertices().clear();
        g.edges.clear();
        ArrayList<Station> result = new ArrayList<>();
        for (int i = 0; i < stationsBus3.size(); i++)
            g.addVertex(stationsBus3.get(i).numero);
        for (int i = 0; i < stationsBus3bis.size(); i++)
            g.addVertex(stationsBus3bis.get(i).numero);
        if (criteria.equals("time")) {
            for (int compteur = 0; compteur < stationsBus3.size() - 1; compteur++)
                g.addEdge(g.getVertices().get(compteur), g.getVertices().get(compteur + 1), 120);
            for (int compteur = stationsBus3.size(); compteur < stationsBus3.size() + stationsBus3bis.size() - 1; compteur++)
                g.addEdge(g.getVertices().get(compteur), g.getVertices().get(compteur + 1), 100);
            g.addEdge(g.getVertices().get(0), g.getVertices().get(stationsBus3.size()), 150);


            if (g.getVertices().contains(g.getVertex(srcNumber)) && g.getVertices().contains(g.getVertex(dstNumber))) {
                path = g.affichage(g, g.getVertex(srcNumber), g.getVertex(dstNumber));
                cost = Math.round(g.cost(g, g.getVertex(srcNumber), g.getVertex(dstNumber)) / 60);
                for (int k = 1; k < path.size(); k++) {
                    for (int i = 0; i < stationsBus3.size(); i++)
                        if (path.get(k).name.equals(stationsBus3.get(i).numero))
                            result.add(stationsBus3.get(i));
                    for (int i = 0; i < stationsBus3bis.size(); i++)
                        if (path.get(k).name.equals(stationsBus3bis.get(i).numero))
                            result.add(stationsBus3bis.get(i));
                }
            } else
                Toast.makeText(getApplicationContext(), "Les stations sont pas de la même ligne", Toast.LENGTH_LONG).show();
        }
        //
        else if (criteria.equals("distance")) {
            for (int compteur = 0; compteur < stationsBus3.size() - 1; compteur++)
                g.addEdge(g.getVertices().get(compteur), g.getVertices().get(compteur + 1), 0);
            for (int compteur = stationsBus3.size(); compteur < stationsBus3.size() + stationsBus3bis.size() - 1; compteur++)
                g.addEdge(g.getVertices().get(compteur), g.getVertices().get(compteur + 1), 0);
            g.addEdge(g.getVertices().get(0), g.getVertices().get(stationsBus3.size()), 104);


            if (g.getVertices().contains(g.getVertex(srcNumber)) && g.getVertices().contains(g.getVertex(dstNumber))) {
                path = g.affichage(g, g.getVertex(srcNumber), g.getVertex(dstNumber));
                cost = Math.round(g.cost(g, g.getVertex(srcNumber), g.getVertex(dstNumber)));
                for (int k = 1; k < path.size(); k++) {
                    for (int i = 0; i < stationsBus3.size(); i++)
                        if (path.get(k).name.equals(stationsBus3.get(i).numero))
                            result.add(stationsBus3.get(i));
                    for (int i = 0; i < stationsBus3bis.size(); i++)
                        if (path.get(k).name.equals(stationsBus3bis.get(i).numero))
                            result.add(stationsBus3bis.get(i));
                }
            } else
                Toast.makeText(getApplicationContext(), "Les stations sont pas de la même ligne", Toast.LENGTH_LONG).show();
        }
        return result;
    }

    public ArrayList<Station> busNavigation(GeoPoint src, GeoPoint dst, ArrayList<Station> list, int weight) {
        path.clear();
        g.edges.clear();
        g.getVertices().clear();
        ArrayList<Station> result = new ArrayList<>();

        for (int i = 0; i < list.size(); i++)
            g.addVertex(list.get(i).numero);
        for (int i = 0; i < list.size() - 1; i++)
            g.addEdge(g.getVertices().get(i), g.getVertices().get(i + 1), weight);

        if (g.getVertices().contains(g.getVertex(srcNumber)) && g.getVertices().contains(g.getVertex(dstNumber))) {
            path = g.affichage(g, g.getVertex(srcNumber), g.getVertex(dstNumber));
            cost = Math.round(g.cost(g, g.getVertex(srcNumber), g.getVertex(dstNumber)) / 60);
            for (int k = 1; k < path.size(); k++)
                for (int i = 0; i < list.size(); i++)
                    if (path.get(k).name.equals(list.get(i).numero))
                        result.add(list.get(i));
        } else
            Toast.makeText(getApplicationContext(), "Les stations sont pas de la même ligne", Toast.LENGTH_LONG).show();
        return result;
    }

    void addBusNavigation(ArrayList<Station> list, int weight) {
        for (int i = 0; i < g.getVertices().size(); i++)
            for (int j = 0; j < list.size() - 1; j++)
                if (g.getVertices().get(i).name.equals(list.get(j).numero))
                    if (Math.abs(removeAfter(g.getVertices().get(i).name) - removeAfter(list.get(j + 1).numero)) == 1)
                        g.addEdge(g.getVertices().get(i), g.getVertices().get(i + 1), weight);
    }

    //Menu Navigation
    private void setNavigationViewListener() {
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public boolean onNavigationItemSelected(@NotNull MenuItem item) {
        getLocation();
        Bundle send = new Bundle();

        if (item.getItemId() == R.id.allTramwayStations) {
            Intent intent = new Intent(MainActivity.this, AllNearTramwayStationsActivity.class);
            send.putDouble("currentLocationLatitude", currentLocation.getLatitude());
            send.putDouble("currentLocationLongitude", currentLocation.getLongitude());
            intent.putExtras(send);
            MainActivity.this.startActivity(intent);
        } else if (item.getItemId() == R.id.closestStations) {
            Intent intent = new Intent(MainActivity.this, NthTramwayStationsActivity.class);
            send.putDouble("currentLocationLatitude", currentLocation.getLatitude());
            send.putDouble("currentLocationLongitude", currentLocation.getLongitude());
            intent.putExtras(send);
            MainActivity.this.startActivity(intent);
        } else if (item.getItemId() == R.id.bestChoice) {
            navigationSearchViews.setVisibility(View.VISIBLE);
            stationSource();
            stationDestination();
        } else {
            Log.d("Error", "MenuNavigationError");
        }
        return true;
    }

    private void setUpList() {
        searchStations = findViewById(R.id.searchStationList);
        navigationSource = findViewById(R.id.source);
        navigationDestination = findViewById(R.id.destination);
        searchStations.setVisibility(View.INVISIBLE);
        navigationSource.setVisibility(View.INVISIBLE);
        navigationDestination.setVisibility(View.INVISIBLE);
    }

    private void initSearch() {
        SearchView searchStations = findViewById(R.id.stationNameText);
        searchStations.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                ArrayList<Station> stations = new ArrayList<>();
                for (int i = 0; i < allStations.size(); i++)
                    if (allStations.get(i).nomFr.toLowerCase().contains(newText.toLowerCase()))
                        stations.add(allStations.get(i));
                StationNameAdapter stationNameAdapter = new StationNameAdapter(getApplicationContext(), 0, stations);
                MainActivity.this.searchStations.setAdapter(stationNameAdapter);
                MainActivity.this.searchStations.setOnItemClickListener((parent, view, position, id) -> {
                    Station o = (Station) MainActivity.this.searchStations.getItemAtPosition(position);
                    if (o.type.equals("bus")) {
                        clearMap();
                        String num = removeLastChars(o.numero, 3);
                        cheminBus = searchBusRouteByNumber(num);
                        tracerCheminBus(cheminBus, myMap, 255, 0, 0, o.numero);
                        addStationBus(myMap, o.coordonnees, o.nomFr, o.numero);
                        myMap.getController().setCenter(o.coordonnees);
                        myMap.invalidate();
                    } else {
                        chemin = routeTramway;
                        tracerCheminTramway(chemin, myMap);
                        addStationTramway(myMap, o.coordonnees, o.nomFr);
                        myMap.getController().setCenter(o.coordonnees);
                        myMap.invalidate();

                    }

                });
                return false;
            }
        });
        searchStations.setOnSearchClickListener(v -> {
            this.searchStations.setVisibility(View.VISIBLE);
            navigationSource.setVisibility(View.INVISIBLE);
            navigationDestination.setVisibility(View.INVISIBLE);
        });
        searchStations.setOnCloseListener(() -> {
            this.searchStations.setVisibility(View.INVISIBLE);
            return false;
        });
    }

    private void stationSource() {
        SearchView pointSource = findViewById(R.id.stationSource);
        pointSource.setBackgroundResource(R.drawable.background_primary);
        pointSource.setVisibility(View.VISIBLE);
        pointSource.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                navigationSource.setVisibility(View.INVISIBLE);
                searchStations.setVisibility(View.INVISIBLE);
                navigationDestination.setVisibility(View.INVISIBLE);
                Log.d("Navigation_Source", srcNumber);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                navigationSource.setVisibility(View.VISIBLE);
                searchStations.setVisibility(View.INVISIBLE);
                navigationDestination.setVisibility(View.INVISIBLE);
                getLocation();
                ArrayList<Station> stations = new ArrayList<>();
                stations.add(new Station("current", "Current location", "current", currentLocation));
                stations.add(new Station("source", "Custom source", "source", defaultLocation));
                for (int i = 0; i < allStations.size(); i++)
                    if (allStations.get(i).nomFr.toLowerCase().contains(newText.toLowerCase()))
                        stations.add(allStations.get(i));

                StationNameAdapter stationNameAdapter = new StationNameAdapter(getApplicationContext(), 0, stations);
                navigationSource.setAdapter(stationNameAdapter);
                navigationSource.setOnItemClickListener((parent, view, position, id) -> {
                    Station o = (Station) navigationSource.getItemAtPosition(position);
                    if (o.numero.equals(("source"))) {
                        Marker markerSource = new Marker(myMap);
                        ok_marker.setVisibility(View.VISIBLE);
                        final Marker[] tempSource = {new Marker(myMap)};
                        tempSource[0].setDraggable(false);
                        tempSource[0].setPosition(defaultLocation);
                        tempSource[0].setIcon(getApplicationContext().getDrawable(R.drawable.marker_source));
                        myMap.getOverlays().add(tempSource[0]);
                        myMap.setMapListener(new MapListener() {
                            @Override
                            public boolean onScroll(ScrollEvent event) {

                                tempSource[0].setPosition(new GeoPoint((float) myMap.getMapCenter().getLatitude(),
                                        (float) myMap.getMapCenter().getLongitude()));
                                ok_marker.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        markerSource.setIcon(getApplicationContext().getDrawable(R.drawable.marker_source));
                                        markerSource.setPosition(tempSource[0].getPosition());
                                        o.coordonnees.setLatitude(markerSource.getPosition().getLatitude());
                                        o.coordonnees.setLongitude(markerSource.getPosition().getLongitude());
                                        myMap.getOverlays().add(markerSource);
                                        srcCoord = markerSource.getPosition();
                                        srcNumber = o.numero;
                                        tempSource[0].setVisible(false);
                                    }
                                });
                                return false;
                            }

                            @Override
                            public boolean onZoom(ZoomEvent event) {
                                return false;
                            }
                        });
                    } else {
                        srcCoord = o.coordonnees;
                        srcNumber = o.numero;
                    }
                    pointSource.setQuery(o.nomFr, true);
                });
                return true;
            }
        });
        pointSource.setOnSearchClickListener(v -> {
            navigationSource.setVisibility(View.VISIBLE);
            searchStations.setVisibility(View.INVISIBLE);
            navigationDestination.setVisibility(View.INVISIBLE);
        });
        pointSource.setOnCloseListener(() -> {
            navigationSource.setVisibility(View.INVISIBLE);
            return false;
        });
    }

    private void stationDestination() {
        SearchView pointDestination = findViewById(R.id.stationDestination);
        pointDestination.setBackgroundResource(R.drawable.background_primary);
        pointDestination.setVisibility(View.VISIBLE);
        pointDestination.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                navigationSource.setVisibility(View.INVISIBLE);
                searchStations.setVisibility(View.INVISIBLE);
                navigationDestination.setVisibility(View.INVISIBLE);
                Log.d("Navigation_Destination", dstNumber);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                navigationDestination.setVisibility(View.VISIBLE);
                searchStations.setVisibility(View.INVISIBLE);
                navigationSource.setVisibility(View.INVISIBLE);
                getLocation();
                ArrayList<Station> stations = new ArrayList<>();
                stations.add(new Station("current", "Current location", "current", currentLocation));
                stations.add(new Station("destination", "Custom destination", "destination", defaultLocation));
                for (int i = 0; i < allStations.size(); i++)
                    if (allStations.get(i).nomFr.toLowerCase().contains(newText.toLowerCase()))
                        stations.add(allStations.get(i));
                StationNameAdapter stationNameAdapter = new StationNameAdapter(getApplicationContext(), 0, stations);
                navigationDestination.setAdapter(stationNameAdapter);
                navigationDestination.setOnItemClickListener((parent, view, position, id) -> {
                    Station o = (Station) navigationDestination.getItemAtPosition(position);
                    if (o.numero.equals(("destination"))) {
                        Marker markerDestination = new Marker(myMap);
                        ok_marker.setVisibility(View.VISIBLE);
                        final Marker[] temp_destination = {new Marker(myMap)};
                        temp_destination[0].setDraggable(false);
                        temp_destination[0].setPosition(defaultLocation);
                        temp_destination[0].setIcon(getApplicationContext().getDrawable(R.drawable.marker_destination));
                        myMap.getOverlays().add(temp_destination[0]);
                        myMap.setMapListener(new MapListener() {
                            @Override
                            public boolean onScroll(ScrollEvent event) {

                                temp_destination[0].setPosition(new GeoPoint((float) myMap.getMapCenter().getLatitude(),
                                        (float) myMap.getMapCenter().getLongitude()));
                                ok_marker.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        markerDestination.setIcon(getApplicationContext().getDrawable(R.drawable.marker_destination));
                                        markerDestination.setPosition(temp_destination[0].getPosition());
                                        o.coordonnees.setLatitude(markerDestination.getPosition().getLatitude());
                                        o.coordonnees.setLongitude(markerDestination.getPosition().getLongitude());
                                        myMap.getOverlays().add(markerDestination);
                                        dstCoord = markerDestination.getPosition();
                                        dstNumber = o.numero;
                                        temp_destination[0].setVisible(false);
                                    }
                                });
                                return false;
                            }

                            @Override
                            public boolean onZoom(ZoomEvent event) {
                                return false;
                            }
                        });
                    } else {
                        dstCoord = o.coordonnees;
                        dstNumber = o.numero;
                    }
                    pointDestination.setQuery(o.nomFr, true);

                });
                return true;
            }
        });

        pointDestination.setOnSearchClickListener(v -> {
            navigationDestination.setVisibility(View.VISIBLE);
            searchStations.setVisibility(View.INVISIBLE);
            navigationSource.setVisibility(View.INVISIBLE);
        });
        pointDestination.setOnCloseListener(() -> {
            navigationDestination.setVisibility(View.INVISIBLE);
            return false;
        });
    }

    private String removeLastChars(String str, int chars) {
        return str.substring(0, str.length() - chars);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ArrayList<Station> ligne;
        SubMenu tramwaySubMenu = menu.addSubMenu(R.id.tramway_stations, R.id.tramway_stations, 1, "Stations de Tramway");
        SubMenu busSubMenu = menu.addSubMenu(R.id.bus_16, R.id.bus_16, 1, "Stations de bus");
        SubMenu ligne3 = busSubMenu.addSubMenu(R.id.bus_stations_3, R.id.bus_stations_3, 1, "Ligne 3");
        SubMenu ligne3bis = busSubMenu.addSubMenu(R.id.bus_stations_3_bis, R.id.bus_stations_3_bis, 1, "Ligne 3 bis");
        SubMenu ligne11 = busSubMenu.addSubMenu(R.id.bus_stations_11, R.id.bus_stations_11, 1, "Ligne 11");
        SubMenu ligne16 = busSubMenu.addSubMenu(R.id.bus_stations_16, R.id.bus_stations_16, 1, "Ligne 16");
        SubMenu ligne17 = busSubMenu.addSubMenu(R.id.bus_stations_17, R.id.bus_stations_17, 1, "Ligne 17");
        SubMenu ligne22 = busSubMenu.addSubMenu(R.id.bus_stations_22, R.id.bus_stations_22, 1, "Ligne 22");
        SubMenu ligne25 = busSubMenu.addSubMenu(R.id.bus_stations_25, R.id.bus_stations_25, 1, "Ligne 25");
        SubMenu ligne27 = busSubMenu.addSubMenu(R.id.bus_stations_27, R.id.bus_stations_27, 1, "Ligne 27");

        for (int i = 0; i < stationsTramway.size(); i++) {
            tramwaySubMenu.add(R.id.tramway_stations, ids_tramway[i], 1, stationsTramway.get(i).nomFr);
        }
        ligne = searchBusStationByNumber("A03_");
        for (int i = 0; i < ligne.size(); i++) {
            ligne3.add(R.id.bus_stations_3, ids_bus3[i], 1, ligne.get(i).nomFr);
        }
        ligne = searchBusStationByNumber("A03bis_");
        for (int i = 0; i < ligne.size(); i++) {
            ligne3bis.add(R.id.bus_stations_3_bis, ids_bus3bis[i], 1, ligne.get(i).nomFr);
        }
        ligne = searchBusStationByNumber("A11_");
        for (int i = 0; i < ligne.size(); i++) {
            ligne11.add(R.id.bus_stations_11, ids_bus11[i], 1, ligne.get(i).nomFr);
        }
        ligne = searchBusStationByNumber("A16_");
        for (int i = 0; i < ligne.size(); i++) {
            ligne16.add(R.id.bus_stations_16, ids_bus16[i], 1, ligne.get(i).nomFr);
        }
        ligne = searchBusStationByNumber("A17_");
        for (int i = 0; i < ligne.size(); i++) {
            ligne17.add(R.id.bus_stations_17, ids_bus17[i], 1, ligne.get(i).nomFr);
        }
        ligne = searchBusStationByNumber("A22_");
        for (int i = 0; i < ligne.size(); i++) {
            ligne22.add(R.id.bus_stations_22, ids_bus22[i], 1, ligne.get(i).nomFr);
        }
        ligne = searchBusStationByNumber("A25_");
        for (int i = 0; i < ligne.size(); i++) {
            ligne25.add(R.id.bus_stations_25, ids_bus25[i], 1, ligne.get(i).nomFr);
        }
        ligne = searchBusStationByNumber("A27_");
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
        if (item.getGroupId() == R.id.tramway_stations) {
            i = item.getItemId();
            verify = 1;
            parcours = 0;
            while (verify != 0 && parcours < stationsTramway.size()) {
                if (i == ids_tramway[parcours]) {
                    addMarker(myMap, stationsTramway.get(parcours).coordonnees, stationsTramway.get(parcours).nomFr, "tramway");
                    verify = 0;
                } else {
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

    //Get current location
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

    //Populating the database
    private void insertAllStations(Response response) throws IOException {
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
                station.type = jsonobject.getString("type");
                station.numero = jsonobject.getString("numero");
                point.setLatitude(jsonobject.getDouble("latitude"));
                point.setLongitude(jsonobject.getDouble("longitude"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            station.coordonnees = point;
            Log.d("Insertion_Station", station.toString());
            database.addStation(station);
        }
    }

    private void insertAllMatrice(Response response) throws IOException {
        myResponse = Objects.requireNonNull(response.body()).string();
        GeoPoint p1 = new GeoPoint(0.0, 0.0);
        GeoPoint p2 = new GeoPoint(0.0, 0.0);
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
                ligne.stationSource.nomFr = jsonobject.getString("nomFrdepart");
                ligne.stationSource.type = jsonobject.getString("typedepart");
                ligne.stationSource.numero = jsonobject.getString("numerodepart");
                p1.setLatitude(jsonobject.getDouble("latitudedepart"));
                p1.setLongitude(jsonobject.getDouble("longitudedepart"));
                ligne.stationSource.coordonnees = p1;
                ligne.stationDestination.nomFr = jsonobject.getString("nomFrarrive");
                ligne.stationDestination.type = jsonobject.getString("typearrive");
                ligne.stationDestination.numero = jsonobject.getString("numeroarrive");
                p2.setLatitude(jsonobject.getDouble("latitudearrive"));
                p2.setLongitude(jsonobject.getDouble("longitudearrive"));
                ligne.stationDestination.coordonnees = p2;
                ligne.distance = jsonobject.getDouble("distance");
                ligne.time = jsonobject.getDouble("duration");
                database.addMatriceLine(ligne);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void insertRouteTramway(Response response) throws IOException {
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

    private void insertRouteCorrespondance(Response response) throws IOException {
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

    private void insertRouteBus(Response response) throws IOException {
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

    private void insertBestRoute(Response response, int r, int g, int b) throws IOException, JSONException {
        myResponse = Objects.requireNonNull(response.body()).string();
        JSONArray jsonarray = null;
        JSONObject result = null;

        try {
            result = new JSONObject(myResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assert result != null;
        JSONArray datas = result.getJSONArray("datas");
//        JSONArray duration = result.getJSONArray("duration");

        bestRoute.clear();
        for (int i = 0; i < datas.length(); i++) {
            JSONObject jsonobject;
            GeoPoint a = new GeoPoint(0.0, 0.0);
            try {
                jsonobject = datas.getJSONObject(i);
                assert jsonobject != null;
                a.setLatitude(jsonobject.getDouble("x"));
                a.setLongitude(jsonobject.getDouble("y"));
                bestRoute.add(a);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Polyline a = new Polyline();
        a.setWidth(10);
        a.setColor(Color.rgb(r, g, b));
        a.setDensityMultiplier(0.5f);
        a.setPoints(bestRoute);
        myMap.getOverlays().add(a);
        myMap.invalidate();

    }


    //Adding Overlays
    private void addStationsTramway() {
        for (int i = 0; i < stationsTramway.size(); i++) {
            addStationTramway(myMap, stationsTramway.get(i).coordonnees, stationsTramway.get(i).nomFr);
        }
    }

    private void tracerCheminTramway(ArrayList<GeoPoint> chemin, MapView mapView) {
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
        marker.setAnchor(ANCHOR_CENTER, ANCHOR_BOTTOM);
        marker.setAlpha(0.8f);
        marker.setDraggable(false);
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

    void addPin(GeoPoint coordinates, String nom, String mean) {
        Marker marker = new Marker(myMap);
        marker.setPosition(coordinates);
        marker.setAnchor(ANCHOR_CENTER, ANCHOR_BOTTOM);
        marker.setAlpha(0.8f);
        marker.setDraggable(false);
        if (mean.equals("tramway"))
            marker.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.pin_tramway));
        else marker.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.pin_bus));

        marker.setPanToView(true);
        myMap.invalidate();
        myMap.getOverlays().add(marker);
        getLocation();
        marker.setSnippet(nom);
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
//        marker.showInfoWindow();
    }

    void addSource(GeoPoint coordinates, String nom) {
        Marker marker = new Marker(myMap);
        marker.setPosition(coordinates);
        marker.setAnchor(ANCHOR_CENTER, ANCHOR_BOTTOM);
        marker.setAlpha(0.8f);
        marker.setDraggable(false);

        marker.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.marker_source));

        marker.setPanToView(true);
        myMap.invalidate();
        myMap.getOverlays().add(marker);
        customOverlays.add(new CustomOverlay("source", marker));
        getLocation();
        marker.setSnippet(nom);
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
//        marker.showInfoWindow();
    }

    void addPin(GeoPoint coordinates, String nom, Drawable d) {
        Marker marker = new Marker(myMap);
        marker.setPosition(coordinates);
        marker.setAnchor(ANCHOR_CENTER, ANCHOR_BOTTOM);
        marker.setAlpha(0.8f);
        marker.setDraggable(false);

        marker.setIcon(d);

        marker.setPanToView(true);
        myMap.invalidate();
        myMap.getOverlays().add(marker);
        getLocation();
        marker.setSnippet(nom);
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

    void addDestination(GeoPoint coordinates, String nom) {
        Marker marker = new Marker(myMap);
        marker.setPosition(coordinates);
        marker.setAnchor(ANCHOR_CENTER, ANCHOR_BOTTOM);
        marker.setAlpha(0.8f);
        marker.setDraggable(false);
        marker.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.marker_destination));
        marker.setPanToView(true);
        myMap.invalidate();
        myMap.getOverlays().add(marker);
        customOverlays.add(new CustomOverlay("destination", marker));
        getLocation();
        marker.setSnippet(nom);
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

    //Get road points and details
    String fetchRoute(GeoPoint start, GeoPoint end, boolean draw) {
        ArrayList<GeoPoint> roadPoints = new ArrayList<>();
        getLocation();
        roadPoints.add((start));
        roadPoints.add(end);
        RoadManager roadManager = new GraphHopperRoadManager(graphhopperkey, false);
        if (car.isSelected())
            roadManager.addRequestOption("vehicle=car");
        else roadManager.addRequestOption("vehicle=foot");


        Road road = roadManager.getRoad(roadPoints);
        Polyline route;
        if (road.mLength == 0) {
            distanceTo = getDistanceOffline(start, end);
            timeTo = 99999.0;
            return "Route indisponible";
        }
        if (draw) {
            if (car.isSelected())
                route = RoadManager.buildRoadOverlay(road, getApplicationContext().getResources().getColor(R.color.taxi), 10.0f);
            else
                route = RoadManager.buildRoadOverlay(road, getApplicationContext().getResources().getColor(R.color.black), 10.0f);

            myMap.getOverlays().add(route);
        }
        String duration = format(road.mDuration / 60);
        String dist = format(road.mLength);
        String distanceTo = "km " + dist + " كم";
        String timeTo = "minutes " + duration + " دقيقة";
        return distanceTo + "\n" + timeTo;
    }

    void fetchRouteByMean(GeoPoint start, GeoPoint end, String mean, boolean draw) {
        ArrayList<GeoPoint> roadPoints = new ArrayList<>();
        getLocation();
        roadPoints.add((start));
        roadPoints.add(end);
        RoadManager roadManager = new GraphHopperRoadManager(graphhopperkey, false);
        if (mean.equals("car"))
            roadManager.addRequestOption("vehicle=car");
        else if (mean.equals("walk"))
            roadManager.addRequestOption("vehicle=foot");
        Road road = roadManager.getRoad(roadPoints);
        Polyline route;
        if (road.mLength == 0) {
            distanceTo = getDistanceOffline(start, end);
            timeTo = 99999.0;
            Toast.makeText(getApplicationContext(), "Route indisponible", Toast.LENGTH_LONG).show();
        }
        if (draw)
            if (mean.equals("car")) {
                route = RoadManager.buildRoadOverlay(road, getApplicationContext().getResources().getColor(R.color.taxi), 10.0f);
                myMap.getOverlays().add(route);
                addSource(start, Math.round(road.mDuration / 60) + " minutes");
                addDestination(end, Math.round(road.mDuration / 60) + " minutes");
            } else if (mean.equals("walk")) {
                route = RoadManager.buildRoadOverlay(road, getApplicationContext().getResources().getColor(R.color.green), 10.0f);
                myMap.getOverlays().add(route);
                addSource(start, Math.round(road.mDuration / 60) + " minutes");
                addDestination(end, Math.round(road.mDuration / 60) + " minutes");
            }


    }

    double fetchDistance(GeoPoint start, GeoPoint end) {
        ArrayList<GeoPoint> roadPoints = new ArrayList<>();
        getLocation();
        roadPoints.add((start));
        roadPoints.add(end);
        RoadManager roadManager = new GraphHopperRoadManager(graphhopperkey, false);
        roadManager.addRequestOption("vehicle=foot");

        Road road = roadManager.getRoad(roadPoints);
        if (road.mLength == 0) {
            return getDistanceOffline(start, end);
        }
        return road.mLength;

    }

    double fetchTime(GeoPoint start, GeoPoint end) {
        ArrayList<GeoPoint> roadPoints = new ArrayList<>();
        getLocation();
        roadPoints.add((start));
        roadPoints.add(end);
        RoadManager roadManager = new GraphHopperRoadManager(graphhopperkey, false);
        roadManager.addRequestOption("vehicle=foot");
        Road road = roadManager.getRoad(roadPoints);
        if (road.mLength == 0) {
            return 99999;
        }
        return road.mDuration / 60;

    }

    private double getDistanceOffline(GeoPoint currentLocation, GeoPoint targetedLocation) {
        float[] distance = new float[2];
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(),
                targetedLocation.getLatitude(), targetedLocation.getLongitude(), distance);
        return distance[0];
    }

    public void addStationTramway(MapView mapMarker, GeoPoint positionMarker, String nomFrMarker) {
        Marker marker = new Marker(mapMarker);
        marker.setPosition(positionMarker);
        marker.setAnchor(ANCHOR_CENTER, ANCHOR_BOTTOM);
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
            marker1.setPanToView(true);
//            mapView.getController().setCenter(marker1.getPosition());
//            mapView.getController().setZoom(16.0);
            return true;
        });
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
            if (stationsBus.get(i).numero.contains(name)) {
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
        marker.setAnchor(ANCHOR_CENTER, ANCHOR_BOTTOM);
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

    //Clean Map from all markers and polylines
    private void clearMap() {
        myMap.getOverlays().clear();
        myMap.getOverlays().add(mLocationOverlay);
        myMap.getOverlays().add(mRotationGestureOverlay);
        myMap.getOverlays().add(echelle);
        myMap.getOverlays().add(mapEventsOverlay);
//        myMap.getOverlays().add(draggableMarker);
        myMap.invalidate();
        bus3_click = bus11_click = bus16_click = bus17_click = bus22_click = bus25_click = bus27_click = tramway_click = 1;
    }

    //Formatting the values to #.##
    static String format(double number) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(number);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        arreterLocalisation();
    }

    @Override
    public void onResume() {
        super.onResume();
        myMap.onResume();
        mLocationOverlay.enableMyLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
        myMap.onPause();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Fermer l'application")
                .setMessage("Voulez-vous vraiment fermer l'application?")
                .setPositiveButton("Oui", (dialog, which) -> super.onBackPressed())
                .setNegativeButton("Non", null)
                .show();
    }

    private void getRouteTramway() {
        Request request1 = new Request.Builder().url(urlRouteTramway).build();
        client.newCall(request1).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    insertRouteTramway(response);
                }
            }
        });
    }

    private void getRouteBus() {
        Request request = new Request.Builder().url(urlRouteBus).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    insertRouteBus(response);
                }
            }
        });
    }

    private void getRouteCorrespondance() {
        Request request = new Request.Builder().url(urlCorrespondance).build();
        client.newCall(request).
                enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            insertRouteCorrespondance(response);
                        }
                    }
                });
    }

    private void getStations() {
        Request request = new Request.Builder().url(urlStations).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    insertAllStations(response);
                    Log.d("Insertion_Station", "success");
                }
            }
        });
    }

    private void getMatrice() {
        Request request = new Request.Builder().url(urlMatrice).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    insertAllMatrice(response);
                }
            }
        });
    }

    private void getBestRoute(String adresse, int r, int g, int b) {
        Request request = new Request.Builder().url(adresse).build();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        insertBestRoute(response, r, g, b);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        });
    }

    public int removeAfter(String a) {
        int res;
        String b = a.substring(a.indexOf("_") + 1);
        res = Integer.parseInt(b);
        return res;
    }

    public String removeFromStart(String a) {
        String b = a.substring(0, 3);
        return b;
    }

    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}