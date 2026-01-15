package front;

import back.Client;
import back.GestionGarage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class DialogSelectionClient extends JDialog {

    // =========================================================================
    // Attributs
    // =========================================================================

    private Client clientSelectionne = null;
    private final GestionGarage garage;
    private final JTextField txtRecherche;
    private final JTable table;
    private final DefaultTableModel modele;

    private final Color COLOR_BG = new Color(45, 45, 45);
    private final Color COLOR_CARD = new Color(60, 60, 60);
    private final Color COLOR_TEXT = new Color(245, 245, 245);
    private final Color COLOR_INPUT = new Color(80, 80, 80);
    private final Color COLOR_ORANGE = new Color(233, 84, 32);
    private final Color COLOR_BORDER = new Color(80, 80, 80);

    // =========================================================================
    // Constructeurs
    // =========================================================================

    public DialogSelectionClient(JFrame parent, GestionGarage garage) {
        super(parent, "Affecter un propriétaire", true);
        this.garage = garage;
        this.setSize(650, 500);
        this.setLocationRelativeTo(parent);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(COLOR_BG);

        // Zone Recherche (Haut)
        JPanel pTop = new JPanel(new BorderLayout(10, 10));
        pTop.setBackground(COLOR_BG);
        pTop.setBorder(new EmptyBorder(20, 20, 10, 20));

        JLabel lblInfo = new JLabel("Rechercher un client (Nom, Prénom, Mail) :");
        lblInfo.setForeground(COLOR_TEXT);
        lblInfo.setFont(new Font("SansSerif", Font.BOLD, 14));
        pTop.add(lblInfo, BorderLayout.NORTH);

        txtRecherche = new JTextField();
        styleField(txtRecherche);
        pTop.add(txtRecherche, BorderLayout.CENTER);

        JButton btnSearch = new JButton("🔍");
        styleBouton(btnSearch, COLOR_CARD);
        pTop.add(btnSearch, BorderLayout.EAST);

        this.add(pTop, BorderLayout.NORTH);

        // Tableau Résultats (Centre)
        // Colonnes : Nom, Prénom, Email, (Objet Caché)
        String[] cols = {"Nom", "Prénom", "Email", "OBJ_CLIENT_HIDDEN"};
        modele = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(modele);
        styleTable(table);

        // On cache la colonne 3 qui contient l'objet Client complet
        table.removeColumn(table.getColumnModel().getColumn(3));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(0, 20, 0, 20));
        scroll.getViewport().setBackground(COLOR_CARD);
        this.add(scroll, BorderLayout.CENTER);

        // Actions (Bas)
        JPanel pBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pBottom.setBackground(COLOR_BG);
        pBottom.setBorder(new EmptyBorder(20, 20, 20, 20));

        JButton btnCreer = new JButton("+ Créer nouveau client");
        styleBouton(btnCreer, new Color(40, 167, 69));
        btnCreer.addActionListener(e -> actionOuvrirCreation());

        JButton btnValider = new JButton("SÉLECTIONNER CE CLIENT");
        styleBouton(btnValider, COLOR_ORANGE);
        btnValider.addActionListener(e -> actionValiderSelection());

        pBottom.add(btnCreer);
        pBottom.add(btnValider);
        this.add(pBottom, BorderLayout.SOUTH);

        // Listeners
        txtRecherche.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { actualiserListe(); }
        });

        // Initialisation
        actualiserListe();
    }

    // =========================================================================
    // Méthodes
    // =========================================================================

    // =========================================================================
    // Rafraîchit le tableau en fonction de la barre de recherche
    // =========================================================================
    private void actualiserListe() {
        String r = txtRecherche.getText().trim();
        List<Client> res = garage.rechercherClients(r);
        modele.setRowCount(0);
        for (Client c : res) {
            modele.addRow(new Object[]{c.getNom(), c.getPrenom(), c.getMail(), c});
        }
    }

    // Ouvre la popup de création de client.
    private void actionOuvrirCreation() {
        DialogAjoutClient dialog = new DialogAjoutClient(this, garage);
        dialog.setVisible(true);

        if (dialog.isValide()) {
            txtRecherche.setText("");
            actualiserListe();

            // Sélection automatique du nouveau client
            if (table.getRowCount() > 0) {
                table.setRowSelectionInterval(table.getRowCount() - 1, table.getRowCount() - 1);
            }
        }
    }

    // Valide la sélection.
    private void actionValiderSelection() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez cliquer sur un client dans le tableau.");
            return;
        }

        // Récupération de l'objet Client
        this.clientSelectionne = (Client) modele.getValueAt(row, 3);
        this.dispose();
    }

    // =========================================================================
    // Styles du champs texte
    // =========================================================================
    private void styleField(JTextField txt) {
        txt.setBackground(COLOR_INPUT);
        txt.setForeground(Color.WHITE);
        txt.setCaretColor(Color.WHITE);
        txt.setBorder(BorderFactory.createCompoundBorder(new LineBorder(COLOR_BORDER), new EmptyBorder(8, 8, 8, 8)));
    }

    // =========================================================================
    // Style du bouton
    // =========================================================================
    private void styleBouton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
    }

    // =========================================================================
    // Style du tableau
    // =========================================================================
    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setShowVerticalLines(false);
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

    // =========================================================================
    // Getters
    // =========================================================================
    public Client getClientSelectionne() { return clientSelectionne; }
}