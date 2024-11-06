import java.awt.*;
import java.util.*;
import java.util.List;

public class Dijkstra {

    private final Map<Node, List<Edge>> adjList;  // Adjazenzliste der Kanten
    private Set<Node> settled;                   // Menge der verarbeiteten Knoten
    private PriorityQueue<NodeDistance> pq;      // Prioritätswarteschlange zur Auswahl des nächsten Knotens
    private Map<Node, Integer> dist;             // Kürzeste Distanzen
    private Map<Node, Node> predecessor;  // Speichert den Vorgänger eines jeden Knotens


    public Dijkstra(List<Node> nodes, List<Edge> edges, Node startNode, boolean isNormalMode) {
        this.adjList = new HashMap<>();
        this.settled = new HashSet<>();
        this.pq = new PriorityQueue<>(Comparator.comparingInt(NodeDistance::getDistance));
        this.dist = new HashMap<>();
        this.predecessor = new HashMap<>();

        // Initialisiere Adjazenzliste
        for (Node node : nodes) {
            adjList.put(node, new LinkedList<>());
            dist.put(node, Integer.MAX_VALUE);  // Unendliche Distanz zu allen Knoten
        }
        // Füge alle Kanten der Adjazenzliste hinzu
        for (Edge edge : edges) {
            adjList.get(edge.getStartNode()).add(edge);
            // Im Normalmodus die Rückkante zur Adjazenzliste hinzufügen
            if (isNormalMode) {
                adjList.get(edge.getEndNode()).add(new Edge(edge.getEndNode(), edge.getStartNode(), edge.getCost(), (ArrayList<Edge>) edges));
            }
        }
        // Setze den Startknoten
        dist.put(startNode, 0);  // Startknoten hat Distanz 0
        pq.add(new NodeDistance(startNode, 0));
    }

    public Map<Node, Integer> findShortestPaths() {
        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            Node currentNode = current.getNode();
            // Knoten ist bereits verarbeitet
            if (!settled.contains(currentNode)) {
                settled.add(currentNode);
                evaluateNeighbors(currentNode);
            }
        }
        return dist;
    }

    private void evaluateNeighbors(Node currentNode) {
        int edgeDistance;
        int newDistance;
        // Für jeden Nachbarn von currentNode
        for (Edge edge : adjList.get(currentNode)) {
            Node neighbor = edge.getEndNode();
            if (!settled.contains(neighbor)) {
                // Verwende die Kantenkosten aus der Edge-Klasse
                edgeDistance = edge.getCost();
                newDistance = dist.get(currentNode) + edgeDistance;
                // Wenn der neue Pfad kürzer ist, aktualisiere den Nachbarn
                if (newDistance < dist.get(neighbor)) {
                    dist.put(neighbor, newDistance);
                    predecessor.put(neighbor, currentNode);
                    pq.add(new NodeDistance(neighbor, newDistance));
                }
            }
        }
    }

    public ArrayList<Edge> highlightShortestPath(Node targetNode) {
        Node currentNode = targetNode;
        ArrayList<Edge> hEdges = new ArrayList<>();
        while (predecessor.containsKey(currentNode)) {
            Node prevNode = predecessor.get(currentNode);
            // Finde die Kante zwischen currentNode und prevNode
            for (Edge edge : adjList.get(prevNode)) {
                if ((edge.getStartNode() == prevNode && edge.getEndNode() == currentNode) ||
                        (edge.getStartNode() == currentNode && edge.getEndNode() == prevNode)) {
                    edge.setColor(Color.RED);  // Setzt die Kantenfarbe auf Rot
                    hEdges.add(new Edge(edge.getStartNode(), edge.getEndNode(),1, hEdges));
                    break;
                }
            }
            // Setze currentNode auf den Vorgänger, um den Pfad zurückzuverfolgen
            currentNode = prevNode;
        }
        for (Edge edge : hEdges) {
            edge.setColor(Color.RED);
        }
        return hEdges;
    }
}