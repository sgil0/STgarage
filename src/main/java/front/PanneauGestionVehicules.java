package front;

import back.GestionGarage;
import back.Vehicule;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class PanneauGestionVehicules extends JPanel {

    // --- CHARTE GRAPHIQUE (Idem PanneauInterventions) ---
    private final Color COLOR_BG = new Color(240, 242, 245);
    private final Color COLOR_CARD = Color.WHITE;
    private final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 16);

    private GestionGarage garage;
    private JTable table;
    private DefaultTableModel modeleTable;
    private JTextField txtRecherche;
    private PanneauUrgences panneauUrgences;

    // Listeners vers l'extérieur
    private List<VehiculeSelectionListener> externalListeners = new ArrayList<>();

    public PanneauGestionVehicules(GestionGarage garage) {
        this.garage = garage;

        // Layout principal : BorderLayout pour gérer la Sidebar à droite
        this.setLayout(new BorderLayout(20, 0)); // 20px d'écart horizontal
        this.setBackground(COLOR_BG);
        this.setBorder(new EmptyBorder(20, 20, 20, 20)); // Marges externes

        // ================= GAUCHE : CARTE "FLOTTE" (Tableau) =================
        JPanel cardFlotte = creerCartePanel();

        // 1. En-tête avec Actions intégrées
        JPanel headerFlotte = new JPanel(new BorderLayout(0, 15));
        headerFlotte.setBackground(COLOR_CARD);
        headerFlotte.setBorder(new EmptyBorder(0, 0, 15, 0)); // Marge sous le header

        // Titre + Séparateur
        headerFlotte.add(creerEnTete("🚗 Parc Automobile"), BorderLayout.NORTH);

        // Barre d'outils (Recherche + Bouton)
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(COLOR_CARD);

        // Zone Recherche
        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pSearch.setBackground(COLOR_CARD);
        pSearch.add(new JLabel("Rechercher :  "));
        txtRecherche = new JTextField(20);
        styleField(txtRecherche);
        pSearch.add(txtRecherche);
        toolbar.add(pSearch, BorderLayout.WEST);

        // Bouton Nouveau
        JButton btnAdd = new JButton("+ NOUVEAU VÉHICULE");
        styleBoutonSuccess(btnAdd);
        btnAdd.addActionListener(e -> ouvrirPopupCreation());
        toolbar.add(btnAdd, BorderLayout.EAST);

        headerFlotte.add(toolbar, BorderLayout.CENTER);
        cardFlotte.add(headerFlotte, BorderLayout.NORTH);


        // 2. Tableau Central
        String[] cols = {"Immat", "Marque", "Modèle", "Propriétaire", "KM"};
        modeleTable = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(modeleTable);
        styleTable(table);

        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.setBorder(BorderFactory.createEmptyBorder());
        scrollTable.getViewport().setBackground(COLOR_CARD);

        cardFlotte.add(scrollTable, BorderLayout.CENTER);

        this.add(cardFlotte, BorderLayout.CENTER);


        // ================= DROITE : CARTE "URGENCES" =================
        JPanel cardUrgences = creerCartePanel();
        cardUrgences.setPreferredSize(new Dimension(350, 0)); // Largeur fixe sidebar

        // 1. En-tête
        // On utilisera ce label pour afficher l'immat sélectionnée dynamiquement
        JLabel lblTitreUrgences = new JLabel("Notifications");
        cardUrgences.add(creerEnTeteDynamique("🔔 État du Véhicule", lblTitreUrgences), BorderLayout.NORTH);

        // 2. Intégration du PanneauUrgences existant
        panneauUrgences = new PanneauUrgences(garage);

        // ASTUCE DESIGN : On retire la bordure "Old School" du composant original
        // pour qu'il s'intègre proprement dans notre Carte Blanche.
        panneauUrgences.setBorder(new EmptyBorder(10, 0, 0, 0));
        panneauUrgences.setBackground(COLOR_CARD); // Fond blanc pour fondre

        // On doit aussi s'assurer que la JList interne n'a pas de fond gris bizarre
        // (Cela dépend de l'implémentation de PanneauUrgences, mais on force le container ici)

        cardUrgences.add(panneauUrgences, BorderLayout.CENTER);

        this.add(cardUrgences, BorderLayout.EAST);


        // ================= LOGIQUE (Inchangée) =================

        // Sélection
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                String immat = (String) table.getValueAt(table.getSelectedRow(), 0);

                // Maj Titre Droite
                lblTitreUrgences.setText(immat);
                lblTitreUrgences.setForeground(COLOR_PRIMARY); // Petit accent couleur

                // Maj Contenu
                panneauUrgences.mettreAJour(immat);

                // Propagation
                notifierExternalListeners(immat);
            }
        });

        // Recherche
        txtRecherche.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { actualiserTableau(txtRecherche.getText()); }
        });

        actualiserTableau("");
    }

    // --- Helpers Design (Copie conforme pour cohérence) ---

    private JPanel creerCartePanel() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COLOR_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(20, 25, 20, 25)
        ));
        return card;
    }

    private JPanel creerEnTete(String titre) {
        JPanel header = new JPanel(new BorderLayout(0, 10));
        header.setBackground(COLOR_CARD);
        JLabel lbl = new JLabel(titre);
        lbl.setFont(FONT_TITLE);
        lbl.setForeground(new Color(33, 37, 41));
        header.add(lbl, BorderLayout.NORTH);
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(230, 230, 230));
        header.add(sep, BorderLayout.SOUTH);
        return header;
    }

    // Variante pour avoir un sous-titre dynamique à droite (ex: Immatriculation)
    private JPanel creerEnTeteDynamique(String titre, JLabel labelDynamique) {
        JPanel header = new JPanel(new BorderLayout(0, 10));
        header.setBackground(COLOR_CARD);

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(COLOR_CARD);

        JLabel lbl = new JLabel(titre);
        lbl.setFont(FONT_TITLE);
        lbl.setForeground(new Color(33, 37, 41));

        labelDynamique.setFont(new Font("SansSerif", Font.BOLD, 14));
        labelDynamique.setForeground(Color.GRAY);

        topRow.add(lbl, BorderLayout.WEST);
        topRow.add(labelDynamique, BorderLayout.EAST);

        header.add(topRow, BorderLayout.NORTH);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(230, 230, 230));
        header.add(sep, BorderLayout.SOUTH);
        return header;
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
        table.setSelectionBackground(new Color(232, 242, 252));
        table.setSelectionForeground(Color.BLACK);
    }

    private void styleBoutonSuccess(JButton btn) {
        btn.setBackground(new Color(40, 167, 69));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleField(JTextField txt) {
        txt.putClientProperty("JTextField.placeholderText", "Rechercher...");
        txt.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200)),
                new EmptyBorder(5, 5, 5, 5)));
    }

    // --- Couleurs locales ---
    private final Color COLOR_PRIMARY = new Color(50, 100, 160);

    // --- Logique Métier ---

    private void ouvrirPopupCreation() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        DialogAjoutVehicule dialog = new DialogAjoutVehicule(topFrame, garage);
        dialog.setVisible(true);
        if (dialog.isValide()) {
            actualiserTableau("");
            JOptionPane.showMessageDialog(this, "Véhicule ajouté au parc.");
        }
    }

    private void actualiserTableau(String filtre) {
        List<Vehicule> resultats = garage.rechercherVehicules(filtre.trim());
        modeleTable.setRowCount(0);
        for (Vehicule v : resultats) {
            String m = (v.getTypeVehicule() != null) ? v.getTypeVehicule().getMarque() : "?";
            String mod = (v.getTypeVehicule() != null) ? v.getTypeVehicule().getModele() : "?";
            String prop = (v.getProprietaire() != null) ? v.getProprietaire().getIdentite() : "?";
            modeleTable.addRow(new Object[]{v.getImmatriculation(), m, mod, prop, v.getKilometrage()});
        }
    }

    public void ajouterEcouteurSelection(VehiculeSelectionListener listener) {
        this.externalListeners.add(listener);
    }

    private void notifierExternalListeners(String immat) {
        for (VehiculeSelectionListener l : externalListeners) l.onVehiculeSelected(immat);
    }
}