package com.esi.navigator_22.ro;

public class Arete {

    // attributs

    int depart, arrive;
    double distance, time;
    String nom;

    // constructeurs

    public Arete() {
    }

    public Arete(int depart, int arrive, double distance, double time,String nom) {
        this.depart = depart;
        this.arrive = arrive;
        this.distance=distance;
        this.time = time;
        this.nom=nom;
    }

    @Override
    public String toString() {
        return "Arete{" +
                "depart=" + depart +
                ", arrive=" + arrive +
                ", distance=" + distance +
                ", time=" + time +
                ", nom='" + nom + '\'' +
                '}';
    }
}
