package com.esi.navigator_22.ro;

public class Dij {
    int distance, predecesseurs;
    String nomPredecesseurs;

    public Dij() {
    }

    public Dij(int distance, int predecesseurs, String nomPredecesseurs) {
        this.distance = distance;
        this.predecesseurs = predecesseurs;
        this.nomPredecesseurs = nomPredecesseurs;
    }

    @Override
    public String toString() {
        return "Dij{" +
                "distance=" + distance +
                ", predecesseurs=" + predecesseurs +
                ", nomPredecesseurs='" + nomPredecesseurs + '\'' +
                '}';
    }
}
