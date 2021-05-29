package com.esi.navigator_22;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TransportSba";
    private static final int DATABASE_VERSION = 1;


    private static DbHelper mInstance = null;
    private final Context mCxt;

    private static final String TABLE_CHEMIN_SUB = "chemin_sub";
    private static final String TABLE_CHEMIN_BUS = "chemin_bus";
    private static final String TABLE_STATIONS = "stations";
    private static final String TABLE_CORRESPONDANCE = "correspondance";

    private static final String TABLE_NEAREST_SUB_STATIONS = "nearest_sub_stations";

    public static DbHelper getInstance(Context ctx) {
        /**
         * use the application context as suggested by CommonsWare.
         * this will ensure that you dont accidentally leak an Activitys
         * context (see this article for more information:
         * http://android-developers.blogspot.nl/2009/01/avoiding-memory-leaks.html)
         */
        if (mInstance == null) {
            mInstance = new DbHelper(ctx);
        }
        return mInstance;
    }

    //    private static final String COLUMN_TIMESTAMP = "timestamp";
//    private static final String COLUMN_ID = "id";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";

    private static final String COLUMN_NOMFR = "nomFR";
    private static final String COLUMN_TYPE = "type";


    private static final String COLUMN_DISTANCE = "distanceTo";
    private static final String COLUMN_TIME = "timeTo";

    private static final String COLUMN_NUMERO = "numero";
    private static final String COLUMN_NUMLIGNE = "numLigne";

    private static final String CREATE_TABLE_Station = "CREATE TABLE "
            + TABLE_STATIONS + "("
            + COLUMN_TYPE + " TEXT ,"
            + COLUMN_NOMFR + " TEXT ,"
            + COLUMN_NUMERO + " TEXT ,"
            + COLUMN_LATITUDE + " REAL ,"
            + COLUMN_LONGITUDE + " REAL ,"
            + "PRIMARY KEY (" + COLUMN_NOMFR + " , " + COLUMN_NUMERO + " , " + COLUMN_TYPE + ")"
            + ")";

    private static final String CREATE_TABLE_CheminSub = "CREATE TABLE "
            + TABLE_CHEMIN_SUB + "("
            + COLUMN_LATITUDE + " REAL ,"
            + COLUMN_LONGITUDE + " REAL ,"
            + "PRIMARY KEY(" + COLUMN_LATITUDE + "," + COLUMN_LONGITUDE + ")"
            + ")";

    private static final String CREATE_TABLE_Correspondance = "CREATE TABLE "
            + TABLE_CORRESPONDANCE + "("
            + COLUMN_LATITUDE + " REAL ,"
            + COLUMN_LONGITUDE + " REAL ,"
            + "PRIMARY KEY(" + COLUMN_LATITUDE + "," + COLUMN_LONGITUDE + ")"
            + ")";

    private static final String CREATE_TABLE_CheminBus = "CREATE TABLE "
            + TABLE_CHEMIN_BUS + "("
            + COLUMN_NUMLIGNE + " TEXT ,"
            + COLUMN_LATITUDE + " REAL ,"
            + COLUMN_LONGITUDE + " REAL ,"
            + "PRIMARY KEY(" + COLUMN_LATITUDE + "," + COLUMN_LONGITUDE + "," + COLUMN_NUMLIGNE + ")"
            + ")";

    private static final String CREATE_TABLE_Closest_Stations = "CREATE TABLE "
            + TABLE_NEAREST_SUB_STATIONS + "("
            + COLUMN_TYPE + " TEXT ,"
            + COLUMN_NOMFR + " TEXT ,"
            + COLUMN_NUMERO + " TEXT ,"
            + COLUMN_DISTANCE + " REAL ,"
            + COLUMN_TIME + " REAL ,"
            + "PRIMARY KEY (" + COLUMN_NOMFR + " , " + COLUMN_NUMERO + " , " + COLUMN_TYPE + ")"
            + ")";

    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mCxt = context;

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_Station);
        sqLiteDatabase.execSQL(CREATE_TABLE_CheminSub);
        sqLiteDatabase.execSQL(CREATE_TABLE_Closest_Stations);
        sqLiteDatabase.execSQL(CREATE_TABLE_CheminBus);
        sqLiteDatabase.execSQL(CREATE_TABLE_Correspondance);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_STATIONS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CHEMIN_SUB);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NEAREST_SUB_STATIONS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CHEMIN_BUS);
        onCreate(sqLiteDatabase);
    }

    long deleteAllNearestSubwayStation() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long word_id = sqLiteDatabase.delete(TABLE_NEAREST_SUB_STATIONS, null, null);
        return word_id;

    }

    long addStation(Station station) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TYPE, station.type);
        contentValues.put(COLUMN_NOMFR, station.nomFr);
        contentValues.put(COLUMN_NUMERO, station.numero);
        contentValues.put(COLUMN_LATITUDE, station.coordonnees.getLatitude());
        contentValues.put(COLUMN_LONGITUDE, station.coordonnees.getLongitude());
        long word_id = db.insert(TABLE_STATIONS, null, contentValues);
        return word_id;
    }

    long addNearStation(StationDetails stationDetails) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TYPE, stationDetails.type);
        contentValues.put(COLUMN_NOMFR, stationDetails.nomFr);
        contentValues.put(COLUMN_NUMERO, stationDetails.numero);
        contentValues.put(COLUMN_DISTANCE, stationDetails.distanceTo);
        contentValues.put(COLUMN_TIME, stationDetails.timeTo);
        long word_id = db.insert(TABLE_NEAREST_SUB_STATIONS, null, contentValues);
        return word_id;
    }

    long addPointSub(GeoPoint point) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
