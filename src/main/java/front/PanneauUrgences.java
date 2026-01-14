package front;

import back.GestionGarage;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PanneauUrgences extends JPanel implements VehiculeSelectionListener {

    private DefaultListModel<String> modele;
    private GestionGarage garage;

    public PanneauUrgences(GestionGarage garage) {
        this.garage = garage;
        this.setLayout(new BorderLayout());

        // Largeur fixe pour la sidebar
        this.setPreferredSize(new Dimension(320, 0));

        this.setBorder(BorderFactory.createTitledBorder("URGENCES / ENTRETIENS"));
        this.setBackground(new Color(255, 250, 250));

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
                    jc.setToolTipText(txt); // Affiche le texte complet au survol

                    if (txt.contains("URGENT")) {
                        c.setForeground(Color.RED);
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(new Color(0, 100, 0)); // Vert foncé
                    }
                }
                return c;
            }
        });

        this.add(new JScrollPane(liste), BorderLayout.CENTER);
    }


    /**
     * Méthode de l'interface VehiculeSelectionListener.
     * Elle redirige simplement vers mettreAJour().
     */
    @Override
    public void onVehiculeSelected(String immat) {
        mettreAJour(immat);
    }

    /**
     * Méthode publique pour forcer la mise à jour (appelée par FenetrePrincipale).
     */
    public void mettreAJour(String immat) {
        modele.clear();

        // Si aucun véhicule n'est sélectionné (ex: au démarrage)
        if (immat == null) {
            ((javax.swing.border.TitledBorder)getBorder()).setTitle("URGENCES");
            this.repaint();
            return;
        }

        // Récupération des calculs depuis le Back
        List<String> urgences = garage.analyserUrgences(immat);

        if (urgences.isEmpty()) {
            modele.addElement("Aucun entretien configuré.");
        } else {
            for (String s : urgences) {
                modele.addElement(s);
            }
        }

        // Mise à jour du titre
        ((javax.swing.border.TitledBorder)getBorder()).setTitle("URGENCES : " + immat);
        this.repaint();
    }
}