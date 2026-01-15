package front;

import back.GestionGarage;
import back.Vehicule;
import front.Listener.VehiculeSelectionListener; // Assurez-vous d'avoir cet import ou supprimez-le si vous n'utilisez pas l'interface

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

    // =========================================================================
    // Attributs
    // =========================================================================

    private final Color COLOR_BG = new Color(45, 45, 45);
    private final Color COLOR_CARD = new Color(60, 60, 60);
    private final Color COLOR_ORANGE = new Color(233, 84, 32);
    private final Color COLOR_TEXT = new Color(245, 245, 245);
    private final Color COLOR_INPUT = new Color(80, 80, 80);
    private final Color COLOR_BORDER = new Color(80, 80, 80);

    private final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 16);

    private final GestionGarage garage;
    private final JTable table;
    private final DefaultTableModel modeleTable;
    private final JTextField txtRecherche;
    private final PanneauUrgences panneauUrgences;
    private final JButton btnAffecterClient;
    private String immatSelectionnee = null;

    private final List<VehiculeSelectionListener> externalListeners = new ArrayList<>();

    // =========================================================================
    // Constructeurs
    // =========================================================================

    public PanneauGestionVehicules(GestionGarage garage) {
        this.garage = garage;
        this.setLayout(new BorderLayout(20, 0));
        this.setBackground(COLOR_BG);
        this.setBorder(new EmptyBorder(20, 20, 20, 20));

        // GAUCHE : CARTE "ensembleVehicules"
        JPanel cardEnsembleVehicules = creerCartePanel();

        // En-tête
        JPanel headerEnsembleVehicules = new JPanel(new BorderLayout(0, 15));
        headerEnsembleVehicules.setBackground(COLOR_CARD);
        headerEnsembleVehicules.setBorder(new EmptyBorder(0, 0, 15, 0));
        headerEnsembleVehicules.add(creerEnTete("Parc Automobile"), BorderLayout.NORTH);

        // Barre d'outils
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(COLOR_CARD);

        // Recherche vehicule
        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pSearch.setBackground(COLOR_CARD);
        JLabel lblSearch = new JLabel("Rechercher :  ");
        lblSearch.setForeground(COLOR_TEXT);
        pSearch.add(lblSearch);
        txtRecherche = new JTextField(20);
        styleField(txtRecherche);
        pSearch.add(txtRecherche);
        toolbar.add(pSearch, BorderLayout.WEST);

        // --- ZONE BOUTONS DROITE ---
        JPanel pBtnRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pBtnRight.setBackground(COLOR_CARD);

        // Bouton Affecter Client
        btnAffecterClient = new JButton("Affecter Propriétaire");
        btnAffecterClient.setBackground(new Color(108, 117, 125));
        btnAffecterClient.setForeground(Color.WHITE);
        btnAffecterClient.setFocusPainted(false);
        btnAffecterClient.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnAffecterClient.setBorder(new EmptyBorder(8, 15, 8, 15));
        btnAffecterClient.setVisible(false); // Caché par défaut
        btnAffecterClient.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAffecterClient.addActionListener(e -> actionAffecterClient());

        // Bouton ajout véhicule
        JButton btnAdd = new JButton("+ NOUVEAU VÉHICULE");
        styleBoutonOrange(btnAdd);
        btnAdd.addActionListener(e -> ouvrirPopupCreation());

        pBtnRight.add(btnAffecterClient);
        pBtnRight.add(btnAdd);
        toolbar.add(pBtnRight, BorderLayout.EAST);

        headerEnsembleVehicules.add(toolbar, BorderLayout.CENTER);
        cardEnsembleVehicules.add(headerEnsembleVehicules, BorderLayout.NORTH);

        // Tableau
        String[] cols = {"Immat", "Marque", "Modèle", "Propriétaire", "KM"};
        modeleTable = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(modeleTable);
        styleTable(table);

        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.setBorder(BorderFactory.createEmptyBorder());
        scrollTable.getViewport().setBackground(COLOR_CARD);
        cardEnsembleVehicules.add(scrollTable, BorderLayout.CENTER);

        this.add(cardEnsembleVehicules, BorderLayout.CENTER);

        // DROITE : CARTE "URGENCES"
        JPanel cardUrgences = creerCartePanel();
        cardUrgences.setPreferredSize(new Dimension(350, 0));

        JLabel lblTitreUrgences = new JLabel("Notifications");
        cardUrgences.add(creerEnTeteDynamique("Entretiens à venir ", lblTitreUrgences), BorderLayout.NORTH);

        panneauUrgences = new PanneauUrgences(garage);
        panneauUrgences.setBorder(new EmptyBorder(10, 0, 0, 0));
        panneauUrgences.setBackground(COLOR_CARD);
        cardUrgences.add(panneauUrgences, BorderLayout.CENTER);

        this.add(cardUrgences, BorderLayout.EAST);

        //  ISTENER SELECTION
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();

                // On stocke l'immatriculation dès la sélection
                this.immatSelectionnee = (String) table.getValueAt(row, 0);

                String prop = (String) table.getValueAt(row, 3);

                // Gestion Visibilité Bouton
                if (prop.equals("?") || prop.equalsIgnoreCase("Inconnu")) {
                    btnAffecterClient.setVisible(true);
                } else {
                    btnAffecterClient.setVisible(false);
                }

                // Mise à jour Urgences
                lblTitreUrgences.setText(immatSelectionnee);
                lblTitreUrgences.setForeground(COLOR_ORANGE);
                panneauUrgences.mettreAJour(immatSelectionnee);
                notifierExternalListeners(immatSelectionnee);
            }
        });

        txtRecherche.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { actualiserTableau(txtRecherche.getText()); }
        });
        actualiserTableau("");
    }

    // =========================================================================
    // Méthodes
    // =========================================================================

    // =========================================================================
    // Ouvre la popup d'affectation
    // =========================================================================
    private void actionAffecterClient() {
        if (this.immatSelectionnee == null) return;

        try {
            // Récupération de la fenêtre parente
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            JFrame topFrame = null;
            if (parentWindow instanceof JFrame) {
                topFrame = (JFrame) parentWindow;
            }

            // Ouverture Popup
            DialogSelectionClient dialog = new DialogSelectionClient(topFrame, garage);
            dialog.setVisible(true);

            if (dialog.getClientSelectionne() != null) {
                garage.associerClientAVehicule(this.immatSelectionnee, dialog.getClientSelectionne());

                actualiserTableau(txtRecherche.getText());
                JOptionPane.showMessageDialog(this, "Propriétaire affecté avec succès !");
                btnAffecterClient.setVisible(false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ouverture de la sélection : " + ex.getMessage());
        }
    }

    // =========================================================================
    // Actualise le tableaux
    // =========================================================================
    private void actualiserTableau(String filtre) {
        List<Vehicule> resultats = garage.rechercherVehicules(filtre.trim());
        modeleTable.setRowCount(0);
        for (Vehicule v : resultats) {
            String m = (v.getTypeVehicule() != null) ? v.getTypeVehicule().getMarque() : "?";
            String mod = (v.getTypeVehicule() != null) ? v.getTypeVehicule().getModele() : "?";

            // Gestion affichage propriétaire
            String prop = "Inconnu";
            if (v.getProprietaire() != null) {
                prop = v.getProprietaire().getNom() + " " + v.getProprietaire().getPrenom();
            }

            modeleTable.addRow(new Object[]{v.getImmatriculation(), m, mod, prop, v.getKilometrage()});
        }
    }

    // =========================================================================
    // Création d'unu carte
    // =========================================================================
    private JPanel creerCartePanel() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COLOR_CARD);
        card.setBorder(new CompoundBorder(new LineBorder(COLOR_BORDER, 1), new EmptyBorder(20, 25, 20, 25)));
        return card;
    }

    // =========================================================================
    // Création header
    // =========================================================================
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

    // =========================================================================
    // Création header dynamique
    // =========================================================================
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

    // =========================================================================
    // Definition du style du tableau
    // =========================================================================
    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setGridColor(COLOR_BG);
        table.setBackground(COLOR_CARD);
        table.setForeground(COLOR_TEXT);
        table.setSelectionBackground(COLOR_ORANGE);
        table.setSelectionForeground(Color.WHITE);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(COLOR_BG);
                c.setForeground(Color.WHITE);
                c.setFont(new Font("SansSerif", Font.BOLD, 12));
                ((JComponent) c).setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
                return c;
            }
        });
    }

    // =========================================================================
    // Definition du style du bouton
    // =========================================================================
    private void styleBoutonOrange(JButton btn) {
        btn.setBackground(COLOR_ORANGE);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // =========================================================================
    // Definition du style du champ texte
    // =========================================================================
    private void styleField(JTextField txt) {
        txt.setBackground(COLOR_INPUT);
        txt.setForeground(Color.WHITE);
        txt.setCaretColor(Color.WHITE);
        txt.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txt.setBorder(BorderFactory.createCompoundBorder(new LineBorder(COLOR_BORDER), new EmptyBorder(5, 5, 5, 5)));
    }

    // =========================================================================
    // Ouverture popup de création de vehicule
    // =========================================================================
    private void ouvrirPopupCreation() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        DialogAjoutVehicule dialog = new DialogAjoutVehicule(topFrame, garage);
        dialog.setVisible(true);
        if (dialog.isValide()) {
            actualiserTableau("");
            JOptionPane.showMessageDialog(this, "Véhicule ajouté au parc.");
        }
    }

    // =========================================================================
    // Ajout d'un écouteur
    // =========================================================================
    public void ajouterEcouteurSelection(VehiculeSelectionListener listener) {
        this.externalListeners.add(listener);
    }

    // =========================================================================
    // Notifier écouteur
    // =========================================================================
    private void notifierExternalListeners(String immat) {
        for (VehiculeSelectionListener l : externalListeners) l.onVehiculeSelected(immat);
    }

    // =========================================================================
    // Forcer le rafraîchissement des données
    // =========================================================================
    public void rechargerDonnees() {
        // On garde le texte de recherche actuel s'il y en a un
        actualiserTableau(txtRecherche.getText());
    }
}