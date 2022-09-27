package com.esi.navigator_22;

import org.osmdroid.util.GeoPoint;

import java.util.Objects;

public class Station {
    String _id;
    String name;
    GeoPoint coordinates;
    String line;
    String type;

    public Station() {
    }

    public Station(String _id, String name, GeoPoint coordinates, String line, String type) {
        this._id = _id;
        this.name = name;
        this.coordinates = coordinates;
        this.line = line;
        this.type = type;
    }

    public Station(String type, String name, String line, GeoPoint coordinates) {
        this.type = type;
        this.name = name;
        this.coordinates = coordinates;
        this.line = line;
    }

    public Station(String line, String name, GeoPoint coordinates) {
        this.name = name;
        this.line = line;
        this.coordinates = coordinates;
    }

    public String get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public GeoPoint getCoordinates() {
        return coordinates;
    }

    public String getLine() {
        return line;
    }

    public String getType() {
        return type;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoordinates(GeoPoint coordinates) {
        this.coordinates = coordinates;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(_id, station._id) && name.equals(station.name) && Objects.equals(coordinates, station.coordinates) && Objects.equals(line, station.line) && Objects.equals(type, station.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, name, coordinates, line, type);
    }

    @Override
    public String toString() {
        return
                "Station(" + name + "," + line + ")"
                ;
    }
}
