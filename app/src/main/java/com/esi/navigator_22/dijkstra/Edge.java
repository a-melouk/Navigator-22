package com.esi.navigator_22.dijkstra;

//To represent the edges in the graph.
public class Edge {
    public Vertex source;
    public Vertex target;
    public double weight;

    public Edge(Vertex target, double weight) {
        this.target = target;
        this.weight = weight;
    }

    public Edge(Vertex source, Vertex target, double weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "{" + source + ", " + target + ", " + weight + "}";
    }
}
