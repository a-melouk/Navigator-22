package com.esi.navigator_22;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TransportSba";
    private static final int DATABASE_VERSION = 1;

    private static DbHelper mInstance = null;

    private static final String TABLE_CHEMIN_TRAMWAY = "chemin_tramway";
    private static final String TABLE_CHEMIN_BUS = "chemin_bus";
    private static final String TABLE_STATIONS = "stations";
    private static final String TABLE_CORRESPONDANCE = "correspondance";
    private static final String TABLE_Matrice = "matrice";

    public static DbHelper getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new DbHelper(ctx);
        }
        return mInstance;
    }

    private static final String COLUMN_ID = "id";

    private static final String COLUMN_NAME = "name";

    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";

    private static final String COLUMN_LINE = "line";

    private static final String COLUMN_TYPE = "type";


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


    private static final String CREATE_TABLE_STATION = "CREATE TABLE "
            + TABLE_STATIONS + "("
            + COLUMN_ID + " TEXT ,"
            + COLUMN_NAME + " TEXT ,"
            + COLUMN_LATITUDE + " REAL ,"
            + COLUMN_LONGITUDE + " REAL ,"
            + COLUMN_LINE + " TEXT ,"
            + COLUMN_TYPE + " TEXT ,"
            + "PRIMARY KEY (" + COLUMN_ID + ")"
            + ")";

    private static final String CREATE_TABLE_TRAMWAY = "CREATE TABLE "
            + TABLE_CHEMIN_TRAMWAY + "("
            + COLUMN_LATITUDE + " REAL ,"
            + COLUMN_LONGITUDE + " REAL ,"
            + "PRIMARY KEY(" + COLUMN_LATITUDE + "," + COLUMN_LONGITUDE + ")"
            + ")";

    private static final String CREATE_TABLE_CORRESPONDANCE = "CREATE TABLE "
            + TABLE_CORRESPONDANCE + "("
            + COLUMN_LATITUDE + " REAL ,"
            + COLUMN_LONGITUDE + " REAL ,"
            + "PRIMARY KEY(" + COLUMN_LATITUDE + "," + COLUMN_LONGITUDE + ")"
            + ")";

    private static final String CREATE_TABLE_CheminBus = "CREATE TABLE "
            + TABLE_CHEMIN_BUS + "("
            + COLUMN_LINE + " TEXT ,"
            + COLUMN_LATITUDE + " REAL ,"
            + COLUMN_LONGITUDE + " REAL ,"
            + "PRIMARY KEY(" + COLUMN_LATITUDE + "," + COLUMN_LONGITUDE + "," + COLUMN_LINE + ")"
            + ")";

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

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_STATION);
        sqLiteDatabase.execSQL(CREATE_TABLE_TRAMWAY);
        sqLiteDatabase.execSQL(CREATE_TABLE_CheminBus);
        sqLiteDatabase.execSQL(CREATE_TABLE_CORRESPONDANCE);
        sqLiteDatabase.execSQL(CREATE_TABLE_Matrice);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_STATIONS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CHEMIN_TRAMWAY);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CHEMIN_BUS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CORRESPONDANCE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_Matrice);
        onCreate(sqLiteDatabase);
    }

    long addStation(Station station) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, station._id);
        contentValues.put(COLUMN_NAME, station.name);
        contentValues.put(COLUMN_LINE, station.line);
        contentValues.put(COLUMN_LATITUDE, station.coordinates.getLatitude());
        contentValues.put(COLUMN_LONGITUDE, station.coordinates.getLongitude());
        contentValues.put(COLUMN_TYPE, station.type);
        long word_id = db.insert(TABLE_STATIONS, null, contentValues);
        return word_id;
    }

    long addMatriceLine(MatriceLine line) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_LATITUDEDEPART, line.stationSource.coordinates.getLatitude());
        contentValues.put(COLUMN_LONGITUDEDEPART, line.stationSource.coordinates.getLongitude());
        contentValues.put(COLUMN_NOMFRDEPART, line.stationSource.name);
        contentValues.put(COLUMN_TYPEDEPART, line.stationSource.type);
        contentValues.put(COLUMN_NUMERODEPART, line.stationSource.line);

        contentValues.put(COLUMN_LATITUDEARRIVE, line.stationDestination.coordinates.getLatitude());
        contentValues.put(COLUMN_LONGITUDEARRIVE, line.stationDestination.coordinates.getLongitude());
        contentValues.put(COLUMN_NOMFRARRIVE, line.stationDestination.name);
        contentValues.put(COLUMN_TYPEARRIVE, line.stationDestination.type);
        contentValues.put(COLUMN_NUMEROARRIVE, line.stationDestination.line);

        contentValues.put(COLUMN_DISTANCEMATRICE, line.distance);
        contentValues.put(COLUMN_TIMEMATRICE, line.time);

        long word_id = db.insert(TABLE_Matrice, null, contentValues);
        return word_id;
    }

    long addPointSub(GeoPoint point) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_LATITUDE, point.getLatitude());
        contentValues.put(COLUMN_LONGITUDE, point.getLongitude());
        long word_id = db.insert(TABLE_CHEMIN_TRAMWAY, null, contentValues);
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
        contentValues.put(COLUMN_LINE, point.line);
        contentValues.put(COLUMN_LATITUDE, point.coordinates.getLatitude());
        contentValues.put(COLUMN_LONGITUDE, point.coordinates.getLongitude());
        long word_id = db.insert(TABLE_CHEMIN_BUS, null, contentValues);
        return word_id;
    }

    ArrayList<GeoPoint> getAllPointsSub() {
        ArrayList<GeoPoint> pointChemins = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CHEMIN_TRAMWAY;
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

    ArrayList<GeoPoint> getAllPointsCorrespondance() {
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

    ArrayList<GeoPoint> getAllPointsBusByNumber(String numero) {
        ArrayList<GeoPoint> pointCheminsBus = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CHEMIN_BUS + " WHERE " + COLUMN_LINE + "= \"" + numero + "\"";
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

    ArrayList<RouteBus> getAllPointsBus() {
        ArrayList<RouteBus> pointCheminsBus = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CHEMIN_BUS+ " ORDER BY " + COLUMN_LINE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                RouteBus routeBus = new RouteBus();
                GeoPoint g = new GeoPoint(0.0, 0.0);
                String name;
                g.setLatitude(c.getDouble((c.getColumnIndex(COLUMN_LATITUDE))));
                g.setLongitude(c.getDouble((c.getColumnIndex(COLUMN_LONGITUDE))));
                name = c.getString(c.getColumnIndex(COLUMN_LINE));
                routeBus.coordinates = g;
                routeBus.line = name;
                pointCheminsBus.add(routeBus);
            } while (c.moveToNext());
        }
        return pointCheminsBus;
    }

    ArrayList<Station> getAllStations() {
        ArrayList<Station> stations = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_STATIONS + " ORDER BY " + COLUMN_LINE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                Station s = new Station();
                GeoPoint g = new GeoPoint(0.0, 0.0);
                s.name = c.getString((c.getColumnIndex(COLUMN_NAME)));
                g.setLatitude(c.getDouble((c.getColumnIndex(COLUMN_LATITUDE))));
                g.setLongitude(c.getDouble((c.getColumnIndex(COLUMN_LONGITUDE))));
                s.coordinates = g;
                s.line = c.getString((c.getColumnIndex(COLUMN_ID)));
                s.type = c.getString(c.getColumnIndex(COLUMN_TYPE));
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
                s1.name = c.getString((c.getColumnIndex(COLUMN_NOMFRDEPART)));
                s1.line = c.getString((c.getColumnIndex(COLUMN_NUMERODEPART)));
                g.setLatitude(c.getDouble((c.getColumnIndex(COLUMN_LATITUDEDEPART))));
                g.setLongitude(c.getDouble((c.getColumnIndex(COLUMN_LONGITUDEDEPART))));
                s1.coordinates = g;
                s2.type = c.getString(c.getColumnIndex(COLUMN_TYPEARRIVE));
                s2.name = c.getString((c.getColumnIndex(COLUMN_NOMFRARRIVE)));
                s2.line = c.getString((c.getColumnIndex(COLUMN_NUMEROARRIVE)));
                g.setLatitude(c.getDouble((c.getColumnIndex(COLUMN_LATITUDEARRIVE))));
                g.setLongitude(c.getDouble((c.getColumnIndex(COLUMN_LONGITUDEARRIVE))));
                s2.coordinates = g;
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
                s1.name = c.getString((c.getColumnIndex(COLUMN_NOMFRDEPART)));
                s1.line = c.getString((c.getColumnIndex(COLUMN_NUMERODEPART)));
                g.setLatitude(c.getDouble((c.getColumnIndex(COLUMN_LATITUDEDEPART))));
                g.setLongitude(c.getDouble((c.getColumnIndex(COLUMN_LONGITUDEDEPART))));
                s1.coordinates = g;
                s2.name = c.getString((c.getColumnIndex(COLUMN_NOMFRARRIVE)));
                s2.line = c.getString((c.getColumnIndex(COLUMN_NUMEROARRIVE)));
                g.setLatitude(c.getDouble((c.getColumnIndex(COLUMN_LATITUDEARRIVE))));
                g.setLongitude(c.getDouble((c.getColumnIndex(COLUMN_LONGITUDEARRIVE))));
                s2.coordinates = g;
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
        ArrayList<Station> stations = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_STATIONS + " WHERE " + COLUMN_LINE + " = 'tramway'" + " ORDER BY " + COLUMN_ID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                Station s = new Station();
                GeoPoint g = new GeoPoint(0.0, 0.0);
                s.type = c.getString(c.getColumnIndex(COLUMN_TYPE));
                s.name = c.getString((c.getColumnIndex(COLUMN_NAME)));
                s.line = c.getString((c.getColumnIndex(COLUMN_LINE)));
                g.setLatitude(c.getDouble((c.getColumnIndex(COLUMN_LATITUDE))));
                g.setLongitude(c.getDouble((c.getColumnIndex(COLUMN_LONGITUDE))));
                s.coordinates = g;
                stations.add(s);
            } while (c.moveToNext());
        }
        return stations;
    }

    ArrayList<Station> getAllBusStations() {
        ArrayList<Station> stations = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_STATIONS + " WHERE " + COLUMN_TYPE + " = 'bus'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                Station s = new Station();
                GeoPoint g = new GeoPoint(0.0, 0.0);
                s.type = c.getString(c.getColumnIndex(COLUMN_TYPE));
                s.name = c.getString((c.getColumnIndex(COLUMN_NAME)));
                s.line = c.getString((c.getColumnIndex(COLUMN_LINE)));
                g.setLatitude(c.getDouble((c.getColumnIndex(COLUMN_LATITUDE))));
                g.setLongitude(c.getDouble((c.getColumnIndex(COLUMN_LONGITUDE))));
                s.coordinates = g;
                stations.add(s);
            } while (c.moveToNext());
        }
        return stations;
    }

    ArrayList<Station> getAllBusStationsByNumber(String numero) {
        ArrayList<Station> stations = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_STATIONS + " WHERE " + COLUMN_TYPE + " = 'bus' AND " + COLUMN_ID + " = '" + numero + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                Station s = new Station();
                GeoPoint g = new GeoPoint(0.0, 0.0);
                s.type = c.getString(c.getColumnIndex(COLUMN_TYPE));
                s.name = c.getString((c.getColumnIndex(COLUMN_NAME)));
                s.line = c.getString((c.getColumnIndex(COLUMN_ID)));
                g.setLatitude(c.getDouble((c.getColumnIndex(COLUMN_LATITUDE))));
                g.setLongitude(c.getDouble((c.getColumnIndex(COLUMN_LONGITUDE))));
                s.coordinates = g;
                stations.add(s);
            } while (c.moveToNext());
        }
        return stations;
    }


    ArrayList<Station> getAllBusStationsByName(String name) {
        ArrayList<Station> stationsBus = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_STATIONS + " WHERE " + COLUMN_NAME + "= '" + name + "'" + " AND " + COLUMN_TYPE + " = 'bus'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                GeoPoint g = new GeoPoint(0.0, 0.0);
                Station station = new Station("A", "Name", g);
                g.setLatitude(c.getDouble((c.getColumnIndex(COLUMN_LATITUDE))));
                g.setLongitude(c.getDouble((c.getColumnIndex(COLUMN_LONGITUDE))));
                station.name = c.getString(c.getColumnIndex(COLUMN_NAME));
                station.coordinates = g;
                station.line = c.getString(c.getColumnIndex(COLUMN_ID));
                stationsBus.add(station);

            } while (c.moveToNext());
        }
        return stationsBus;
    }

    ArrayList<Station> getAllStationsByName(String nomFr) {
        ArrayList<Station> stationsBus = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_STATIONS + " WHERE " + COLUMN_NAME + "like \"" + nomFr + "\"";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                GeoPoint g = new GeoPoint(0.0, 0.0);
                Station station = new Station("DefaultNumber", "DefaultName", g);
                g.setLatitude(c.getDouble((c.getColumnIndex(COLUMN_LATITUDE))));
                g.setLongitude(c.getDouble((c.getColumnIndex(COLUMN_LONGITUDE))));
                station.name = c.getString(c.getColumnIndex(COLUMN_NAME));
                station.coordinates = g;
                station.line = c.getString(c.getColumnIndex(COLUMN_ID));
                stationsBus.add(station);
            } while (c.moveToNext());
        }
        return stationsBus;
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }


}

