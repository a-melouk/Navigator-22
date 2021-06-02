package com.esi.navigator_22;

public class MatrixDistance {
    Station depart;
    Station arrive;
    double distance;
    double time;

    public MatrixDistance() {
    }

    public MatrixDistance(Station depart, Station arrive, double distance, double time) {
        this.depart = depart;
        this.arrive = arrive;
        this.distance = distance;
        this.time = time;
    }

    @Override
    public String toString() {
        return "MatrixDistance{" +
                "depart=" + depart +
                ", arrive=" + arrive +
                ", distance=" + distance +
                ", time=" + time +
                '}';
    }
}
