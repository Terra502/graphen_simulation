import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class EdgeEditor extends JFrame implements ActionListener {

    private EdgeEditorListener listener;
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;
    private ArrayList<JCheckBox> cbArray;
    private ArrayList<JTextField> tfArray;
    private JComboBox<ComboItem> nodeChooser;

    public EdgeEditor(ArrayList<Node> nodes, ArrayList<Edge> edges, EdgeEditorListener listener, Point location) {
        setTitle("Edge Editor");
        setSize(400, 500);  // Setze die Größe des Fensters
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);  // Zentriere das Fenster auf dem Bildschirm
        setResizable(false);
        fensterZentrieren(this, location);
        setVisible(true);

        this.nodes = nodes;
        this.edges = edges;
        this.listener = listener;

        JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);  // Setze Abstände für bessere Lesbarkeit

        // ComboBox
        nodeChooser = new JComboBox<>();
        for (Node node : nodes) {
            nodeChooser.addItem(new ComboItem("Node: " + node.getValue(), String.valueOf(node.getValue())));
        }
        panel1.add(nodeChooser, gbc);

        // Checkboxen
        gbc.gridy++;  // Nächste Zeile für die Checkboxen
        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.gridy = 0;
        gbc2.anchor = GridBagConstraints.WEST;  // Setzt die Checkboxen linksbündig
        cbArray = new ArrayList<>();
        tfArray = new ArrayList<>();

        for (Node node : nodes) {
            JCheckBox cb = new JCheckBox(String.valueOf(node.getValue()));

            JTextField tf = new JTextField();
            tf.setText("Cost");
            tf.setForeground(Color.GRAY);
            tf.setFont(new Font("Serif",Font.BOLD,12));
            tf.setPreferredSize(new Dimension(50, 20));
            tf.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (tf.getText().equals("Cost")) {
                        tf.setText("");
                        tf.setForeground(Color.BLACK);
                    }
                }
                @Override
                public void focusLost(FocusEvent e) {
                    if (tf.getText().isEmpty()) {
                        tf.setForeground(Color.GRAY);
                        tf.setText("Cost");
                    }
                }
            });
            tfArray.add(tf);

            cbArray.add(cb);
            panel2.add(cb, gbc2);
            gbc2.gridx = 1;
            panel2.add(tf, gbc2);
            gbc2.gridx = 0;
            gbc2.gridy++;  // Erhöhe nur die Zeile, damit die Checkboxen untereinander angeordnet werden
        }

        JButton saveEdges = new JButton("Save");
        saveEdges.addActionListener(this);
        saveEdges.setActionCommand("save");
        panel2.add(saveEdges, gbc2);
        panel1.add(panel2, gbc);

        // ItemListener für ComboBox, um die Checkboxen zu aktualisieren
        nodeChooser.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                updateCheckboxes();
            }
        });

        // Checkboxen initial aktualisieren, basierend auf dem ersten ausgewählten Knoten
        updateCheckboxes();
        add(panel1);
    }

    private void updateCheckboxes() {
        // Den aktuell ausgewählten Node aus der ComboBox abrufen
        ComboItem selectedItem = (ComboItem) nodeChooser.getSelectedItem();
        if (selectedItem == null) return;
        String selectedNodeValue = selectedItem.getValue();

        // Alle Checkboxen zurücksetzen (auf unmarkiert setzen)
        for (JCheckBox checkBox : cbArray) {
            checkBox.setSelected(false);
        }

        for (JTextField tfield : tfArray){
          tfield.setText("Cost");
        }

        // Alle Edges durchlaufen und prüfen, ob sie mit dem ausgewählten Node verbunden sind
        for (int i = 0; i < edges.size(); i++) {
          if (String.valueOf(edges.get(i).getStartNode().getValue()).equals(selectedNodeValue)) {
            for (int j = 0; j < cbArray.size(); j++) {
              if (cbArray.get(j).getText().equals(String.valueOf(edges.get(i).getEndNode().getValue()))) {
                cbArray.get(j).setSelected(true);
                tfArray.get(j).setText(String.valueOf(edges.get(i).getCost()));
              }
            }
          }
        }
    }

    public void fensterZentrieren(JFrame fenster, Point location) {
        int x = location.x + (getWidth() * 2);
        int y = location.y;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        if (x + getWidth() > width){
          x = location.x - getWidth();
        }
        fenster.setLocation(x, y);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (actionCommand.equals("save")) {
            Node selectedNode = getNodeFromComboBox();
            if (selectedNode != null) {
                ArrayList<Edge> newEdges = new ArrayList<>();

                for (int i = 0; i < cbArray.size(); i++) {
                    JCheckBox cb = cbArray.get(i);
                    JTextField tf = tfArray.get(i);  // Das zugehörige Textfeld abrufen

                    Node targetNode = getNodeFromCheckbox(cb.getText());
                    if (targetNode != null) {
                        if (cb.isSelected()) {
                            if (!edgeExists(selectedNode, targetNode)) {
                                try {
                                    int cost;
                                    if (tf.getText().equals("Cost")){
                                        cost = 1;
                                    } else {
                                        cost = Integer.parseInt(tf.getText());  // Textfeldwert als Kosten verwenden
                                    }
                                    newEdges.add(new Edge(selectedNode, targetNode, cost));
                                } catch (NumberFormatException ex) {
                                    System.out.println("Ungültiger Kostenwert für Kante: " + ex.getMessage());
                                }
                            } else {
                                try {
                                    int cost;
                                    if (tf.getText().equals("Cost")){
                                        cost = 1;
                                    } else {
                                        cost = Integer.parseInt(tf.getText());  // Textfeldwert als Kosten verwenden
                                    }
                                    removeEdgeIfExists(selectedNode, targetNode);
                                    newEdges.add(new Edge(selectedNode, targetNode, cost));
                                } catch (NumberFormatException ex) {
                                    System.out.println("Ungültiger Kostenwert für Kante: " + ex.getMessage());
                                }
                            }
                        } else {
                            removeEdgeIfExists(selectedNode, targetNode);
                        }
                    }
                }

                edges.addAll(newEdges);
                listener.onEdgesUpdated(nodes, edges);
            }
        }
    }


    private Node getNodeFromComboBox() {
        ComboItem selectedItem = (ComboItem) nodeChooser.getSelectedItem();
        for (Node node : nodes) {
            assert selectedItem != null;
            if (String.valueOf(node.getValue()).equals(selectedItem.getValue())) {
                return node;
            }
        }
        return null;
    }

    private Node getNodeFromCheckbox(String value) {
        for (Node node : nodes) {
            if (String.valueOf(node.getValue()).equals(value)) {
                return node;
            }
        }
        return null;
    }

    // Hilfsmethode, um zu prüfen, ob eine Kante bereits existiert
    private boolean edgeExists(Node node1, Node node2) {
        for (Edge edge : edges) {
            if ((edge.getStartNode().equals(node1) && edge.getEndNode().equals(node2)) ||
                    (edge.getStartNode().equals(node2) && edge.getEndNode().equals(node1))) {
                return true;
            }
        }
        return false;
    }

    // Hilfsmethode, um eine Kante zu entfernen, falls sie existiert
    private void removeEdgeIfExists(Node node1, Node node2) {
        edges.removeIf(edge ->
                (edge.getStartNode().equals(node1) && edge.getEndNode().equals(node2))); // || (edge.getStartNode().equals(node2) && edge.getEndNode().equals(node1)
    }
}
