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

    // --- THEME UBUNTU DARK ---
    private final Color COLOR_BG = new Color(45, 45, 45);
    private final Color COLOR_CARD = new Color(60, 60, 60);
    private final Color COLOR_ORANGE = new Color(233, 84, 32);
    private final Color COLOR_TEXT = new Color(245, 245, 245);
    private final Color COLOR_INPUT = new Color(80, 80, 80);
    private final Color COLOR_BORDER = new Color(80, 80, 80);

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
        this.setLayout(new GridLayout(1, 2, 20, 0));
        this.setBackground(COLOR_BG);
        this.setBorder(new EmptyBorder(20, 20, 20, 20));

        // ================= GAUCHE : ATELIER =================
        JPanel cardAtelier = creerCartePanel();
        cardAtelier.add(creerEnTete("🔧 Nouvelle Intervention"), BorderLayout.NORTH);

        JPanel contentAtelier = new JPanel(new BorderLayout(0, 15));
        contentAtelier.setBackground(COLOR_CARD);
        contentAtelier.setBorder(new EmptyBorder(15, 0, 0, 0));

        lblVehiculeInfos = new JLabel("Veuillez sélectionner un véhicule...", SwingConstants.CENTER);
        lblVehiculeInfos.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblVehiculeInfos.setForeground(Color.LIGHT_GRAY);
        contentAtelier.add(lblVehiculeInfos, BorderLayout.NORTH);

        schema2D = new PanneauSchema2D(garage);
        // Le schéma s'adaptera (voir modification suivante)
        contentAtelier.add(schema2D, BorderLayout.CENTER);

        // Zone Panier
        JPanel zoneAction = new JPanel(new BorderLayout(0, 10));
        zoneAction.setBackground(COLOR_CARD);

        JLabel lblPanier = new JLabel("Détail du devis estimatif :");
        lblPanier.setFont(FONT_SUBTITLE);
        lblPanier.setForeground(Color.LIGHT_GRAY);
        zoneAction.add(lblPanier, BorderLayout.NORTH);

        modelePanier = new DefaultListModel<>();
        JList<String> listePanier = new JList<>(modelePanier);
        listePanier.setBackground(COLOR_INPUT); // Liste sur fond input
        listePanier.setForeground(COLOR_TEXT);
        listePanier.setBorder(new LineBorder(COLOR_BORDER, 1));
        listePanier.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPanier = new JScrollPane(listePanier);
        scrollPanier.setPreferredSize(new Dimension(0, 100));
        scrollPanier.setBorder(BorderFactory.createEmptyBorder());
        zoneAction.add(scrollPanier, BorderLayout.CENTER);

        JPanel panelForm = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelForm.setBackground(COLOR_CARD);

        JLabel lblKm = new JLabel("Nouveau KM :");
        lblKm.setForeground(COLOR_TEXT);
        panelForm.add(lblKm);

        txtKm = new JTextField(8);
        styleField(txtKm);
        panelForm.add(txtKm);

        JButton btnValider = new JButton("VALIDER & FACTURER");
        styleBoutonSuccess(btnValider);
        panelForm.add(btnValider);

        zoneAction.add(panelForm, BorderLayout.SOUTH);
        contentAtelier.add(zoneAction, BorderLayout.SOUTH);
        cardAtelier.add(contentAtelier, BorderLayout.CENTER);

        // ================= DROITE : HISTORIQUE =================
        JPanel cardHistorique = creerCartePanel();
        cardHistorique.add(creerEnTete("📋 Historique du Véhicule"), BorderLayout.NORTH);

        String[] cols = {"Date", "Intervention(s)", "Prix", "KM"};
        modeleTableHistorique = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable tableHist = new JTable(modeleTableHistorique);
        styleTable(tableHist);

        JScrollPane scrollHist = new JScrollPane(tableHist);
        scrollHist.setBorder(BorderFactory.createEmptyBorder());
        scrollHist.getViewport().setBackground(COLOR_CARD);

        JPanel wrapTable = new JPanel(new BorderLayout());
        wrapTable.setBackground(COLOR_CARD);
        wrapTable.setBorder(new EmptyBorder(15, 0, 0, 0));
        wrapTable.add(scrollHist);
        cardHistorique.add(wrapTable, BorderLayout.CENTER);

        this.add(cardAtelier);
        this.add(cardHistorique);

        // Logique
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

    // --- Helpers Design ---

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

    private void styleBoutonSuccess(JButton btn) {
        btn.setBackground(COLOR_ORANGE);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleField(JTextField txt) {
        txt.setBackground(COLOR_INPUT);
        txt.setForeground(Color.WHITE);
        txt.setCaretColor(Color.WHITE);
        txt.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER),
                new EmptyBorder(5, 5, 5, 5)));
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

    // --- Logique Métier (Identique) ---
    public void chargerVehicule(String immat) {
        List<Vehicule> resultats = garage.rechercherVehicules(immat);
        this.vehiculeActif = null;
        for(Vehicule v : resultats) {
            if(v.getImmatriculation().equals(immat)) {
                this.vehiculeActif = v;
                break;
            }
        }
        if (vehiculeActif == null) return;

        // Mise à jour HTML adaptée au mode sombre (couleur gris clair pour le texte statique)
        lblVehiculeInfos.setText("<html><span style='color:#ccc;'>Véhicule : </span><b style='color:#fff'>" +
                vehiculeActif.getImmatriculation() + "</b> &nbsp;|&nbsp; <span style='color:#eee'>" +
                vehiculeActif.getTypeVehicule().getMarque() + " " + vehiculeActif.getTypeVehicule().getModele() +
                "</span> &nbsp;|&nbsp; <span style='color:#eee'>" + vehiculeActif.getProprietaire().getNom() + "</span></html>");

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
            modeleTableHistorique.addRow(new Object[]{i.getDate(), types.toString(), i.getPrix() + " €", (int)i.getKilometrage() + " km"});
        }
    }

    private void validerIntervention() {
        if (vehiculeActif == null) { JOptionPane.showMessageDialog(this, "Aucun véhicule chargé."); return; }
        try {
            float km = Float.parseFloat(txtKm.getText());
            if (km < vehiculeActif.getKilometrage()) {
                if(JOptionPane.showConfirmDialog(this, "KM inférieur à l'actuel. Confirmer ?", "Attention", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
            }
            List<TypeIntervention> selection = schema2D.getTypesSelectionnes();
            if (selection.isEmpty()) throw new Exception("Sélectionnez une zone.");
            garage.creerIntervention(vehiculeActif.getImmatriculation(), selection, km);
            JOptionPane.showMessageDialog(this, "Validé !");
            chargerVehicule(vehiculeActif.getImmatriculation());
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage()); }
    }
}