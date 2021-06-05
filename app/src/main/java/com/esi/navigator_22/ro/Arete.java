package com.esi.navigator_22.ro;

public class Arete {

    // attributs

    int depart, arrive, poid;
    String nomDepart, nomArrive;
    Sommet sommetDepart, sommetArrive;

    // constructeurs

    public Arete() {
    }

    public Arete(int depart, int arrive, int poid) {
        this.depart = depart;
        this.arrive = arrive;
        this.poid = poid;
    }

    public Arete(int depart, int arrive, int poid, String nomDepart, String nomArrive) {
        this.depart = depart;
        this.arrive = arrive;
        this.poid = poid;
        this.nomDepart = nomDepart;
        this.nomArrive = nomArrive;
    }

    public Arete(int depart, int arrive, int poid, Sommet sommetDepart, Sommet sommetArrive) {
        this.depart = depart;
        this.arrive = arrive;
        this.poid = poid;
        this.sommetDepart = sommetDepart;
        this.sommetArrive = sommetArrive;
    }

    @Override
    public String toString() {
        return "Arete{" +
                "Depart=" + sommetDepart +
                ", Arrive=" + sommetArrive +
                ", Time=" + poid+
                '}' +"\n";
    }
}
