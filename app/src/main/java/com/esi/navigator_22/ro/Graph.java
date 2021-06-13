package com.esi.navigator_22.ro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;


public class Graph {
    private Map<String, Node> nodes = new HashMap<>();

    public Graph() {
    }

    public void addEdgeByName(String nodeName1, String nodeName2, int weight) {
        Node node1 = nodes.get(nodeName1);
        if (node1 == null) {
            node1 = new Node(nodeName1);
        }

        Node node2 = nodes.get(nodeName2);
        if (node2 == null) {
            node2 = new Node(nodeName2);
        }

        node1.addNeighbor(node2, weight);
        node2.addNeighbor(node1, weight);

        nodes.put(nodeName1, node1);
        nodes.put(nodeName2, node2);
    }

    public void addEdgeByNodes(Node nodeName1, Node nodeName2, int weight) {
        Node node1 = nodes.get(nodeName1.name);
        if (node1 == null) {
            node1 = new Node(nodeName1.name);
        }

        Node node2 = nodes.get(nodeName2.name);
        if (node2 == null) {
            node2 = new Node(nodeName2.name);
        }

        node1.addNeighbor(node2, weight);
        node2.addNeighbor(node1, weight);

        nodes.put(nodeName1.name, node1);
        nodes.put(nodeName2.name, node2);
    }

    public List<String> shortestPath(String startNodeName, String endNodeName) {
        // key node, value parent
        Map<String, String> parents = new HashMap<>();
        Set<String> visited = new HashSet<>();
        PriorityQueue<PathNode> temp = new PriorityQueue<>();

        PathNode start = new PathNode(startNodeName, null, 0);
        temp.add(start);

        while (temp.size() > 0) {
            PathNode currentPathNode = temp.remove();

            if (!visited.contains(currentPathNode.name)) {
                Node currentNode = nodes.get(currentPathNode.name);
                parents.put(currentPathNode.name, currentPathNode.parent);
                visited.add(currentPathNode.name);

                // return the shortest path if end node is reached
                if (currentPathNode.name.equals(endNodeName)) {
                    return getPath(parents, endNodeName);
                }

                Node[] neighbors = nodes.get(currentPathNode.name).getNeighbors();
                for (int i = 0; i < neighbors.length; i++) {
                    Node neighbor = neighbors[i];

                    int distance2root =
                            currentPathNode.distance2root + currentNode.getNeighborDistance(neighbor);
                    // PriorityQueue ensure that the node with shortest distance to the root is put at the
                    // head of the queue
                    temp.add(new PathNode(neighbor.name, currentPathNode.name, distance2root));
                }

                System.out.println("current node: " + currentPathNode.name);
                System.out.println("PriorityQueue: " + temp);
                System.out.println("Parents: " + parents);
                System.out.println("Visited: " + visited);
                System.out.println("");
            }
        }
        return null;
    }

    private List<String> getPath(Map<String, String> parents, String endNodeName) {
        List<String> path = new ArrayList<>();
        List<Integer> distance = new ArrayList<>();
        String node = endNodeName;
        while (node != null) {
            path.add(0, node);
//            distance.add(0,nodes.get(node).getNeighborDistance(nodes.get(parents.get(nodes.get(node)))));
//            if (Integer.valueOf(nodes.get(node).getNeighborDistance(nodes.get(parents.get(node)))).equals(null)) {
//            } else
//                distance.add(0, nodes.get(node).getNeighborDistance(nodes.get(parents.get(node))));
            String parent = parents.get(node);
            node = parent;
        }
        System.out.println(distance + "");
        return path;
    }

    @Override
    public String toString() {
        return "Graph{" +
                "nodes=" + nodes +
                '}';
    }
}
