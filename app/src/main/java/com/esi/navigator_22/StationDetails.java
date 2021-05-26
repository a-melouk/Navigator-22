package com.esi.navigator_22;

import org.osmdroid.util.GeoPoint;

public class StationDetails extends Station {
    double distanceTo;
    double timeTo;


    public StationDetails(String nomFr, String nomAr, GeoPoint coordonnees, int numero, double distanceTo, double timeTo) {
        super(nomFr, nomAr, coordonnees, numero);
        this.distanceTo = distanceTo;
        this.timeTo = timeTo;
    }

    public StationDetails() {
    }


    @Override
    public String toString() {
        return "StationDetails{" +
                "distanceTo=" + distanceTo +
                ", timeTo=" + timeTo +
                ", nomFr='" + nomFr + '\'' +
                ", nomAr='" + nomAr + '\'' +
                ", coordonnees=" + coordonnees +
                ", numero=" + numero +
                '}';
    }
}
