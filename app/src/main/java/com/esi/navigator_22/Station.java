package com.esi.navigator_22;

import org.osmdroid.util.GeoPoint;

public class Station {
    String type;
    String nomFr;
    String numero;
    GeoPoint coordonnees;


    public Station() {
    }

    public Station(String type, String nomFr, String numero, GeoPoint coordonnees) {
        this.type = type;
        this.nomFr = nomFr;
        this.coordonnees = coordonnees;
        this.numero = numero;
    }

    public Station(String numero, String nomFr, GeoPoint coordonnees) {
        this.nomFr = nomFr;
        this.numero = numero;
        this.coordonnees = coordonnees;
    }

    @Override
    public String toString() {
        return "Station{" +
                "nomFr='" + nomFr + '\'' +
                ", coordonnees=" + coordonnees +
                ", numero=" + numero +
                '}';
    }
}
