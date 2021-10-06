package com.esi.navigator_22;

import org.osmdroid.util.GeoPoint;

public class StationDetails extends Station {
    double distanceTo;
    double timeTo;


/*    public StationDetails(String nomFr, String nomAr, GeoPoint coordonnees, int numero, double distanceTo, double timeTo) {
        super(nomFr, nomAr, coordonnees, numero);
        this.distanceTo = distanceTo;
        this.timeTo = timeTo;
    }*/

    public StationDetails(String type, String nomFr, String numero, GeoPoint coordonnees, double distanceTo, double timeTo) {
        super(type, nomFr, numero, coordonnees);
        this.distanceTo = distanceTo;
        this.timeTo = timeTo;
    }

    public StationDetails() {
    }


    @Override
    public String toString() {
        return "StationDetails{" +
                "type='" + type + '\'' +
                ", nomFr='" + nomFr + '\'' +
                ", numero=" + numero +
                ", distanceTo=" + distanceTo +
                ", timeTo=" + timeTo +
                '}';
    }
}
