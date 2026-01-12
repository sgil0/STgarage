package front;

import javax.swing.*;
import java.awt.*;

public class FenetrePrincipale extends JFrame {

    public FenetrePrincipale() {
        // Configuration de la fenêtre
        this.setTitle("Auto21 - Gestion Garage");
        this.setSize(1200, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null); // Centrer

        this.setLayout(new BorderLayout());

        // 1. ZONE GAUCHE : Urgences (Toujours visible)
        this.add(new PanneauUrgences(), BorderLayout.WEST);

        // 2. ZONE CENTRE : Les Onglets
        JTabbedPane onglets = new JTabbedPane();

        // On ne passe plus "this" car il n'y a plus de listes partagées
        onglets.addTab("Gestion Véhicules", new PanneauGestionVehicules());
        onglets.addTab("Gestion Interventions", new PanneauGestionInterventions());

        this.add(onglets, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FenetrePrincipale fenetre = new FenetrePrincipale();
            fenetre.setVisible(true);
        });
    }
}