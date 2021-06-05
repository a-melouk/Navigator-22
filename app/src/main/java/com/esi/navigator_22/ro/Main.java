package com.esi.navigator_22.ro;

import java.util.Scanner;

public class Main {


    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);


        int ns, na, cc = 0;

        ns = 23;
        na = 88;
        Graphe g = new Graphe(false, ns, na);
        Sommet a = new Sommet(0, "Les cascades");
        Sommet b = new Sommet(1, "Ghalmi");
        Sommet c = new Sommet(2, "Frères Adnane");
        Sommet d = new Sommet(3, "Benhamouda");
        Sommet e = new Sommet(4, "Environnement");
        Sommet f = new Sommet(5, "Fac de Droit");
        Sommet gg = new Sommet(6, "Centre Niaâma");
        Sommet h = new Sommet(7, "Campus");
        Sommet i = new Sommet(8, "Gare Ferroviaire");
        Sommet j = new Sommet(9, "Gare routière Nord, Sogral");
        Sommet jj = new Sommet(10, "AADL Benhamouda");
        Sommet k = new Sommet(11, "Sidi Djilali");
        Sommet l = new Sommet(12, "Wiam");
        Sommet m = new Sommet(13, "Daira");
        Sommet n = new Sommet(14, "Houari Boumediene");
        Sommet o = new Sommet(15, "Radio");
        Sommet p = new Sommet(16, "Maternité");
        Sommet q = new Sommet(17, "Adda Boudjelal");
        Sommet r = new Sommet(18, "Amir Abdelkader");
        Sommet s = new Sommet(19, "4 Horloges");
        Sommet t = new Sommet(20, "Jardin Public");
        Sommet u = new Sommet(21, "Gare routière Sud");
        Sommet v = new Sommet(22, "Current location");
        initalisation(g);

        System.out.println(g.toString());
        g.Djiskra(v);
//        do {
//            System.out.print("Saisir le sommet de depart : ");
//            cc = sc.nextInt();
//            Sommet aaa = new Sommet(cc);
//            g.Djiskra(aaa);
//        }
//        while (cc != 99);

    }

    private static void initalisation(Graphe g) {
        Sommet a = new Sommet(0, "Les cascades");
        Sommet b = new Sommet(1, "Ghalmi");
        Sommet c = new Sommet(2, "Frères Adnane");
        Sommet d = new Sommet(3, "Benhamouda");
        Sommet e = new Sommet(4, "Environnement");
        Sommet f = new Sommet(5, "Fac de Droit");
        Sommet gg = new Sommet(6, "Centre Niaâma");
        Sommet h = new Sommet(7, "Campus");
        Sommet i = new Sommet(8, "Gare Ferroviaire");
        Sommet j = new Sommet(9, "Gare routière Nord, Sogral");
        Sommet jj = new Sommet(10, "AADL Benhamouda");
        Sommet k = new Sommet(11, "Sidi Djilali");
        Sommet l = new Sommet(12, "Wiam");
        Sommet m = new Sommet(13, "Daira");
        Sommet n = new Sommet(14, "Houari Boumediene");
        Sommet o = new Sommet(15, "Radio");
        Sommet p = new Sommet(16, "Maternité");
        Sommet q = new Sommet(17, "Adda Boudjelal");
        Sommet r = new Sommet(18, "Amir Abdelkader");
        Sommet s = new Sommet(19, "4 Horloges");
        Sommet t = new Sommet(20, "Jardin Public");
        Sommet u = new Sommet(21, "Gare routière Sud");
        Sommet v = new Sommet(22, "Current location");

        g.Ajouter(new Arete(0, 1, 105, a, b));
        g.Ajouter(new Arete(1, 2, 94, b, c));
        g.Ajouter(new Arete(2, 3, 98, c, d));
        g.Ajouter(new Arete(3, 4, 224, d, e));
        g.Ajouter(new Arete(4, 5, 100, e, f));
        g.Ajouter(new Arete(5, 6, 100, f, gg));
        g.Ajouter(new Arete(6, 7, 95, gg, h));
        g.Ajouter(new Arete(7, 8, 200, h, i));
        g.Ajouter(new Arete(8, 9, 120, i, j));
        g.Ajouter(new Arete(9, 10, 110, j, jj));
        g.Ajouter(new Arete(10, 11, 150, jj, k));
        g.Ajouter(new Arete(11, 12, 145, k, l));
        g.Ajouter(new Arete(12, 13, 120, l, m));
        g.Ajouter(new Arete(13, 14, 122, m, n));
        g.Ajouter(new Arete(14, 15, 85, n, o));
        g.Ajouter(new Arete(15, 16, 103, o, p));
        g.Ajouter(new Arete(16, 17, 78, p, q));
        g.Ajouter(new Arete(17, 18, 87, q, r));
        g.Ajouter(new Arete(18, 19, 110, r, s));
        g.Ajouter(new Arete(19, 20, 130, s, t));
        g.Ajouter(new Arete(20, 21, 130, t, u));

        g.Ajouter(new Arete(22, 0, 505, v, a));
        g.Ajouter(new Arete(22, 1, 806, v, b));
        g.Ajouter(new Arete(22, 2, 1253, v, c));
        g.Ajouter(new Arete(22, 3, 1627, v, d));
        g.Ajouter(new Arete(22, 4, 2266, v, e));
        g.Ajouter(new Arete(22, 5, 1958, v, f));
        g.Ajouter(new Arete(22, 6, 2574, v, gg));
        g.Ajouter(new Arete(22, 7, 2104, v, h));
        g.Ajouter(new Arete(22, 8, 3000, v, i));
        g.Ajouter(new Arete(22, 9, 2705, v, j));
        g.Ajouter(new Arete(22, 10, 2128, v, jj));
        g.Ajouter(new Arete(22, 11, 1921, v, k));
        g.Ajouter(new Arete(22, 12, 1545, v, l));
        g.Ajouter(new Arete(22, 13, 840, v, m));
        g.Ajouter(new Arete(22, 14, 1146, v, n));
        g.Ajouter(new Arete(22, 15, 1106, v, o));
        g.Ajouter(new Arete(22, 16, 1496, v, p));
        g.Ajouter(new Arete(22, 17, 1674, v, q));
        g.Ajouter(new Arete(22, 18, 1866, v, r));
        g.Ajouter(new Arete(22, 19, 2499, v, s));
        g.Ajouter(new Arete(22, 20, 3178, v, t));
        g.Ajouter(new Arete(22, 21, 3696, v, u));

        g.Ajouter(new Arete(4, 11, 45, k, e));
    }

}
