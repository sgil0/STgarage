package front;

import back.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PanneauGestionVehicules extends JPanel {

    private JTextField txtImmat, txtModele, txtKm;
    private JTextField txtRecherche;
    private DefaultTableModel modeleTable;
    private GestionGarage garage;

    public PanneauGestionVehicules(GestionGarage garage) {
        this.setLayout(new GridLayout(1, 2, 10, 0));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.garage = garage;
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

        // DROITE : Recherche + Tableau
        JPanel panelDroit = new JPanel(new BorderLayout(0, 10));

        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pSearch.add(new JLabel("Rechercher :"));
        txtRecherche = new JTextField(15);
        pSearch.add(txtRecherche);
        panelDroit.add(pSearch, BorderLayout.NORTH);

        String[] colonnes = {"Immat", "Marque", "Modèle", "KM"};
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
                // 1. Récupération et nettoyage des données
                String immat = txtImmat.getText().trim().toUpperCase();
                String modele = txtModele.getText().trim();
                String kmText = txtKm.getText().trim();

                // 2. Validations de base
                if (immat.isEmpty()) throw new Exception("L'immatriculation est obligatoire.");
                if (modele.isEmpty()) throw new Exception("Le modèle est obligatoire.");
                if (kmText.isEmpty()) throw new Exception("Le kilométrage est obligatoire.");

                // Conversion du kilométrage (risque d'erreur si ce n'est pas un nombre)
                float km;
                try {
                    km = Float.parseFloat(kmText);
                } catch (NumberFormatException nfe) {
                    throw new Exception("Le kilométrage doit être un nombre valide (ex: 50000).");
                }

                // 3. Création de l'objet Véhicule
                // (On met null pour le client et le type pour l'instant)
                Vehicule v = new Vehicule(immat, java.time.LocalDate.now(), (int) km, null); // Note: casting en int car votre constructeur prend un int actuellement

                // 4. ENVOI AU BACKEND
                garage.creerVehicule(v);

                // 5. Succès
                JOptionPane.showMessageDialog(this, "Véhicule " + immat + " ajouté avec succès !");

                // Reset des champs pour la prochaine saisie
                txtImmat.setText("");
                txtModele.setText("");
                txtKm.setText("");

            } catch (Exception ex) {
                // Affichage propre de l'erreur
                JOptionPane.showMessageDialog(this,
                        "Erreur : " + ex.getMessage(),
                        "Impossible d'ajouter",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        txtRecherche.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String texte = txtRecherche.getText().trim();
                java.util.List<back.Vehicule> resultats = garage.rechercherVehicules(texte);

                // 1. On vide le tableau (très important !)
                modeleTable.setRowCount(0);

                // 2. On remplit s'il y a des résultats
                if (!resultats.isEmpty()) {
                    for (back.Vehicule v : resultats) {
                        // Récupération sécurisée du modèle
                        String modeleAffiche = "Inconnu";
                        if (v.getTypeVehicule() != null) {
                            modeleAffiche = v.getTypeVehicule().getMarque() + " " + v.getTypeVehicule().getModele();
                        }

                        modeleTable.addRow(new Object[]{
                                v.getImmatriculation(),
                                v.getTypeVehicule().getMarque(),
                                v.getTypeVehicule().getModele(),
                                v.getKilometrage()
                        });
                    }
                }
            }
        });
    }
}