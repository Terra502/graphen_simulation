import java.awt.*;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;

public class Edge {
    private Node startNode, endNode;
    Color color;
    private int cost;
    ArrayList<Edge> edges = new ArrayList<>();

    public Edge(Node startNode, Node endNode, int cost, ArrayList<Edge> edges) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.cost = cost;
        this.color = Color.BLACK;
        this.edges = edges;
    }

    public Node getStartNode() {
        return startNode;
    }

    public Node getEndNode() {
        return endNode;
    }

    public int getCost() {
        return cost;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void drawEdge(Graphics g) {
        g.setColor(color);
        Graphics2D g2 = (Graphics2D) g;

        int startX = startNode.getX();
        int startY = startNode.getY();
        int endX = endNode.getX();
        int endY = endNode.getY();

        if (startNode != endNode) {
            // Berechne den Kontrollpunkt für die Krümmung nach rechts
            int controlX = (startX + endX) / 2 + (endY - startY) / 10; // Kontrollpunkt leicht verschoben
            int controlY = (startY + endY) / 2 - (endX - startX) / 10;
            double angle;

            if (uniqueEdge(startNode, endNode)) {
                g2.drawLine(startX, startY, endX, endY);
                // Berechne den Winkel der Linie
                angle = Math.atan2(endY - startY, endX - startX);
            } else {
                // Erstelle den gekrümmten Pfad als Quadratische Kurve
                QuadCurve2D curve = new QuadCurve2D.Float();
                curve.setCurve(startX, startY, controlX, controlY, endX, endY);
                g2.draw(curve);
                // Berechne den Winkel der Tangente am Endpunkt für den Pfeilkopf
                angle = Math.atan2(endY - controlY, endX - controlX);
            }


            // Länge des Pfeilkopfes
            int arrowLength = 10;

            // Berechne die Punkte für die Pfeilspitzen
            int x1 = endX - (int) (arrowLength * Math.cos(angle - Math.PI / 6));
            int y1 = endY - (int) (arrowLength * Math.sin(angle - Math.PI / 6));
            int x2 = endX - (int) (arrowLength * Math.cos(angle + Math.PI / 6));
            int y2 = endY - (int) (arrowLength * Math.sin(angle + Math.PI / 6));

            // Zeichne den Pfeilkopf
            Polygon arrowHead = new Polygon();
            arrowHead.addPoint(endX, endY);
            arrowHead.addPoint(x1, y1);
            arrowHead.addPoint(x2, y2);
            g2.fill(arrowHead);
        } else {
            // Selbstverbundene Kante als Oval
            g.drawOval(startX - 2, startY, 5, 15);
        }
    }

    public boolean uniqueEdge(Node startNode, Node endNode){
        int i = 0;
        for (Edge edge : edges) {
            if ((edge.getStartNode() == startNode && edge.getEndNode() == endNode) || (edge.getEndNode() == startNode && edge.getStartNode() == endNode)){
                i++;
            }
        }
        if (i >= 2){
            return false;
        }
        return true;
    }

    public void drawCost(Graphics g) {
        if (startNode == endNode) {
            return;
        }
        int x,y;
        if (uniqueEdge(startNode, endNode)){
            x = (startNode.getX() + (startNode.getX() - endNode.getX()) / 22) - (startNode.getX() - endNode.getX()) / 2;
            y = (startNode.getY() + (startNode.getY() - endNode.getY()) / 22) - (startNode.getY() - endNode.getY()) / 2;
        } else {
            x = (startNode.getX() + endNode.getX()) / 2 + (endNode.getY() - startNode.getY()) / 10;
            y = (startNode.getY() + endNode.getY()) / 2 - (endNode.getX() - startNode.getX()) / 10;
        }
        g.drawString(String.valueOf(cost), x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Edge)) return false;
        Edge edge = (Edge) obj;
        return (this.startNode == edge.startNode && this.endNode == edge.endNode) ||
                (this.startNode == edge.endNode && this.endNode == edge.startNode);
    }
}