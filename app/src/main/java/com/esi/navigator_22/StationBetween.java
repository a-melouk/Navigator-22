package com.esi.navigator_22;

public class StationBetween {
    Station src;
    Station dst;
    double duration;

    public StationBetween() {
    }

    public StationBetween(Station src, Station dst, double duration) {
        this.src = src;
        this.dst = dst;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "StationBetween{" +
                "src=" + src +
                ", dst=" + dst +
                ", duration=" + duration +
                '}';
    }
}
