package com.esi.navigator_22;

import static org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM;
import static org.osmdroid.views.overlay.Marker.ANCHOR_CENTER;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    String adresse = "http://192.168.43.119:3002/";
    //    String adresse = "https://routing22.herokuapp.com/";
    String urlStations = adresse + "stations_sba";
    String urlRouteTramway = adresse + "subway";
    String urlRouteBus = adresse + "bus";
    String urlCorrespondance = adresse + "correspondance";
    String urlMatrice = adresse + "matrice";
    String urlBestRoute = adresse + "getbeststation4/merges/Sidi%20Lahcene%201&A11_11/Sidi%20Brahim,Terminus%20A22&A22_07";
    static String graphhopperkey = "9590df1e-f158-492f-a5cb-d3e6ca11760f";
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ProgressDialog barProgressDialog;


    private String myResponse;

    public static MapView myMap;
    ScaleBarOverlay echelle;
    MyLocationNewOverlay mLocationOverlay;
    RotationGestureOverlay mRotationGestureOverlay;
    MapEventsOverlay mapEventsOverlay;

    ImageView currentPosition, reset;
    RelativeLayout menu_linear, navigationSearchViews;
    ImageView tramway, bus3, bus11, bus16, bus17, bus25, bus27, bus22;
    ImageView walk, car;
    ImageView mean_walk, mean_car, mean_bus, mean_tram, the_best_time, the_best_distance, ok_marker;
    ImageButton close;
    Button start;
    ListView searchStations, navigationSource, navigationDestination;
    NavigationView navigationView;
    TextView from_to, walk_duration, total_duration;
    LinearLayout mBottomSheetLayout;

    Station station = new Station();
    MatriceLine ligne = new MatriceLine(new Station("type", "nom", "numero", new GeoPoint(0.0, 0.0)), new Station("type", "nom", "numero", new GeoPoint(0.0, 0.0)), 0.0, 0.0);
    GeoPoint defaultLocation = new GeoPoint(35.19115853846664, -0.6298066051152207);
    static GeoPoint currentLocation = new GeoPoint(0.0, 0.0);
    GeoPoint srcCoord = new GeoPoint(0.0, 0.0);
    GeoPoint dstCoord = new GeoPoint(0.0, 0.0);
    String srcNumber = "", dstNumber = "";
    String srcName = "", dstName = "";
    Station srcStation = new Station("type", "nom", "numero", new GeoPoint(0.0, 0.0));
    Station dstStation = new Station("type", "nom", "numero", new GeoPoint(0.0, 0.0));
    GeoPoint point = new GeoPoint(0.0, 0.0);

    DbHelper database = DbHelper.getInstance(this);
    public static ArrayList<RouteBus> routeBus = new ArrayList<>();
    public static ArrayList<GeoPoint> routeTramway = new ArrayList<>();
    public static ArrayList<GeoPoint> routeCorrespondance = new ArrayList<>();
    public static ArrayList<Station> allStations = new ArrayList<>();
    public static ArrayList<Station> stationsTramway = new ArrayList<>();
    public static ArrayList<Station> stationsBus = new ArrayList<>();
    public static ArrayList<Station> stationsBus3 = new ArrayList<>();
    public static ArrayList<Station> stationsBus3bis = new ArrayList<>();
    public static ArrayList<Station> stationsBus11 = new ArrayList<>();
    public static ArrayList<Station> stationsBus16 = new ArrayList<>();
    public static ArrayList<Station> stationsBus17 = new ArrayList<>();
    public static ArrayList<Station> stationsBus22 = new ArrayList<>();
    public static ArrayList<Station> stationsBus25 = new ArrayList<>();
    public static ArrayList<Station> stationsBus27 = new ArrayList<>();
    public static ArrayList<MatriceLine> matrice = new ArrayList<>();
    public static ArrayList<CustomOverlay> customOverlays = new ArrayList<>();
    ArrayList<GeoPoint> chemin = new ArrayList<>();
    ArrayList<RouteBus> cheminBus = new ArrayList<>();
    ArrayList<GeoPoint> bestRoute = new ArrayList<>();

    double minZ = 13.0;
    double maxZ = 19.0;
    double distanceTo, timeTo;
    double duration_foot = 0;
    double duration_all = 0;
    int durationfoot = 0, durationall = 0;
    String unitefoot = "", uniteall = "";
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
    int bus3_click = 1, bus11_click = 1, bus16_click = 1, bus17_click = 1, bus22_click = 1, bus25_click = 1, bus27_click = 1, tramway_click = 1;

    ArrayAdapter<String> arrayAdapter;
    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(7, TimeUnit.SECONDS) // connect timeout
            .writeTimeout(5, TimeUnit.MINUTES) // write timeout
            .readTimeout(5, TimeUnit.MINUTES) // read timeout
            .build();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ids_tramway = new int[]{R.id.station1, R.id.station2, R.id.station3, R.id.station4, R.id.station5, R.id.station6, R.id.station7, R.id.station8, R.id.station9, R.id.station10, R.id.station11, R.id.station12, R.id.station13, R.id.station14, R.id.station15, R.id.station16, R.id.station17, R.id.station18, R.id.station19, R.id.station20, R.id.station21, R.id.station22};
        ids_bus3 = new int[]{R.id.station_3_1, R.id.station_3_2, R.id.station_3_3, R.id.station_3_4, R.id.station_3_5, R.id.station_3_6, R.id.station_3_7, R.id.station_3_8, R.id.station_3_9, R.id.station_3_10, R.id.station_3_11, R.id.station_3_12, R.id.station_3_13, R.id.station_3_14, R.id.station_3_15};
        ids_bus3bis = new int[]{R.id.station_3bis_1, R.id.station_3bis_2, R.id.station_3bis_3, R.id.station_3bis_4, R.id.station_3bis_5, R.id.station_3bis_6, R.id.station_3bis_7, R.id.station_3bis_8, R.id.station_3bis_9};
        ids_bus11 = new int[]{R.id.station_11_1, R.id.station_11_2, R.id.station_11_3, R.id.station_11_4, R.id.station_11_5, R.id.station_11_6, R.id.station_11_7, R.id.station_11_8, R.id.station_11_9, R.id.station_11_10, R.id.station_11_11, R.id.station_11_12, R.id.station_11_13, R.id.station_11_14};
        ids_bus16 = new int[]{R.id.station_16_1, R.id.station_16_2, R.id.station_16_3, R.id.station_16_4, R.id.station_16_5, R.id.station_16_6, R.id.station_16_7, R.id.station_16_8, R.id.station_16_9, R.id.station_16_10, R.id.station_16_11, R.id.station_16_12, R.id.station_16_13, R.id.station_16_14, R.id.station_16_15, R.id.station_16_15};
        ids_bus17 = new int[]{R.id.station_17_1, R.id.station_17_2, R.id.station_17_3, R.id.station_17_4, R.id.station_17_5, R.id.station_17_6, R.id.station_17_7, R.id.station_17_8, R.id.station_17_9, R.id.station_17_10, R.id.station_17_11, R.id.station_17_12};
        ids_bus22 = new int[]{R.id.station_22_1, R.id.station_22_2, R.id.station_22_3, R.id.station_22_4, R.id.station_22_5, R.id.station_22_6, R.id.station_22_7, R.id.station_22_8,};
        ids_bus25 = new int[]{R.id.station_25_1, R.id.station_25_2, R.id.station_25_3, R.id.station_25_4, R.id.station_25_5, R.id.station_25_6, R.id.station_25_7, R.id.station_25_8, R.id.station_25_9, R.id.station_25_10};
        ids_bus27 = new int[]{R.id.station_27_1, R.id.station_27_2, R.id.station_27_3, R.id.station_27_4, R.id.station_27_5, R.id.station_27_6, R.id.station_27_7};

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
        bus22 = findViewById(R.id.bus_22);
        bus25 = findViewById(R.id.bus_25);
        bus27 = findViewById(R.id.bus_27);
        tramway = findViewById(R.id.tramway);
        car = findViewById(R.id.car);
        walk = findViewById(R.id.walk);
        FloatingActionButton floatingActionButton = findViewById(R.id.lignes);
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
        drawerLayout = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
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
                    Log.d("SoutenanceMap", "Done");
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

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
        } else {
            Toast.makeText(this, "GPS requis", Toast.LENGTH_LONG).show();
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1
            );
        }

        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()), myMap);

        Drawable currentDraw = ResourcesCompat.getDrawable(getResources(), R.drawable.person, null);
        Bitmap currentIcon = null;
        if (currentDraw != null)
            currentIcon = ((BitmapDrawable) currentDraw).getBitmap();
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


        mRotationGestureOverlay = new RotationGestureOverlay(myMap);
        mRotationGestureOverlay.setEnabled(true);
        myMap.getOverlays().add(mRotationGestureOverlay);

        myMap.setMultiTouchControls(true);

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
//        matrice = database.getAllLines();
        stationsBus3 = searchBusStationByNumber("A03_");
        stationsBus3bis = searchBusStationByNumber("A03bis_");
        stationsBus11 = searchBusStationByNumber("A11_");
        stationsBus16 = searchBusStationByNumber("A16_");
        stationsBus17 = searchBusStationByNumber("A17_");
        stationsBus22 = searchBusStationByNumber("A22_");
        stationsBus25 = searchBusStationByNumber("A25_");
        stationsBus27 = searchBusStationByNumber("A27_");
        Animation rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        Animation rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        Animation toLeft = AnimationUtils.loadAnimation(this, R.anim.horizental_to_left);
        Animation toRight = AnimationUtils.loadAnimation(this, R.anim.horizental_to_right);

        final int[] fab_click = {0};
        floatingActionButton.setOnClickListener(v -> {
//            getBestRoute(urlBestRoute);
            if (fab_click[0] == 0) {
                floatingActionButton.startAnimation(rotateOpen);
                menu_linear.startAnimation(toLeft);

                tramway.setVisibility(View.VISIBLE);
                bus3.setVisibility(View.VISIBLE);
                bus11.setVisibility(View.VISIBLE);
                bus16.setVisibility(View.VISIBLE);
                bus17.setVisibility(View.VISIBLE);
                bus22.setVisibility(View.VISIBLE);
                bus25.setVisibility(View.VISIBLE);
                bus27.setVisibility(View.VISIBLE);
                menu_linear.setVisibility(View.VISIBLE);
                fab_click[0]++;
            }
            //
            else if (fab_click[0] == 1) {
                floatingActionButton.startAnimation(rotateClose);
                menu_linear.startAnimation(toRight);


                tramway.setVisibility(View.GONE);
                bus3.setVisibility(View.GONE);
                bus11.setVisibility(View.GONE);
                bus16.setVisibility(View.GONE);
                bus17.setVisibility(View.GONE);
                bus22.setVisibility(View.GONE);
                bus25.setVisibility(View.GONE);
                bus27.setVisibility(View.GONE);
                menu_linear.setVisibility(View.GONE);
                fab_click[0]--;
            }
        });
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
        bus22.setOnClickListener(v -> {
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
        mean_walk.setOnClickListener(v -> {
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
        });
        mean_tram.setOnClickListener(v -> {
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
        });
        mean_bus.setOnClickListener(v -> {
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
        });
        mean_car.setOnClickListener(v -> {
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
        });
        the_best_time.setOnClickListener(v -> {
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
        });
        the_best_distance.setOnClickListener(v -> {
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
        });
        start.setOnClickListener(v -> {
            getLocation();
            addSource(srcStation.coordonnees, "Source");
            addDestination(dstStation.coordonnees, "Destination");

            /*
            fetchRouteByMean(srcCoord, dstCoord, "walk");
            navigation(srcCoord, dstCoord, "tramway", "time");
            navigation(srcCoord, dstCoord, "buses", "time");
            fetchRouteByMean(srcCoord, dstCoord, "car");
            navigation(srcCoord, dstCoord, "All", "time");
            navigation(srcCoord, dstCoord, "All", "distance");

             */

            if (mean_walk.isSelected())
                fetchRouteByMean(srcCoord, dstCoord, "walk");
            else if (mean_tram.isSelected())
                navigation(srcStation, dstStation, "tramway", "time");
            else if (mean_bus.isSelected())
//                    navigation(srcCoord, dstCoord, removeFromStart(srcNumber),"distance");
                navigation(srcStation, dstStation, "buses", "time");
            else if (mean_car.isSelected())
                fetchRouteByMean(srcCoord, dstCoord, "car");
            else if (the_best_time.isSelected())
                navigation(srcStation, dstStation, "All", "time");
//            else if (the_best_distance.isSelected())
//                navigation(srcCoord, dstCoord, "All", "distance");


        });

        close.setOnClickListener(v -> {
            navigationSearchViews.setVisibility(View.INVISIBLE);
            navigationDestination.setVisibility(View.INVISIBLE);
            navigationSource.setVisibility(View.INVISIBLE);
            mBottomSheetLayout.setVisibility(View.INVISIBLE);
        });

        ConstraintLayout constraint;

        LinearLayout linear_durations, linear_walk, linear_total;

        ImageView header_Arrow_Image;
        BottomSheetBehavior sheetBehavior;


        constraint = findViewById(R.id.constraint);
        linear_durations = findViewById(R.id.linear_durations);
        linear_walk = findViewById(R.id.linear_walk);
        linear_total = findViewById(R.id.linear_total);
        from_to = findViewById(R.id.from_to);
        walk_duration = findViewById(R.id.walk_duration);
        total_duration = findViewById(R.id.total_duration);

        mBottomSheetLayout = findViewById(R.id.bottom_sheet_layout);
        sheetBehavior = BottomSheetBehavior.from(mBottomSheetLayout);
        header_Arrow_Image = findViewById(R.id.bottom_sheet_arrow);
        mBottomSheetLayout.setVisibility(View.INVISIBLE);
        header_Arrow_Image.setOnClickListener(v -> {

            if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            else
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


        });
        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                header_Arrow_Image.setRotation(slideOffset * 180);
            }
        });
        preferences = PreferenceManager.getDefaultSharedPreferences(this);


        setUpList();
        initSearch();

        adresse = preferences.getString("serveur", "");
        urlStations = adresse + "stations_sba";
        urlRouteTramway = adresse + "subway";
        urlRouteBus = adresse + "bus";
        urlCorrespondance = adresse + "correspondance";
        urlMatrice = adresse + "matrice";
        barProgressDialog = new ProgressDialog(MainActivity.this);
        barProgressDialog.setTitle("Recupérations du meilleur chemin ...");
        barProgressDialog.setMessage("En cours ...");
        barProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(1);
        barProgressDialog.setCancelable(true);

    }

    public void navigation(Station src, Station dst, String mean, String criteria) {
        Log.d("KhratAll1", barProgressDialog.getProgress() + "Progress");
        barProgressDialog.show();
        ArrayList<Station> result = new ArrayList<>();
        if (src.coordonnees.equals(dst.coordonnees))
            Toast.makeText(getApplicationContext(), "0", Toast.LENGTH_SHORT).show();
        else {
            if (criteria.equals("time")) {
                if (((srcStation.type.equals("bus") || srcStation.type.equals("tramway")) && ((dstStation.type.equals("bus")) || dstStation.type.equals("tramway")))) {

                    if (mean.equals("tramway")) {
                        getBestRoute(adresse + "beetweenstations1/tram/" + src.nomFr + "&" + src.numero + "/" + dst.nomFr + "&" + dst.numero);
                        Log.d("SoutenanceURLTram", "http://localhost:3002/" + "beetweenstations1/tram/" + src.nomFr + "&" + src.numero + "/" + dst.nomFr + "&" + dst.numero);
                    } else if (mean.equals("buses")) {
                        getBestRoute(adresse + "beetweenstations1/bus/" + src.nomFr + "&" + src.numero + "/" + dst.nomFr + "&" + dst.numero);
                        Log.d("SoutenanceURLBus", "http://localhost:3002/" + "beetweenstations1/bus/" + src.nomFr + "&" + src.numero + "/" + dst.nomFr + "&" + dst.numero);
                    } else if (mean.equals("All")) {
                        Log.d("KhratAll2", barProgressDialog.getProgress() + "Progress");
                        getBestRoute(adresse + "beetweenstations1/all/" + src.nomFr + "&" + src.numero + "/" + dst.nomFr + "&" + dst.numero);

                        Log.d("KhratAll3", barProgressDialog.getProgress() + "Progress");
                        Log.d("SoutenanceURLAll", "http://localhost:3002/" + "beetweenstations1/all/" + src.nomFr + "&" + src.numero + "/" + dst.nomFr + "&" + dst.numero);
                    }
                } else {
                    if (mean.equals("tramway")) {
                        getBestRoute(adresse + "costum/tram/" + src.coordonnees.getLatitude() + "/" + src.coordonnees.getLongitude() + "/" + dst.coordonnees.getLatitude() + "/" + dst.coordonnees.getLongitude());
                        Log.d("SoutenanceURLTram", "http://localhost:3002/" + "costum/tram/" + src.coordonnees.getLatitude() + "/" + src.coordonnees.getLongitude() + "/" + dst.coordonnees.getLatitude() + "/" + dst.coordonnees.getLongitude());
                    } else if (mean.equals("buses")) {
                        getBestRoute(adresse + "costum/bus/" + src.coordonnees.getLatitude() + "/" + src.coordonnees.getLongitude() + "/" + dst.coordonnees.getLatitude() + "/" + dst.coordonnees.getLongitude());
                        Log.d("SoutenanceURLBus", "http://localhost:3002/" + "costum/bus/" + src.coordonnees.getLatitude() + "/" + src.coordonnees.getLongitude() + "/" + dst.coordonnees.getLatitude() + "/" + dst.coordonnees.getLongitude());
                    } else if (mean.equals("All")) {
                        getBestRoute(adresse + "costum/all/" + src.coordonnees.getLatitude() + "/" + src.coordonnees.getLongitude() + "/" + dst.coordonnees.getLatitude() + "/" + dst.coordonnees.getLongitude());
                        Log.d("SoutenanceURLAll", "http://localhost:3002/" + "costum/all/" + src.coordonnees.getLatitude() + "/" + src.coordonnees.getLongitude() + "/" + dst.coordonnees.getLatitude() + "/" + dst.coordonnees.getLongitude());
                    }
                }
            }
        }
        Log.d("KhratAll4", barProgressDialog.getProgress() + "Progress");

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
        } else if (item.getItemId() == R.id.adresse) {
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            editor = preferences.edit();
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            final EditText edittext = new EditText(getApplicationContext());
            edittext.setText(preferences.getString("serveur", ""));
            alert.setTitle("Configurer l'adresse IP");
            alert.setMessage("Saisir l'adresse IP");
            alert.setView(edittext);
            alert.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    editor.putString("serveur", String.valueOf(edittext.getText()));
                    editor.apply();
                    adresse = preferences.getString("serveur", "");
                    Log.d("SoutenanceAdresseDuServeur", String.valueOf(edittext.getText()));
                }
            });
            alert.show();

        } else if (item.getItemId() == R.id.infos) {
            Intent intent = new Intent(MainActivity.this, InfoActivity.class);
            MainActivity.this.startActivity(intent);
        } else {
            Log.d("Soutenance", "MenuNavigationError");
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
                        addStationBus(myMap, o.coordonnees, o.nomFr, o.numero);
                    } else {
                        chemin = routeTramway;
                        addStationTramway(myMap, o.coordonnees, o.nomFr);
                    }
                    myMap.getController().setCenter(o.coordonnees);
                    myMap.invalidate();
                    MainActivity.this.searchStations.setVisibility(View.INVISIBLE);
                    searchStations.setQuery(o.nomFr, true);
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
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                navigationSource.setVisibility(View.VISIBLE);
                searchStations.setVisibility(View.INVISIBLE);
                navigationDestination.setVisibility(View.INVISIBLE);
                getLocation();
                ArrayList<Station> stations = new ArrayList<>();
                stations.add(new Station("source", "Current location", "current", currentLocation));
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
                        tempSource[0].setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.marker_source));
                        myMap.getOverlays().add(tempSource[0]);
                        myMap.setMapListener(new MapListener() {
                            @Override
                            public boolean onScroll(ScrollEvent event) {

                                tempSource[0].setPosition(new GeoPoint((float) myMap.getMapCenter().getLatitude(),
                                        (float) myMap.getMapCenter().getLongitude()));
                                ok_marker.setOnClickListener(v -> {
                                    markerSource.setIcon(getApplicationContext().getDrawable(R.drawable.marker_source));
                                    markerSource.setPosition(tempSource[0].getPosition());
                                    o.coordonnees.setLatitude(markerSource.getPosition().getLatitude());
                                    o.coordonnees.setLongitude(markerSource.getPosition().getLongitude());
                                    myMap.getOverlays().add(markerSource);
                                    srcCoord = markerSource.getPosition();
                                    srcNumber = "source";
                                    srcName = "source";

                                    srcStation.coordonnees.setLatitude(markerSource.getPosition().getLatitude());
                                    srcStation.coordonnees.setLongitude(markerSource.getPosition().getLongitude());
                                    srcStation.numero = "source";
                                    srcStation.nomFr = "source";
                                    srcStation.type = "source";

                                    tempSource[0].setVisible(false);
                                    ok_marker.setVisibility(View.INVISIBLE);
                                });
                                return false;
                            }

                            @Override
                            public boolean onZoom(ZoomEvent event) {
                                return false;
                            }
                        });
                    } else {
                        srcStation.coordonnees.setLatitude(o.coordonnees.getLatitude());
                        srcStation.coordonnees.setLongitude(o.coordonnees.getLongitude());
                        srcStation.numero = o.numero;
                        srcStation.nomFr = o.nomFr;
                        srcStation.type = o.type;
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
                        temp_destination[0].setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.marker_destination));
                        myMap.getOverlays().add(temp_destination[0]);
                        myMap.setMapListener(new MapListener() {
                            @Override
                            public boolean onScroll(ScrollEvent event) {

                                temp_destination[0].setPosition(new GeoPoint((float) myMap.getMapCenter().getLatitude(),
                                        (float) myMap.getMapCenter().getLongitude()));
                                ok_marker.setOnClickListener(v -> {
                                    markerDestination.setIcon(getApplicationContext().getDrawable(R.drawable.marker_destination));
                                    markerDestination.setPosition(temp_destination[0].getPosition());
                                    o.coordonnees.setLatitude(markerDestination.getPosition().getLatitude());
                                    o.coordonnees.setLongitude(markerDestination.getPosition().getLongitude());
                                    myMap.getOverlays().add(markerDestination);
                                    dstCoord = markerDestination.getPosition();
                                    dstNumber = "destination";
                                    dstName = "destination";


                                    dstStation.coordonnees.setLatitude(markerDestination.getPosition().getLatitude());
                                    dstStation.coordonnees.setLongitude(markerDestination.getPosition().getLongitude());
                                    dstStation.numero = "destination";
                                    dstStation.nomFr = "destination";
                                    dstStation.type = "destination";

                                    temp_destination[0].setVisible(false);
                                    ok_marker.setVisibility(View.INVISIBLE);
                                });
                                return false;
                            }

                            @Override
                            public boolean onZoom(ZoomEvent event) {
                                return false;
                            }
                        });
                    } else {
                        dstStation.coordonnees.setLatitude(o.coordonnees.getLatitude());
                        dstStation.coordonnees.setLongitude(o.coordonnees.getLongitude());
                        dstStation.numero = o.numero;
                        dstStation.nomFr = o.nomFr;
                        dstStation.type = o.type;
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

        for (int i = 0; i < stationsTramway.size(); i++)
            tramwaySubMenu.add(R.id.tramway_stations, ids_tramway[i], 1, stationsTramway.get(i).nomFr);

        ligne = searchBusStationByNumber("A03_");
        for (int i = 0; i < ligne.size(); i++)
            ligne3.add(R.id.bus_stations_3, ids_bus3[i], 1, ligne.get(i).nomFr);

        ligne = searchBusStationByNumber("A03bis_");
        for (int i = 0; i < ligne.size(); i++)
            ligne3bis.add(R.id.bus_stations_3_bis, ids_bus3bis[i], 1, ligne.get(i).nomFr);

        ligne = searchBusStationByNumber("A11_");
        for (int i = 0; i < ligne.size(); i++)
            ligne11.add(R.id.bus_stations_11, ids_bus11[i], 1, ligne.get(i).nomFr);

        ligne = searchBusStationByNumber("A16_");
        for (int i = 0; i < ligne.size(); i++)
            ligne16.add(R.id.bus_stations_16, ids_bus16[i], 1, ligne.get(i).nomFr);

        ligne = searchBusStationByNumber("A17_");
        for (int i = 0; i < ligne.size(); i++)
            ligne17.add(R.id.bus_stations_17, ids_bus17[i], 1, ligne.get(i).nomFr);

        ligne = searchBusStationByNumber("A22_");
        for (int i = 0; i < ligne.size(); i++)
            ligne22.add(R.id.bus_stations_22, ids_bus22[i], 1, ligne.get(i).nomFr);

        ligne = searchBusStationByNumber("A25_");
        for (int i = 0; i < ligne.size(); i++)
            ligne25.add(R.id.bus_stations_25, ids_bus25[i], 1, ligne.get(i).nomFr);

        ligne = searchBusStationByNumber("A27_");
        for (int i = 0; i < ligne.size(); i++)
            ligne27.add(R.id.bus_stations_27, ids_bus27[i], 1, ligne.get(i).nomFr);


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
            while (verify != 0 && parcours < stationsTramway.size())
                if (i == ids_tramway[parcours]) {
                    addMarker(myMap, stationsTramway.get(parcours).coordonnees, stationsTramway.get(parcours).nomFr, "tramway");
                    verify = 0;
                } else
                    parcours++;
        }
        //
        else if (item.getGroupId() == R.id.bus_stations_3)
            ItemsMenuBus(item, stationsBus, ids_bus3);
        else if (item.getGroupId() == R.id.bus_stations_3_bis)
            ItemsMenuBus(item, stationsBus3bis, ids_bus3bis);
        else if (item.getGroupId() == R.id.bus_stations_11)
            ItemsMenuBus(item, stationsBus11, ids_bus11);
        else if (item.getGroupId() == R.id.bus_stations_16)
            ItemsMenuBus(item, stationsBus16, ids_bus16);
        else if (item.getGroupId() == R.id.bus_stations_17)
            ItemsMenuBus(item, stationsBus17, ids_bus17);
        else if (item.getGroupId() == R.id.bus_stations_22)
            ItemsMenuBus(item, stationsBus22, ids_bus22);
        else if (item.getGroupId() == R.id.bus_stations_25)
            ItemsMenuBus(item, stationsBus25, ids_bus25);
        else if (item.getGroupId() == R.id.bus_stations_27)
            ItemsMenuBus(item, stationsBus27, ids_bus27);
        return super.onOptionsItemSelected(item);
    }

    void ItemsMenuBus(MenuItem item, ArrayList<Station> list, int[] ids) {
        int verify = 1;
        int parcours = 0;
        while (verify != 0 && parcours < list.size())
            if (item.getItemId() == ids[parcours]) {
                addMarker(myMap, list.get(parcours).coordonnees, list.get(parcours).nomFr + " " + removeFromStart(list.get(parcours).numero), removeFromStart(list.get(parcours).numero));
                verify = 0;
            } else
                parcours++;
    }

    //Get current location
    public void getLocation() {
        if (mLocationOverlay.getMyLocation() != null)
            currentLocation = mLocationOverlay.getMyLocation();
        else {
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

    //Get best merged route
    private void getRoute(Response response) throws IOException {
        Log.d("SoutenanceStartBestRoute", java.util.Calendar.getInstance().getTime() + "");
        Polyline a = new Polyline();
        myResponse = Objects.requireNonNull(response.body()).string();
        GeoPoint temp = new GeoPoint(0.0, 0.0);
        JSONArray jsonarray = null;
        try {
            jsonarray = new JSONArray(myResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < Objects.requireNonNull(jsonarray).length(); i++) {
            ArrayList<GeoPoint> listPoly = new ArrayList();
            ArrayList<String> listStationsFoot = new ArrayList();
            ArrayList<String> listStationsFootFrom = new ArrayList();
            ArrayList<String> listStationsVehicle = new ArrayList();
            JSONObject jsonobject;
            try {
                jsonobject = jsonarray.getJSONObject(i);
                //Vehicle
                JSONArray vehicle = jsonobject.getJSONArray("vehicle");
                for (int j = 0; j < Objects.requireNonNull(vehicle).length(); j++) {
                    JSONObject obj = vehicle.getJSONObject(j);
                    //poly
                    JSONArray poly = obj.getJSONArray("poly");
                    for (int l = 0; l < Objects.requireNonNull(poly).length(); l++) {
                        JSONObject obj2 = poly.getJSONObject(l);
                        JSONArray coordinates = obj2.getJSONArray("coordinates");
                        for (int k = 0; k < Objects.requireNonNull(coordinates).length(); k++) {
                            JSONArray obj3 = coordinates.getJSONArray(k);
                            temp = new GeoPoint(Double.parseDouble(obj3.toString().substring(obj3.toString().indexOf(",") + 1, obj3.toString().length() - 1)), Double.parseDouble(obj3.toString().substring(1, obj3.toString().indexOf(","))));
                            listPoly.add(temp);
                        }
                        a = new Polyline();
                        a.setWidth(10);
                        a.setColor(Color.rgb(0, 0, 0));
                        a.setDensityMultiplier(0.5f);
                        a.setPoints(listPoly);
                        myMap.getOverlays().add(a);
                        listPoly.clear();
                        myMap.invalidate();
                    }
                    //stations
                    JSONArray stations = obj.getJSONArray("stations");
                    for (int l = 0; l < Objects.requireNonNull(stations).length(); l++) {
                        JSONObject obj2 = stations.getJSONObject(l);
                        String num = obj2.getString("numero");
                        if (num.equals("t00")) num = srcStation.numero;
                        if (num.equals("t01")) num = dstStation.numero;
                        listStationsVehicle.add(num);
                    }
                }
                //duration_foot

                duration_foot = jsonobject.getDouble("duration_foot");
                duration_all = jsonobject.getDouble("duration_all");

                if (duration_foot < 60) {
                    durationfoot = (int) Math.round(duration_foot);
                    unitefoot = " secondes";
                } else {
                    durationfoot = (int) Math.round(duration_foot / 60);
                    unitefoot = " minutes";
                }

                if (duration_all < 60) {
                    durationall = (int) Math.round(duration_all);
                    uniteall = " secondes";
                } else {
                    durationall = (int) Math.round(duration_all / 60);
                    uniteall = " minutes";
                }


                //foot
                JSONArray foot = jsonobject.getJSONArray("foot");
                for (int j = 0; j < Objects.requireNonNull(foot).length(); j++) {
                    JSONObject obj = foot.getJSONObject(j);

                    //poly_foot
                    JSONObject poly_foot = obj.getJSONObject("poly_foot");
                    //coordinates
                    JSONArray coordinates = poly_foot.getJSONArray("coordinates");
                    for (int k = 0; k < Objects.requireNonNull(coordinates).length(); k++) {
                        JSONArray coordinate = coordinates.getJSONArray(k);
                        temp = new GeoPoint(Double.parseDouble(coordinate.toString().substring(coordinate.toString().indexOf(",") + 1, coordinate.toString().length() - 1)), Double.parseDouble(coordinate.toString().substring(1, coordinate.toString().indexOf(","))));
                        listPoly.add(temp);
                    }
                    a = new Polyline();
                    a.setWidth(10);
                    a.setColor(Color.rgb(255, 0, 0));
                    a.setDensityMultiplier(0.5f);
                    a.setPoints(listPoly);
                    myMap.getOverlays().add(a);
                    listPoly.clear();
                    myMap.invalidate();

                    //stations
                    String from = obj.getString("num_from");
                    String to = obj.getString("num_to");
                    if (from.equals("t00")) from = srcStation.numero;
                    if (to.equals("t01")) to = dstStation.numero;
                    listStationsFoot.add(from);
                    listStationsFoot.add(to);
                    listStationsFootFrom.add(from);
                    double duration = obj.getDouble("duration");
                    String unite = "";
                    int durationBetween = 0;
                    if (duration < 60) {
                        durationBetween = (int) Math.round(duration);
                        unite = " secondes";
                    } else {
                        durationBetween = (int) Math.round(duration / 60);
                        unite = " minutes";
                    }

                    Marker f = new Marker(myMap);
                    f.setPosition(fromNumtoStation(from).coordonnees);
                    if (from.equals(to)) {
                    } else {
                        if (fromNumtoStation(from).type.equals("tramway")) {

                            if (fromNumtoStation(to).type.equals("tramway"))
                                f.setTitle("Correspondance Tramway, From " + fromNumtoStation(from).nomFr + " To " + fromNumtoStation(to).nomFr + "\n" + "Walk : " + durationBetween + unite);
                            else if (fromNumtoStation(to).type.equals("bus"))
                                f.setTitle("From " + fromNumtoStation(from).nomFr + " of Tramway to " + fromNumtoStation(to).nomFr + " of Bus " + removeBeforeDash(fromNumtoStation(to).numero) + "\n" + "Walk : " + durationBetween + unite);
                            else if (fromNumtoStation(to).type.equals("source"))
                                f.setTitle("From " + fromNumtoStation(from).nomFr + " of Tramway to Source" + "\n" + "Walk : " + durationBetween + unite);
                            else if (fromNumtoStation(to).type.equals("destination"))
                                f.setTitle("From " + fromNumtoStation(from).nomFr + " of Tramway to Destination" + "\n" + "Walk : " + durationBetween + unite);
                            else if (fromNumtoStation(to).type.equals("current"))
                                f.setTitle("From " + fromNumtoStation(from).nomFr + " of Tramway to Your Position" + "\n" + "Walk : " + durationBetween + unite);

                        } else if (fromNumtoStation(from).type.equals("bus")) {

                            if (fromNumtoStation(to).type.equals("tramway"))
                                f.setTitle("From " + fromNumtoStation(from).nomFr + " of Bus " + removeBeforeDash(fromNumtoStation(from).numero) + " to " + fromNumtoStation(to).nomFr + " of Tramway" + "\n" + "Walk : " + durationBetween + unite);
                            else if (fromNumtoStation(to).type.equals("bus"))
                                f.setTitle("From " + fromNumtoStation(from).nomFr + " of Bus " + removeBeforeDash(fromNumtoStation(from).numero) + " to " + fromNumtoStation(to).nomFr + " of Bus " + removeBeforeDash(fromNumtoStation(to).numero) + "\n" + "Walk : " + durationBetween + unite);
                            else if (fromNumtoStation(to).type.equals("source"))
                                f.setTitle("From " + fromNumtoStation(from).nomFr + " of Bus " + removeBeforeDash(fromNumtoStation(from).numero) + " to Source" + "\n" + "Walk : " + durationBetween + unite);
                            else if (fromNumtoStation(to).type.equals("destination"))
                                f.setTitle("From " + fromNumtoStation(from).nomFr + " of Bus " + removeBeforeDash(fromNumtoStation(from).numero) + " to Destination" + "\n" + "Walk : " + durationBetween + unite);
                            else if (fromNumtoStation(to).type.equals("current"))
                                f.setTitle("From " + fromNumtoStation(from).nomFr + " of Bus " + removeBeforeDash(fromNumtoStation(from).numero) + " to Your Position" + "\n" + "Walk : " + durationBetween + unite);

                        } else if (fromNumtoStation(from).type.equals("current")) {
                            if (fromNumtoStation(to).type.equals("tramway"))
                                f.setTitle("From Your Position to " + fromNumtoStation(to).nomFr + " of Tramway" + "\n" + "Walk : " + durationBetween + unite);
                            else if (fromNumtoStation(to).type.equals("bus"))
                                f.setTitle("From Your Position to " + fromNumtoStation(to).nomFr + " of Bus " + removeBeforeDash(fromNumtoStation(to).numero) + "\n" + "Walk : " + durationBetween + unite);
                            else if (fromNumtoStation(to).type.equals("destination"))
                                f.setTitle("From Your Position to Destination" + "\n" + "Walk : " + durationBetween + unite);
                            else if (fromNumtoStation(to).type.equals("source"))
                                f.setTitle("From Your Position to Source" + "\n" + "Walk : " + durationBetween + unite);
//                            else if (fromNumtoStation(to).type.equals("current")) ;
                        } else if (fromNumtoStation(from).type.equals("source")) {
                            if (fromNumtoStation(to).type.equals("tramway"))
                                f.setTitle("From Source to " + fromNumtoStation(to).nomFr + " of Tramway" + "\n" + "Walk : " + durationBetween + unite);
                            else if (fromNumtoStation(to).type.equals("bus"))
                                f.setTitle("From Source to " + fromNumtoStation(to).nomFr + " of Bus " + removeBeforeDash(fromNumtoStation(to).numero) + "\n" + "Walk : " + durationBetween + unite);
                            else if (fromNumtoStation(to).type.equals("destination"))
                                f.setTitle("From Source to Destination" + "\n" + "Walk : " + durationBetween + unite);
                            else if (fromNumtoStation(to).type.equals("source"))
                                f.setTitle("From Source to Source" + "\n" + "Walk : " + durationBetween + unite);
                            else if (fromNumtoStation(to).type.equals("current"))
                                f.setTitle("From Source to Your Position" + "\n" + "Walk : " + durationBetween + unite);
                        } else if (fromNumtoStation(from).type.equals("destination")) {
                            if (fromNumtoStation(to).type.equals("tramway"))
                                f.setTitle("From Destination to " + fromNumtoStation(to).nomFr + " of Tramway" + "\n" + "Walk : " + durationBetween + unite);
                            else if (fromNumtoStation(to).type.equals("bus"))
                                f.setTitle("From Destination to " + fromNumtoStation(to).nomFr + " of Bus " + removeBeforeDash(fromNumtoStation(to).numero) + "\n" + "Walk : " + durationBetween + unite);
                            else if (fromNumtoStation(to).type.equals("destination"))
                                f.setTitle("From Destination to Destination" + "\n" + "Walk : " + durationBetween + unite);
                            else if (fromNumtoStation(to).type.equals("source"))
                                f.setTitle("From Destination to Source" + "\n" + "Walk : " + durationBetween + unite);
                            else if (fromNumtoStation(to).type.equals("current"))
                                f.setTitle("From Destination to Your Position" + "\n" + "Walk : " + durationBetween + unite);
                        }
                        myMap.getOverlays().add(f);
                        myMap.invalidate();
                    }
                }
                ArrayList<String> copy = new ArrayList<>();
                copy.addAll(listStationsVehicle);
                for (int c = 0; c < copy.size(); c++)
                    for (int d = 0; d < listStationsFootFrom.size(); d++)
                        if (copy.get(c).equals(listStationsFootFrom.get(d)))
                            listStationsVehicle.remove(copy.get(c));


                for (int c = 0; c < listStationsVehicle.size(); c++)
                    addStationsMarker(myMap, fromNumtoStation(listStationsVehicle.get(c)));


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d("SoutenanceEndBestRoute", java.util.Calendar.getInstance().getTime() + "");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBottomSheetLayout.setVisibility(View.VISIBLE);
                from_to.setText(srcStation.nomFr + " to " + dstStation.nomFr);
                walk_duration.setText(durationfoot + unitefoot);
                total_duration.setText(durationall + uniteall);
            }
        });
    }

    private Station fromNumtoStation(String numero) {
        Station a = new Station();
        ArrayList<Station> copy = new ArrayList<>();
        copy.addAll(allStations);
        copy.add(new Station("current", "Current location", "current", currentLocation));
        copy.add(new Station("destination", "Custom destination", "destination", dstStation.coordonnees));
        copy.add(new Station("source", "Custom source", "source", srcStation.coordonnees));

        for (int i = 0; i < copy.size(); i++)
            if (copy.get(i).numero.equals(numero))
                a = copy.get(i);
        return a;
    }

    private void addStationsMarker(MapView mapMarker, Station station) {
        Marker marker = new Marker(mapMarker);
        marker.setPosition(station.coordonnees);
        marker.setAnchor(ANCHOR_CENTER, ANCHOR_BOTTOM);
        marker.setAlpha(0.8f);
        marker.setTitle(station.nomFr);
        marker.setPanToView(true);

        if (station.type.equals("tramway"))
            marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin_tramway));
        else if (station.type.equals("bus")) {
            switch (removeFromStart(station.numero)) {
                case "A03":
                    marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin_bus_03));
                    break;
                case "A11":
                    marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin_bus_11));
                    break;
                case "A16":
                    marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin_bus_16));
                    break;
                case "A17":
                    marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin_bus_17));
                    break;
                case "A22":
                    marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin_bus_22));
                    break;
                case "A25":
                    marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin_bus_25));
                    break;
                case "A27":
                    marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin_bus_27));
                    break;
            }
        }


        mapMarker.invalidate();
        mapMarker.getOverlays().add(marker);
        customOverlays.add(customOverlays.size(), new CustomOverlay("bestroute", marker));
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
            Log.d("SoutenanceStationInserted", station.toString());
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
        JSONObject result = null;

        try {
            result = new JSONObject(myResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assert result != null;
        JSONArray datas = result.getJSONArray("datas");
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

    //                  Tramway Overlays
    private void addStationsTramway() {
        for (int i = 0; i < stationsTramway.size(); i++)
            addStationTramway(myMap, stationsTramway.get(i).coordonnees, stationsTramway.get(i).nomFr);
    }

    public void addStationTramway(MapView mapMarker, GeoPoint positionMarker, String nomFrMarker) {
        Marker marker = new Marker(mapMarker);
        marker.setPosition(positionMarker);
        marker.setAnchor(ANCHOR_CENTER, ANCHOR_BOTTOM);
        marker.setAlpha(0.8f);
        marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin_tramway));
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
            return true;
        });
    }

    private void tracerCheminTramway(ArrayList<GeoPoint> chemin, MapView mapView) {
        Polyline line = new Polyline();
        line.setWidth(8);
//        line.getOutlinePaint();
        line.setColor(Color.rgb(230, 138, 0));
        line.setDensityMultiplier(0.1f);
        line.setGeodesic(true);
        line.setPoints(chemin);
        mapView.getOverlayManager().add(line);
        customOverlays.add(customOverlays.size(), new CustomOverlay("tramway", line));
    }

    private void tracerCorrespondance(ArrayList<GeoPoint> chemin, MapView mapView) {
        Polyline polyline_correspondance = new Polyline();
        polyline_correspondance.setWidth(12);
        polyline_correspondance.setColor(Color.rgb(0, 0, 0));
        polyline_correspondance.setDensityMultiplier(0.9f);
        polyline_correspondance.setGeodesic(true);
        polyline_correspondance.setPoints(chemin);
        mapView.getOverlayManager().add(polyline_correspondance);
        customOverlays.add(customOverlays.size(), new CustomOverlay("correspondance", polyline_correspondance));
    }

    //                  Bus
    ArrayList<RouteBus> searchBusRouteByNumber(String name) {
        ArrayList<RouteBus> result = new ArrayList<>();
        for (int i = 0; i < routeBus.size(); i++)
            if (routeBus.get(i).numLigne.equals(name))
                result.add(routeBus.get(i));
        return result;
    }

    void addBus(ArrayList<RouteBus> chemin, MapView mapView, int red, int green, int blue, String numero) {
        tracerCheminBus(chemin, mapView, red, green, blue, numero);
        addBusStationByNumber(numero);
    }

    void tracerCheminBus(ArrayList<RouteBus> chemin, MapView mapView, int red, int green, int blue, String numero) {
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

    void addBusStationByNumber(String name) {
        ArrayList<Station> busStations = searchBusStationByNumber(name);
        for (int i = 0; i < busStations.size(); i++)
            addStationBus(myMap, busStations.get(i).coordonnees, busStations.get(i).nomFr, busStations.get(i).numero);
    }

    ArrayList<Station> searchBusStationByNumber(String name) {
        ArrayList<Station> result = new ArrayList<>();
        for (int i = 0; i < stationsBus.size(); i++)
            if (stationsBus.get(i).numero.contains(name))
                result.add(stationsBus.get(i));
        return result;
    }

    void addStationBus(MapView mapMarker, GeoPoint positionMarker, String nomFr, String numLigne) {
        Marker marker = new Marker(mapMarker);
        marker.setPosition(positionMarker);
        marker.setAnchor(ANCHOR_CENTER, ANCHOR_BOTTOM);
        switch (removeFromStart(numLigne)) {
            case "A03":
                marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin_bus_03));
                break;
            case "A11":
                marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin_bus_11));
                break;
            case "A16":
                marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin_bus_16));
                break;
            case "A17":
                marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin_bus_17));
                break;
            case "A22":
                marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin_bus_22));
                break;
            case "A25":
                marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin_bus_25));
                break;
            case "A27":
                marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin_bus_27));
                break;
        }
        marker.setTitle(nomFr + " " + removeFromStart(numLigne));
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

    //                  Markers
    public void addMarker(MapView mapMarker, GeoPoint positionMarker, String nom, String numero) {
        Marker marker = new Marker(mapMarker);
        marker.setPosition(positionMarker);
        marker.setAnchor(ANCHOR_CENTER, ANCHOR_BOTTOM);
        marker.setDraggable(false);
        switch (numero) {
            case "tramway":
                marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin_tramway));
                break;
            case "A03":
                marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin_bus_03));
                break;
            case "A11":
                marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin_bus_11));
                break;
            case "A16":
                marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin_bus_16));
                break;
            case "A17":
                marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin_bus_17));
                break;
            case "A22":
                marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin_bus_22));
                break;
            case "A25":
                marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin_bus_25));
                break;
            case "A27":
                marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin_bus_27));
                break;
        }
        marker.setPanToView(true);
        mapMarker.getOverlays().add(marker);
        mapMarker.invalidate();
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
            marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin_tramway));
        else marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin_bus));

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
    }

    void addSource(GeoPoint coordinates, String nom) {
        Marker marker = new Marker(myMap);
        marker.setPosition(coordinates);
        marker.setDraggable(false);
        marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.marker_source));
        myMap.getOverlays().add(marker);
        myMap.invalidate();
        customOverlays.add(new CustomOverlay("source", marker));
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
    }

    void addDestination(GeoPoint coordinates, String nom) {
        Marker marker = new Marker(myMap);
        marker.setPosition(coordinates);
        marker.setDraggable(false);
        marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.marker_destination));
        marker.setPanToView(true);
        myMap.getOverlays().add(marker);
        myMap.invalidate();
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
        roadPoints.add((start));
        roadPoints.add(end);
        RoadManager roadManager = new GraphHopperRoadManager(graphhopperkey, false);
        if (car.isSelected()) roadManager.addRequestOption("vehicle=car");
        else roadManager.addRequestOption("vehicle=foot");
        Road road = roadManager.getRoad(roadPoints);
        Polyline polyline;
        if (road.mLength == 0) {
            distanceTo = getDistanceOffline(start, end);
            timeTo = 99999.0;
            return "Route indisponible";
        }

        if (car.isSelected())
            polyline = RoadManager.buildRoadOverlay(road, getApplicationContext().getResources().getColor(R.color.taxi), 10.0f);
        else
            polyline = RoadManager.buildRoadOverlay(road, getApplicationContext().getResources().getColor(R.color.black), 10.0f);

        myMap.getOverlays().add(polyline);

        String duration = String.valueOf(Math.round(road.mDuration / 60));
        String dist = format(road.mLength);
        String distanceTo = " كم" + dist + " km";
        String timeTo = " دقيقة " + duration + "minutes";
        return distanceTo + "\n" + timeTo;
    }

    void fetchRouteByMean(GeoPoint start, GeoPoint end, String mean) {
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
        if (mean.equals("car")) {
            route = RoadManager.buildRoadOverlay(road, getApplicationContext().getResources().getColor(R.color.taxi), 10.0f);
            myMap.getOverlays().add(route);
            addSource(start, road.mLength + " kilometres");
            addDestination(end, Math.round(road.mDuration / 60) + " minutes");
        }
        //
        else if (mean.equals("walk")) {
            route = RoadManager.buildRoadOverlay(road, getApplicationContext().getResources().getColor(R.color.green), 10.0f);
            myMap.getOverlays().add(route);
            addSource(start, road.mLength + " kilometres");
            addDestination(end, Math.round(road.mDuration / 60) + " minutes");
        }
    }

    double fetchDistance(GeoPoint start, GeoPoint end) {
        ArrayList<GeoPoint> roadPoints = new ArrayList<>();
        roadPoints.add((start));
        roadPoints.add(end);
        RoadManager roadManager = new GraphHopperRoadManager(graphhopperkey, false);
        roadManager.addRequestOption("vehicle=foot");

        Road road = roadManager.getRoad(roadPoints);
        if (road.mLength == 0) return getDistanceOffline(start, end);
        return road.mLength;

    }

    double fetchTime(GeoPoint start, GeoPoint end) {
        ArrayList<GeoPoint> roadPoints = new ArrayList<>();
        roadPoints.add((start));
        roadPoints.add(end);
        RoadManager roadManager = new GraphHopperRoadManager(graphhopperkey, false);
        roadManager.addRequestOption("vehicle=foot");
        Road road = roadManager.getRoad(roadPoints);
        if (road.mLength == 0) return 99999;
        return road.mDuration / 60;
    }

    private double getDistanceOffline(GeoPoint currentLocation, GeoPoint targetedLocation) {
        float[] distance = new float[2];
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), targetedLocation.getLatitude(), targetedLocation.getLongitude(), distance);
        return distance[0];
    }

    //Clean Map from all markers and polylines
    private void clearMap() {
        myMap.getOverlays().clear();
        myMap.getOverlays().add(mLocationOverlay);
        myMap.getOverlays().add(mRotationGestureOverlay);
        myMap.getOverlays().add(echelle);
        myMap.getOverlays().add(mapEventsOverlay);
        InfoWindow.closeAllInfoWindowsOn(myMap);
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
                    Log.d("SoutenanceInsertionStations", "success");
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

    private void getBestRoute(String urlMerge) {
        Handler handler = new Handler(MainActivity.this.getMainLooper());

        Request request = new Request.Builder().url(urlMerge).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                barProgressDialog.dismiss();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Erreur de connexion", Toast.LENGTH_LONG).show();
                    }
                });
            }

