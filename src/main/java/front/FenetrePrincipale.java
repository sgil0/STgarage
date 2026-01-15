package front;

import back.GestionGarage;
import javax.swing.*;
import java.awt.*;

public class FenetrePrincipale extends JFrame {

    private GestionGarage garage;

    public FenetrePrincipale() {
        this.garage = new GestionGarage();

        this.setTitle("ST Garage - Gestion Garage");
        this.setSize(1300, 850);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        // Thème Nimbus (Optionnel, cf réponse précédente)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {}

        this.setLayout(new BorderLayout());

        // Création des deux gros panneaux
        PanneauGestionVehicules panneauVehicules = new PanneauGestionVehicules(this.garage);
        PanneauGestionInterventions panneauInterventions = new PanneauGestionInterventions(this.garage);

        // --- CÂBLAGE ---
        // Quand on clique sur une ligne dans l'onglet 1 :
        panneauVehicules.ajouterEcouteurSelection(immat -> {
            // L'onglet 1 a déjà mis à jour son panneau "Urgences" en interne.
            // On prévient juste l'onglet 2 pour charger le véhicule.
            panneauInterventions.chargerVehicule(immat);
        });

        // Onglets
        JTabbedPane onglets = new JTabbedPane();
        onglets.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // Astuce padding interne des onglets
        UIManager.put("TabbedPane.tabInsets", new Insets(10, 20, 10, 20));

        onglets.addTab("Gestion Vehicules", panneauVehicules);
        onglets.addTab("Gestion Interventions", panneauInterventions);

        this.add(onglets, BorderLayout.CENTER);

        // Fermeture propre
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                garage.fermer();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FenetrePrincipale f = new FenetrePrincipale();
            f.setVisible(true);
        });
    }
}