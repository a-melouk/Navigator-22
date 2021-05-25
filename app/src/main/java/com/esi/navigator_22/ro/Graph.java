package com.esi.navigator_22.ro;

import java.util.ArrayList;
import java.util.Arrays;

public class Graph {
    boolean oriente;
    int nombre_sommets, nombre_aretes;
    ArrayList<Arete> aretes;

    public Graph() {}

    public Graph(boolean oriente, int nombre_sommets, int nombre_aretes) {
        this.oriente = oriente;
        this.nombre_sommets = nombre_sommets;
        this.nombre_aretes = nombre_aretes;
        this.aretes = new ArrayList<>();
    }

    public void Ajouter(Arete a) {
        if (this.aretes.size() < this.nombre_aretes) this.aretes.add(a);
    }

    public void Djiskra(int sommet_depart) {
        ArrayList<Integer> visites = new ArrayList<Integer>(); // FILE DES SOMMETS VISITES
        ArrayList<Integer> atteints = new ArrayList<Integer>(); // FILE DES SOMMETS ATTEINTS
        double djiskra[][] = new double[2][this.nombre_sommets]; // TABLEAU FINAL ( DISTANCES + PREDECESSEURS )
        int sommet = sommet_depart;
        Arrays.fill(djiskra[0], -1); // INITIALISATION DES DISTANCES A NULL
        Arrays.fill(djiskra[1], -1); // INITIALISATION DES PREDECESSEURS A NULL

        // APPLICATION

        // INITIALISATION
        djiskra[0][sommet] = 0; // DISTANCE DU SOMMET DE DEPART
        djiskra[1][sommet] = sommet; // PREDECESSERU DU SOMMET DE DEPART
        atteints.add(sommet);

        do {
            visites.add(sommet); // AJOUT DU SOMMET COURANT A L ENSEMBLE DES SOMMETS VISITES
            atteints.remove(atteints.indexOf(sommet)); // SUPPRESSION DU SOMMET COURANT DE L ENSEMBLE DES SOMMETS ATTEINTS
            for (Arete arete : this.aretes)
                if (this.oriente) {
                    if ((arete.depart == sommet) && (!visites.contains(arete.arrive))) // SOMMET ADJACENT NEST PAS ENCORE VISITE
                        if (!atteints.contains(arete.arrive)) // SOMMET ADJACENT NEST PAS ENCORE ATTEINT
                        {
                            atteints.add(arete.arrive);
                            djiskra[0][arete.arrive] = djiskra[0][sommet] + arete.time;
                            djiskra[1][arete.arrive] = sommet;
                        } else if (djiskra[0][arete.arrive] > djiskra[0][sommet] + arete.time) // ATTEINT MAIS DISTANCE PLUS GRANDE
                        {
                            djiskra[0][arete.arrive] = djiskra[0][sommet] + arete.time;
                            djiskra[1][arete.arrive] = sommet;
                        }
                } else {
                    if ((arete.depart == sommet) && (!visites.contains(arete.arrive))) {
                        if (!atteints.contains(arete.arrive)) {
                            atteints.add(arete.arrive);
                            djiskra[0][arete.arrive] = djiskra[0][sommet] + arete.time;
                            djiskra[1][arete.arrive] = sommet;
                        } else if (djiskra[0][arete.arrive] > djiskra[0][sommet] + arete.time) {
                            djiskra[0][arete.arrive] = djiskra[0][sommet] + arete.time;
                            djiskra[1][arete.arrive] = sommet;
                        }
                    } else if ((arete.arrive == sommet) && (!visites.contains(arete.depart))) {
                        if (!atteints.contains(arete.depart)) {
                            atteints.add(arete.depart);
                            djiskra[0][arete.depart] = djiskra[0][sommet] + arete.time;
                            djiskra[1][arete.depart] = sommet;
                        } else if (djiskra[0][arete.depart] > djiskra[0][sommet] + arete.time) {
                            djiskra[0][arete.depart] = djiskra[0][sommet] + arete.time;
                            djiskra[1][arete.depart] = sommet;
                        }
                    }
                }

            sommet = -1;
            for (int s : atteints)
                if ((sommet == -1) || (djiskra[0][s] < djiskra[0][sommet]))
                    sommet = s; // SOMMET ATTEINT AVEC DISTANCE MINIMALE
        } while (sommet != -1); // SI YA PLUS DE SOMMETS ATTEINTS

        // AFFICHAGE

        System.out.println("Application d'algorithme de Djiskra :\n");
        System.out.println("DijikstraFinal10:\n");

        for (int i = 0; i < this.nombre_sommets; i++)
            if (i != sommet_depart) {
                System.out.print(sommet_depart + " -> " + i + " : ");
                if (djiskra[0][i] == -1) System.out.println("Le sommet est inaccessible.");
                else
                    System.out.println("d(" + i + ") = " + djiskra[0][i] + " ; p(" + i + ") = " + djiskra[1][i]);
            }
        System.out.println("DijikstraFinal11:\n");
    }

    @Override
    public String toString() {
        return "Graph{" +
                "oriente=" + oriente +
                ", nombre_sommets=" + nombre_sommets +
                ", nombre_aretes=" + nombre_aretes +
                ", aretes=" + aretes +
                '}';
    }
}
