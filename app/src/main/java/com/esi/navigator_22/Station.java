package com.esi.navigator_22;

import org.osmdroid.util.GeoPoint;

import java.util.Objects;

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

    public String getType() {
        return type;
    }

    public String getNomFr() {
        return nomFr;
    }

    public String getNumero() {
        return numero;
    }

    public GeoPoint getCoordonnees() {
        return coordonnees;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setNomFr(String nomFr) {
        this.nomFr = nomFr;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setCoordonnees(GeoPoint coordonnees) {
        this.coordonnees = coordonnees;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(type, station.type) &&
                Objects.equals(nomFr, station.nomFr) &&
                Objects.equals(numero, station.numero) &&
                Objects.equals(coordonnees, station.coordonnees);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, nomFr, numero, coordonnees);
    }

    @Override
    public String toString() {
        return
                "Station(" + nomFr + "," + numero + ")"
                ;
    }
}
