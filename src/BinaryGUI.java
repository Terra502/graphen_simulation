import com.sun.source.tree.Tree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class BinaryGUI extends JFrame implements ActionListener {
    private JPanel panel1;
    private JPanel panel2;
    private JComboBox<ComboItem> from, to;
    private JTextField addField;
    private  JTextField deleteField;
    TreeNode root;
    int x,y;

    public BinaryGUI() {
        setTitle("Binary Tree Editor");
        setSize(800, 600); // Setze die Größe des Fensters
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Zentriere das Fenster auf dem Bildschirm
        setResizable(false);
        fensterZentrieren(this);

        // Panel1: Visualisierung ohne Mausinteraktion
        panel1 = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int startX = panel1.getWidth() / 2 - 20;  // zentriert die x-Position des Wurzelknotens
                int startY = 10;  // y-Position für den Wurzelknoten (oberhalb)
                // Methode zum Zeichnen der Nodes aufrufen
                drawNodes(g, root, startX, startY, 0);
            }
        };
        panel1.setBackground(Color.WHITE);

        // Panel2: Steuerelemente und Layout
        panel2 = new JPanel();
        panel2.setPreferredSize(new Dimension(250, 800));
        panel2.setBackground(Color.LIGHT_GRAY);
        panel2.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        x = panel1.getWidth() / 2;  // x-Position des Kreises (horizontal zentriert)
        y = 10;  // y-Position des Kreises

        gbc.gridy = 0;
        gbc.gridx = 0;

        // Buttons
        JButton addNode = new JButton("Add Node");
        JButton shortestButton = new JButton("Kürzester P.");
        JButton resetButton = new JButton("Zurücksetzen");
        JButton deleteNode = new JButton("Delete Node");
        JButton exportButton = new JButton("Export");
        JButton importButton = new JButton("Import");

        // ComboBoxen für die Auswahl von Nodes
        from = new JComboBox<>();
        to = new JComboBox<>();
        updateToFrom();

        addField = new JTextField();
        deleteField = new JTextField();

        // Layout für Panel2 setzen
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        Dimension buttonSize = new Dimension(120, 50);

        addNode.setPreferredSize(buttonSize);
        shortestButton.setPreferredSize(buttonSize);
        resetButton.setPreferredSize(buttonSize);
        deleteNode.setPreferredSize(buttonSize);
        exportButton.setPreferredSize(buttonSize);
        importButton.setPreferredSize(buttonSize);
        addField.setPreferredSize(buttonSize);
        deleteField.setPreferredSize(buttonSize);

        addNode.addActionListener(this);
        addNode.setActionCommand("addNode");
        shortestButton.addActionListener(this);
        shortestButton.setActionCommand("shortest");
        resetButton.addActionListener(this);
        resetButton.setActionCommand("reset");
        deleteNode.addActionListener(this);
        deleteNode.setActionCommand("deleteNode");
        exportButton.addActionListener(this);
        exportButton.setActionCommand("export");
        importButton.addActionListener(this);
        importButton.setActionCommand("import");

        gbc.gridy = 0;
        gbc.gridx = 0;
        panel2.add(addNode, gbc);
        gbc.gridx = 1;
        panel2.add(addField, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        panel2.add(deleteNode, gbc);
        gbc.gridx = 1;
        panel2.add(deleteField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        panel2.add(resetButton, gbc);
        gbc.gridx = 1;
        panel2.add(importButton, gbc);
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel2.add(exportButton, gbc);
        gbc.gridx = 1;
        panel2.add(shortestButton, gbc);
        gbc.gridy = 4;
        gbc.gridx = 0;
        panel2.add(from, gbc);
        gbc.gridx = 1;
        panel2.add(to, gbc);
        gbc.gridy = 5;
        gbc.gridx = 0;

        // Layout setzen
        setLayout(new BorderLayout());
        add(panel1, BorderLayout.CENTER);  // Panel1 nimmt den linken Bereich ein
        add(panel2, BorderLayout.EAST);

        this.root = null;
        setVisible(true);
    }

    private void updateToFrom() {
        // Beispiel: Logik zum Aktualisieren der ComboBoxen (nodes-Liste wird verwendet)

    }

    public void drawNodes(Graphics g, TreeNode node, int x, int y, int level) {
        if (node == null) {
            return;
        }
        int width = 20;
        int height = 20;
        y += 50;  // Abstand zwischen den Nodes
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        // Zeichne den aktuellen Knoten
        g.drawOval(x, y, width, height);
        g.drawString(node.getValue(), centerX - g.getFontMetrics().stringWidth(node.getValue()) / 2, centerY + g.getFontMetrics().getHeight() / 4);
        // Berechne Position für linkes und rechtes Kind
        int horizontalGap = 50;  // Horizontaler Abstand zwischen den Nodes
        int verticalGap = 100;  // Vertikaler Abstand für die Tiefe
        // Zeichne linkes Kind
        if (node.getLeftChild() != null) {
            g.drawLine(centerX, centerY + height / 2, x - horizontalGap, y + verticalGap + height / 2);
            drawNodes(g, node.getLeftChild(), x - horizontalGap, y + verticalGap, level + 1);
        }
        // Zeichne rechtes Kind
        if (node.getRightChild() != null) {
            g.drawLine(centerX, centerY + height / 2, x + horizontalGap, y + verticalGap + height / 2);
            drawNodes(g, node.getRightChild(), x + horizontalGap, y + verticalGap, level + 1);
        }
    }



    public void fensterZentrieren(JFrame fenster) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (dimension.width - fenster.getWidth()) / 2;
        int y = (dimension.height - fenster.getHeight()) / 2;
        fenster.setLocation(x, y);
    }

    public void addNode(){
        String valueToAdd = addField.getText();
        if (root == null) {
            root = new TreeNode(valueToAdd); // Erstelle den Wurzelknoten, falls der Baum leer ist
        } else {
            // Suchen nach dem richtigen Platz und Hinzufügen des Knotens
            TreeNode parentNode = getNode(root, valueToAdd);
            if (parentNode != null) {
                // Wenn der Knoten noch nicht existiert, dann den neuen Knoten hinzufügen
                if (Integer.parseInt(valueToAdd) < Integer.parseInt(parentNode.getValue())) {
                    parentNode.setLeftChild(new TreeNode(valueToAdd, parentNode));
                } else {
                    parentNode.setRightChild(new TreeNode(valueToAdd, parentNode));
                }
            }
        }
        repaint();  // Erneutes Zeichnen des Baums
    }


    public TreeNode getNode(TreeNode node, String value){
        if (node == null) {
            return null;  // Wenn der Knoten null ist, gibt es nichts zu finden.
        }
        if (node.getValue().equals(value)){
            return node;
        }
        if (Integer.parseInt(node.getValue()) > Integer.parseInt(value)){
            if (node.getLeftChild() != null){
                return getNode(node.getLeftChild(), value);
            } else {
                return node;
            }
        } else {
            if (node.getRightChild() != null){
                return getNode(node.getRightChild(), value);
            } else {
                return node;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (actionCommand.equals("addNode")){
            addNode();
        }
        if (actionCommand.equals("shortest")){

        }
        if (actionCommand.equals("reset")){

        }
        if (actionCommand.equals("deleteNode")){

        }
        if (actionCommand.equals("export")){

        }
        if (actionCommand.equals("import")){

        }
    }
}
