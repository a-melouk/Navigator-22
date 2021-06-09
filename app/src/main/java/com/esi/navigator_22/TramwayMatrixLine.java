package com.esi.navigator_22;

public class TramwayMatrixLine extends MatriceLine{

    double timetramway;

    public TramwayMatrixLine(Station stationSource, Station stationDestination, double distance, double time, double timetramway) {
        super(stationSource, stationDestination, distance, time);
        this.timetramway = timetramway;
    }

    @Override
    public String toString() {
        return "TramwayMatrixLine{" +

                "stationSource=" + stationSource +
                ", stationDestination=" + stationDestination +
                ", distance=" + distance +
                ", time=" + time +
                ", timetramway=" + timetramway +
                '}';
    }
}