//        contentValues.put(COLUMN_TIMESTAMP, pointChemin.timestamp);
//        contentValues.put(COLUMN_ID, pointChemin.id);
        contentValues.put(COLUMN_LATITUDE, point.getLatitude());
        contentValues.put(COLUMN_LONGITUDE, point.getLongitude());
        long word_id = db.insert(TABLE_CHEMIN_SUB, null, contentValues);
        return word_id;
    }

    long addPointCorrespondance(GeoPoint point) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_LATITUDE, point.getLatitude());
        contentValues.put(COLUMN_LONGITUDE, point.getLongitude());
        long word_id = db.insert(TABLE_CORRESPONDANCE, null, contentValues);
        return word_id;
    }

    long addPointBus(RouteBus point) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
//        contentValues.put(COLUMN_TIMESTAMP, pointChemin.timestamp);
        contentValues.put(COLUMN_NUMLIGNE, point.numLigne);
        contentValues.put(COLUMN_LATITUDE, point.coordinates.getLatitude());
        contentValues.put(COLUMN_LONGITUDE, point.coordinates.getLongitude());
        long word_id = db.insert(TABLE_CHEMIN_BUS, null, contentValues);
        return word_id;
    }

    public ArrayList<GeoPoint> getAllPointsSub() {
        ArrayList<GeoPoint> pointChemins = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CHEMIN_SUB;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                GeoPoint g = new GeoPoint(0.0, 0.0);
//                s.timestamp = c.getString((c.getColumnIndex(COLUMN_TIMESTAMP)));
//                s.id = c.getInt((c.getColumnIndex(COLUMN_ID)));
                g.setLatitude(c.getDouble((c.getColumnIndex(COLUMN_LATITUDE))));
                g.setLongitude(c.getDouble((c.getColumnIndex(COLUMN_LONGITUDE))));
                pointChemins.add(g);
            } while (c.moveToNext());
        }
        return pointChemins;
    }

    public ArrayList<GeoPoint> getAllPointsCorrespondance() {
        ArrayList<GeoPoint> pointChemins = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CORRESPONDANCE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                GeoPoint g = new GeoPoint(0.0, 0.0);
//                s.timestamp = c.getString((c.getColumnIndex(COLUMN_TIMESTAMP)));
//                s.id = c.getInt((c.getColumnIndex(COLUMN_ID)));
                g.setLatitude(c.getDouble((c.getColumnIndex(COLUMN_LATITUDE))));
                g.setLongitude(c.getDouble((c.getColumnIndex(COLUMN_LONGITUDE))));
                pointChemins.add(g);
            } while (c.moveToNext());
        }
        return pointChemins;
    }

    public ArrayList<GeoPoint> getAllPointsBusByNumber(String numero) {
        ArrayList<GeoPoint> pointCheminsBus = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CHEMIN_BUS + " WHERE " + COLUMN_NUMLIGNE + "= \"" + numero + "\"";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                GeoPoint g = new GeoPoint(0.0, 0.0);
                g.setLatitude(c.getDouble((c.getColumnIndex(COLUMN_LATITUDE))));
                g.setLongitude(c.getDouble((c.getColumnIndex(COLUMN_LONGITUDE))));
                pointCheminsBus.add(g);
            } while (c.moveToNext());
        }
        return pointCheminsBus;
    }

    ArrayList<Station> getAllStations() {
        ArrayList<Station> stations = new ArrayList<Station>();
        String selectQuery = "SELECT * FROM " + TABLE_STATIONS + " ORDER BY " + COLUMN_NOMFR;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                Station s = new Station();
                GeoPoint g = new GeoPoint(0.0, 0.0);
                s.type = c.getString(c.getColumnIndex(COLUMN_TYPE));
                s.nomFr = c.getString((c.getColumnIndex(COLUMN_NOMFR)));
                s.numero = c.getString((c.getColumnIndex(COLUMN_NUMERO)));
                g.setLatitude(c.getDouble((c.getColumnIndex(COLUMN_LATITUDE))));
                g.setLongitude(c.getDouble((c.getColumnIndex(COLUMN_LONGITUDE))));
                s.coordonnees = g;
                stations.add(s);
            } while (c.moveToNext());
        }
        return stations;
    }

    ArrayList<Station> getAllTramwayStations() {
        ArrayList<Station> stations = new ArrayList<Station>();
        String selectQuery = "SELECT * FROM " + TABLE_STATIONS + " WHERE " + COLUMN_TYPE + " = 'tramway'" + " ORDER BY CAST(" + COLUMN_NUMERO + " AS INTEGER)";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                Station s = new Station();
                GeoPoint g = new GeoPoint(0.0, 0.0);
                s.type = c.getString(c.getColumnIndex(COLUMN_TYPE));
                s.nomFr = c.getString((c.getColumnIndex(COLUMN_NOMFR)));
                s.numero = c.getString((c.getColumnIndex(COLUMN_NUMERO)));
                g.setLatitude(c.getDouble((c.getColumnIndex(COLUMN_LATITUDE))));
                g.setLongitude(c.getDouble((c.getColumnIndex(COLUMN_LONGITUDE))));
                s.coordonnees = g;
                stations.add(s);
                Log.d("TramwayStation", s.toString());
            } while (c.moveToNext());
        }
        return stations;
    }


    ArrayList<Station> getAllBusStations() {
        ArrayList<Station> stations = new ArrayList<Station>();
        String selectQuery = "SELECT * FROM " + TABLE_STATIONS + " WHERE " + COLUMN_TYPE + " = 'bus'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                Station s = new Station();
                GeoPoint g = new GeoPoint(0.0, 0.0);
                s.type = c.getString(c.getColumnIndex(COLUMN_TYPE));
                s.nomFr = c.getString((c.getColumnIndex(COLUMN_NOMFR)));
                s.numero = c.getString((c.getColumnIndex(COLUMN_NUMERO)));
                g.setLatitude(c.getDouble((c.getColumnIndex(COLUMN_LATITUDE))));
                g.setLongitude(c.getDouble((c.getColumnIndex(COLUMN_LONGITUDE))));
                s.coordonnees = g;
                stations.add(s);
            } while (c.moveToNext());
        }
        return stations;
    }

    ArrayList<Station> getAllBusStationsByNumber(String numero) {
        ArrayList<Station> stations = new ArrayList<Station>();
        String selectQuery = "SELECT * FROM " + TABLE_STATIONS + " WHERE " + COLUMN_TYPE + " = 'bus' AND " + COLUMN_NUMERO + " = '" + numero + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                Station s = new Station();
                GeoPoint g = new GeoPoint(0.0, 0.0);
                s.type = c.getString(c.getColumnIndex(COLUMN_TYPE));
                s.nomFr = c.getString((c.getColumnIndex(COLUMN_NOMFR)));
                s.numero = c.getString((c.getColumnIndex(COLUMN_NUMERO)));
                g.setLatitude(c.getDouble((c.getColumnIndex(COLUMN_LATITUDE))));
                g.setLongitude(c.getDouble((c.getColumnIndex(COLUMN_LONGITUDE))));
                s.coordonnees = g;
                stations.add(s);
                Log.d("BusStation", s.toString());
            } while (c.moveToNext());
        }
        return stations;
    }


    public ArrayList<Station> getAllBusStationsByName(String name) {
        ArrayList<Station> stationsBus = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_STATIONS + " WHERE " + COLUMN_NOMFR + "= '" + name + "'" + " AND " + COLUMN_TYPE + " = 'bus'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                GeoPoint g = new GeoPoint(0.0, 0.0);
                Station station = new Station("A", "Name", g);
                g.setLatitude(c.getDouble((c.getColumnIndex(COLUMN_LATITUDE))));
                g.setLongitude(c.getDouble((c.getColumnIndex(COLUMN_LONGITUDE))));
                station.nomFr = c.getString(c.getColumnIndex(COLUMN_NOMFR));
                station.coordonnees = g;
                station.numero = c.getString(c.getColumnIndex(COLUMN_NUMERO));
                stationsBus.add(station);
                Log.d("SearchBusStationByName",station.toString());
            } while (c.moveToNext());
        }
        return stationsBus;
    }

    public ArrayList<Station> getAllStationsByName(String nomFr) {
        ArrayList<Station> stationsBus = new ArrayList<>();
//        String selectQuery = "SELECT * FROM " + TABLE_STATIONS + " WHERE " + COLUMN_NOMFR + "= \"" + nomFr + "\"";
        String selectQuery = "SELECT * FROM " + TABLE_STATIONS + " WHERE " + COLUMN_NOMFR + "like \"" + nomFr + "\"";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                GeoPoint g = new GeoPoint(0.0, 0.0);
                Station station = new Station("DefaultNumber", "DefaultName", g);
                g.setLatitude(c.getDouble((c.getColumnIndex(COLUMN_LATITUDE))));
                g.setLongitude(c.getDouble((c.getColumnIndex(COLUMN_LONGITUDE))));
                station.nomFr = c.getString(c.getColumnIndex(COLUMN_NOMFR));
                station.coordonnees = g;
                station.numero = c.getString(c.getColumnIndex(COLUMN_NUMERO));
                stationsBus.add(station);
                Log.d("SearchStationByName",station.toString());
            } while (c.moveToNext());
        }
        return stationsBus;
    }

    public ArrayList<StationDetails> getAllNearestSubStationsSortedByDistance() {
        ArrayList<StationDetails> stationDetails = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NEAREST_SUB_STATIONS + " ORDER BY " + COLUMN_DISTANCE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                StationDetails s = new StationDetails();
                s.numero = c.getString((c.getColumnIndex(COLUMN_NUMERO)));
                s.nomFr = c.getString((c.getColumnIndex(COLUMN_NOMFR)));
                s.distanceTo = c.getDouble((c.getColumnIndex(COLUMN_DISTANCE)));
                s.timeTo = c.getDouble((c.getColumnIndex(COLUMN_TIME)));
                stationDetails.add(s);
            } while (c.moveToNext());
        }

        return stationDetails;
    }

    public ArrayList<StationDetails> getAllNearestSubStationsSortedByTime() {
        ArrayList<StationDetails> stationDetails = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NEAREST_SUB_STATIONS + " ORDER BY " + COLUMN_TIME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                StationDetails s = new StationDetails();
                s.nomFr = c.getString((c.getColumnIndex(COLUMN_NOMFR)));
                s.distanceTo = c.getDouble((c.getColumnIndex(COLUMN_DISTANCE)));
                s.timeTo = c.getDouble((c.getColumnIndex(COLUMN_TIME)));
                stationDetails.add(s);
            } while (c.moveToNext());
        }

        return stationDetails;
    }

    /*public ArrayList<StationDetails> getAllNearestSubStations() {
        ArrayList<StationDetails> stationDetails = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NEAREST_SUB_STATIONS + " ORDER BY " + COLUMN_NUMERO;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                StationDetails s = new StationDetails();

                s.nomFr = c.getString((c.getColumnIndex(COLUMN_NOMFR)));
                s.numero = c.getInt((c.getColumnIndex(COLUMN_NUMERO)));

                s.distanceTo = c.getDouble((c.getColumnIndex(COLUMN_DISTANCE)));
                s.timeTo = c.getDouble((c.getColumnIndex(COLUMN_TIME)));
                stationDetails.add(s);
            } while (c.moveToNext());
        }
        return stationDetails;
    }*/

    /*public ArrayList<StationDetails> getAllNearestSubStationsSortedByTime() {
        ArrayList<StationDetails> stationDetails = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NEAREST_SUB_STATIONS + " ORDER BY " + COLUMN_TIME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                StationDetails s = new StationDetails();
                s.type = c.getString(c.getColumnIndex(COLUMN_TYPE));
                s.nomFr = c.getString((c.getColumnIndex(COLUMN_NOMFR)));
                s.numero = c.getInt((c.getColumnIndex(COLUMN_NUMERO)));
                s.distanceTo = c.getDouble((c.getColumnIndex(COLUMN_DISTANCE)));
                s.timeTo = c.getDouble((c.getColumnIndex(COLUMN_TIME)));
                stationDetails.add(s);
            } while (c.moveToNext());
        }
        return stationDetails;
    }*/

    public ArrayList<StationDetails> getNthNearestSubStationsSortedByDistance(int number) {
        ArrayList<StationDetails> stationDetails = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NEAREST_SUB_STATIONS + " ORDER BY " + COLUMN_DISTANCE + " LIMIT " + number;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                StationDetails s = new StationDetails();
                s.nomFr = c.getString((c.getColumnIndex(COLUMN_NOMFR)));
                s.distanceTo = c.getDouble((c.getColumnIndex(COLUMN_DISTANCE)));
                s.timeTo = c.getDouble((c.getColumnIndex(COLUMN_TIME)));
                stationDetails.add(s);
            } while (c.moveToNext());
        }
        return stationDetails;
    }

/*    public StationDetails getNearestSubStationsSortedByDistance() {
        StationDetails stationDetails = new StationDetails();
        String selectQuery = "SELECT * FROM " + TABLE_NEAREST_SUB_STATIONS + " ORDER BY " + COLUMN_DISTANCE + " LIMIT " + 1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                StationDetails s = new StationDetails();
                s.nomFr = c.getString((c.getColumnIndex(COLUMN_NOMFR)));
                s.numero = c.getInt((c.getColumnIndex(COLUMN_NUMERO)));
                s.numero = c.getInt((c.getColumnIndex(COLUMN_NUMERO)));
                s.distanceTo = c.getDouble((c.getColumnIndex(COLUMN_DISTANCE)));
                s.timeTo = c.getDouble((c.getColumnIndex(COLUMN_TIME)));
                stationDetails = s;
            } while (c.moveToNext());
        }
        return stationDetails;
    }*/

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }


}

