package front;

import back.GestionGarage;
import javax.swing.*;
import java.awt.*;

public class FenetrePrincipale extends JFrame {

    // =========================================================================
    // Attributs
    // =========================================================================

    private static final Color COLOR_BG = new Color(45, 45, 45);
    private static final Color COLOR_ORANGE = new Color(233, 84, 32);
    private static final Color COLOR_TEXT = new Color(245, 245, 245);

    private final GestionGarage garage;

    // =========================================================================
    // Constructeurs
    // =========================================================================

    public FenetrePrincipale() {
        this.garage = new GestionGarage();
        this.setTitle("Auto2I - Gestion Garage");
        this.setSize(1350, 850);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        // Appliquer le fond global à la fenêtre
        this.getContentPane().setBackground(COLOR_BG);
        this.setLayout(new BorderLayout());

        PanneauGestionVehicules panneauVehicules = new PanneauGestionVehicules(this.garage);
        PanneauGestionInterventions panneauInterventions = new PanneauGestionInterventions(this.garage);

        // Liaison entre les onglets
        panneauVehicules.ajouterEcouteurSelection(immat -> {
            panneauInterventions.chargerVehicule(immat);
        });

        // Configuration des onglets
        JTabbedPane onglets = new JTabbedPane();
        onglets.setFont(new Font("SansSerif", Font.BOLD, 14));
        onglets.setBackground(COLOR_BG);
        onglets.setForeground(Color.LIGHT_GRAY);

        onglets.addTab("  Gestion Véhicules  ", panneauVehicules);
        onglets.addTab("  Gestions Interventions  ", panneauInterventions);

        // Quand on change d'onglet, on met à jour les données
        onglets.addChangeListener(e -> {
            if (onglets.getSelectedIndex() == 0) {
                panneauVehicules.rechargerDonnees();
            }
        });

        onglets.setBorder(BorderFactory.createEmptyBorder());
        this.add(onglets, BorderLayout.CENTER);
        onglets.setBorder(BorderFactory.createEmptyBorder());

        this.add(onglets, BorderLayout.CENTER);

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                garage.fermer();
            }
        });
    }

    // =========================================================================
    // Méthodes
    // =========================================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }

                // Configuration du Thème Global
                // FONDS
                UIManager.put("control", COLOR_BG);
                UIManager.put("nimbusBase", COLOR_BG);
                UIManager.put("nimbusFocus", COLOR_ORANGE);
                UIManager.put("OptionPane.background", COLOR_BG);
                UIManager.put("Panel.background", COLOR_BG);

                // TEXTES
                UIManager.put("text", COLOR_TEXT);
                UIManager.put("controlText", COLOR_TEXT);
                UIManager.put("infoText", COLOR_TEXT);
                UIManager.put("OptionPane.messageForeground", COLOR_TEXT);
                UIManager.put("Label.foreground", COLOR_TEXT);

                // BOUTONS
                UIManager.put("Button.background", new Color(70, 70, 70));
                UIManager.put("Button.foreground", Color.WHITE);

                // ONGLETS
                UIManager.put("TabbedPane.background", COLOR_BG);
                UIManager.put("TabbedPane.foreground", COLOR_TEXT);
                UIManager.put("TabbedPane.selected", COLOR_ORANGE);
                UIManager.put("TabbedPane.selectedForeground", Color.WHITE);
                UIManager.put("TabbedPane.contentAreaColor", COLOR_BG);
                UIManager.put("TabbedPane.shadow", COLOR_BG);

                // SCROLLBAR
                UIManager.put("ScrollBar.thumb", new Color(80, 80, 80));
                UIManager.put("ScrollBar.track", COLOR_BG);

                // TOOLTIPS
                Color tooltipBg = new Color(60, 60, 60);
                UIManager.put("ToolTip.background", tooltipBg);
                UIManager.put("ToolTip.foreground", Color.WHITE);
                UIManager.put("ToolTip.border", BorderFactory.createLineBorder(new Color(100, 100, 100)));
                UIManager.put("info", tooltipBg);

            } catch (Exception e) {
                e.printStackTrace();
            }

            FenetrePrincipale f = new FenetrePrincipale();
            f.setVisible(true);
        });
    }
}