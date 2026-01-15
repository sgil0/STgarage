package front;

import back.*;
import back.EnumType.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DialogAjoutVehicule extends JDialog {

    // =========================================================================
    // Attributs
    // =========================================================================

    private boolean valide = false;
    private final JTextField txtImmat, txtMarque, txtModele, txtKm, txtDateCirc;
    private final GestionGarage garage;


    private final Color COLOR_BG = new Color(45, 45, 45);
    private final Color COLOR_TEXT = new Color(245, 245, 245);
    private final Color COLOR_INPUT = new Color(80, 80, 80);
    private final Color COLOR_ORANGE = new Color(233, 84, 32);
    private final Color COLOR_BORDER = new Color(100, 100, 100);

    private final Font FONT_INPUT = new Font("SansSerif", Font.PLAIN, 14);

    // =========================================================================
    // Constructeurs
    // =========================================================================

    public DialogAjoutVehicule(JFrame parent, GestionGarage garage) {
        super(parent, "Nouveau Véhicule", true);
        this.garage = garage;
        this.setSize(450, 600); // Hauteur ajustée pour le nouveau champ
        this.setLocationRelativeTo(parent);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(COLOR_BG);

        // Header
        JLabel lblTitre = new JLabel("Ajouter un véhicule", SwingConstants.CENTER);
        lblTitre.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitre.setForeground(COLOR_TEXT);
        lblTitre.setBorder(new EmptyBorder(20, 0, 20, 0));
        this.add(lblTitre, BorderLayout.NORTH);

        // Formulaire
        JPanel form = new JPanel(new GridLayout(0, 1, 0, 20));
        form.setBackground(COLOR_BG);
        form.setBorder(new EmptyBorder(0, 40, 0, 40));

        txtImmat = addField(form, "Immatriculation (ex: AA-123-BB)");
        txtMarque = addField(form, "Marque (ex: Peugeot)");
        txtModele = addField(form, "Modèle (ex: 308)");
        txtDateCirc = addField(form, "Date Mise en Circ. (JJ/MM/AAAA)");
        txtKm = addField(form, "Kilométrage Actuel");

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
    // Ajouter un champ texte stylisé
    // =========================================================================
    private JTextField addField(JPanel p, String labelText) {
        JLabel lbl = new JLabel(labelText);
        lbl.setForeground(Color.LIGHT_GRAY);
        p.add(lbl);

        JTextField txt = new JTextField();
        txt.setPreferredSize(new Dimension(0, 40));
        txt.setFont(FONT_INPUT);

        txt.setBackground(COLOR_INPUT);
        txt.setForeground(Color.WHITE);
        txt.setCaretColor(Color.WHITE);

        txt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER),
                new EmptyBorder(5, 10, 5, 10)
        ));
        p.add(txt);
        return txt;
    }

    // =========================================================================
    // Style Bouton
    // =========================================================================
    private void styleBouton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
    }

    // =========================================================================
    // Action Valider
    // =========================================================================
    private void actionValider() {
        try {
            // Récupération des textes
            String immat = txtImmat.getText().trim().toUpperCase();
            String marque = txtMarque.getText().trim();
            String modele = txtModele.getText().trim();
            String kmTxt = txtKm.getText().trim();
            String dateTxt = txtDateCirc.getText().trim();

            // Vérification champs vides
            if (immat.isEmpty() || marque.isEmpty() || modele.isEmpty() || kmTxt.isEmpty() || dateTxt.isEmpty()) {
                throw new Exception("Tous les champs sont obligatoires.");
            }

            // Parsing (Conversion) des données
            float km = Float.parseFloat(kmTxt);

            // Parsing de la date (Format JJ/MM/AAAA)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate dateCirculation = LocalDate.parse(dateTxt, formatter);

            if (dateCirculation.isAfter(LocalDate.now())) {
                throw new Exception("La date de mise en circulation ne peut pas être dans le futur.");
            }


            // Gestion du Type de Véhicule (Existant ou Nouveau)
            TypeVehicule type = garage.trouverTypeVehicule(marque, modele);

            // Si le type n'existe pas, on propose de le créer
            if (type == null) {
                int choix = JOptionPane.showConfirmDialog(this,
                        "Le modèle '" + marque + " " + modele + "' est inconnu.\nVoulez-vous le créer ?",
                        "Modèle inconnu", JOptionPane.YES_NO_OPTION);

                if (choix == JOptionPane.YES_OPTION) {
                    DialogAjoutTypeVehicule dialogType = new DialogAjoutTypeVehicule(this, garage, marque, modele);
                    dialogType.setVisible(true);

                    if (dialogType.isValide()) {
                        // On recharge le type qui vient d'être créé
                        type = garage.trouverTypeVehicule(marque, modele);
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            }

            // Double sécurité
            if (type == null) throw new Exception("Impossible d'associer un type de véhicule.");

            // Création du Véhicule
            Vehicule v = new Vehicule(immat, dateCirculation, km, type, null);
            garage.creerVehicule(v);

            this.valide = true;
            this.dispose();

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Le kilométrage doit être un nombre valide (ex: 15000).");
        } catch (DateTimeParseException dtpe) {
            JOptionPane.showMessageDialog(this, "Format de date invalide. Utilisez JJ/MM/AAAA (ex: 25/12/2020).");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());

        }
    }

    // =========================================================================
    // Getters
    // =========================================================================
    public boolean isValide() { return valide; }
}