import java.awt.*;

public class Edge {
    private Node node1, node2;
    Color color;
    private int cost;

    public Edge(Node node1, Node node2, int cost){
        this.node1 = node1;
        this.node2 = node2;
        this.cost = cost;
        this.color = Color.BLACK;
    }

    public Node getNode1() {
        return node1;
    }

    // Getter für Node2
    public Node getNode2() {
        return node2;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int weight) {
        this.cost = weight;
    }

    public void setColor(Color color){
        this.color = color;
    }

    public void drawEdge(Graphics g){
        g.setColor(color);
        g.drawLine(node1.getX(), node1.getY(), node2.getX(), node2.getY());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Edge)) return false;
        Edge edge = (Edge) obj;
        return (this.node1 == edge.node1 && this.node2 == edge.node2) ||
                (this.node1 == edge.node2 && this.node2 == edge.node1);
    }

}
