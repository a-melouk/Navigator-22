package com.esi.navigator_22.ro;

public class Sommet {

    int id;
    String nom;

    public Sommet() {
    }

    public Sommet(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public Sommet(String nom) {
        this.nom = nom;
    }

    public Sommet(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return nom;
    }
}
