package front;

import back.*;
import back.EnumType.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDate;

public class DialogAjoutVehicule extends JDialog {

    private boolean valide = false;
    private JTextField txtImmat, txtMarque, txtModele, txtKm;
    private GestionGarage garage;

    // Couleurs Ubuntu Dark
    private final Color COLOR_BG = new Color(45, 45, 45);
    private final Color COLOR_TEXT = new Color(245, 245, 245);
    private final Color COLOR_INPUT = new Color(80, 80, 80);
    private final Color COLOR_ORANGE = new Color(233, 84, 32);

    public DialogAjoutVehicule(JFrame parent, GestionGarage garage) {
        super(parent, "Nouveau Véhicule", true);
        this.garage = garage;
        this.setSize(400, 500);
        this.setLocationRelativeTo(parent);
        this.setLayout(new BorderLayout());

        // Appliquer le fond sombre
        this.getContentPane().setBackground(COLOR_BG);

        // Header
        JLabel lblTitre = new JLabel("Ajouter un véhicule", SwingConstants.CENTER);
        lblTitre.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitre.setForeground(COLOR_TEXT); // Texte blanc
        lblTitre.setBorder(new EmptyBorder(20, 0, 20, 0));
        this.add(lblTitre, BorderLayout.NORTH);

        // Formulaire
        JPanel form = new JPanel(new GridLayout(0, 1, 0, 10));
        form.setBackground(COLOR_BG); // Fond sombre
        form.setBorder(new EmptyBorder(0, 40, 0, 40));

        txtImmat = addField(form, "Immatriculation (AA-123-BB)");
        txtMarque = addField(form, "Marque");
        txtModele = addField(form, "Modèle");
        txtKm = addField(form, "Kilométrage");

        this.add(form, BorderLayout.CENTER);

        // Boutons
        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pBtn.setBackground(COLOR_BG); // Fond sombre
        pBtn.setBorder(new EmptyBorder(20, 20, 20, 20));

        JButton btnAnnuler = new JButton("Annuler");
        styleBouton(btnAnnuler, new Color(70, 70, 70));
        btnAnnuler.addActionListener(e -> dispose());

        JButton btnValider = new JButton("Enregistrer");
        styleBouton(btnValider, COLOR_ORANGE);
        btnValider.addActionListener(e -> actionValider());

        pBtn.add(btnAnnuler);
        pBtn.add(btnValider);
        this.add(pBtn, BorderLayout.SOUTH);
    }

    private JTextField addField(JPanel p, String labelText) {
        JLabel lbl = new JLabel(labelText);
        lbl.setForeground(Color.LIGHT_GRAY); // Texte gris clair
        p.add(lbl);

        JTextField txt = new JTextField();
        txt.setBackground(COLOR_INPUT); // Fond champ saisie
        txt.setForeground(Color.WHITE); // Texte saisie blanc
        txt.setCaretColor(Color.WHITE);
        txt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(100, 100, 100)),
                new EmptyBorder(5, 5, 5, 5)
        ));
        p.add(txt);
        return txt;
    }

    private void styleBouton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
    }

    // ... (Méthodes actionValider et isValide identiques à avant) ...
    // Copiez-les simplement ici ou gardez celles que vous aviez.
    private void actionValider() {
        try {
            String immat = txtImmat.getText().trim().toUpperCase();
            String marque = txtMarque.getText().trim();
            String modele = txtModele.getText().trim();
            float km = Float.parseFloat(txtKm.getText().trim());

            if (immat.isEmpty() || marque.isEmpty()) throw new Exception("Champs vides");
            TypeVehicule type = garage.trouverTypeVehicule(marque, modele);
            if (type == null) throw new Exception("Modèle inconnu.");

            Vehicule v = new Vehicule(immat, LocalDate.now(), km, type, null);
            garage.creerVehicule(v);

            this.valide = true;
            this.dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }
    }
    public boolean isValide() { return valide; }
}