//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    barProgressDialog.dismiss();
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Log.d("KhratHandler", barProgressDialog.getProgress()+"");
//                            try {
//                                Log.d("KhratHandler1", barProgressDialog.getProgress()+"");
//                                getRoute(response);
//                                Log.d("KhratHandler2", barProgressDialog.getProgress()+"");
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            // whatever you want to do on the main thread
//                        }
//                    });
//                    Log.d("Khrat3", java.util.Calendar.getInstance().getTime() + "");
//                }
//            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("Khrat47", barProgressDialog.getProgress() + "");
                    barProgressDialog.dismiss();
                    getRoute(response);
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
                }
            }
        });
    }

    public int price(ArrayList<String> path) {
        String temp1, temp2;
        int cost = 0;
        if (!path.get(0).contains("A")) cost += 30;
        else if (path.get(0).contains("A")) cost += 15;
        for (int i = 0; i < path.size() - 2; i++) {

            //Entre Tramway et Tramway
            if (!(path.get(i).contains("A")) && !(path.get(i + 1).contains("A"))) {
                cost += 0;
//                System.out.println(path.get(i) + ", " + path.get(i + 1) + ", " + cost);
            }
            //Entre Tramway et Bus
            else if (!(path.get(i).contains("A")) && path.get(i + 1).contains("A")) {
                cost += 15;
//                System.out.println(path.get(i) + ", " + path.get(i + 1) + ", " + cost);
            }
            //Entre Bus et Tramway
            else if (path.get(i).contains("A") && !(path.get(i + 1).contains("A"))) {
                cost += 30;
//                System.out.println(path.get(i) + ", " + path.get(i + 1) + ", " + cost);
            }
            //Entre Bus et Bus
            else if (path.get(i).contains("A") && path.get(i + 1).contains("A")) {
                temp1 = removeBeforeDash(path.get(i));
                temp2 = removeBeforeDash(path.get(i + 1));
                if (!temp1.equals(temp2)) cost += 15;
//                System.out.println(path.get(i) + ", " + path.get(i + 1) + ", " + cost);
            }
        }
        return cost;
    }

    public int removeAfterDash(String a) {
        return Integer.parseInt(a.substring(a.indexOf("_") + 1));
    }

    public String removeBeforeDash(String a) {
        return a.substring(0, a.indexOf("_"));
    }

    public String removeFromStart(String a) {
        return a.substring(0, 3);
    }

    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}