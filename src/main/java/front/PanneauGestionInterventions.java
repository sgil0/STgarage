package front;

import back.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// Ce panneau doit pouvoir charger un véhicule quand on lui demande
public class PanneauGestionInterventions extends JPanel {

    private PanneauSchema2D schema2D;
    private JTextField txtKm;
    private DefaultTableModel modeleTableHistorique;
    private DefaultListModel<String> modelePanier; // Pour afficher la liste des sélections
    private GestionGarage garage;
    private JLabel lblVehiculeInfos;

    // On garde le véhicule actif en mémoire
    private Vehicule vehiculeActif;

    public PanneauGestionInterventions(GestionGarage garage) {
        this.setLayout(new GridLayout(1, 2, 10, 0));
        this.garage = garage;

        // ================= GAUCHE : Schéma + Panier + Validation =================
        JPanel panelGauche = new JPanel(new BorderLayout());

        // 1. Infos du véhicule (En haut)
        lblVehiculeInfos = new JLabel("<html><center><h3>Aucun véhicule sélectionné</h3>Veuillez sélectionner un véhicule dans l'onglet 'Gestion Véhicules'</center></html>", SwingConstants.CENTER);
        lblVehiculeInfos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelGauche.add(lblVehiculeInfos, BorderLayout.NORTH);

        // 2. Le Schéma (Au centre)
        schema2D = new PanneauSchema2D(garage);
        schema2D.setPreferredSize(new Dimension(0, 300));
        panelGauche.add(schema2D, BorderLayout.CENTER);

        // 3. Zone Panier + Validation (En bas)
        JPanel panelBas = new JPanel(new BorderLayout());
        panelBas.setBorder(BorderFactory.createTitledBorder("Nouvelle Intervention"));
        panelBas.setPreferredSize(new Dimension(0, 200));

        // 3a. Liste des sélections (Le Panier)
        modelePanier = new DefaultListModel<>();
        JList<String> listePanier = new JList<>(modelePanier);
        listePanier.setBackground(new Color(245, 245, 245));
        panelBas.add(new JScrollPane(listePanier), BorderLayout.CENTER);

        // Connecter le schéma à la liste
        schema2D.setListener(selection -> {
            modelePanier.clear();
            double total = 0;
            for (TypeIntervention t : selection) {
                modelePanier.addElement("• " + t.getNom() + " : " + t.getPrix() + " €");
                total += t.getPrix();
            }
            if (total > 0) {
                modelePanier.addElement("--------------------------------");
                modelePanier.addElement("TOTAL ESTIMÉ : " + total + " €");
            }
        });

        // 3b. Formulaire KM + Bouton
        JPanel panelForm = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelForm.add(new JLabel("Nouveau KM :"));
        txtKm = new JTextField(8);
        panelForm.add(txtKm);

        JButton btnValider = new JButton("Valider & Facturer");
        btnValider.setBackground(new Color(200, 255, 200));
        btnValider.setFont(new Font("Arial", Font.BOLD, 12));
        panelForm.add(btnValider);

        panelBas.add(panelForm, BorderLayout.SOUTH);
        panelGauche.add(panelBas, BorderLayout.SOUTH);

        this.add(panelGauche);


        // ================= DROITE : Historique =================
        JPanel panelDroit = new JPanel(new BorderLayout());
        panelDroit.setBorder(BorderFactory.createTitledBorder("Historique des Interventions"));

        String[] cols = {"Date", "Type(s)", "Prix", "KM"};
        modeleTableHistorique = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable tableHist = new JTable(modeleTableHistorique);
        tableHist.setRowHeight(25);
        panelDroit.add(new JScrollPane(tableHist), BorderLayout.CENTER);

        this.add(panelDroit);


        // ================= ACTIONS =================
        btnValider.addActionListener(e -> validerIntervention());
    }

    /**
     * Cette méthode est appelée par la Fenêtre Principale quand on clique dans l'autre onglet.
     */
    public void chargerVehicule(String immat) {
        // 1. Récupérer le véhicule depuis la BDD
        List<Vehicule> resultats = garage.rechercherVehicules(immat);
        // On filtre pour être sûr d'avoir l'exact match
        this.vehiculeActif = null;
        for(Vehicule v : resultats) {
            if(v.getImmatriculation().equals(immat)) {
                this.vehiculeActif = v;
                break;
            }
        }

        if (vehiculeActif == null) return;

        // 2. Mettre à jour les infos Visuelles
        String infos = "<html><center><h3>" + vehiculeActif.getImmatriculation() + "</h3>" +
                vehiculeActif.getTypeVehicule().getMarque() + " " + vehiculeActif.getTypeVehicule().getModele() +
                " (" + vehiculeActif.getTypeVehicule().getEnergie() + ")<br>" +
                "Client : " + vehiculeActif.getProprietaire().getNom() + "</center></html>";
        lblVehiculeInfos.setText(infos);

        // 3. Pré-remplir le KM
        txtKm.setText(String.valueOf((int)vehiculeActif.getKilometrage()));

        // 4. Activer le schéma
        schema2D.setVehicule(vehiculeActif);

        // 5. Charger l'historique
        chargerHistorique();
    }

    private void chargerHistorique() {
        modeleTableHistorique.setRowCount(0);
        if (vehiculeActif == null) return;

        List<Intervention> historique = garage.getHistoriqueVehicule(vehiculeActif.getImmatriculation());

        for (Intervention i : historique) {
            StringBuilder types = new StringBuilder();
            // Grâce au correctif Backend, cette liste n'est plus vide !
            for (TypeIntervention t : i.getTypesIntervention()) {
                if (!types.isEmpty()) types.append(", "); // Ajoute une virgule entre les types
                types.append(t.getNom());
            }
            modeleTableHistorique.addRow(new Object[]{
                    i.getDate(),
                    types.toString(), // On affiche la chaîne construite (ex: "Vidange, Pneus AV")
                    i.getPrix() + " €",
                    (int)i.getKilometrage() + " km"
            });
        }
    }

    private void validerIntervention() {
        if (vehiculeActif == null) {
            JOptionPane.showMessageDialog(this, "Aucun véhicule chargé.");
            return;
        }

        try {
            float km = Float.parseFloat(txtKm.getText());
            if (km < vehiculeActif.getKilometrage()) {
                JOptionPane.showMessageDialog(this, "Attention : Le nouveau kilométrage est inférieur à l'ancien !", "Alerte", JOptionPane.WARNING_MESSAGE);
            }

            List<TypeIntervention> selection = schema2D.getTypesSelectionnes();
            if (selection.isEmpty()) {
                throw new Exception("Le panier est vide. Sélectionnez des zones sur la voiture.");
            }

            // Création en BDD
            garage.creerIntervention(vehiculeActif.getImmatriculation(), selection, km);

            JOptionPane.showMessageDialog(this, "Intervention validée avec succès !");

            // On recharge tout pour mettre à jour l'historique et le KM
            chargerVehicule(vehiculeActif.getImmatriculation());

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Kilométrage invalide.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }
}