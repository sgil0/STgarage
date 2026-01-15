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

        // On retire la couleur de fond et la bordure en dur pour laisser le parent gérer le design
        this.setOpaque(false);

        modele = new DefaultListModel<>();
        JList<String> liste = new JList<>(modele);

        // Configuration du rendu des lignes (Couleurs + Tooltip)
        liste.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (c instanceof JComponent) {
                    JComponent jc = (JComponent) c;
                    String txt = value.toString();
                    jc.setToolTipText(txt);

                    // On garde le style visuel simple pour la liste
                    jc.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Un peu d'air entre les lignes

                    if (txt.contains("URGENT")) {
                        c.setForeground(new Color(220, 53, 69)); // Rouge moderne
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(new Color(40, 167, 69)); // Vert moderne
                    }
                }
                return c;
            }
        });

        // Suppression des bordures du JScrollPane pour un look épuré
        JScrollPane scroll = new JScrollPane(liste);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        this.add(scroll, BorderLayout.CENTER);
    }

    /**
     * Méthode publique pour forcer la mise à jour.
     * CORRECTION : On ne touche plus aux bordures ici !
     */
    public void mettreAJour(String immat) {
        modele.clear();

        if (immat == null) {
            modele.addElement("Sélectionnez un véhicule.");
            return;
        }

        // Récupération des calculs depuis le Back
        List<String> urgences = garage.analyserUrgences(immat);

        if (urgences.isEmpty()) {
            modele.addElement("✅ Aucun entretien à prévoir.");
        } else {
            for (String s : urgences) {
                modele.addElement(s);
            }
        }

        this.repaint();
    }
}