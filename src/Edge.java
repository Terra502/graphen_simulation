import java.awt.*;

public class Edge {
    private Node startNode, endNode;
    Color color;
    private int cost;

    public Edge(Node startNode, Node endNode, int cost){
        this.startNode = startNode;
        this.endNode = endNode;
        this.cost = cost;
        this.color = Color.BLACK;
    }

    public Node getStartNode() {
        return startNode;
    }

    // Getter für Node2
    public Node getEndNode() {
        return endNode;
    }

    public int getCost() {
        return cost;
    }


    public void setColor(Color color){
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
            // Zeichne die Linie der Kante
            g2.drawLine(startX, startY, endX, endY);

            // Berechne den Winkel der Linie
            double angle = Math.atan2(endY - startY, endX - startX);

            // Setze Länge und Breite des Pfeilkopfes
            int arrowLength = 10;  // Länge des Pfeilkopfes

            // Berechne die beiden Seiten des Pfeilkopfes
            int x1 = endX - (int) (arrowLength * Math.cos(angle - Math.PI / 6));
            int y1 = endY - (int) (arrowLength * Math.sin(angle - Math.PI / 6));
            int x2 = endX - (int) (arrowLength * Math.cos(angle + Math.PI / 6));
            int y2 = endY - (int) (arrowLength * Math.sin(angle + Math.PI / 6));

            // Erstelle und fülle das Polygon für den Pfeilkopf
            Polygon arrowHead = new Polygon();
            arrowHead.addPoint(endX, endY);
            arrowHead.addPoint(x1, y1);
            arrowHead.addPoint(x2, y2);
            g2.fill(arrowHead);
        } else {
            // Zeichne einen Kreis für eine selbstverbundene Kante
            g.drawOval(startX - 2, startY, 5, 15);
        }
    }


    public void drawCost(Graphics g){
        /*
        int x = (startNode.getX() + endNode.getX()) / 2;
        int y = (startNode.getY() + endNode.getY()) / 2;
         */
        if (startNode == endNode){
            return;
        }
        int x = (startNode.getX() + (startNode.getX() - endNode.getX()) / 15) - (startNode.getX() - endNode.getX()) / 2;
        int y = (startNode.getY() + (startNode.getY() - endNode.getY()) / 15) - (startNode.getY() - endNode.getY()) / 2;
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
