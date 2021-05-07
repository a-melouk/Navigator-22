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


    private static final String TABLE_STATIONS_SUB = "stations_sub";
    private static final String TABLE_CHEMIN_SUB = "chemin_sub";

//    private static final String COLUMN_TIMESTAMP = "timestamp";
//    private static final String COLUMN_ID = "id";


    private static final String COLUMN_NOMFR = "nomFR";
    private static final String COLUMN_NOMAR = "nomAR";


    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";


    private static final String CREATE_TABLE_StationSub = "CREATE TABLE "
            + TABLE_STATIONS_SUB + "("
            + COLUMN_NOMFR + " TEXT ,"
            + COLUMN_NOMAR + " TEXT ,"
            + COLUMN_LATITUDE + " REAL ,"
            + COLUMN_LONGITUDE + " REAL ,"
            + "PRIMARY KEY (" + COLUMN_NOMFR + " , " + COLUMN_NOMAR + ")"
            + ")";

    private static final String CREATE_TABLE_CheminSub = "CREATE TABLE "
            + TABLE_CHEMIN_SUB + "("
//            + COLUMN_TIMESTAMP + " TEXT ,"
//            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + COLUMN_LATITUDE + " REAL ,"
            + COLUMN_LONGITUDE + " REAL ,"
            + "PRIMARY KEY(" + COLUMN_LATITUDE + "," + COLUMN_LONGITUDE + ")"
            + ")";

    DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_StationSub);
        sqLiteDatabase.execSQL(CREATE_TABLE_CheminSub);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_STATIONS_SUB);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CHEMIN_SUB);
        onCreate(sqLiteDatabase);
    }

    long addStation(Station station) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NOMFR, station.nomFr);
        contentValues.put(COLUMN_NOMAR, station.nomAr);
        contentValues.put(COLUMN_LATITUDE, station.coordonnees.getLatitude());
        contentValues.put(COLUMN_LONGITUDE, station.coordonnees.getLongitude());
        long word_id = db.insert(TABLE_STATIONS_SUB, null, contentValues);
        return word_id;
    }

    long addPointChemin(GeoPoint point) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
//        contentValues.put(COLUMN_TIMESTAMP, pointChemin.timestamp);
//        contentValues.put(COLUMN_ID, pointChemin.id);
        contentValues.put(COLUMN_LATITUDE, point.getLatitude());
        contentValues.put(COLUMN_LONGITUDE, point.getLongitude());
        long word_id = db.insert(TABLE_CHEMIN_SUB, null, contentValues);
        return word_id;
    }


    public ArrayList<Station> getAllStations() {
        ArrayList<Station> stations = new ArrayList<Station>();
        String selectQuery = "SELECT * FROM " + TABLE_STATIONS_SUB;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                Station s = new Station();
                GeoPoint g = new GeoPoint(0.0, 0.0);
                s.nomFr = c.getString((c.getColumnIndex(COLUMN_NOMFR)));
                s.nomAr = c.getString((c.getColumnIndex(COLUMN_NOMAR)));
                g.setLatitude(c.getDouble((c.getColumnIndex(COLUMN_LATITUDE))));
                g.setLongitude(c.getDouble((c.getColumnIndex(COLUMN_LONGITUDE))));
                s.coordonnees = g;
                stations.add(s);
            } while (c.moveToNext());
        }
        return stations;
    }

    public ArrayList<GeoPoint> getAllPointsChemin() {
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

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }


}

