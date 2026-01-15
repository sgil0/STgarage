package front;

import back.Client;
import back.GestionGarage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class DialogAjoutClient extends JDialog {

    // =========================================================================
    // Attributs
    // =========================================================================

    private boolean valide = false;
    private Client nouveauClient = null;
    private final JTextField txtNom, txtPrenom, txtMail, txtAdresse;
    private final GestionGarage garage;

    // Theme Ubuntu Dark
    private final Color COLOR_BG = new Color(45, 45, 45);
    private final Color COLOR_TEXT = new Color(245, 245, 245);
    private final Color COLOR_INPUT = new Color(80, 80, 80);
    private final Color COLOR_ORANGE = new Color(233, 84, 32);

    // =========================================================================
    // Constructeurs
    // =========================================================================

    public DialogAjoutClient(JDialog parent, GestionGarage garage) {
        super(parent, "Nouveau Client", true);
        this.garage = garage;
        this.setSize(400, 450); // Un peu moins haut car moins de champs
        this.setLocationRelativeTo(parent);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(COLOR_BG);

        // Header
        JLabel lblTitre = new JLabel("Création Fiche Client", SwingConstants.CENTER);
        lblTitre.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitre.setForeground(COLOR_TEXT);
        lblTitre.setBorder(new EmptyBorder(20, 0, 20, 0));
        this.add(lblTitre, BorderLayout.NORTH);

        // Formulaire
        JPanel form = new JPanel(new GridLayout(0, 1, 0, 10));
        form.setBackground(COLOR_BG);
        form.setBorder(new EmptyBorder(0, 40, 0, 40));

        txtNom = addField(form, "Nom");
        txtPrenom = addField(form, "Prénom");
        txtMail = addField(form, "Email");
        txtAdresse = addField(form, "Adresse Postale");

        this.add(form, BorderLayout.CENTER);

        // Boutons
        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pBtn.setBackground(COLOR_BG);
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

    // =========================================================================
    // Méthodes
    // =========================================================================


    // =========================================================================
    // Ajoute un champ texte stylisé
    // =========================================================================
    private JTextField addField(JPanel p, String labelText) {
        JLabel lbl = new JLabel(labelText);
        lbl.setForeground(Color.LIGHT_GRAY);
        p.add(lbl);

        JTextField txt = new JTextField();
        txt.setBackground(COLOR_INPUT);
        txt.setForeground(Color.WHITE);
        txt.setCaretColor(Color.WHITE);
        txt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(100, 100, 100)),
                new EmptyBorder(5, 5, 5, 5)
        ));
        p.add(txt);
        return txt;
    }

    // =========================================================================
    // Applique le style bouton
    // =========================================================================
    private void styleBouton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
    }

    // =========================================================================
    // Valide et crée le client
    // =========================================================================
    private void actionValider() {
        try {
            if (txtNom.getText().isEmpty() || txtPrenom.getText().isEmpty()) {
                throw new Exception("Le Nom et le Prénom sont obligatoires.");
            }

            Client c = new Client(
                    txtNom.getText().trim(),
                    txtPrenom.getText().trim(),
                    txtAdresse.getText().trim(),
                    txtMail.getText().trim()
            );

            garage.creerClient(c);
            this.nouveauClient = c;
            this.valide = true;
            this.dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }
    }

    // =========================================================================
    // Getters
    // =========================================================================
    public boolean isValide() { return valide; }
    public Client getNouveauClient() { return nouveauClient; }
}