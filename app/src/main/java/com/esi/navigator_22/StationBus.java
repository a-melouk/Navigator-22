package com.esi.navigator_22;

import org.osmdroid.util.GeoPoint;

public class StationBus {
    String nomFr;
    String numLigne;
    GeoPoint coordonnees;

    public StationBus() { }

    public StationBus(String nomFr, String numLigne, GeoPoint coordonnees) {
        this.nomFr = nomFr;
        this.numLigne = numLigne;
        this.coordonnees = coordonnees;
    }

    @Override
    public String toString() {
        return "StationBus{" +
                "nomFr='" + nomFr + '\'' +
                ", numLigne='" + numLigne + '\'' +
                ", coordonnees=" + coordonnees +
                '}';
    }
}
