import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class EdgeEditor extends JFrame implements ActionListener {

    private EdgeEditorListener listener;
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;
    private ArrayList<JCheckBox> cbArray;
    private JComboBox<ComboItem> nodeChooser;

    public EdgeEditor(ArrayList<Node> nodes, ArrayList<Edge> edges, EdgeEditorListener listener) {
        setTitle("Edge Editor");
        setSize(400, 500);  // Setze die Größe des Fensters
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);  // Zentriere das Fenster auf dem Bildschirm
        setResizable(false);
        fensterZentrieren(this);
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
            nodeChooser.addItem(new ComboItem("Node: " + node.getValue(), node.getValue() + ""));
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

        for (Node node : nodes) {
            JCheckBox cb = new JCheckBox(String.valueOf(node.getValue()));
            cbArray.add(cb);
            panel2.add(cb, gbc2);
            gbc2.gridy++;  // Erhöhe nur die Zeile, damit die Checkboxen untereinander angeordnet werden
        }

        JButton saveEdges = new JButton("Save");
        saveEdges.addActionListener(this);
        saveEdges.setActionCommand("save");
        panel2.add(saveEdges, gbc2);

        panel1.add(panel2, gbc);

        // ItemListener für ComboBox, um die Checkboxen zu aktualisieren
        nodeChooser.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    updateCheckboxes();
                }
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

        // Alle Edges durchlaufen und prüfen, ob sie mit dem ausgewählten Node verbunden sind
        for (Edge edge : edges) {
            if (String.valueOf(edge.getNode1().getValue()).equals(selectedNodeValue)) {
                for (JCheckBox jcb : cbArray) {
                    if (jcb.getText().equals(String.valueOf(edge.getNode2().getValue()))) {
                        jcb.setSelected(true);
                    }
                }
            }
            if (String.valueOf(edge.getNode2().getValue()).equals(selectedNodeValue)) {
                for (JCheckBox jcb : cbArray) {
                    if (jcb.getText().equals(String.valueOf(edge.getNode1().getValue()))) {
                        jcb.setSelected(true);
                    }
                }
            }
        }
    }

    public void fensterZentrieren(JFrame fenster) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (dimension.width - fenster.getWidth()) / 2 + (getWidth() * 3) / 2;
        int y = (dimension.height - fenster.getHeight()) / 2;
        fenster.setLocation(x, y);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (actionCommand.equals("save")){
            // Hole den ausgewählten Knoten aus der ComboBox
            Node selectedNode = getNodeFromComboBox();
            if (selectedNode != null) {
                // Neue Liste für die hinzugefügten Kanten
                ArrayList<Edge> newEdges = new ArrayList<>();

                // Durchlaufe die Checkboxen, um die neuen Verbindungen zu prüfen
                for (JCheckBox cb : cbArray) {
                    Node targetNode = getNodeFromCheckbox(cb.getText());
                    if (targetNode != null) {
                        if (cb.isSelected()) {
                            // Prüfe, ob die Kante bereits existiert
                            if (!edgeExists(selectedNode, targetNode)) {
                                // Füge nur hinzu, wenn die Kante nicht bereits existiert
                                newEdges.add(new Edge(selectedNode, targetNode, 1));
                            }
                        } else {
                            // Falls nicht ausgewählt, entferne die Kante (falls vorhanden)
                            removeEdgeIfExists(selectedNode, targetNode);
                        }
                    }
                }

                // Füge die neuen Kanten der bestehenden Liste hinzu
                edges.addAll(newEdges);
                listener.onEdgesUpdated(nodes, edges);
            }
        }
    }

    private Node getNodeFromComboBox() {
        ComboItem selectedItem = (ComboItem) nodeChooser.getSelectedItem();
        for (Node node : nodes) {
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
            if ((edge.getNode1().equals(node1) && edge.getNode2().equals(node2)) ||
                    (edge.getNode1().equals(node2) && edge.getNode2().equals(node1))) {
                return true;
            }
        }
        return false;
    }

    // Hilfsmethode, um eine Kante zu entfernen, falls sie existiert
    private void removeEdgeIfExists(Node node1, Node node2) {
        edges.removeIf(edge ->
                (edge.getNode1().equals(node1) && edge.getNode2().equals(node2)) ||
                        (edge.getNode1().equals(node2) && edge.getNode2().equals(node1))
        );
    }
}