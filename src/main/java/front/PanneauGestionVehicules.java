package front;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PanneauGestionVehicules extends JPanel {

    private JTextField txtImmat, txtModele, txtKm;
    private JTextField txtRecherche;
    private DefaultTableModel modeleTable;

    public PanneauGestionVehicules() {
        this.setLayout(new GridLayout(1, 2, 10, 0));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- GAUCHE : Formulaire Ajout ---
        JPanel panelGauche = new JPanel(new GridBagLayout());
        panelGauche.setBorder(BorderFactory.createTitledBorder("Nouveau Véhicule"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx=0; g.gridy=0; panelGauche.add(new JLabel("Immatriculation :"), g);
        txtImmat = new JTextField(12);
        g.gridx=1; panelGauche.add(txtImmat, g);

        g.gridx=0; g.gridy=1; panelGauche.add(new JLabel("Modèle :"), g);
        txtModele = new JTextField(12);
        g.gridx=1; panelGauche.add(txtModele, g);

        g.gridx=0; g.gridy=2; panelGauche.add(new JLabel("Kilométrage :"), g);
        txtKm = new JTextField(12);
        g.gridx=1; panelGauche.add(txtKm, g);

        JButton btnAjouter = new JButton("Ajouter");
        g.gridx=0; g.gridy=3; g.gridwidth=2;
        panelGauche.add(btnAjouter, g);

        this.add(panelGauche);

        // --- DROITE : Recherche + Tableau ---
        JPanel panelDroit = new JPanel(new BorderLayout(0, 10));

        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pSearch.add(new JLabel("Rechercher :"));
        txtRecherche = new JTextField(15);
        pSearch.add(txtRecherche);
        panelDroit.add(pSearch, BorderLayout.NORTH);

        String[] colonnes = {"Immat", "Modèle", "KM"};
        modeleTable = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(modeleTable);
        panelDroit.add(new JScrollPane(table), BorderLayout.CENTER);

        this.add(panelDroit);

        // --- ACTIONS ---
        btnAjouter.addActionListener(e -> {
            try {
                // 1. Récupération des données
                String immat = txtImmat.getText().trim().toUpperCase();
                String modele = txtModele.getText();
                String km = txtKm.getText();

                // Validation de base
                if (immat.isEmpty()) throw new Exception("Le champ Immatriculation est vide");
                if (modele.isEmpty()) throw new Exception("Le champ Modèle est vide");

                // 2. TENTATIVE D'ENVOI AU BACKEND (BDD)
                // C'est ici que tu mettras ta requête JPA plus tard.
                // Pour l'instant, comme il n'y a pas de lien, on lève une erreur volontaire.

                boolean bddConnectee = false; // Variable temporaire pour le TP
                if (!bddConnectee) {
                    throw new Exception("Aucune connexion à la base de données n'est active.\nImpossible de vérifier les doublons ou d'enregistrer.");
                }

                // --- SI L'AJOUT BDD REUSSIT (Code futur) ---
                JOptionPane.showMessageDialog(this, "Ajout réussi !");

                // Reset champs
                txtImmat.setText(""); txtModele.setText(""); txtKm.setText("");

            } catch (Exception ex) {
                // 3. GESTION DES ERREURS (C'est ici qu'on arrive maintenant)
                JOptionPane.showMessageDialog(this,
                        "L'ajout a échoué.\nCause : " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        txtRecherche.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                // TODO: Recherche BDD
            }
        });
    }
}