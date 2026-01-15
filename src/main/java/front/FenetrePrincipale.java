package front;

import back.GestionGarage;
import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;

public class FenetrePrincipale extends JFrame {

    private GestionGarage garage;

    // Couleurs Ubuntu
    private static final Color UBUNTU_BG = new Color(45, 45, 45);
    private static final Color UBUNTU_ORANGE = new Color(233, 84, 32);
    private static final Color UBUNTU_TEXT = new Color(245, 245, 245);

    public FenetrePrincipale() {
        this.garage = new GestionGarage();
        this.setTitle("ST Garage - Gestion Garage");
        this.setSize(1350, 850);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        // Appliquer le fond global à la fenêtre
        this.getContentPane().setBackground(UBUNTU_BG);

        this.setLayout(new BorderLayout());

        PanneauGestionVehicules panneauVehicules = new PanneauGestionVehicules(this.garage);
        PanneauGestionInterventions panneauInterventions = new PanneauGestionInterventions(this.garage);

        // Liaison entre les onglets
        panneauVehicules.ajouterEcouteurSelection(immat -> {
            panneauInterventions.chargerVehicule(immat);
        });

        // --- CUSTOMISATION DES ONGLETS ---
        JTabbedPane onglets = new JTabbedPane();
        onglets.setFont(new Font("SansSerif", Font.BOLD, 14));
        onglets.setBackground(UBUNTU_BG); // Fond de la barre d'onglets
        onglets.setForeground(Color.LIGHT_GRAY); // Couleur texte onglet inactif

        // Astuce : On ne peut pas changer la couleur "Selected" facilement en pur Nimbus via code
        // sans toucher à l'UIManager global (fait dans le main), mais on peut tweaker ici :
        onglets.addTab("  GESTION VEHICULES  ", panneauVehicules);
        onglets.addTab("  GESTION INTERVENTIONS  ", panneauInterventions);

        // Retirer la bordure blanche autour du contenu
        onglets.setBorder(BorderFactory.createEmptyBorder());

        this.add(onglets, BorderLayout.CENTER);

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                garage.fermer();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // 1. Activation de Nimbus
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }

                // 2. --- THEME UBUNTU DARK GLOBAL ---

                // A. FONDS (Backgrounds)
                UIManager.put("control", UBUNTU_BG);          // Fond fenêtres
                UIManager.put("nimbusBase", UBUNTU_BG);       // Base Nimbus
                UIManager.put("nimbusFocus", UBUNTU_ORANGE);  // Focus global
                UIManager.put("OptionPane.background", UBUNTU_BG);
                UIManager.put("Panel.background", UBUNTU_BG);

                // B. TEXTES (C'est ici que ça se joue !)
                // On écrase les couleurs "système" qui forcent le noir
                UIManager.put("text", UBUNTU_TEXT);           // Texte par défaut
                UIManager.put("controlText", UBUNTU_TEXT);    // Texte des contrôles
                UIManager.put("infoText", UBUNTU_TEXT);       // Texte d'info (tooltips)

                // Spécifique OptionPane (Message & Titre interne)
                UIManager.put("OptionPane.messageForeground", UBUNTU_TEXT);
                UIManager.put("OptionPane.foreground", UBUNTU_TEXT);

                // Labels standards
                UIManager.put("Label.foreground", UBUNTU_TEXT);

                // C. BOUTONS (Dans les popups)
                UIManager.put("Button.background", new Color(70, 70, 70));
                UIManager.put("Button.foreground", Color.WHITE);

                // D. ONGLETS (TabbedPane)
                UIManager.put("TabbedPane.background", UBUNTU_BG);
                UIManager.put("TabbedPane.foreground", UBUNTU_TEXT);
                UIManager.put("TabbedPane.selected", UBUNTU_ORANGE);
                UIManager.put("TabbedPane.selectedForeground", Color.WHITE);
                UIManager.put("TabbedPane.contentAreaColor", UBUNTU_BG);
                UIManager.put("TabbedPane.shadow", UBUNTU_BG);

                // E. SCROLLBAR
                UIManager.put("ScrollBar.thumb", new Color(80, 80, 80));
                UIManager.put("ScrollBar.track", UBUNTU_BG);

            } catch (Exception e) {
                e.printStackTrace();
            }

            FenetrePrincipale f = new FenetrePrincipale();
            f.setVisible(true);
        });
    }
}