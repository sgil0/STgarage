package front;

import back.GestionGarage;
import javax.swing.*;
import java.awt.*;

public class FenetrePrincipale extends JFrame {

    private GestionGarage garage;

    public FenetrePrincipale() {
        this.garage = new GestionGarage();

        this.setTitle("Auto21 - Gestion Garage");
        this.setSize(1300, 850);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        this.setLayout(new BorderLayout());

        // Création des panneaux
        PanneauUrgences panneauUrgences = new PanneauUrgences(this.garage);
        PanneauGestionVehicules panneauVehicules = new PanneauGestionVehicules(this.garage);

        // ATTENTION : On utilise le nouveau panneau intelligent
        PanneauGestionInterventions panneauInterventions = new PanneauGestionInterventions(this.garage);

        // --- CÂBLAGE MAGIQUE ---

        // Quand on clique sur un véhicule :
        panneauVehicules.ajouterEcouteurSelection(immat -> {
            // 1. On met à jour la sidebar Urgences
            panneauUrgences.mettreAJour(immat);

            // 2. On charge automatiquement le véhicule dans l'onglet Interventions
            panneauInterventions.chargerVehicule(immat);
        });

        this.add(panneauUrgences, BorderLayout.WEST);

        JTabbedPane onglets = new JTabbedPane();
        onglets.addTab("Gestion Véhicules", panneauVehicules);
        onglets.addTab("Gestion Interventions", panneauInterventions);

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
            FenetrePrincipale fenetre = new FenetrePrincipale();
            fenetre.setVisible(true);
        });
    }
}