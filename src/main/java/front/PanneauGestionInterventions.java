package front;

import back.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class PanneauGestionInterventions extends JPanel {

    private PanneauSchema2D schema2D;
    private JTextField txtImmat, txtDate, txtKm;
    private JComboBox<String> comboType;
    private JTextField txtRecherche;
    private DefaultTableModel modeleTable;
    private GestionGarage garage;


    public PanneauGestionInterventions(GestionGarage garage) {
        this.setLayout(new GridLayout(1, 2, 10, 0));
        this.garage = garage;

        // --- GAUCHE : Schéma + Formulaire ---
        JPanel panelGauche = new JPanel(new BorderLayout());

        schema2D = new PanneauSchema2D(garage);
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

        // On récupère la liste réelle depuis la BDD
        List<String> typesDispo = garage.getNomsTypesIntervention();
        comboType = new JComboBox<>(typesDispo.toArray(new String[0]));

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
                String typeSelectionne = (String) comboType.getSelectedItem(); // Ex: "Vidange"
                float km = Float.parseFloat(txtKm.getText());

                // On récupère les pièces cochées du schéma 2D
                List<Pieces> piecesReelles = schema2D.getPiecesSelectionnees();

                // APPEL BDD
                // Le type ("Réparation", "Vidange") doit exister exactement avec ce nom en BDD (voir Main.java)
                Intervention interv = garage.creerIntervention(immat, typeSelectionne, piecesReelles, km);

                if (interv != null) {
                    JOptionPane.showMessageDialog(this, "Intervention créée ! Prix : " + interv.getPrix() + "€");
                } else {
                    throw new Exception("Echec de création (Véhicule introuvable ou Type incorrect)");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
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