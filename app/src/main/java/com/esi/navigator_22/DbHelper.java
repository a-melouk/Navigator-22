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
    private static final String TABLE_Matrice = "matrice";
    private static final String TABLE_TRAMWAY_MATRIX = "tramway_matrice";
    private static final String TABLE_NEAREST_SUB_STATIONS = "nearest_sub_stations";

    public static DbHelper getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new DbHelper(ctx);
        }
        return mInstance;
    }

    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";

    private static final String COLUMN_NOMFR = "nomFR";
    private static final String COLUMN_TYPE = "type";

    private static final String COLUMN_DISTANCE = "distanceTo";
    private static final String COLUMN_TIME = "timeTo";

    private static final String COLUMN_NUMERO = "numero";
    private static final String COLUMN_NUMLIGNE = "numLigne";

    private static final String COLUMN_LATITUDEDEPART = "latitudedepart";
    private static final String COLUMN_LONGITUDEDEPART = "longitudedepart";
    private static final String COLUMN_NOMFRDEPART = "nomfrdepart";
    private static final String COLUMN_TYPEDEPART = "typedepart";
    private static final String COLUMN_NUMERODEPART = "numerodepart";
    private static final String COLUMN_LATITUDEARRIVE = "latitudearrive";
    private static final String COLUMN_LONGITUDEARRIVE = "longitudearrive";
    private static final String COLUMN_NOMFRARRIVE = "nomfrarrive";
    private static final String COLUMN_TYPEARRIVE = "typearrive";
    private static final String COLUMN_NUMEROARRIVE = "numeroarrive";
    private static final String COLUMN_DISTANCEMATRICE = "distance";
    private static final String COLUMN_TIMEMATRICE = "time";


    private static final String CREATE_TABLE_Station = "CREATE TABLE "
            + TABLE_STATIONS + "("
            + COLUMN_TYPE + " TEXT ,"
            + COLUMN_NOMFR + " TEXT ,"
            + COLUMN_NUMERO + " TEXT ,"
            + COLUMN_LATITUDE + " REAL ,"
            + COLUMN_LONGITUDE + " REAL ,"
            + "PRIMARY KEY (" + COLUMN_NUMERO + ")"
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
    //    private static final String COLUMN_LATITUDEDEPART = "latitudedepart";
