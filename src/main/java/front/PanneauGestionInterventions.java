package front;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class PanneauGestionInterventions extends JPanel {

    private PanneauSchema2D schema2D;
    private JTextField txtImmat, txtDate, txtKm;
    private JComboBox<String> comboType;
    private JTextField txtRecherche;
    private DefaultTableModel modeleTable;

    public PanneauGestionInterventions() {
        this.setLayout(new GridLayout(1, 2, 10, 0));

        // --- GAUCHE : Schéma + Formulaire ---
        JPanel panelGauche = new JPanel(new BorderLayout());

        schema2D = new PanneauSchema2D();
        schema2D.setPreferredSize(new Dimension(0, 350));
        panelGauche.add(schema2D, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Nouvelle Intervention"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx=0; g.gridy=0; formPanel.add(new JLabel("Immatriculation :"), g);
        txtImmat = new JTextField(10); g.gridx=1; formPanel.add(txtImmat, g);

        g.gridx=0; g.gridy=1; formPanel.add(new JLabel("Type :"), g);
        comboType = new JComboBox<>(new String[]{"Réparation", "Entretien", "Vidange"});
        g.gridx=1; formPanel.add(comboType, g);

        g.gridx=0; g.gridy=2; formPanel.add(new JLabel("Date :"), g);
        txtDate = new JTextField(10); g.gridx=1; formPanel.add(txtDate, g);

        g.gridx=0; g.gridy=3; formPanel.add(new JLabel("KM :"), g);
        txtKm = new JTextField(10); g.gridx=1; formPanel.add(txtKm, g);

        JButton btnValider = new JButton("Valider Intervention");
        g.gridx=0; g.gridy=4; g.gridwidth=2; formPanel.add(btnValider, g);

        panelGauche.add(formPanel, BorderLayout.SOUTH);
        this.add(panelGauche);

        // --- DROITE : Historique + Recherche ---
        JPanel panelDroit = new JPanel(new BorderLayout());
        panelDroit.setBorder(BorderFactory.createTitledBorder("Historique Interventions"));

        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pSearch.add(new JLabel("Recherche (Immat) :"));
        txtRecherche = new JTextField(15);
        pSearch.add(txtRecherche);
        panelDroit.add(pSearch, BorderLayout.NORTH);

        // Tableau NON EDITABLE
        String[] cols = {"Immat", "Type", "Date", "Pièces"};
        modeleTable = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(modeleTable);
        panelDroit.add(new JScrollPane(table), BorderLayout.CENTER);

        this.add(panelDroit);

        // --- ACTIONS ---
        btnValider.addActionListener(e -> {
            try {
                String immat = txtImmat.getText();
                List<String> pieces = schema2D.getPiecesCochees();

                if (immat.isEmpty()) throw new Exception("Immatriculation manquante");

                // TENTATIVE D'ENVOI BDD

                // Simulation d'échec car pas de BDD
                throw new Exception("Erreur de connexion : Impossible de vérifier l'existence du véhicule en base de données.");

                // (Code inatteignable tant que la BDD n'est pas là)
                // JOptionPane.showMessageDialog(this, "Ajout réussi !");
                // schema2D.reset();
                // txtImmat.setText("");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "L'ajout a échoué.\nCause : " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        txtRecherche.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                // TODO : Recherche BDD
            }
        });
    }
}