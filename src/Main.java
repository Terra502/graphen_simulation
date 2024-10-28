import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/*
TODO:
    - Cost im EdgeEditor implementieren, damit dies bei bereits vorhandenen Kanten angezeigt wird
    - Bei Multigraphen, Kanten leicht nach rechts krümmen (Vom Ausgangspunkt)
    - Im EdgeEditor instant bidirekte Kanten einfügen können
    //- Wenn EdgeEditor nicht mir auf den Bildschirm passt, komplett auf den nächsten schieben
 */
public class Main extends JFrame implements KeyListener, MouseListener, ActionListener, EdgeEditorListener {

    private final JPanel panel1;
    private Node selectedNode = null;
    private ArrayList<Node> nodes = new ArrayList<>();
    private ArrayList<Edge> edges = new ArrayList<>();
    private final int MAX_NODES = 25;
    private int anzahlNodes = -1;
    private boolean doDrawCost = false;


    JLabel xPos;
    JLabel yPos;
    JLabel grad;
    JLabel distanz;
    JLabel durchmesser;
    JComboBox<ComboItem> from;
    JComboBox<ComboItem> to;
    JCheckBox drawCost;

    public Main(){
        setTitle("Graphen Menü");
        setSize(800, 600);  // Setze die Größe des Fensters
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Zentriere das Fenster auf dem Bildschirm
        setResizable(false);
        fensterZentrieren(this);
        addKeyListener(this);
        nodes.add(new Node(10, 10, getFreeValue()));
        nodes.add(new Node(100, 100, getFreeValue()));
        edges.add(new Edge(nodes.get(0), nodes.get(1), 1));

        panel1 = new JPanel() {
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                updateToFrom();
                for (Edge edge: edges) {
                    edge.drawEdge(g);
                }
                for (Node node: nodes) {
                    node.drawNode(g);
                }
                if (doDrawCost){
                  for (Edge edge : edges) {
                    edge.drawCost(g);
                  }
                }
            }
        };

