package front;

import back.*;
import back.EnumType.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class DialogAjoutTypeVehicule extends JDialog {

    // =========================================================================
    // Attributs
    // =========================================================================

    private boolean valide = false;
    private final GestionGarage garage;
    private final String marque, modele;

    // Champs du formulaire
    private JComboBox<Energie> comboEnergie;
    private JComboBox<BoiteVitesse> comboBoite;
    private JTextField txtPortes, txtPlaces, txtChevaux;

    // Thème Ubuntu Dark
    private final Color COLOR_BG = new Color(45, 45, 45);
    private final Color COLOR_TEXT = new Color(245, 245, 245);
    private final Color COLOR_INPUT = new Color(80, 80, 80);
    private final Color COLOR_ORANGE = new Color(233, 84, 32);
    private final Color COLOR_BORDER = new Color(100, 100, 100);

    // =========================================================================
    // Constructeurs
    // =========================================================================

    public DialogAjoutTypeVehicule(JDialog parent, GestionGarage garage, String marque, String modele) {
        super(parent, "Définition du Modèle", true);
        this.garage = garage;
        this.marque = marque;
        this.modele = modele;
        this.setSize(400, 600);
        this.setLocationRelativeTo(parent);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(COLOR_BG);

        // Header
        // Utilisation de HTML pour mettre en couleur la marque/modèle (IA)
        JLabel lblTitre = new JLabel("<html>Nouveau Modèle détecté :<br/><center><span style='color:#E95420'>" + marque + " " + modele + "</span></center></html>", SwingConstants.CENTER);
        lblTitre.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitre.setForeground(COLOR_TEXT);
        lblTitre.setBorder(new EmptyBorder(20, 20, 20, 20));
        this.add(lblTitre, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 10));
        form.setBackground(COLOR_BG);
        form.setBorder(new EmptyBorder(0, 40, 0, 40));

        // Ajout des composants
        comboEnergie = addCombo(form, "Type d'Énergie", Energie.values());
        comboBoite = addCombo(form, "Boîte de Vitesse", BoiteVitesse.values());
        txtPortes = addField(form, "Nombre de Portes");
        txtPlaces = addField(form, "Nombre de Places");
        txtChevaux = addField(form, "Puissance (Chevaux)");

        this.add(form, BorderLayout.CENTER);

        // Boutons
        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pBtn.setBackground(COLOR_BG);
        pBtn.setBorder(new EmptyBorder(20, 20, 20, 20));

        JButton btnAnnuler = new JButton("Annuler");
        styleBouton(btnAnnuler, new Color(70, 70, 70));
        btnAnnuler.addActionListener(e -> dispose());

        JButton btnValider = new JButton("Créer le Modèle");
        styleBouton(btnValider, COLOR_ORANGE);
        btnValider.addActionListener(e -> actionValider());

        pBtn.add(btnAnnuler);
        pBtn.add(btnValider);
        this.add(pBtn, BorderLayout.SOUTH);
    }

    // =========================================================================
    // Méthodes Helper
    // =========================================================================

    // =========================================================================
    // Ajouter champs
    // =========================================================================
    private JTextField addField(JPanel p, String labelText) {
        JLabel lbl = new JLabel(labelText);
        lbl.setForeground(Color.LIGHT_GRAY);
        p.add(lbl); // Ajout du label dans la grille

        JTextField txt = new JTextField();
        txt.setBackground(COLOR_INPUT);
        txt.setForeground(Color.WHITE);
        txt.setCaretColor(Color.WHITE);
        // Bordure composée pour l'esthétique
        txt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER),
                new EmptyBorder(5, 5, 5, 5)
        ));
        p.add(txt); // Ajout du champ dans la grille (ligne suivante)
        return txt;
    }

    // =========================================================================
    // Ajouter un combo
    // =========================================================================
    private <T> JComboBox<T> addCombo(JPanel p, String labelText, T[] values) {
        JLabel lbl = new JLabel(labelText);
        lbl.setForeground(Color.LIGHT_GRAY);
        p.add(lbl);

        JComboBox<T> combo = new JComboBox<>(values);
        combo.setBackground(COLOR_INPUT);
        combo.setForeground(Color.WHITE);
        combo.setBorder(new LineBorder(COLOR_BORDER));

        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    setBackground(COLOR_ORANGE);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(COLOR_INPUT);
                    setForeground(Color.WHITE);
                }
                return this;
            }
        });

        p.add(combo);
        return combo;
    }

    // =========================================================================
    // Définition du style du bouton
    // =========================================================================
    private void styleBouton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
    }

    // =========================================================================
    // Logique de validation
    // =========================================================================

    // =========================================================================
    // Valide l'envoie du formulaire de création d'un type de véhicule
    // =========================================================================
    private void actionValider() {
        try {
            // Vérification champs vides
            if (txtPortes.getText().trim().isEmpty() ||
                    txtPlaces.getText().trim().isEmpty() ||
                    txtChevaux.getText().trim().isEmpty()) {
                throw new Exception("Tous les champs numériques sont obligatoires.");
            }

            // Parsing des entiers
            int portes = Integer.parseInt(txtPortes.getText().trim());
            int places = Integer.parseInt(txtPlaces.getText().trim());
            int chevaux = Integer.parseInt(txtChevaux.getText().trim());

            // Récupération des Enums
            Energie nrj = (Energie) comboEnergie.getSelectedItem();
            BoiteVitesse bv = (BoiteVitesse) comboBoite.getSelectedItem();

            // Création dans le garage
            TypeVehicule nouveauType = new TypeVehicule(marque, modele, nrj, bv, portes, chevaux, places);
            garage.creerTypeVehicule(nouveauType);

            this.valide = true;
            this.dispose();

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer des nombres entiers valides pour les Portes, Places et Chevaux.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }
    }

    // =========================================================================
    // Getters
    // =========================================================================
    public boolean isValide() { return valide; }
}