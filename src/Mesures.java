import org.graphstream.algorithm.Toolkit;
import org.graphstream.algorithm.generator.BarabasiAlbertGenerator;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.RandomGenerator;
import org.graphstream.algorithm.generator.WattsStrogatzGenerator;
import org.graphstream.graph.BreadthFirstIterator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceEdge;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Mesures {

    public String valeur = "";

    public String getValeur() {
    return valeur;
}
    public void setValeur(String valeur) {
        this.valeur = valeur;
    }
        public static void main(String[] args) throws IOException {
            Mesures mesure = new Mesures();
            // 1-la lecture des données télécharger
            String filePath = "com-dblp.ungraph.txt";
            Graph graph = new SingleGraph("graphe");
            FileSource fs = new FileSourceEdge();
            fs.addSink(graph);
            try {
                fs.readAll(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 2- les Quelques Mesures
            int noeuds = graph.getNodeCount();
            int liens = graph.getEdgeCount();
            System.out.println("Nombre de Noeuds = " + noeuds);
            System.out.println("Nombre de Liens = " + liens);
            double degreMoyen = Toolkit.averageDegree(graph);
            System.out.println("le degré moyen = " + degreMoyen);
            System.out.println("coefficient de clustering: " + Toolkit.averageClusteringCoefficient(graph));
            System.out.println("coefficient de clustering pour un réseau aléatoire de la même taille et du même degré moyen est : "
                    + Toolkit.averageClusteringCoefficient(mesure.reseauAleatoire(noeuds, degreMoyen)));
            //- la Connexité
            if (Toolkit.isConnected(graph))
                System.out.println( "le réseau est connexe");
            else System.out.println("le réseau n'est pas connexe");
            System.out.println("Un réseau aléatoire de la même taille et de degré moyen " +
                    ((Toolkit.isConnected(mesure.reseauAleatoire(noeuds,
                            degreMoyen))) ? " est" : "n'est pas") + " connexe");

            // 4- calcul de la distribution des degrés
            int[] distri = Toolkit.degreeDistribution(graph);
            for (int i = 0; i < distri.length; i++) {
                if (distri[i] > 0) {
                    mesure.setValeur(mesure.getValeur() + " \n" + i + " " + (double) distri[i] / noeuds);
                    mesure.generateData("distributionDegre");
                }
            }
            // 5- calcul des distances moyennes
            System.out.println("la distance moyenne pour le DBLP est: " + mesure.distancesReseau(graph, 1000));
            Graph g2 = mesure.reseauAleatoire(noeuds, degreMoyen);
            System.out.println("la distance moyenne pour le reseau aléatoire est: "
                    + Math.log(noeuds) / Math.log(Toolkit.averageDegree(g2)));

             suiteDuProgramme(mesure, noeuds, degreMoyen);
        }

    private static void suiteDuProgramme(Mesures mesure, int noeuds, double degMoyen) throws IOException {
        Graph reseauAl = mesure.reseauAleatoire(noeuds, degMoyen);
        Graph reseauBA = mesure.reseauBarabasiAlbert(noeuds, degMoyen);
        System.out.println("Nombre de Noeuds du reseau aleatoire est: " + reseauAl.getNodeCount());
        System.out.println("Nombre de Noeuds du reseau Barabasi-Albert est: " + reseauBA.getNodeCount());
        System.out.println("Nombre de Liens du reseau aleatoire est: " + reseauAl.getEdgeCount());
        System.out.println("Nombre de Liens du reseau Barabasi-Albert est: " + reseauBA.getEdgeCount());
        System.out.println("le degré moyen du reseau aleatoire est: " + Toolkit.averageDegree(reseauAl));
        System.out.println("le degré moyen du reseau Barabasi-Albert est:" + Toolkit.averageDegree(reseauBA));
        System.out.println(" le coefficient de clustering du reseau aleatoire est: " + Toolkit.averageClusteringCoefficient(reseauAl));
        System.out.println("le coefficient de clustering du reseau Barabasi-Albert est:" + Toolkit.averageClusteringCoefficient(reseauBA));
        System.out.println("Le reseau aleatoire" + ((Toolkit.isConnected(reseauAl)) ? " est" : " n'est pas") + " connexe");
        System.out.println("Le reseau Barabasi-Albert" + ((Toolkit.isConnected(reseauBA)) ? " est" : " n'est pas") + " connexe");
        System.out.println("la distance moyenne dans le reseau aléatoire est: " + Math.log(reseauAl.getNodeCount()) / Math.log(Toolkit.averageDegree(reseauAl)));
        System.out.println("la distance moyenne dans le reseau de Barabasi-Albert  est:" + Math.log(reseauBA.getNodeCount()) / Math.log(Math.log(reseauBA.getNodeCount())));

        //7-Question bonus
        System.out.println("le Coeficient de clustering est=  " + Toolkit.averageClusteringCoefficient(mesure.varianteMethodeCopie(100, 4, 0.9)));
        mesure.distancesReseau(reseauAl, 100);
        // Comparaison réseau aléatoire et réseau Barabasi-albert
        Graph gA = mesure.reseauAleatoire(noeuds, 50);
        System.out.println("taille du graphe aléatoire " + gA.getNodeCount());
        Graph gBBA = mesure.reseauBarabasiAlbert(noeuds, degMoyen);
    }

    //methode pour générer un reseau aleatoire
    public Graph reseauAleatoire(int taille, double degre) {
        Graph graph = new SingleGraph("Graphe aléatoire");
        Generator g = new RandomGenerator(degre);
        g.addSink(graph);
        g.begin();
        for (int i = 0; i < taille; i++)
            g.nextEvents();
        g.end();
        return graph;
    }

    //methode pour générer un réseau Barabasi-Albert
    public Graph reseauBarabasiAlbert(int taille, double degre) {
        Graph graph = new SingleGraph("Barabasi-Albert");
        Generator g = new BarabasiAlbertGenerator((int) degre);
        g.addSink(graph);
        g.begin();
        for (int i = 0; i < taille; i++) {
            g.nextEvents();
        }
        g.end();
        return graph;
    }

    // methode pour générer des valeurs en fichier
    public void generateData(String nom_fichier) throws IOException {
        try {
            PrintWriter printWriter = new PrintWriter(nom_fichier, StandardCharsets.UTF_8);
            printWriter.write(getValeur());
            printWriter.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    // methode pour générer la distance du reseau de collaboration
    private double distancesReseau(Graph graph, int taille) throws IOException {
        setValeur("");
        List<Node> sources = Toolkit.randomNodeSet(graph, taille);
        long[] dd = new long[50];
        sources.forEach(source -> {
            BreadthFirstIterator bf = new BreadthFirstIterator(source);
            while (bf.hasNext()) {
                Node v = bf.next();
                dd[bf.getDepthOf(v)]++;
            }
        });
        double dAvg = 0;
        for (int d = 0; d < dd.length; d++) {
            if (dd[d] > 0) {
                double pd = (double) dd[d] / (taille * graph.getNodeCount());
                dAvg += d * pd;
                setValeur(getValeur() + " \n" + d + " " + pd);
                generateData("distributionDistance");
            }
        }
        return dAvg;
    }

    public Graph varianteMethodeCopie(int node, int degre, double p) {
        Graph graph = new SingleGraph("graph");
        Generator gen = new WattsStrogatzGenerator(node,degre,p);
        gen.addSink(graph);
        gen.begin();
        while (gen.nextEvents()) {}
        gen.end();
        //graph.display(false);
        return graph;
    }
}
