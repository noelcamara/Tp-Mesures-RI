import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

public class Propagation {

    public static int date = 84;
    public static HashSet<Node> desinfectes = new HashSet<>();
    public static HashSet<Node> infectes = new HashSet<>();
    public String valeur = "";

    public String getValeur() {
        return valeur;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }

    public double moyenneCarre(Graph g) {
        int sum = 0;
        for (Node x : g) sum += Math.pow(x.getDegree(), 2);
        return sum / (double) g.getNodeCount();
    }

    public void infecter(Node n) {
        n.setAttribute("etat_virus", "infecté");
        infectes.add(n);
    }

    public static void desinfecter(Node n) {
        n.setAttribute("etat_virus", "desinfecté");
        desinfectes.add(n);
    }

    public Graph propager(Graph g, double beta, double mu) {

        ArrayList<Node> desin = new ArrayList<>();
        ArrayList<Node> inf = new ArrayList<>();
        setValeur("");
        int Size = g.getNodeCount();
        int immuneNumber = desinfectes.size();
        for(int i = 0 ; i < date ; i++) {
            for (Node n : infectes) {
                Iterator<Node> it = n.getDepthFirstIterator(false);
                while(it.hasNext()) {
                    Node voisin = it.next();
                    if(voisin.getAttribute("etat_virus") == "desinfecté") {
                        if(Math.random()< beta)
                            inf.add(voisin);
                    }
                }
                if(Math.random() < mu)
                    desin.add(n);
            }
            for (Node n : desin)
                desinfecter(n);
            desin.clear();
            for (Node n : inf) {
                infecter(n);
            }
            System.out.printf("\njour %d/%d : %d/%d infectés\n ", i, date, infectes.size(), (Size - immuneNumber));
            setValeur(getValeur() + "\n" + i+"  "+((double) infectes.size()));
        }
        return g;
    }

    public Node generateRandomNode(Graph graph) {
        return Toolkit.randomNode(graph);
    }

    public void infecteRandomNode(Graph g) {
        infecter(generateRandomNode(g));
    }
    public void desinfectNode(Graph graph){
        for (Node n : graph) {
            n.setAttribute("etat_virus","desinfecté");
        }
    }
    public static void reinitialisation() {
        infectes.clear();
        desinfectes.clear();
    }
    public static void immunisationAleatoire(Graph g) {
        desinfectes = new HashSet<>(Toolkit.randomNodeSet(g, g.getNodeCount() / 2));
        for (Node n : desinfectes) {
            desinfecter(n);
        }
    }
    public static void immunisationSelective(Graph g) {
        ArrayList<Node> desinfectionList = (ArrayList<Node>)Toolkit.randomNodeSet(g, g.getNodeCount() / 2);
        double degMoy0 = 0;
        double degMoy1 = 0;
        for (Node n : desinfectionList) {
            Iterator<Node> iterateur = n.getDepthFirstIterator();
            ArrayList<Node> neighbors = new ArrayList<>();
            while(iterateur.hasNext())
                neighbors.add(iterateur.next());
            int unVoisin = (int)Math.floor(Math.random()*neighbors.size());
            desinfecter(neighbors.get(unVoisin));
            degMoy0 += n.getDegree();
            int noeuds =(int)(Math.random() * n.getDegree());
            Node Voisin = n.getEdge(noeuds).getOpposite(n);
            desinfectes.add(Voisin);
            Voisin.setAttribute("etat", "immunisé");
            degMoy1 += Voisin.getDegree();
        }
        degMoy0 = degMoy0 /  (g.getNodeCount() / 2);
        degMoy1 = degMoy1 /  (g.getNodeCount() / 2);
        System.out.println("\ndegré moyen du groupe 0 = " + degMoy0);
        System.out.println("\ndegré moyen du groupe 1 = " + degMoy1);
    }
    public static Graph removeNodeInfect(Graph g) {
        for(Node n : infectes) {
            g.removeNode(n);
        }
        return g;
    }

    public void generateData(String file_binomial) {
        try {
            PrintWriter printWriter = new PrintWriter(file_binomial, "UTF-8");
            printWriter.write(getValeur());
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
