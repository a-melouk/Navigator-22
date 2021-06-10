package com.esi.navigator_22.ro;

import java.util.HashMap;
import java.util.Map;

public class Node {
    String name;
    // key: neighbor; value: distance from this node to the neighbor
    Map<Node, Integer> neighbors = new HashMap<>();

    public Node(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void addNeighbor(Node neighbor, int distance) {
        neighbors.put(neighbor, distance);
    }

    public Node[] getNeighbors() {
        Node[] result = new Node[neighbors.size()];
        neighbors.keySet().toArray(result);
        return result;
    }

    public int getNeighborDistance(Node node) {
        return neighbors.get(node);
    }

    public String toString() {
        return this.name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object another) {
        return name.equals(((Node) another).name);
    }
}
