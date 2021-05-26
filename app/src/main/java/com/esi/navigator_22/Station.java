package com.esi.navigator_22;

import org.osmdroid.util.GeoPoint;

public class Station {
    String nomFr;
    String nomAr;
    GeoPoint coordonnees;
    int numero;

    public Station(GeoPoint coordonnees, String nomAr, String nomFr) {
        this.nomFr = nomFr;
        this.nomAr = nomAr;
        this.coordonnees = coordonnees;
    }

    public Station() {
    }

    public Station(String nomFr, String nomAr, GeoPoint coordonnees, int numero) {
        this.nomFr = nomFr;
        this.nomAr = nomAr;
        this.coordonnees = coordonnees;
        this.numero = numero;
    }

    @Override
    public String toString() {
        return "Station{" +
                "nomFr='" + nomFr + '\'' +
                ", nomAr='" + nomAr + '\'' +
                ", coordonnees=" + coordonnees +
                ", numero=" + numero +
                '}';
    }
}
