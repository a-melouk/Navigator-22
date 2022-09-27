package com.esi.navigator_22;

public class MatriceLine {
    Station stationSource;
    Station stationDestination;
    double distance;
    double time;

    public MatriceLine() {
    }

    public MatriceLine(Station stationSource, Station stationDestination, double distance, double time) {
        this.stationSource = stationSource;
        this.stationDestination = stationDestination;
        this.distance = distance;
        this.time = time;
    }

    @Override
    public String toString() {
        return "MatriceLine{" + stationSource.line + ", " + stationDestination.line + ", " + distance + ", " + time + '}';
    }
}
