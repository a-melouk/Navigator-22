package com.esi.navigator_22;

import org.osmdroid.util.GeoPoint;

public class RouteBus {
    GeoPoint coordinates;
    String numLigne;

    public RouteBus() {
    }

    public RouteBus(GeoPoint coordinates, String numLigne) {
        this.coordinates = coordinates;
        this.numLigne = numLigne;
    }

    @Override
    public String toString() {
        return "RouteBus{" +
                "coordinates=" + coordinates +
                ", numLigne='" + numLigne + '\'' +
                '}';
    }
}
