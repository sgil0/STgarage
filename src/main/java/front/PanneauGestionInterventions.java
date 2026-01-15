package front;

import back.*;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class PanneauGestionInterventions extends JPanel {

    // Couleurs & Styles Modernes
    private final Color COLOR_BG = new Color(240, 242, 245);
    private final Color COLOR_CARD = Color.WHITE;
    private final Color COLOR_PRIMARY = new Color(50, 100, 160);
    private final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 16);
    private final Font FONT_SUBTITLE = new Font("SansSerif", Font.BOLD, 13);

    private PanneauSchema2D schema2D;
    private JTextField txtKm;
    private DefaultTableModel modeleTableHistorique;
    private DefaultListModel<String> modelePanier;
    private GestionGarage garage;
    private JLabel lblVehiculeInfos;
    private Vehicule vehiculeActif;

    public PanneauGestionInterventions(GestionGarage garage) {
        this.garage = garage;

        // Configuration globale
        this.setLayout(new GridLayout(1, 2, 20, 0)); // 2 colonnes avec 20px d'écart
        this.setBackground(COLOR_BG);
        this.setBorder(new EmptyBorder(20, 20, 20, 20)); // Marges externes

        // ================= GAUCHE : CARTE "ATELIER" =================
        JPanel cardAtelier = creerCartePanel();

        // 1. En-tête de la carte
        cardAtelier.add(creerEnTete("🔧 Nouvelle Intervention"), BorderLayout.NORTH);

        // 2. Contenu Central (Infos + Schéma + Panier)
        JPanel contentAtelier = new JPanel(new BorderLayout(0, 15));
        contentAtelier.setBackground(COLOR_CARD);
        contentAtelier.setBorder(new EmptyBorder(15, 0, 0, 0));

        // 2a. Infos Véhicule (Design "Tag")
        lblVehiculeInfos = new JLabel("Veuillez sélectionner un véhicule...", SwingConstants.CENTER);
        lblVehiculeInfos.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblVehiculeInfos.setForeground(Color.GRAY);
        contentAtelier.add(lblVehiculeInfos, BorderLayout.NORTH);

        // 2b. Le Schéma (Centre)
        schema2D = new PanneauSchema2D(garage);
        // On retire la bordure noire moche du schéma original s'il y en a une,
        // ou on l'intègre proprement. Ici le schéma a son propre style.
        contentAtelier.add(schema2D, BorderLayout.CENTER);

        // 2c. Zone Basse (Panier + Validation)
        JPanel zoneAction = new JPanel(new BorderLayout(0, 10));
        zoneAction.setBackground(COLOR_CARD);

        // Titre discret pour le panier
        JLabel lblPanier = new JLabel("Détail du devis estimatif :");
        lblPanier.setFont(FONT_SUBTITLE);
        lblPanier.setForeground(Color.DARK_GRAY);
        zoneAction.add(lblPanier, BorderLayout.NORTH);

        // Liste Panier (Style épuré)
        modelePanier = new DefaultListModel<>();
        JList<String> listePanier = new JList<>(modelePanier);
        listePanier.setBackground(new Color(250, 250, 250)); // Gris très clair
        listePanier.setBorder(new LineBorder(new Color(230,230,230), 1));
        listePanier.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPanier = new JScrollPane(listePanier);
        scrollPanier.setPreferredSize(new Dimension(0, 100)); // Hauteur fixe pour le panier
        scrollPanier.setBorder(BorderFactory.createEmptyBorder());
        zoneAction.add(scrollPanier, BorderLayout.CENTER);

        // Formulaire KM + Bouton (Designé)
        JPanel panelForm = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panelForm.setBackground(COLOR_CARD);

        panelForm.add(new JLabel("Kilométrage actuel :"));
        txtKm = new JTextField(8);
        styleField(txtKm);
        panelForm.add(txtKm);

        JButton btnValider = new JButton("VALIDER & FACTURER");
        styleBoutonSuccess(btnValider);
        panelForm.add(btnValider);

        zoneAction.add(panelForm, BorderLayout.SOUTH);
        contentAtelier.add(zoneAction, BorderLayout.SOUTH);

        cardAtelier.add(contentAtelier, BorderLayout.CENTER);


        // ================= DROITE : CARTE "HISTORIQUE" =================
        JPanel cardHistorique = creerCartePanel();

        // 1. En-tête
        cardHistorique.add(creerEnTete("📋 Historique du Véhicule"), BorderLayout.NORTH);

        // 2. Tableau
        String[] cols = {"Date", "Intervention(s)", "Prix", "KM"};
        modeleTableHistorique = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable tableHist = new JTable(modeleTableHistorique);
        styleTable(tableHist); // Application du look moderne

        JScrollPane scrollHist = new JScrollPane(tableHist);
        scrollHist.setBorder(BorderFactory.createEmptyBorder());
        scrollHist.getViewport().setBackground(COLOR_CARD);

        // Petit hack pour ajouter une marge interne au contenu de la carte
        JPanel wrapTable = new JPanel(new BorderLayout());
        wrapTable.setBackground(COLOR_CARD);
        wrapTable.setBorder(new EmptyBorder(15, 0, 0, 0));
        wrapTable.add(scrollHist);

        cardHistorique.add(wrapTable, BorderLayout.CENTER);

        // Ajout des deux grandes cartes au panneau principal
        this.add(cardAtelier);
        this.add(cardHistorique);

        // ================= ACTIONS =================

        // Listener du schéma (Panier)
        schema2D.setListener(selection -> {
            modelePanier.clear();
            double total = 0;
            for (TypeIntervention t : selection) {
                modelePanier.addElement(String.format("• %-30s : %6.2f €", t.getNom(), t.getPrix()));
                total += t.getPrix();
            }
            if (total > 0) {
                modelePanier.addElement("------------------------------------------------");
                modelePanier.addElement(String.format("TOTAL ESTIMÉ                      : %6.2f €", total));
            }
        });

        btnValider.addActionListener(e -> validerIntervention());
    }

    // --- Helpers de Design "Tech Giant" ---

    /**
     * Crée le conteneur blanc avec ombre légère (Card).
     */
    private JPanel creerCartePanel() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COLOR_CARD);
        // Bordure grise fine + Marge interne généreuse
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(20, 25, 20, 25)
        ));
        return card;
    }

    /**
     * Crée un header propre : Titre + Ligne de séparation subtile.
     */
    private JPanel creerEnTete(String titre) {
        JPanel header = new JPanel(new BorderLayout(0, 10)); // 10px entre texte et ligne
        header.setBackground(COLOR_CARD);

        JLabel lbl = new JLabel(titre);
        lbl.setFont(FONT_TITLE);
        lbl.setForeground(new Color(33, 37, 41)); // Noir doux

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(230, 230, 230)); // Gris très clair

        header.add(lbl, BorderLayout.NORTH);
        header.add(sep, BorderLayout.SOUTH);
        return header;
    }

    private void styleBoutonSuccess(JButton btn) {
        btn.setBackground(new Color(40, 167, 69)); // Vert standard web
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20)); // Padding interne
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleField(JTextField txt) {
        txt.putClientProperty("JTextField.placeholderText", "00000"); // Marche si Nimbus ou FlatLaf
        txt.setFont(new Font("Monospaced", Font.PLAIN, 13));
    }

    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setGridColor(new Color(240, 240, 240));
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(248, 249, 250));
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setForeground(new Color(73, 80, 87));
        header.setBorder(BorderFactory.createMatteBorder(0,0,1,0, new Color(220,220,220)));
    }

    // --- Logique Métier (Inchangée) ---

    public void chargerVehicule(String immat) {
        // Recherche exacte
        List<Vehicule> resultats = garage.rechercherVehicules(immat);
        this.vehiculeActif = null;
        for(Vehicule v : resultats) {
            if(v.getImmatriculation().equals(immat)) {
                this.vehiculeActif = v;
                break;
            }
        }

        if (vehiculeActif == null) return;

        // Mise à jour de l'UI avec du HTML pour le style
        lblVehiculeInfos.setText("<html><span style='color:#555;'>Véhicule : </span><b>" +
                vehiculeActif.getImmatriculation() + "</b> &nbsp;|&nbsp; " +
                vehiculeActif.getTypeVehicule().getMarque() + " " + vehiculeActif.getTypeVehicule().getModele() +
                " &nbsp;|&nbsp; " + vehiculeActif.getProprietaire().getNom() + "</html>");

        txtKm.setText(String.valueOf((int)vehiculeActif.getKilometrage()));
        schema2D.setVehicule(vehiculeActif);
        chargerHistorique();
    }

    private void chargerHistorique() {
        modeleTableHistorique.setRowCount(0);
        if (vehiculeActif == null) return;

        List<Intervention> historique = garage.getHistoriqueVehicule(vehiculeActif.getImmatriculation());
        for (Intervention i : historique) {
            StringBuilder types = new StringBuilder();
            for (TypeIntervention t : i.getTypesIntervention()) {
                if (!types.isEmpty()) types.append(", ");
                types.append(t.getNom());
            }
            modeleTableHistorique.addRow(new Object[]{
                    i.getDate(),
                    types.toString(),
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
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Le kilométrage saisi ("+km+") est inférieur à l'actuel ("+vehiculeActif.getKilometrage()+"). Confirmer ?",
                        "Alerte Incohérence", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;
            }

            List<TypeIntervention> selection = schema2D.getTypesSelectionnes();
            if (selection.isEmpty()) throw new Exception("Veuillez sélectionner au moins une zone sur le schéma.");

            garage.creerIntervention(vehiculeActif.getImmatriculation(), selection, km);
            JOptionPane.showMessageDialog(this, "Intervention validée et facturée !");
            chargerVehicule(vehiculeActif.getImmatriculation());

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Kilométrage invalide.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }
}