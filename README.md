# Graphen Simulation

Diese kleine Java Anwendung soll einem die Arbeit mit Graphen erleichtern.

Mit einem Rechtsklick kann man neue Knoten einfügen, 
diese können anschließend bei Bedarf mit dem "Add Edge" Menü verbunden werden.

Import: Es können auch Graphen importiert werden, die Syntax habe ich dabei
aus meinen Vorlesungen übernommen und ist wie folgt:
{a,b,c}: Dies würde die Knoten a, b und c einfügen

{{a,b}, {a,c}}: Dies würde Kanten zwischen den Knoten a - b und a - c einfügen

Export: Die erstellten Graphen können auch exportiert werden, damit diese zB.
beim neustarten des Programms weiterhin verwendet werden können.
Die exportierten Graphen laden in dem oben beschreibenen Muster in der Datei:
export.txt

Die "Sortieren"-Funktion ist noch nicht optimal implementiert und soll aktuell hauptsächlich
dabei helfen, die Knoten ein wenig voneinander zu trennen, damit man besser mit
diesen arbeiten kann.

Kürzester P.: berechnet den kürzesten Pfad zwischen den beiden ausgewählten Knoten.
