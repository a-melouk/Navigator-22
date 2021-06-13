package com.esi.navigator_22.dijkstra;

import java.util.*;

public class Vertex implements Comparable<Vertex> {
    public String name;
    public ArrayList<Edge> neighbours;
    public LinkedList<Vertex> path;
    public double minDistance = Double.POSITIVE_INFINITY;
    public Vertex previous;

    public int compareTo(Vertex other) {
        return Double.compare(minDistance, other.minDistance);
    }

    public Vertex(String name) {
        this.name = name;
        neighbours = new ArrayList<>();
        path = new LinkedList<>();
    }

    public Vertex() {
        neighbours = new ArrayList<>();
        path = new LinkedList<>();
    }

    public String toString() {
        return name;
    }


}
