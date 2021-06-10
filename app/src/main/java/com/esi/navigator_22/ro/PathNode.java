package com.esi.navigator_22.ro;

class PathNode implements Comparable<PathNode> {
    String name;
    String parent;
    // distance to the root
    int distance2root;

    public PathNode(String name, String parent, int distance2root) {
        this.name = name;
        this.parent = parent;
        this.distance2root = distance2root;
    }

    @Override
    public int compareTo(PathNode another) {
        return distance2root - another.distance2root;
    }

    @Override
    public String toString() {
        return "(" + this.name + "," + this.parent + "," + this.distance2root + ")";
    }
}
