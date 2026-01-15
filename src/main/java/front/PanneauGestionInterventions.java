package front;

import back.*;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

public class PanneauGestionInterventions extends JPanel {

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
    private final Font FONT_SUBTITLE = new Font("SansSerif", Font.BOLD, 13);

    private final PanneauSchema2D schema2D;
    private final JTextField txtKm;
    private final DefaultTableModel modeleTableHistorique;
    private final DefaultListModel<String> modelePanier;
    private final GestionGarage garage;
    private final JLabel lblVehiculeInfos;
    private Vehicule vehiculeActif;


    // =========================================================================
    // Constructeurs
    // =========================================================================

    public PanneauGestionInterventions(GestionGarage garage) {
        this.garage = garage;
        this.setLayout(new GridLayout(1, 2, 20, 0));
        this.setBackground(COLOR_BG);
        this.setBorder(new EmptyBorder(20, 20, 20, 20));

        // GAUCHE : ATELIER
        JPanel cardAtelier = creerCartePanel();
        cardAtelier.add(creerEnTete("Nouvelle Intervention"), BorderLayout.NORTH);

        JPanel contentAtelier = new JPanel(new BorderLayout(0, 15));
        contentAtelier.setBackground(COLOR_CARD);
        contentAtelier.setBorder(new EmptyBorder(15, 0, 0, 0));

        lblVehiculeInfos = new JLabel("Veuillez sélectionner un véhicule...", SwingConstants.CENTER);
        lblVehiculeInfos.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblVehiculeInfos.setForeground(Color.LIGHT_GRAY);
        contentAtelier.add(lblVehiculeInfos, BorderLayout.NORTH);

        schema2D = new PanneauSchema2D(garage);
        contentAtelier.add(schema2D, BorderLayout.CENTER);

        // Zone Panier
        JPanel zoneAction = new JPanel(new BorderLayout(0, 10));
        zoneAction.setBackground(COLOR_CARD);

        JLabel lblPanier = new JLabel("Détail du devis estimatif :");
        lblPanier.setFont(FONT_SUBTITLE);
        lblPanier.setForeground(Color.LIGHT_GRAY);
        zoneAction.add(lblPanier, BorderLayout.NORTH);

        modelePanier = new DefaultListModel<>();

        // Liste avec Tooltips
        JList<String> listePanier = new JList<>(modelePanier) {
            @Override
            public String getToolTipText(MouseEvent e) {
                int index = locationToIndex(e.getPoint());
                if (index > -1) {
                    return getModel().getElementAt(index);
                }
                return null;
            }
        };

        listePanier.setBackground(COLOR_INPUT);
        listePanier.setForeground(COLOR_TEXT);
        listePanier.setBorder(new LineBorder(COLOR_BORDER, 1));
        listePanier.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPanier = new JScrollPane(listePanier);
        scrollPanier.setPreferredSize(new Dimension(0, 100));
        scrollPanier.setBorder(BorderFactory.createEmptyBorder());
        zoneAction.add(scrollPanier, BorderLayout.CENTER);

        // Formulaire KM
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


        // DROITE : HISTORIQUE
        JPanel cardHistorique = creerCartePanel();
        cardHistorique.add(creerEnTete("Historique du Véhicule"), BorderLayout.NORTH);

        String[] cols = {"Date", "Intervention(s)", "Prix", "KM"};
        modeleTableHistorique = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        // Tableau avec Tooltips
        JTable tableHist = new JTable(modeleTableHistorique) {
            @Override
            public String getToolTipText(MouseEvent e) {
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);
                if (rowIndex >= 0 && colIndex >= 0) {
                    Object val = getValueAt(rowIndex, colIndex);
                    return val == null ? null : val.toString();
                }
                return super.getToolTipText(e);
            }
        };

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

        // Listeners
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

    // =========================================================================
    // Méthodes
    // =========================================================================

    // =========================================================================
    // Chargement Véhicule
    // =========================================================================
    public void chargerVehicule(String immat) {
        java.util.List<Vehicule> resultats = garage.rechercherVehicules(immat);
        this.vehiculeActif = null;
        for (Vehicule v : resultats) {
            if (v.getImmatriculation().equals(immat)) {
                this.vehiculeActif = v;
                break;
            }
        }
        if (vehiculeActif == null) return;

        // On vérifie si le propriétaire existe avant d'essayer de lire son nom
        String nomProprio = "Sans propriétaire";
        if (vehiculeActif.getProprietaire() != null) {
            nomProprio = vehiculeActif.getProprietaire().getNom();
        }

        // On construit le HTML avec la variable sécurisée 'nomProprio'
        lblVehiculeInfos.setText("<html><span style='color:#ccc;'>Véhicule : </span><b style='color:#fff'>" +
                vehiculeActif.getImmatriculation() + "</b> &nbsp;|&nbsp; <span style='color:#eee'>" +
                vehiculeActif.getTypeVehicule().getMarque() + " " + vehiculeActif.getTypeVehicule().getModele() +
                "</span> &nbsp;|&nbsp; <span style='color:#eee'>" + nomProprio + "</span></html>");

        txtKm.setText(String.valueOf((int) vehiculeActif.getKilometrage()));
        schema2D.setVehicule(vehiculeActif);
        chargerHistorique();
    }

    // =========================================================================
    // Historique
    // =========================================================================
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
            modeleTableHistorique.addRow(new Object[]{i.getDate(), types.toString(), i.getPrix() + " €", (int) i.getKilometrage() + " km"});
        }
    }

    // =========================================================================
    // Validation
    // =========================================================================
    private void validerIntervention() {
        if (vehiculeActif == null) {
            JOptionPane.showMessageDialog(this, "Aucun véhicule chargé.");
            return;
        }
        try {
            float km = Float.parseFloat(txtKm.getText());
            if (km < vehiculeActif.getKilometrage()) {
                if (JOptionPane.showConfirmDialog(this, "KM inférieur à l'actuel. Confirmer ?", "Attention", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
                    return;
            }
            List<TypeIntervention> selection = schema2D.getTypesSelectionnes();
            if (selection.isEmpty()) throw new Exception("Sélectionnez une zone.");
            garage.creerIntervention(vehiculeActif.getImmatriculation(), selection, km);
            JOptionPane.showMessageDialog(this, "Validé !");
            chargerVehicule(vehiculeActif.getImmatriculation());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }

    // =========================================================================
    // Création panel
    // =========================================================================
    private JPanel creerCartePanel() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COLOR_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                new EmptyBorder(20, 25, 20, 25)
        ));
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
    // Définition du style du bouton success
    // =========================================================================
    private void styleBoutonSuccess(JButton btn) {
        btn.setBackground(COLOR_ORANGE);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // =========================================================================
    // Définition du style du champ de texte
    // =========================================================================
    private void styleField(JTextField txt) {
        txt.setBackground(COLOR_INPUT);
        txt.setForeground(Color.WHITE);
        txt.setCaretColor(Color.WHITE);
        txt.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER),
                new EmptyBorder(5, 5, 5, 5)));
    }

    // =========================================================================
    // Définition du style du tableau
    // =========================================================================
    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setGridColor(COLOR_BG);
        table.setBackground(COLOR_CARD);
        table.setForeground(COLOR_TEXT);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setSelectionBackground(COLOR_ORANGE);
        table.setSelectionForeground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(COLOR_BG);
                c.setForeground(Color.WHITE);
                c.setFont(new Font("SansSerif", Font.BOLD, 12));
                ((JComponent) c).setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
                return c;
            }
        });
    }
}