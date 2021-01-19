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

             //suiteDuProgramme(mesure, noeuds, degreMoyen);
             
              /* PROPAGATION DANS UN RESEAU */
            //suiteDuPrograme2(mesure, graph, noeuds, degreMoyen);

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
       

private static void suiteDuPrograme2(Mesures mesure, Graph graph, int noeuds, double degreMoyen) {
        Propagation propagation = new Propagation();
        Graph graph2 = mesure.reseauAleatoire(noeuds, degreMoyen);

        double beta = 1.0/7.0;
        double mu = 1.0/14.0;
        System.out.println("Taux de propagation = "+ beta/mu);
        System.out.println("Le seuil épidémique du réseau de collaborationest est : "+
                degreMoyen /propagation.moyenneCarre(graph));
        System.out.println("Le seuil épidémique du réseau aléatoire est :"
                + Toolkit.averageDegree(graph2)/propagation.moyenneCarre(graph2));

        // Simulation 1er cas on ne fait rien pour  empêcher l'épidémie

            propagation.desinfectNode(graph);
            propagation.infecteRandomNode(graph);
            propagation.propager(graph,1.0/7.0,1.0/14.0);
            propagation.generateData("resprop/Cas1");
            Propagation.reinitialisation();

            // Simulation 2e cas Immunisation aléatoire
            propagation.infecteRandomNode(graph);
            Propagation.immunisationAleatoire(graph);
            Graph gs2 = propagation.propager(graph,1.0/7.0,1.0/14.0);
            propagation.generateData("resprop/Cas2");
            Graph gs2_2 = Propagation.removeNodeInfect(gs2);
            System.out.println("Le seuil épidémique du réseau avec stratégies d'immunisation aléatoire est : "
                    + Toolkit.averageDegree(gs2_2)/propagation.moyenneCarre(gs2_2));
            Propagation.reinitialisation();


            // Simulation 3e cas Immunisation sélective
            propagation.infecteRandomNode(graph);
            Propagation.immunisationSelective(graph);
            Graph gs3 =  propagation.propager(graph,1.0/7.0,1.0/14.0);
            propagation.generateData("resprop/Cas3");
            Graph gs3_3 = Propagation.removeNodeInfect(gs3);
            System.out.println("Le seuil épidémique du réseau avec stratégies d'immunisation sélective  est : "
                    + Toolkit.averageDegree(gs3_3)/propagation.moyenneCarre(gs3_3));


            // Comparaison réseau aléatoire et réseau Barabasi-albert
            Graph g_A = mesure.reseauAleatoire(noeuds,50);
            System.out.println("taille du graphe aléatoire " + g_A.getNodeCount());
            Graph g_BA = mesure.reseauBarabasiAlbert(noeuds, degreMoyen);

            // 1er Cas on ne fait rien pour empêcher l'épidémie
            propagation.desinfectNode(g_A);
            propagation.infecteRandomNode(g_A);
            propagation.propager(g_A,beta,mu);
            propagation.generateData("resprop/Cas1Aleatoire");
            Propagation.reinitialisation();

            propagation.desinfectNode(g_BA);
            propagation.infecteRandomNode(g_BA);
            propagation.propager(g_BA,beta,mu);
            propagation.generateData("resprop/cas1Barabasi-albert");
            Propagation.reinitialisation();

            //2e cas Immunisation aléatoire
            propagation.infecteRandomNode(g_A);
            Propagation.immunisationAleatoire(g_A);
            Graph gA2 = propagation.propager(g_A,beta,mu);
            propagation.generateData("resprop/Cas2Aleatoire");
            Graph gA2_2 = Propagation.removeNodeInfect(gA2);
            System.out.println("Le seuil épidémique du réseau avec stratégies d'immunisation aléatoire est : "
                    + Toolkit.averageDegree(gA2_2)/propagation.moyenneCarre(gA2_2));
            Propagation.reinitialisation();

            propagation.infecteRandomNode(g_BA);
            Propagation.immunisationAleatoire(g_BA);
            Graph gBA2 = propagation.propager(g_BA,beta,mu);
            propagation.generateData("resprop/cas2Barabasi-albert");
            Graph gBA2_2 = Propagation.removeNodeInfect(gBA2);
            System.out.println("Le seuil épidémique du réseau avec stratégies d'immunisation aléatoire est : "
                    + Toolkit.averageDegree(gBA2_2)/propagation.moyenneCarre(gBA2_2));
            Propagation.reinitialisation();

            //3e cas Immunisation sélective
            //Graph gA3 = mesure.reseauAleatoire(100,3);
            propagation.infecteRandomNode(g_A);
            Propagation.immunisationSelective(g_A);
            Graph gA3 =  propagation.propager(g_A,beta,mu);
            propagation.generateData("resprop/Cas3Aleatoire");
            Graph gA3_3 = Propagation.removeNodeInfect(gA3);
            System.out.println("Le seuil épidémique du réseau avec stratégies d'immunisation sélective est : "
                    + Toolkit.averageDegree(gA3_3)/propagation.moyenneCarre(gA3_3));
            Propagation.reinitialisation();

            propagation.infecteRandomNode(g_BA);
            Propagation.immunisationSelective(g_BA);
            Graph gBA3 =  propagation.propager(g_BA,beta,mu);
            propagation.generateData("resprop/cas3Barabasi-albert");
            Graph gBA3_3 = Propagation.removeNodeInfect(gBA3);
            System.out.println("Le seuil épidémique du réseau avec stratégies d'immunisation sélective  est : "
                    + Toolkit.averageDegree(gBA3_3)/propagation.moyenneCarre(gBA3_3));

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