        panel1.setBackground(Color.WHITE);
        panel1.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    // Erstelle neuen Knoten bei Rechtsklick
                    if (anzahlNodes < MAX_NODES) {
                        Node node = new Node(e.getX(), e.getY(), getFreeValue());
                        nodes.add(node);
                        updateUI(node);
                        repaint();
                    }
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    // Auswahl eines Knotens oder Erstellen einer Kante bei Linksklick
                    Node clickedNode = getNodeAt(e.getX(), e.getY());
                    if (clickedNode != null) {
                        updateUI(clickedNode);
                        if (selectedNode == null) {
                            // Node sofort auswählen für das Ziehen
                            selectedNode = clickedNode;
                        } else {
                            // Erstelle eine Kante, wenn bereits eine Node ausgewählt ist
                            edges.add(new Edge(selectedNode, clickedNode, 1));
                            selectedNode = null;
                            repaint();
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                selectedNode = null;  // Node freigeben, wenn die Maustaste losgelassen wird
            }
        });

        panel1.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedNode != null) {
                    // Aktualisiere die Position der ausgewählten Node
                    selectedNode.setX(e.getX());
                    selectedNode.setY(e.getY());
                    updateUI(selectedNode);
                    repaint();  // Panel neu zeichnen
                }
            }
        });

        drawCost = new JCheckBox("Draw Cost");
        drawCost.setBackground(Color.LIGHT_GRAY);

        drawCost.addChangeListener(changeEvent -> {
          if (drawCost.isSelected()) {
            doDrawCost = true;
            repaint();
          } else {
            doDrawCost = false;
            repaint();
          }
        });


        add(panel1);

        JPanel panel2 = new JPanel();
        panel2.setPreferredSize(new Dimension(250, 800));
        panel2.setBackground(Color.LIGHT_GRAY);
        panel2.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridy = 0;
        gbc.gridx = 0;

        xPos = new JLabel("<html><h2 style=\"text-align:center; border: solid\">X:</h2></html>");
        xPos.setBackground(Color.BLUE);
        xPos.setSize(new Dimension(panel2.getWidth() / 2, 100));
        yPos = new JLabel("<html><h2 style=\"text-align:center; border: solid\">Y:</h2></html>");

        grad = new JLabel("<html><h2 style=\"text-align:center; border: solid\">Grad:</h2></html>");
        distanz = new JLabel("<html><h2 style=\"text-align:center; border: solid\">Distanz:</h2></html>");
        durchmesser = new JLabel("<html><h2 style=\"text-align:center; border: solid\">Durchm.:</h2></html>");

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        Dimension buttonSize = new Dimension(120, 50);

        JButton addEdgeButton = new JButton("Add Edge");
        addEdgeButton.addActionListener(this);
        addEdgeButton.setActionCommand("edgeEdit");
        addEdgeButton.setPreferredSize(buttonSize);

        JButton shortestButton = new JButton("Kürzester P.");
        shortestButton.addActionListener(this);
        shortestButton.setActionCommand("shortest");
        shortestButton.setPreferredSize(buttonSize);

        JButton resetButton = new JButton("Zurücksetzen");
        resetButton.addActionListener(this);
        resetButton.setActionCommand("reset");
        resetButton.setPreferredSize(buttonSize);

        JButton sortierenButton = new JButton("Sortieren");
        sortierenButton.addActionListener(this);
        sortierenButton.setActionCommand("sortieren");
        sortierenButton.setPreferredSize(buttonSize);

        JButton exportButton = new JButton("Export");
        exportButton.addActionListener(this);
        exportButton.setActionCommand("export");
        exportButton.setPreferredSize(buttonSize);

        JButton importButton = new JButton("Import");
        importButton.addActionListener(this);
        importButton.setActionCommand("import");
        importButton.setPreferredSize(buttonSize);

        from = new JComboBox<>();
        to = new JComboBox<>();
        updateToFrom();

        panel2.add(xPos, gbc);
        gbc.gridx = 1;
        panel2.add(yPos, gbc);
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel2.add(grad, gbc);
        gbc.gridx = 1;
        panel2.add(drawCost, gbc);
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel2.add(addEdgeButton, gbc);
        gbc.gridx = 1;
        panel2.add(importButton, gbc);
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel2.add(resetButton, gbc);
        gbc.gridx = 1;
        panel2.add(sortierenButton, gbc);
        gbc.gridy = 4;
        gbc.gridx = 0;
        panel2.add(exportButton, gbc);
        gbc.gridx = 1;
        panel2.add(shortestButton, gbc);
        gbc.gridy = 5;
        gbc.gridx = 0;
        panel2.add(from, gbc);
        gbc.gridx = 1;
        panel2.add(to, gbc);
        gbc.gridy = 6;
        gbc.gridx = 0;
        panel2.add(distanz, gbc);
        gbc.gridy = 7;
        panel2.add(durchmesser, gbc);

        setLayout(new BorderLayout());
        add(panel1, BorderLayout.CENTER);  // Panel 1 nimmt den linken Bereich ein
        add(panel2, BorderLayout.EAST);

        setDurchmesser();
        setVisible(true);
    }

    public void resetEdgeColor(){
        for (Edge edge: edges) {
            edge.setColor(Color.BLACK);
        }
        repaint();
    }

    public void findShortest(){
        resetEdgeColor();
        Node startNode = null;
        Node destNode = null;
        String startString = String.valueOf(from.getSelectedItem());
        if (startString.equals("null")){
            return;
        }
        startString = startString.substring(startString.length() - 1);
        String destString = String.valueOf(to.getSelectedItem());
        if (destString.equals("null")){
            return;
        }
        destString = destString.substring(destString.length() - 1);
        for (Node node : nodes){
            if (String.valueOf(node.getValue()).equals(startString)){
                startNode = node;
            }
            if (String.valueOf(node.getValue()).equals(destString)){
                destNode = node;
            }
        }
        Dijkstra dijkstra = new Dijkstra(nodes, edges, startNode);
        Map<Node, Integer> shortestPaths = dijkstra.findShortestPaths();
        int distanceToTarget = shortestPaths.get(destNode);
        if (distanceToTarget == 2147483647){
            distanz.setText("<html><h2 style=\"text-align:center; border: solid\">Distanz: &infin;</h2></html>");
        } else {
            distanz.setText("<html><h2 style=\"text-align:center; border: solid\">Distanz: " + distanceToTarget + "</h2></html>");
        }
        dijkstra.highlightShortestPath(destNode);
        repaint();
    }

    public void exportData() {
        try {
            BufferedWriter writer;
            if (System.getProperty("user.dir").endsWith("src")){
              writer = new BufferedWriter(new FileWriter("export.txt"));
            } else {
                writer = new BufferedWriter(new FileWriter("src\\export.txt"));
            }
            StringBuilder zusammenfassung = new StringBuilder("{");
            for (Node node : nodes) {
                zusammenfassung.append(node.getValue()).append(",");
            }
            zusammenfassung = new StringBuilder(zusammenfassung.substring(0, zusammenfassung.length() - 1) + "}");
            writer.write(zusammenfassung.toString());
            writer.newLine();
            zusammenfassung = new StringBuilder("{");
            for (Edge edge : edges) {
                zusammenfassung.append("{").append(edge.getStartNode().getValue()).append(",").append(edge.getEndNode().getValue()).append("},");
            }
            zusammenfassung = new StringBuilder(zusammenfassung.substring(0, zusammenfassung.length() - 1) + "}");
            writer.write(zusammenfassung.toString());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateUI(Node node){
        xPos.setText("<html><h2 style=\"text-align:center; border: solid\">X: "+ node.getX() +"</h2></html>");
        yPos.setText("<html><h2 style=\"text-align:center; border: solid\">Y: "+ node.getY() +"</h2></html>");
        grad.setText("<html><h2 style=\"text-align:center; border: solid\">Grad: "+ calcDegree(node) +"</h2></html>");
    }

    public int calcDegree(Node node) {
        int degree = 0;
        for (Edge edge : edges) {
            if (edge.getStartNode() == node || edge.getEndNode() == node) {
                degree++;
            }
        }
        return degree;
    }

    public char getFreeValue(){
        if (anzahlNodes >= MAX_NODES){
            return (char) 97;
        }
        anzahlNodes++;
        return (char) ((char) 97 + anzahlNodes);
    }

    private Node getNodeAt(int x, int y) {
        for (Node node : nodes) {
            if (node.contains(x, y)) {
                return node;
            }
        }
        return null;
    }

    public void importData(){
        try {
            BufferedReader reader;
            if (System.getProperty("user.dir").endsWith("src")){
                reader = new BufferedReader(new FileReader("import.txt"));
            } else {
                reader = new BufferedReader(new FileReader("src\\import.txt"));
            }
            String line;
            int lineNumber = 0;
            int x = 10;
            int y = 10;
            nodes.clear();
            edges.clear();
            anzahlNodes = -1;
            while ((line = reader.readLine()) != null) {
                line = line.replaceAll("\\s+", "").replace(";", ",");
                line = line.replace("(", "{").replace(")", "}");
                if (line.charAt(1) == '{'){
                    //Edge
                    line = line.substring(1, line.length() - 1); // Entfernt das erste und letzte Zeichen
                    ArrayList<String> newEdges = new ArrayList<>();
                    while (line.contains("{")){
                        String edge = line.substring(line.indexOf("{") + 1, line.indexOf("}"));
                        newEdges.add(edge);
                        line = line.substring(1);
                        if (line.contains("{")){
                            line = line.substring(line.indexOf("{"));
                        } else {
                            line = "";
                        }
                    }
                    for (String edge : newEdges) {
                        Node node1 = null;
                        Node node2 = null;
                        for (Node node : nodes) {
                            if (node.getValue() == edge.charAt(0)) {
                                node1 = node;
                            }
                            if (node.getValue() == edge.charAt(2)) {
                                node2 = node;
                            }
                        }

                        // Überprüfen, ob die Kante bereits vorhanden ist
                        if (!edges.contains(new Edge(node1, node2, 1))) {
                            edges.add(new Edge(node1, node2, 1));
                        }
                    }
                    repaint();
                } else if (line.charAt(0) == '{') {
                    //Node
                    line = line.substring(1, line.length() - 1); // Entfernt das erste und letzte Zeichen
                    String[] newNodes = line.split(",");
                    for (String node : newNodes){
                        this.nodes.add(new Node(x,y, node.charAt(0)));
                        anzahlNodes++;
                        x += 50;
                        if (x > panel1.getWidth() - 100){
                            x = 10;
                            y += 50;
                        }
                    }
                    repaint();
                } else {
                    System.out.println("Fehler beim Parsen der Zeile: " + lineNumber);
                }
                lineNumber++;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public void fensterZentrieren(JFrame fenster) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (dimension.width - fenster.getWidth()) / 2;
        int y = (dimension.height - fenster.getHeight()) / 2;
        fenster.setLocation(x, y);
    }

    public void setDurchmesser(){
        int max = 0;
        for (Node startNode : nodes) {
            for (Node destNode : nodes) {
                Dijkstra dijkstra = new Dijkstra(nodes, edges, startNode);
                Map<Node, Integer> shortestPaths = dijkstra.findShortestPaths();
                int distanceToTarget = shortestPaths.get(destNode);
                if (distanceToTarget != 2147483647 && distanceToTarget > max){
                    max = distanceToTarget;
                }
            }
        }
        durchmesser.setText("<html><h2 style=\"text-align:center; border: solid\">Durchm.: " + max + "</h2></html>");
    }

    public static void main(String[] args) {
        new Main();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if(keyCode == KeyEvent.VK_ESCAPE){
            selectedNode = null;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (actionCommand.equals("reset")){
            nodes.clear();
            edges.clear();
            anzahlNodes = -1;
            nodes.add(new Node(10, 10, getFreeValue()));
            nodes.add(new Node(100, 100, getFreeValue()));
            edges.add(new Edge(nodes.get(0), nodes.get(1), 1));
            repaint();
        }
        if (actionCommand.equals("import")){
            importData();
        }
        if (actionCommand.equals("edgeEdit")){
            new EdgeEditor(nodes, edges, this, getLocationOnScreen());
        }
        if (actionCommand.equals("sortieren")){
            positionNodes();
        }
        if (actionCommand.equals("shortest")){
            findShortest();
        }
        if (actionCommand.equals("export")){
            exportData();
        }
    }


    @Override
    public void onEdgesUpdated(ArrayList<Node> updatedNodes, ArrayList<Edge> updatedEdges) {
        // Aktualisiere die Listen in der Main-Klasse
        this.nodes = updatedNodes;
        this.edges = updatedEdges;
        setDurchmesser();
        repaint();  // Aktualisiere die Anzeige
    }


    private void positionNodes() {
        // Abstand zwischen den vertikalen Linien
        int verticalLineOffset = 50;
        int horizontalOffset = 100; // Abstand von der linken Seite
        int initialY = 10; // Anfang Y-Position

        // Die Startposition für die erste Node
        Node startNode = nodes.get(0);
        startNode.setX(10);
        startNode.setY(initialY); // Setze die Position der ersten Node

        // Karte zur Speicherung der Position der Knoten
        Map<Node, Point> nodePositions = new HashMap<>();
        nodePositions.put(startNode, new Point(startNode.getX(), startNode.getY()));

        // Liste für die Knoten, die in der nächsten vertikalen Linie platziert werden sollen
        ArrayList<Node> currentLevel = new ArrayList<>();
        currentLevel.add(startNode);

        int level = 1; // Zähle die vertikalen Linien

        while (!currentLevel.isEmpty()) {
            ArrayList<Node> nextLevel = new ArrayList<>();
            int yOffset = initialY + (level * verticalLineOffset); // Y-Position für die nächste Linie
            int count = 0; // Zähler für die horizontale Position

            for (Node currentNode : currentLevel) {
                // Finde Nachbarn des aktuellen Knotens
                for (Edge edge : edges) {
                    Node neighbor = null;
                    if (edge.getStartNode() == currentNode) {
                        neighbor = edge.getEndNode();
                    } else if (edge.getEndNode() == currentNode) {
                        neighbor = edge.getStartNode();
                    }

                    if (neighbor != null && !nodePositions.containsKey(neighbor)) {
                        // Füge Nachbarn zur nächsten Ebene hinzu
                        nextLevel.add(neighbor);
                        // Setze die Position der Nachbarn
                        neighbor.setX(horizontalOffset + (count * 50)); // Verschiebe die Position für jeden Nachbarn
                        neighbor.setY(yOffset);
                        nodePositions.put(neighbor, new Point(neighbor.getX(), neighbor.getY()));
                        count++; // Zähle den nächsten Nachbarn
                    }
                }
            }

            // Setze die nächste Ebene als aktuelle Ebene und erhöhe den Linienlevel
            currentLevel = nextLevel;
            level++;
        }

        repaint(); // Aktualisiere die Anzeige
    }

    public void updateToFrom() {
        to.removeAllItems();
        from.removeAllItems();
        for (Node node : nodes){
            to.addItem(new ComboItem("Node: " + node.getValue(), String.valueOf(node.getValue())));
            from.addItem(new ComboItem("Node: " + node.getValue(), String.valueOf(node.getValue())));
        }
    }
}
