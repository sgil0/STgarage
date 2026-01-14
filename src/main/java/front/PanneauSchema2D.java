package front;

import back.GestionGarage;
import back.Pieces;
import back.EnumType.ZoneIntervention;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class PanneauSchema2D extends JPanel {

    // On stocke maintenant des OBJETS Pieces, pas juste des noms
    private List<Pieces> piecesSelectionnees = new ArrayList<>();
    private JLabel infoLabel;
    private GestionGarage garage; // Lien vers le Back

    // Le constructeur prend le garage en paramètre
    public PanneauSchema2D(GestionGarage garage) {
        this.garage = garage; // On récupère le lien
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Consigne utilisateur
        infoLabel = new JLabel("Cliquez sur : Moteur, Train Avant ou Train Arrière", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 12));
        this.add(infoLabel, BorderLayout.NORTH);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                verifierZone(e.getX(), e.getY());
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int cx = w / 2;
        int cy = h / 2;

        // --- 1. DESSIN DE LA VOITURE (Vue de dessus) ---

        // A. Zone AVANT (Bleu) : Roues avant + Nez
        g2.setColor(Color.BLUE);
        g2.fillRect(cx - 70, cy - 100, 20, 40); // Roue AVG
        g2.fillRect(cx + 50, cy - 100, 20, 40); // Roue AVD
        g2.setColor(new Color(200, 200, 255)); // Bleu clair pour la zone
        g2.fillRoundRect(cx - 50, cy - 130, 100, 50, 20, 20); // Pare-choc AV
        g2.setColor(Color.BLUE);
        g2.drawString("AVANT", cx - 20, cy - 110);

        // B. Zone ARRIÈRE (Vert) : Roues arrière + Coffre
        g2.setColor(new Color(0, 128, 0)); // Vert foncé
        g2.fillRect(cx - 70, cy + 60, 20, 40); // Roue ARG
        g2.fillRect(cx + 50, cy + 60, 20, 40); // Roue ARD
        g2.setColor(new Color(200, 255, 200)); // Vert clair
        g2.fillRoundRect(cx - 50, cy + 80, 100, 50, 20, 20); // Coffre
        g2.setColor(new Color(0, 128, 0));
        g2.drawString("ARRIÈRE", cx - 25, cy + 105);

        // C. Zone MOTEUR / HABITACLE (Rouge/Gris)
        // Corps principal de la voiture
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawRoundRect(cx - 50, cy - 80, 100, 160, 10, 10); // Châssis

        // Le bloc MOTEUR (Rouge)
        g2.setColor(new Color(255, 200, 200)); // Rouge pâle
        g2.fillRect(cx - 40, cy - 70, 80, 60);
        g2.setColor(Color.RED);
        g2.drawRect(cx - 40, cy - 70, 80, 60);
        g2.drawString("MOTEUR", cx - 25, cy - 40);
    }

    private void verifierZone(int x, int y) {
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        // Logique de détection des clics (Zones approximatives)
        // On appelle le Back pour avoir les vraies pièces !

        // 1. Zone Moteur (Centre Haut)
        if (x > cx - 40 && x < cx + 40 && y > cy - 70 && y < cy - 10) {
            List<Pieces> piecesDispo = garage.getPiecesParZone(ZoneIntervention.BLOC_MOTEUR);
            ouvrirPopup("Moteur", piecesDispo);
        }
        // 2. Zone Avant (Haut)
        else if (y < cy - 80) {
            List<Pieces> piecesDispo = garage.getPiecesParZone(ZoneIntervention.TRAIN_AVANT);
            ouvrirPopup("Train Avant", piecesDispo);
        }
        // 3. Zone Arrière (Bas)
        else if (y > cy + 50) {
            List<Pieces> piecesDispo = garage.getPiecesParZone(ZoneIntervention.TRAIN_ARRIERE);
            ouvrirPopup("Train Arrière", piecesDispo);
        }
    }

    private void ouvrirPopup(String titreZone, List<Pieces> piecesDispo) {
        if (piecesDispo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucune pièce trouvée en stock pour : " + titreZone);
            return;
        }

        JPanel p = new JPanel(new GridLayout(0, 1));
        List<JCheckBox> checkboxes = new ArrayList<>();

        for (Pieces piece : piecesDispo) {
            // On affiche le Nom de la pièce et son prix
            JCheckBox box = new JCheckBox(piece.getNom() + " (" + piece.getPrix() + "€)");

            // On vérifie si cette pièce précise est déjà sélectionnée
            if (piecesSelectionnees.contains(piece)) {
                box.setSelected(true);
            }

            // On stocke l'objet Piece DANS le composant pour le retrouver facilement plus tard
            box.putClientProperty("pieceObj", piece);

            checkboxes.add(box);
            p.add(box);
        }

        int rep = JOptionPane.showConfirmDialog(this, p, "Pièces : " + titreZone, JOptionPane.OK_CANCEL_OPTION);

        if (rep == JOptionPane.OK_OPTION) {
            for (JCheckBox box : checkboxes) {
                Pieces laPiece = (Pieces) box.getClientProperty("pieceObj");
                if (box.isSelected()) {
                    if (!piecesSelectionnees.contains(laPiece)) {
                        piecesSelectionnees.add(laPiece);
                    }
                } else {
                    piecesSelectionnees.remove(laPiece);
                }
            }
            infoLabel.setText(piecesSelectionnees.size() + " pièces sélectionnées");
        }
    }

    // Nouvelle méthode qui retourne directement les objets Pieces pour le formulaire principal
    public List<Pieces> getPiecesSelectionnees() {
        return piecesSelectionnees;
    }

    public void reset() {
        piecesSelectionnees.clear();
        infoLabel.setText("Sélectionnez des pièces...");
    }

}