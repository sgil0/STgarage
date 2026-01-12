package front;

import javax.swing.*;
import java.awt.*;

public class PanneauUrgences extends JPanel {

    private DefaultListModel<String> modele;

    public PanneauUrgences() {
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(200, 0));
        this.setBorder(BorderFactory.createTitledBorder("URGENCES"));

        modele = new DefaultListModel<>();
        JList<String> liste = new JList<>(modele);

        // Rendu conditionnel (Rouge si urgent) - Code prêt pour quand il y aura des données
        liste.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value.toString().contains("Urgent")) {
                    c.setForeground(Color.RED);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                }
                return c;
            }
        });

        this.add(new JScrollPane(liste), BorderLayout.CENTER);
    }

    // TODO: Créer une méthode ici pour recevoir les données de la BDD plus tard
}