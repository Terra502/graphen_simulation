import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

public class Node {
    private int x,y;
    private char value;
    private static final int DURCHMESSER = 10;

    public Node(int x, int y, char value){
        this.x = x;
        this.y = y;
        this.value = value;
    }


    public int getX(){
        return x;
    }

    public void setX(int x){
        this.x = x;
    }

    public int getY(){
        return y;
    }

    public void setY(int y){
        this.y = y;
    }

    public char getValue() {
        return value;
    }

    public void drawNode(Graphics g){
        g.setColor(Color.BLACK);
        g.fillOval(x - DURCHMESSER / 2, y - DURCHMESSER / 2, DURCHMESSER, DURCHMESSER);
        if (y < 20){
            g.drawString(String.valueOf(value), x - 3, y + 15);
        } else {
            g.drawString(String.valueOf(value), x - 3, y - 7);
        }
    }

    public boolean contains(int x, int y){
        return Math.sqrt(Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2)) <= (double) DURCHMESSER / 2;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public int getIndex() {
        return value - 'a';  // Example: for 'a', returns 0, for 'b', returns 1
    }

    public int getCost(Node neighbor) {
        // Assuming Euclidean distance as the cost
        return (int) Math.sqrt(Math.pow(this.x - neighbor.getX(), 2) + Math.pow(this.y - neighbor.getY(), 2));
    }
}
