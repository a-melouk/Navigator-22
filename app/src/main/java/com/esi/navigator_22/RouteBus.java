package com.esi.navigator_22;

import org.osmdroid.util.GeoPoint;

public class RouteBus {
    GeoPoint coordinates;
    String line;

    public RouteBus() {
    }

    public RouteBus(GeoPoint coordinates, String line) {
        this.coordinates = coordinates;
        this.line = line;
    }

    @Override
    public String toString() {
        return "RouteBus{" +
                "coordinates=" + coordinates +
                ", numLigne='" + line + '\'' +
                '}';
    }
}