//    private static final String COLUMN_LONGITUDEDEPART = "longitudedepart";
//    private static final String COLUMN_NOMFRDEPART = "nomfrdepart";
//    private static final String COLUMN_TYPEDEPART = "typedepart";
//    private static final String COLUMN_NUMERODEPART = "numerodepart";
//    private static final String COLUMN_LATITUDEARRIVE = "latitudearrive";
//    private static final String COLUMN_LONGITUDEARRIVE = "longitudearrive";
//    private static final String COLUMN_NOMFRARRIVE = "nomfrarrive";
//    private static final String COLUMN_TYPEARRIVE = "typearrive";
//    private static final String COLUMN_NUMEROARRIVE = "numeroarrive";
//    private static final String COLUMN_DISTANCEMATRICE = "distance";
//    private static final String COLUMN_TIMEMATRICE = "time";
    private static final String CREATE_TABLE_Matrice = "CREATE TABLE "
            + TABLE_Matrice + "("
            + COLUMN_LATITUDEDEPART + " REAL ,"
            + COLUMN_LONGITUDEDEPART + " REAL ,"
            + COLUMN_NOMFRDEPART + " TEXT ,"
            + COLUMN_TYPEDEPART + " TEXT ,"
            + COLUMN_NUMERODEPART + " TEXT ,"
            + COLUMN_LATITUDEARRIVE + " REAL ,"
            + COLUMN_LONGITUDEARRIVE + " REAL ,"
            + COLUMN_NOMFRARRIVE + " TEXT ,"
            + COLUMN_TYPEARRIVE + " TEXT ,"
            + COLUMN_NUMEROARRIVE + " TEXT ,"

            + COLUMN_DISTANCEMATRICE + " REAL ,"
            + COLUMN_TIMEMATRICE + " REAL ,"
            + "PRIMARY KEY (" + COLUMN_NOMFRDEPART + " , " + COLUMN_NUMERODEPART + " , " + COLUMN_NOMFRARRIVE + " ," + COLUMN_NUMEROARRIVE + ")"
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
        sqLiteDatabase.execSQL(CREATE_TABLE_Matrice);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_STATIONS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CHEMIN_SUB);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NEAREST_SUB_STATIONS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CHEMIN_BUS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CORRESPONDANCE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_Matrice);
        onCreate(sqLiteDatabase);
    }

    long deleteAllNearestTramwayStation() {
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

    long addMatriceLine(MatriceLine line) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_LATITUDEDEPART, line.stationSource.coordonnees.getLatitude());
        contentValues.put(COLUMN_LONGITUDEDEPART, line.stationSource.coordonnees.getLongitude());
        contentValues.put(COLUMN_NOMFRDEPART, line.stationSource.nomFr);
        contentValues.put(COLUMN_TYPEDEPART, line.stationSource.type);
        contentValues.put(COLUMN_NUMERODEPART, line.stationSource.numero);

        contentValues.put(COLUMN_LATITUDEARRIVE, line.stationDestination.coordonnees.getLatitude());
        contentValues.put(COLUMN_LONGITUDEARRIVE, line.stationDestination.coordonnees.getLongitude());
        contentValues.put(COLUMN_NOMFRARRIVE, line.stationDestination.nomFr);
        contentValues.put(COLUMN_TYPEARRIVE, line.stationDestination.type);
        contentValues.put(COLUMN_NUMEROARRIVE, line.stationDestination.numero);

        contentValues.put(COLUMN_DISTANCEMATRICE, line.distance);
        contentValues.put(COLUMN_TIMEMATRICE, line.time);


        long word_id = db.insert(TABLE_Matrice, null, contentValues);
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

    public ArrayList<RouteBus> getAllPointsBus() {
        ArrayList<RouteBus> pointCheminsBus = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CHEMIN_BUS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                RouteBus routeBus = new RouteBus();
                GeoPoint g = new GeoPoint(0.0, 0.0);
                String name;
                g.setLatitude(c.getDouble((c.getColumnIndex(COLUMN_LATITUDE))));
                g.setLongitude(c.getDouble((c.getColumnIndex(COLUMN_LONGITUDE))));
                name = c.getString(c.getColumnIndex(COLUMN_NUMLIGNE));
                routeBus.coordinates = g;
                routeBus.numLigne = name;
                pointCheminsBus.add(routeBus);
            } while (c.moveToNext());
        }
        return pointCheminsBus;
    }

    ArrayList<Station> getAllStations() {
        ArrayList<Station> stations = new ArrayList<Station>();
        String selectQuery = "SELECT * FROM " + TABLE_STATIONS + " ORDER BY " + COLUMN_NUMERO;
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

    ArrayList<MatriceLine> getAllLines() {
        ArrayList<MatriceLine> lines = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_Matrice + " ORDER BY " + COLUMN_NUMERODEPART + "," + COLUMN_NUMEROARRIVE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                GeoPoint g = new GeoPoint(0.0, 0.0);
                Station s1 = new Station();
                Station s2 = new Station();
                MatriceLine line = new MatriceLine(s1, s2, 0.0, 0.0);
                s1.type = c.getString(c.getColumnIndex(COLUMN_TYPEDEPART));
                s1.nomFr = c.getString((c.getColumnIndex(COLUMN_NOMFRDEPART)));
                s1.numero = c.getString((c.getColumnIndex(COLUMN_NUMERODEPART)));
                g.setLatitude(c.getDouble((c.getColumnIndex(COLUMN_LATITUDEDEPART))));
                g.setLongitude(c.getDouble((c.getColumnIndex(COLUMN_LONGITUDEDEPART))));
                s1.coordonnees = g;
                s2.type = c.getString(c.getColumnIndex(COLUMN_TYPEARRIVE));
                s2.nomFr = c.getString((c.getColumnIndex(COLUMN_NOMFRARRIVE)));
                s2.numero = c.getString((c.getColumnIndex(COLUMN_NUMEROARRIVE)));
                g.setLatitude(c.getDouble((c.getColumnIndex(COLUMN_LATITUDEARRIVE))));
                g.setLongitude(c.getDouble((c.getColumnIndex(COLUMN_LONGITUDEARRIVE))));
                s2.coordonnees = g;
                line.stationSource = s1;
                line.stationDestination = s2;
                line.distance = c.getDouble(c.getColumnIndex(COLUMN_DISTANCEMATRICE));
                line.time = c.getDouble(c.getColumnIndex(COLUMN_TIMEMATRICE));
                lines.add(line);
            } while (c.moveToNext());
        }
        return lines;
    }

    ArrayList<TramwayMatrixLine> getAllTramwayLines() {
        ArrayList<TramwayMatrixLine> lines = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_Matrice + " WHERE " + COLUMN_TYPEDEPART + "= \"tramway\"" + " " +
                "AND " + COLUMN_TYPEARRIVE + " =\"tramway\"" +
                " ORDER BY " + COLUMN_NUMERODEPART + "," + COLUMN_NUMEROARRIVE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {

                GeoPoint g = new GeoPoint(0.0, 0.0);
                Station s1 = new Station();
                Station s2 = new Station();
                TramwayMatrixLine line = new TramwayMatrixLine(s1, s2, 0.0, 0.0);
                s1.nomFr = c.getString((c.getColumnIndex(COLUMN_NOMFRDEPART)));
                s1.numero = c.getString((c.getColumnIndex(COLUMN_NUMERODEPART)));
                g.setLatitude(c.getDouble((c.getColumnIndex(COLUMN_LATITUDEDEPART))));
                g.setLongitude(c.getDouble((c.getColumnIndex(COLUMN_LONGITUDEDEPART))));
                s1.coordonnees = g;
                s2.nomFr = c.getString((c.getColumnIndex(COLUMN_NOMFRARRIVE)));
                s2.numero = c.getString((c.getColumnIndex(COLUMN_NUMEROARRIVE)));
                g.setLatitude(c.getDouble((c.getColumnIndex(COLUMN_LATITUDEARRIVE))));
                g.setLongitude(c.getDouble((c.getColumnIndex(COLUMN_LONGITUDEARRIVE))));
                s2.coordonnees = g;
                line.stationSource = s1;
                line.stationDestination = s2;
                line.distance = c.getDouble(c.getColumnIndex(COLUMN_DISTANCEMATRICE));
                line.time = c.getDouble(c.getColumnIndex(COLUMN_TIMEMATRICE));
                lines.add(line);
            } while (c.moveToNext());
        }
        return lines;
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

