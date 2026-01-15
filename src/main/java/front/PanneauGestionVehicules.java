package front;

import back.GestionGarage;
import back.Vehicule;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class PanneauGestionVehicules extends JPanel {

    // --- CHARTE GRAPHIQUE UBUNTU DARK ---
    private final Color COLOR_BG = new Color(45, 45, 45);       // Fond Anthracite
    private final Color COLOR_CARD = new Color(60, 60, 60);     // Gris Carte
    private final Color COLOR_ORANGE = new Color(233, 84, 32);  // Orange Ubuntu
    private final Color COLOR_TEXT = new Color(245, 245, 245);  // Blanc Texte
    private final Color COLOR_INPUT = new Color(80, 80, 80);    // Gris Input
    private final Color COLOR_BORDER = new Color(80, 80, 80);   // Bordure subtile

    private final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 16);

    private GestionGarage garage;
    private JTable table;
    private DefaultTableModel modeleTable;
    private JTextField txtRecherche;
    private PanneauUrgences panneauUrgences;
    private List<VehiculeSelectionListener> externalListeners = new ArrayList<>();

    public PanneauGestionVehicules(GestionGarage garage) {
        this.garage = garage;
        this.setLayout(new BorderLayout(20, 0));
        this.setBackground(COLOR_BG);
        this.setBorder(new EmptyBorder(20, 20, 20, 20));

        // ================= GAUCHE : CARTE "FLOTTE" =================
        JPanel cardFlotte = creerCartePanel();

        // 1. En-tête
        JPanel headerFlotte = new JPanel(new BorderLayout(0, 15));
        headerFlotte.setBackground(COLOR_CARD);
        headerFlotte.setBorder(new EmptyBorder(0, 0, 15, 0));
        headerFlotte.add(creerEnTete("🚗 Parc Automobile"), BorderLayout.NORTH);

        // Barre d'outils
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(COLOR_CARD);

        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pSearch.setBackground(COLOR_CARD);
        JLabel lblSearch = new JLabel("Rechercher :  ");
        lblSearch.setForeground(COLOR_TEXT);
        pSearch.add(lblSearch);
        txtRecherche = new JTextField(20);
        styleField(txtRecherche);
        pSearch.add(txtRecherche);
        toolbar.add(pSearch, BorderLayout.WEST);

        JButton btnAdd = new JButton("+ NOUVEAU VÉHICULE");
        styleBoutonOrange(btnAdd);
        btnAdd.addActionListener(e -> ouvrirPopupCreation());
        toolbar.add(btnAdd, BorderLayout.EAST);

        headerFlotte.add(toolbar, BorderLayout.CENTER);
        cardFlotte.add(headerFlotte, BorderLayout.NORTH);

        // 2. Tableau
        String[] cols = {"Immat", "Marque", "Modèle", "Propriétaire", "KM"};
        modeleTable = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(modeleTable);
        styleTable(table);

        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.setBorder(BorderFactory.createEmptyBorder());
        scrollTable.getViewport().setBackground(COLOR_CARD); // Fond gris si vide
        cardFlotte.add(scrollTable, BorderLayout.CENTER);

        this.add(cardFlotte, BorderLayout.CENTER);

        // ================= DROITE : CARTE "URGENCES" =================
        JPanel cardUrgences = creerCartePanel();
        cardUrgences.setPreferredSize(new Dimension(350, 0));

        JLabel lblTitreUrgences = new JLabel("Notifications");
        cardUrgences.add(creerEnTeteDynamique("🔔 État du Véhicule", lblTitreUrgences), BorderLayout.NORTH);

        panneauUrgences = new PanneauUrgences(garage);
        panneauUrgences.setBorder(new EmptyBorder(10, 0, 0, 0));
        panneauUrgences.setBackground(COLOR_CARD); // Important pour la transparence
        cardUrgences.add(panneauUrgences, BorderLayout.CENTER);

        this.add(cardUrgences, BorderLayout.EAST);

        // LOGIQUE
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                String immat = (String) table.getValueAt(table.getSelectedRow(), 0);
                lblTitreUrgences.setText(immat);
                lblTitreUrgences.setForeground(COLOR_ORANGE);
                panneauUrgences.mettreAJour(immat);
                notifierExternalListeners(immat);
            }
        });

        txtRecherche.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { actualiserTableau(txtRecherche.getText()); }
        });
        actualiserTableau("");
    }

    // --- DESIGN HELPERS (Dark Mode) ---

    private JPanel creerCartePanel() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COLOR_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                new EmptyBorder(20, 25, 20, 25)
        ));
        return card;
    }

    private JPanel creerEnTete(String titre) {
        JPanel header = new JPanel(new BorderLayout(0, 10));
        header.setBackground(COLOR_CARD);
        JLabel lbl = new JLabel(titre);
        lbl.setFont(FONT_TITLE);
        lbl.setForeground(COLOR_TEXT);
        header.add(lbl, BorderLayout.NORTH);
        JSeparator sep = new JSeparator();
        sep.setForeground(COLOR_BORDER);
        header.add(sep, BorderLayout.SOUTH);
        return header;
    }

    private JPanel creerEnTeteDynamique(String titre, JLabel labelDynamique) {
        JPanel header = new JPanel(new BorderLayout(0, 10));
        header.setBackground(COLOR_CARD);
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(COLOR_CARD);

        JLabel lbl = new JLabel(titre);
        lbl.setFont(FONT_TITLE);
        lbl.setForeground(COLOR_TEXT);

        labelDynamique.setFont(new Font("SansSerif", Font.BOLD, 14));
        labelDynamique.setForeground(Color.GRAY);

        topRow.add(lbl, BorderLayout.WEST);
        topRow.add(labelDynamique, BorderLayout.EAST);
        header.add(topRow, BorderLayout.NORTH);

        JSeparator sep = new JSeparator();
        sep.setForeground(COLOR_BORDER);
        header.add(sep, BorderLayout.SOUTH);
        return header;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        // Couleurs du corps du tableau
        table.setGridColor(COLOR_BG);      // Lignes de la couleur du fond (donc invisibles ou subtiles)
        table.setBackground(COLOR_CARD);   // Fond des cellules (Gris moyen)
        table.setForeground(COLOR_TEXT);   // Texte blanc
        table.setSelectionBackground(COLOR_ORANGE); // Orange Ubuntu
        table.setSelectionForeground(Color.WHITE);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));

        // --- CORRECTION DU HEADER (EN-TÊTE) ---
        JTableHeader header = table.getTableHeader();

        // On force un rendu personnalisé pour être sûr d'avoir le fond sombre
        header.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                // On récupère le composant de base (JLabel)
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // On applique le style Ubuntu Dark
                c.setBackground(COLOR_BG); // Fond Anthracite (45, 45, 45)
                c.setForeground(Color.WHITE); // Texte Blanc pur
                c.setFont(new Font("SansSerif", Font.BOLD, 12));

                // Petite bordure grise en bas pour séparer
                ((JComponent)c).setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));

                return c;
            }
        });
    }

    private void styleBoutonOrange(JButton btn) {
        btn.setBackground(COLOR_ORANGE);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleField(JTextField txt) {
        txt.setBackground(COLOR_INPUT);
        txt.setForeground(Color.WHITE);
        txt.setCaretColor(Color.WHITE); // Curseur blanc
        txt.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER),
                new EmptyBorder(5, 5, 5, 5)));
    }

    // --- LOGIQUE METIER INCHANGEE ---
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