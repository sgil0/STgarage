package front;

import back.GestionGarage;

import javax.swing.*;
import java.awt.*;

public class FenetrePrincipale extends JFrame {

    private GestionGarage garage;

    public FenetrePrincipale() {
        // 1. Démarrer la connexion BDD
        this.garage = new GestionGarage();

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


        // 2. On passe 'garage' aux constructeurs qu'on vient de modifier
        onglets.addTab("Gestion Véhicules", new PanneauGestionVehicules(this.garage));
        onglets.addTab("Gestion Interventions", new PanneauGestionInterventions(this.garage));

        this.add(onglets, BorderLayout.CENTER);

        // 3. Fermer proprement la connexion quand on ferme la fenêtre
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