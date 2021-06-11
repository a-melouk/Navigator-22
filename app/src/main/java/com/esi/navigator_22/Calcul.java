package com.esi.navigator_22;

public class Calcul {
    String current;
    String destination;
    int cost;

    public Calcul() {
    }

    public Calcul(String current, String destination, int cost) {
        this.current = current;
        this.destination = destination;
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "Arete{" +
                current + ", " +
                destination + ", " +
                +cost +
                '}';
    }
}
