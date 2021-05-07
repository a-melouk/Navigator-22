package com.esi.navigator_22;

import org.osmdroid.util.GeoPoint;

public class PointChemin {
    int id;
    GeoPoint coordonnees;


    public PointChemin(int id, GeoPoint coordonnees) {
        this.coordonnees = coordonnees;
        this.id = id;
    }

    public PointChemin(GeoPoint coordonnees) {
        this.coordonnees = coordonnees;
    }

    public PointChemin() {
    }

    @Override
    public String toString() {
        return "Point{" +
                "id=" + id +
                ", coordonnees=" + coordonnees +
                '}';
    }
}
