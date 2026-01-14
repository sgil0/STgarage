package front;

import back.*;
import back.EnumType.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class PanneauGestionVehicules extends JPanel {

    private JTextField txtImmat, txtMarque, txtModele, txtKm;
    private JTextField txtRecherche;
    private DefaultTableModel modeleTable;
    private GestionGarage garage;
    private JTable table; // Promu en attribut de classe

    // Liste des écouteurs (Observer Pattern)
    private List<VehiculeSelectionListener> listeners = new ArrayList<>();

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

        // 1. Immat
        g.gridx=0; g.gridy=0; panelGauche.add(new JLabel("Immatriculation :"), g);
        txtImmat = new JTextField(12); g.gridx=1; panelGauche.add(txtImmat, g);

        // 2. Marque
        g.gridx=0; g.gridy=1; panelGauche.add(new JLabel("Marque :"), g);
        txtMarque = new JTextField(12); g.gridx=1; panelGauche.add(txtMarque, g);

        // 3. Modèle
        g.gridx=0; g.gridy=2; panelGauche.add(new JLabel("Modèle :"), g);
        txtModele = new JTextField(12); g.gridx=1; panelGauche.add(txtModele, g);

        // 4. Kilométrage
        g.gridx=0; g.gridy=3; panelGauche.add(new JLabel("Kilométrage :"), g);
        txtKm = new JTextField(12); g.gridx=1; panelGauche.add(txtKm, g);

        // Bouton Ajouter Véhicule
        JButton btnAjouter = new JButton("Ajouter Véhicule");
        g.gridx=0; g.gridy=5; g.gridwidth=2; panelGauche.add(btnAjouter, g);

        this.add(panelGauche);

        // --- DROITE : Recherche + Tableau ---
        JPanel panelDroit = new JPanel(new BorderLayout(0, 10));
        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pSearch.add(new JLabel("Rechercher :"));
        txtRecherche = new JTextField(15);
        pSearch.add(txtRecherche);
        panelDroit.add(pSearch, BorderLayout.NORTH);

        String[] colonnes = {"Immat", "Marque", "Modèle", "Propriétaire", "Email", "KM"};
        modeleTable = new DefaultTableModel(colonnes, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        // Configuration de la JTable avec écouteur de sélection
        table = new JTable(modeleTable);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                // On récupère l'immatriculation (colonne 0)
                String immat = (String) table.getValueAt(table.getSelectedRow(), 0);
                notifierLesEcouteurs(immat);
            }
        });

        panelDroit.add(new JScrollPane(table), BorderLayout.CENTER);
        this.add(panelDroit);

        // --- ACTIONS ---
        btnAjouter.addActionListener(e -> {
            try {
                String immat = txtImmat.getText().trim().toUpperCase();
                String marque = txtMarque.getText().trim();
                String modele = txtModele.getText().trim();
                String kmText = txtKm.getText().trim();

                if (immat.isEmpty() || marque.isEmpty() || modele.isEmpty() || kmText.isEmpty()) {
                    throw new Exception("Tous les champs sont obligatoires.");
                }

                // Vérif TypeVehicule
                TypeVehicule type = garage.trouverTypeVehicule(marque, modele);
                if (type == null) {
                    int choix = JOptionPane.showConfirmDialog(this,
                            "Modèle inconnu. Configurer " + marque + " " + modele + " ?", "Nouveau Modèle", JOptionPane.YES_NO_OPTION);
                    if (choix == JOptionPane.YES_OPTION) {
                        type = afficherFormulaireCreationType(marque, modele);
                        if (type != null) garage.creerTypeVehicule(type);
                        else return;
                    } else return;
                }

                float km = Float.parseFloat(kmText);

                // Création
                Vehicule v = new Vehicule(immat, java.time.LocalDate.now(), (int)km, type, null);
                garage.creerVehicule(v);

                JOptionPane.showMessageDialog(this, v.getTypeVehicule().getMarque() + " " + v.getTypeVehicule().getModele() + " ajoutée.");
                txtImmat.setText(""); txtKm.setText("");
                actualiserTableau(txtRecherche.getText());

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        txtRecherche.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { actualiserTableau(txtRecherche.getText()); }
        });

        actualiserTableau("");
    }

    public void ajouterEcouteurSelection(VehiculeSelectionListener listener) {
        this.listeners.add(listener);
    }

    private void notifierLesEcouteurs(String immat) {
        for (VehiculeSelectionListener listener : listeners) {
            listener.onVehiculeSelected(immat);
        }
    }

    private TypeVehicule afficherFormulaireCreationType(String marque, String modele) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        JComboBox<Energie> cbEnergie = new JComboBox<>(Energie.values());
        JComboBox<BoiteVitesse> cbBoite = new JComboBox<>(BoiteVitesse.values());
        JSpinner spinPortes = new JSpinner(new SpinnerNumberModel(5, 2, 9, 1));
        JSpinner spinPlaces = new JSpinner(new SpinnerNumberModel(5, 1, 9, 1));
        JTextField txtPuissance = new JTextField();

        panel.add(new JLabel("Énergie :")); panel.add(cbEnergie);
        panel.add(new JLabel("Boîte :")); panel.add(cbBoite);
        panel.add(new JLabel("Portes :")); panel.add(spinPortes);
        panel.add(new JLabel("Places :")); panel.add(spinPlaces);
        panel.add(new JLabel("Puissance (ch) :")); panel.add(txtPuissance);

        int result = JOptionPane.showConfirmDialog(null, panel, "Config : " + marque + " " + modele, JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                return new TypeVehicule(marque, modele, (Energie)cbEnergie.getSelectedItem(), (BoiteVitesse)cbBoite.getSelectedItem(),
                        (int)spinPlaces.getValue(), (int)spinPortes.getValue(), Integer.parseInt(txtPuissance.getText()));
            } catch (Exception e) { return null; }
        }
        return null;
    }

    private void actualiserTableau(String filtre) {
        List<Vehicule> resultats = garage.rechercherVehicules(filtre.trim());
        modeleTable.setRowCount(0);
        if (resultats != null) {
            for (Vehicule v : resultats) {
                String m = (v.getTypeVehicule() != null) ? v.getTypeVehicule().getMarque() : "Inconnue";
                String mod = (v.getTypeVehicule() != null) ? v.getTypeVehicule().getModele() : "Inconnu";
                String propNom = "Inconnu";
                String propMail = "Inconnu";

                if (v.getProprietaire() != null) {
                    propNom = v.getProprietaire().getIdentite();
                    propMail = v.getProprietaire().getMail();
                }
                modeleTable.addRow(new Object[]{
                        v.getImmatriculation(), m, mod, propNom, propMail, v.getKilometrage()
                });
            }
        }
    }
}