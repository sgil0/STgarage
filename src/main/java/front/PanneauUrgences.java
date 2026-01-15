package front;

import back.GestionGarage;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PanneauUrgences extends JPanel {

    private DefaultListModel<String> modele;
    private GestionGarage garage;

    public PanneauUrgences(GestionGarage garage) {
        this.garage = garage;
        this.setLayout(new BorderLayout());
        this.setOpaque(false); // Transparent pour prendre la couleur de la carte parente

        modele = new DefaultListModel<>();
        JList<String> liste = new JList<>(modele);

        // Custom Renderer pour le mode sombre
        liste.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (c instanceof JComponent) {
                    JComponent jc = (JComponent) c;
                    String txt = value.toString();
                    jc.setToolTipText(txt);
                    jc.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                    // Important : Fond transparent pour la cellule
                    jc.setOpaque(false);

                    if (txt.contains("URGENT")) {
                        c.setForeground(new Color(255, 85, 85)); // Rouge clair (Pastel Red)
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(new Color(80, 250, 123)); // Vert Fluo (Dracula Green style)
                    }
                }
                return c;
            }
        });

        // Configuration JList
        liste.setOpaque(false); // Transparent
        liste.setBackground(new Color(0,0,0,0)); // Vraiment transparent

        JScrollPane scroll = new JScrollPane(liste);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        this.add(scroll, BorderLayout.CENTER);
    }

    public void mettreAJour(String immat) {
        modele.clear();
        if (immat == null) {
            modele.addElement("Sélectionnez un véhicule.");
            return;
        }
        List<String> urgences = garage.analyserUrgences(immat);
        if (urgences.isEmpty()) {
            modele.addElement("✅ Aucun entretien à prévoir.");
        } else {
            for (String s : urgences) modele.addElement(s);
        }
        this.repaint();
    }
}