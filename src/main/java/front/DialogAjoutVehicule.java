package front;

import back.*;
import back.EnumType.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

public class DialogAjoutVehicule extends JDialog {

    private boolean valide = false;
    private JTextField txtImmat, txtMarque, txtModele, txtKm;
    private GestionGarage garage;

    public DialogAjoutVehicule(JFrame parent, GestionGarage garage) {
        super(parent, "Nouveau Véhicule", true); // true = Modal (bloque la fenêtre derrière)
        this.garage = garage;
        this.setSize(400, 500);
        this.setLocationRelativeTo(parent);
        this.setLayout(new BorderLayout());

        // Header
        JLabel lblTitre = new JLabel("Ajouter un véhicule", SwingConstants.CENTER);
        lblTitre.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitre.setBorder(new EmptyBorder(20, 0, 20, 0));
        this.add(lblTitre, BorderLayout.NORTH);

        // Formulaire
        JPanel form = new JPanel(new GridLayout(0, 1, 0, 10));
        form.setBorder(new EmptyBorder(0, 40, 0, 40));

        txtImmat = addField(form, "Immatriculation (AA-123-BB)");
        txtMarque = addField(form, "Marque");
        txtModele = addField(form, "Modèle");
        txtKm = addField(form, "Kilométrage");

        this.add(form, BorderLayout.CENTER);

        // Boutons
        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pBtn.setBorder(new EmptyBorder(20, 20, 20, 20));

        JButton btnAnnuler = new JButton("Annuler");
        btnAnnuler.addActionListener(e -> dispose());

        JButton btnValider = new JButton("Enregistrer");
        btnValider.setBackground(new Color(50, 100, 160));
        btnValider.setForeground(Color.WHITE);
        btnValider.addActionListener(e -> actionValider());

        pBtn.add(btnAnnuler);
        pBtn.add(btnValider);
        this.add(pBtn, BorderLayout.SOUTH);
    }

    private JTextField addField(JPanel p, String label) {
        p.add(new JLabel(label));
        JTextField txt = new JTextField();
        p.add(txt);
        return txt;
    }

    private void actionValider() {
        try {
            String immat = txtImmat.getText().trim().toUpperCase();
            String marque = txtMarque.getText().trim();
            String modele = txtModele.getText().trim();
            float km = Float.parseFloat(txtKm.getText().trim());

            if (immat.isEmpty() || marque.isEmpty()) throw new Exception("Champs vides");

            // Recherche ou Création du Type (Simplifié pour l'exemple)
            TypeVehicule type = garage.trouverTypeVehicule(marque, modele);
            if (type == null) {
                // Ici, idéalement, on ouvrirait une autre popup pour créer le type.
                // Pour l'instant, on bloque.
                throw new Exception("Modèle inconnu. Veuillez le créer en base d'abord.");
            }

            Vehicule v = new Vehicule(immat, LocalDate.now(), km, type, null);
            garage.creerVehicule(v);

            this.valide = true;
            this.dispose();

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Kilométrage invalide");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }
    }

    public boolean isValide() { return valide; }
}