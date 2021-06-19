package com.esi.navigator_22.dijkstra;

import java.util.*;

public class Graph {
    private final ArrayList<Vertex> vertices;

    public ArrayList<Edge> edges = new ArrayList<>();
    ArrayList<Vertex> points = new ArrayList<>();

    public Graph() {
        vertices = new ArrayList<>();
    }

    public void addVertex(Vertex a) {
        vertices.add(a);
    }

    public void addVertex(String a) {
        Vertex v = new Vertex(a);
        vertices.add(v);
    }

    public void addEdge(Vertex src, Vertex dest, int weight) {

        Edge new_edge = new Edge(src, dest, weight);
        src.neighbours.add(new_edge);
        edges.add(new_edge);
        new_edge = new Edge(dest, src, weight);
        dest.neighbours.add(new_edge);

    }


    public ArrayList<Vertex> getVertices() {
        return vertices;
    }

    public Vertex getVertex(int vert) {
        return vertices.get(vert);
    }

    public Vertex getVertex(Vertex a) {
        Vertex b = new Vertex();
        for (Vertex vertex : vertices) {
            if (vertex.equals(a)) b = vertex;
        }
        return b;
    }

    public Vertex getVertex(String numero) {
        Vertex b = new Vertex();
        for (Vertex vertex : vertices) {
            if (vertex.name.equals(numero)) b = vertex;
        }
        return b;
    }

    public void calculate(Vertex source) {
        // Algo:
        // 1. Take the unvisited node with minimum weight.
        // 2. Visit all its neighbours.
        // 3. Update the distances for all the neighbours (In the Priority Queue).
        // Repeat the process till all the connected nodes are visited.

        source.cost = 0;
        PriorityQueue<Vertex> queue = new PriorityQueue<>();
        queue.add(source);

        while (!queue.isEmpty()) {

            Vertex u = queue.poll();

            for (Edge neighbour : u.neighbours) {
                double newDist = u.cost + neighbour.weight;

                if (neighbour.target.cost > newDist) {
                    // Remove the node from the queue to update the distance value.
                    queue.remove(neighbour.target);
                    neighbour.target.cost = newDist;

                    // Take the path visited till now and add the new node.s
                    neighbour.target.path = new LinkedList<>(u.path);
                    neighbour.target.path.add(u);

                    //Reenter the node with new distance.
                    queue.add(neighbour.target);
                }
            }
        }
    }

    public ArrayList<Vertex> getNodes(Graph g, Vertex source, Vertex dest) {
        g.calculate(source);
        for (int i = 0; i < g.getVertices().size(); i++) {
            if (g.getVertices().get(i).equals(dest)) {
                points.addAll(dest.path);
            }
        }
        points.add(dest);
        return points;
    }

    public ArrayList<Vertex> affichage(Graph g, Vertex source, Vertex dest) {
        ArrayList<Vertex> result = new ArrayList<>();
        result.add(source);
        g.calculate(source);
        System.out.print(source + " to " + dest + " Distance : " + dest.cost + " Path : [");
        for (int i = 0; i < g.getVertices().size(); i++) {
            if (g.getVertices().get(i).equals(dest)) {
                for (int j = 0; j < dest.path.size(); j++) {
                    System.out.print(dest.path.get(j) + ", ");
                    result.add(dest.path.get(j));
                }
            }
        }
        result.add(dest);
        System.out.println(dest + "]");
        return result;
    }

    public double cost(Graph g, Vertex src, Vertex dest) {
        g.calculate(g.getVertex(src));

        return dest.cost;
    }
}
