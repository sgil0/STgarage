package front;

import back.*;
import back.EnumType.*;
import front.Listener.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class PanneauSchema2D extends JPanel {

    // =========================================================================
    // Attributs
    // =========================================================================

    private final Color COLOR_CARD = new Color(60, 60, 60);

    private final List<TypeIntervention> typesSelectionnes = new ArrayList<>();
    private final JLabel infoLabel;
    private final GestionGarage garage;
    private Vehicule vehiculeCourant;
    private SchemaListener listener;

    // =========================================================================
    // Constructeurs
    // =========================================================================

    public PanneauSchema2D(GestionGarage garage) {
        this.garage = garage;
        this.setLayout(new BorderLayout());
        this.setBackground(COLOR_CARD);

        infoLabel = new JLabel("Selectionnez un vehicule", SwingConstants.CENTER);
        infoLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        infoLabel.setForeground(Color.LIGHT_GRAY);
        this.add(infoLabel, BorderLayout.NORTH);

        // Ecouteur souris
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (vehiculeCourant != null) {
                    verifierZone(e.getX(), e.getY());
                }
            }
        });
    }

    // =========================================================================
    // Methodes
    // =========================================================================

    public void setListener(SchemaListener listener) {
        this.listener = listener;
    }

    // =========================================================================
    // Dessin
    // =========================================================================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // J'ai essayé de mettre l'anti aliasing mais ça ralentissait l'affichage sur mon PC
        // g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int cx = w / 2;
        int cy = h / 2;

        // Roues
        g2.setColor(new Color(30, 30, 30));
        // Roue gauche haut
        g2.fillRect(cx - 70, cy - 100, 20, 40);
        // Roue droite haut
        g2.fillRect(cx + 50, cy - 100, 20, 40);
        // Roue gauche bas
        g2.fillRect(cx - 70, cy + 60, 20, 40);
        // Roue droite bas
        g2.fillRect(cx + 50, cy + 60, 20, 40);

        // Corps Avant (Capot)
        g2.setColor(new Color(80, 100, 180));
        g2.fillRoundRect(cx - 50, cy - 130, 100, 50, 20, 20);
        g2.setColor(Color.WHITE);
        g2.drawString("AVANT", cx - 20, cy - 110);

        // Corps Arriere (Coffre)
        g2.setColor(new Color(80, 160, 100));
        g2.fillRoundRect(cx - 50, cy + 80, 100, 50, 20, 20);
        g2.setColor(Color.WHITE);
        g2.drawString("ARRIERE", cx - 25, cy + 105);

        // Habitacle (Centre)
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawRoundRect(cx - 50, cy - 80, 100, 160, 10, 10);

        // Moteur (bruitage visuel rouge)
        g2.setColor(new Color(200, 100, 100));
        g2.fillRect(cx - 40, cy - 70, 80, 60);
        g2.setColor(new Color(255, 80, 80));
        g2.drawRect(cx - 40, cy - 70, 80, 60);
        g2.setColor(Color.WHITE);
        g2.drawString("MOTEUR", cx - 25, cy - 40);
    }

    // =========================================================================
    // Logique Zone
    // =========================================================================
    private void verifierZone(int x, int y) {
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        ZoneIntervention zone = null;

        // On regarde où on a cliqué
        if (x > cx - 40 && x < cx + 40 && y > cy - 70 && y < cy - 10) {
            zone = ZoneIntervention.BLOC_MOTEUR;
        }
        else if (y < cy - 80) {
            zone = ZoneIntervention.TRAIN_AVANT;
        }
        else if (y > cy + 50) {
            zone = ZoneIntervention.TRAIN_ARRIERE;
        }

        if (zone != null) {
            // On recupere l'energie du vehicule pour filtrer
            Energie energie = Energie.ESSENCE; // Valeur par defaut

            if (vehiculeCourant.getTypeVehicule() != null) {
                energie = vehiculeCourant.getTypeVehicule().getEnergie();
            }

            List<TypeIntervention> dispos = garage.getTypesParZoneEtEnergie(zone, energie);
            ouvrirPopup(zone.toString(), dispos);
        }
    }

    // =========================================================================
    // Popup choix
    // =========================================================================
    private void ouvrirPopup(String titreZone, List<TypeIntervention> dispos) {
        if (dispos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucune intervention disponible pour ce moteur.");
            return;
        }

        JPanel p = new JPanel(new GridLayout(0, 1));
        p.setBackground(new Color(45, 45, 45));

        List<JCheckBox> checkboxes = new ArrayList<>();

        for (TypeIntervention type : dispos) {
            JCheckBox box = new JCheckBox(type.getNom() + " (" + type.getPrix() + " euros)");
            box.setBackground(new Color(45, 45, 45));
            box.setForeground(Color.WHITE);
            box.setFocusPainted(false);

            if (typesSelectionnes.contains(type)) {
                box.setSelected(true);
            }
            // Garder l'objet dans la checkbox
            box.putClientProperty("typeObj", type);
            checkboxes.add(box);
            p.add(box);
        }

        int rep = JOptionPane.showConfirmDialog(this, p, "Zone : " + titreZone, JOptionPane.OK_CANCEL_OPTION);

        if (rep == JOptionPane.OK_OPTION) {
            for (JCheckBox box : checkboxes) {
                TypeIntervention t = (TypeIntervention) box.getClientProperty("typeObj");

                if (box.isSelected()) {
                    // Si coché et pas encore dans la liste, on ajoute
                    boolean dejaPresent = false;
                    for(TypeIntervention existing : typesSelectionnes) {
                        if(existing.equals(t)) {
                            dejaPresent = true;
                        }
                    }
                    if (!dejaPresent) {
                        typesSelectionnes.add(t);
                    }
                } else {
                    // Si pas coché, on enleve
                    typesSelectionnes.remove(t);
                }
            }
            // Notification
            if (listener != null) {
                listener.onSelectionChanged(typesSelectionnes);
            }
        }
    }

    // =========================================================================
    // Reset la selection
    // =========================================================================
    public void reset() {
        typesSelectionnes.clear();
        if (listener != null) {
            listener.onSelectionChanged(typesSelectionnes);
        }
    }

    public void setVehicule(Vehicule v) {
        this.vehiculeCourant = v;
        reset();
        if (v != null) {
            infoLabel.setText("");
        } else {
            infoLabel.setText("Selectionnez un vehicule");
        }
        this.repaint();
    }

    public List<TypeIntervention> getTypesSelectionnes() {
        return typesSelectionnes;
    }
}