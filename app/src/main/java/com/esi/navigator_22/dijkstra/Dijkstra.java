package com.esi.navigator_22.dijkstra;

import java.util.*;

public class Dijkstra {

    public static void main(String[] arg) {

        Dijkstra obj = new Dijkstra();


        // Create a new graph.
        Graph g = new Graph();

        // Add the required edges.
        Vertex a = new Vertex("0");
        Vertex b = new Vertex("1");
        Vertex c = new Vertex("2");
        Vertex d = new Vertex("3");
        Vertex e = new Vertex("4");
        g.addVertex(a);
        g.addVertex(b);
        g.addVertex(c);
        g.addVertex(d);
        g.addVertex(e);

        g.addEdge(a, b, 4);
        g.addEdge(b, c, 8);
        g.addEdge(c, d, 8);
        g.addEdge(d, e, 11);
        g.addEdge(e, a, 8);
        System.out.println(g.getVertices());
        // Calculate Dijkstra.
        g.calculate(g.getVertex(e));
        System.out.println("aaaaaa " + g.getVertex(a));

        // Print the minimum Distance.
        for (Vertex v : g.getVertices()) {
            System.out.print(g.getVertex(b) + " to " + v + " , Dist - " + v.minDistance + " , Path: [");
            for (Vertex pathvert : v.path) {
                System.out.print(pathvert + ", ");
            }

            System.out.print("" + v);
            System.out.println("]");
        }

    }

}

