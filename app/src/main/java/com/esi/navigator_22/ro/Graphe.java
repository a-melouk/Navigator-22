package com.esi.navigator_22.ro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Graphe {

    // attributs

    boolean oriente;
    int nombre_sommets, nombre_aretes;
    ArrayList<Arete> aretes;

    // constructeurs

    public Graphe() {
    }

    public Graphe(boolean oriente, int nombre_sommets, int nombre_aretes) {
        this.oriente = oriente;
        this.nombre_sommets = nombre_sommets;
        this.nombre_aretes = nombre_aretes;
        this.aretes = new ArrayList<>();
    }

    // méthodes

    public void Ajouter(Arete a) {
        if (this.aretes.size() < this.nombre_aretes) this.aretes.add(a);
    }

    public void Djiskra(Sommet sommet_depart) {
        ArrayList<Integer> visites = new ArrayList<>(); // FILE DES SOMMETS VISITES
        ArrayList<Integer> atteints = new ArrayList<Integer>(); // FILE DES SOMMETS ATTEINTS
        Map<Integer, String> nom = new HashMap<>();
        int djiskra[][] = new int[2][this.nombre_sommets]; // TABLEAU FINAL ( DISTANCES + PREDECESSEURS )
        Dij dijikstra[] = new Dij[23];
        Sommet sommet = new Sommet(sommet_depart.id, sommet_depart.nom);
        String nomSommet = sommet_depart.nom;
        Arrays.fill(djiskra[0], -1); // INITIALISATION DES DISTANCES A NULL
        Arrays.fill(djiskra[1], -1); // INITIALISATION DES PREDECESSEURS A NULL

        // APPLICATION

        // INITIALISATION
        djiskra[0][sommet.id] = 0; // DISTANCE DU SOMMET DE DEPART
        djiskra[1][sommet.id] = sommet.id;// PREDECESSERU DU SOMMET DE DEPART
        dijikstra[0] = new Dij(0, sommet.id, sommet.nom);
        atteints.add(sommet.id);
        nom.put(sommet.id, sommet_depart.nom);

        do {
            visites.add(sommet.id); // AJOUT DU SOMMET COURANT A L ENSEMBLE DES SOMMETS VISITES
            atteints.remove(atteints.indexOf(sommet.id)); // SUPPRESSION DU SOMMET COURANT DE L ENSEMBLE DES SOMMETS ATTEINTS
            for (Arete arete : this.aretes)
                if ((arete.depart == sommet.id) && (!visites.contains(arete.arrive))) {
                    if (!atteints.contains(arete.arrive)) {
                        atteints.add(arete.arrive);
                        djiskra[0][arete.arrive] = djiskra[0][sommet.id] + arete.poid;
                        djiskra[1][arete.arrive] = sommet.id;

                        dijikstra[arete.arrive] = new Dij(djiskra[0][sommet.id] + arete.poid, sommet.id, arete.sommetDepart.nom);
                        System.out.println("Wa3 1 "+sommet.id);

                    } else if (djiskra[0][arete.arrive] > djiskra[0][sommet.id] + arete.poid) {
                        djiskra[0][arete.arrive] = djiskra[0][sommet.id] + arete.poid;
                        djiskra[1][arete.arrive] = sommet.id;

                        dijikstra[arete.arrive] = new Dij(djiskra[0][sommet.id] + arete.poid, sommet.id, arete.sommetDepart.nom);
                        System.out.println("Wa3 2 "+sommet.id);

                    }
                } else if ((arete.arrive == sommet.id) && (!visites.contains(arete.depart))) {
                    if (!atteints.contains(arete.depart)) {
                        atteints.add(arete.depart);
                        djiskra[0][arete.depart] = djiskra[0][sommet.id] + arete.poid;
                        djiskra[1][arete.depart] = sommet.id;

                        dijikstra[arete.depart] = new Dij(djiskra[0][sommet.id] + arete.poid, sommet.id, arete.sommetDepart.nom);
                        System.out.println("Wa3 3 "+sommet.id);

                    } else if (djiskra[0][arete.depart] > djiskra[0][sommet.id] + arete.poid) {
                        djiskra[0][arete.depart] = djiskra[0][sommet.id] + arete.poid;
                        djiskra[1][arete.depart] = sommet.id;

                        dijikstra[arete.depart] = new Dij(djiskra[0][sommet.id] + arete.poid, sommet.id, arete.sommetDepart.nom);
                        System.out.println("Wa3 4 "+sommet.id);


                    }
                }


            sommet.id = -1;
            for (int s : atteints)
                if ((sommet.id == -1) || (djiskra[0][s] < djiskra[0][sommet.id]))
                    sommet.id = s; // SOMMET ATTEINT AVEC DISTANCE MINIMALE
        } while (sommet.id != -1); // SI YA PLUS DE SOMMETS ATTEINTS

        // AFFICHAGE

//        System.out.println("Application d'algorithme de Djiskra :\n");
//        for (int i = 0; i < this.nombre_sommets; i++)
//            if (i != sommet_depart.id) {
//                System.out.print(sommet_depart + " -> " + i + " : ");
//                if (djiskra[0][i] == -1) System.out.println("Le sommet est inaccessible.");
//                else System.out.println("d(" + i + ") = " + djiskra[0][i] + " ; p(" + i + ") = " + djiskra[1][i]);
//            }

        for (int i = 0; i < 23; i++) {
            if (i != sommet_depart.id) {
                System.out.print(sommet_depart + " -> " + i + " : ");
                if (djiskra[0][i] == -1) System.out.println("Le sommet est inaccessible.");
//                else System.out.println("d(" + i + ") = " + djiskra[0][i] + " ; p(" + i + ") = " + djiskra[1][i]);
                else System.out.println("d(" + i + ") = " + dijikstra[i]);
            }
        }
    }

    @Override
    public String toString() {
        return "Graphe{" +
                "nombre_sommets=" + nombre_sommets +
                ", nombre_aretes=" + nombre_aretes +
                ", aretes=" + aretes +
                '}';
    }
}
