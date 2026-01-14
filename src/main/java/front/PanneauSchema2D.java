package front;

import back.*;
import back.EnumType.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class PanneauSchema2D extends JPanel {

    private List<TypeIntervention> typesSelectionnes = new ArrayList<>();
    private JLabel infoLabel;
    private GestionGarage garage;
    private Vehicule vehiculeCourant;
    private SchemaListener listener;

    public PanneauSchema2D(GestionGarage garage) {
        this.garage = garage;
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        infoLabel = new JLabel("Sélectionnez un véhicule dans l'onglet 'Gestion Véhicules'", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 12));
        this.add(infoLabel, BorderLayout.NORTH);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (vehiculeCourant == null) return; // On ne fait rien si pas de véhicule
                verifierZone(e.getX(), e.getY());
            }
        });
    }

    public void setListener(SchemaListener listener) {
        this.listener = listener;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(); int h = getHeight(); int cx = w / 2; int cy = h / 2;

        // --- DESSIN DE LA VOITURE (Identique) ---
        g2.setColor(Color.BLUE);
        g2.fillRect(cx - 70, cy - 100, 20, 40); g2.fillRect(cx + 50, cy - 100, 20, 40);
        g2.setColor(new Color(200, 200, 255));
        g2.fillRoundRect(cx - 50, cy - 130, 100, 50, 20, 20);
        g2.setColor(Color.BLUE); g2.drawString("AVANT", cx - 20, cy - 110);

        g2.setColor(new Color(0, 128, 0));
        g2.fillRect(cx - 70, cy + 60, 20, 40); g2.fillRect(cx + 50, cy + 60, 20, 40);
        g2.setColor(new Color(200, 255, 200));
        g2.fillRoundRect(cx - 50, cy + 80, 100, 50, 20, 20);
        g2.setColor(new Color(0, 128, 0)); g2.drawString("ARRIÈRE", cx - 25, cy + 105);

        g2.setColor(Color.LIGHT_GRAY);
        g2.drawRoundRect(cx - 50, cy - 80, 100, 160, 10, 10);

        g2.setColor(new Color(255, 200, 200));
        g2.fillRect(cx - 40, cy - 70, 80, 60);
        g2.setColor(Color.RED);
        g2.drawRect(cx - 40, cy - 70, 80, 60);
        g2.drawString("MOTEUR", cx - 25, cy - 40);
    }

    private void verifierZone(int x, int y) {
        int cx = getWidth() / 2; int cy = getHeight() / 2;
        ZoneIntervention zone = null;

        if (x > cx - 40 && x < cx + 40 && y > cy - 70 && y < cy - 10) zone = ZoneIntervention.BLOC_MOTEUR;
        else if (y < cy - 80) zone = ZoneIntervention.TRAIN_AVANT;
        else if (y > cy + 50) zone = ZoneIntervention.TRAIN_ARRIERE;

        if (zone != null) {
            Energie energie = (vehiculeCourant.getTypeVehicule() != null) ? vehiculeCourant.getTypeVehicule().getEnergie() : Energie.ESSENCE;
            List<TypeIntervention> dispos = garage.getTypesParZoneEtEnergie(zone, energie);
            ouvrirPopup(zone.toString(), dispos);
        }
    }

    private void ouvrirPopup(String titreZone, List<TypeIntervention> dispos) {
        if (dispos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucune intervention disponible pour cette zone sur ce type de moteur.");
            return;
        }

        JPanel p = new JPanel(new GridLayout(0, 1));
        List<JCheckBox> checkboxes = new ArrayList<>();

        for (TypeIntervention type : dispos) {
            JCheckBox box = new JCheckBox(type.getNom() + " (" + type.getPrix() + "€)");
            if (typesSelectionnes.contains(type)) box.setSelected(true);
            box.putClientProperty("typeObj", type);
            checkboxes.add(box);
            p.add(box);
        }

        int rep = JOptionPane.showConfirmDialog(this, p, "Interventions : " + titreZone, JOptionPane.OK_CANCEL_OPTION);

        if (rep == JOptionPane.OK_OPTION) {
            for (JCheckBox box : checkboxes) {
                TypeIntervention t = (TypeIntervention) box.getClientProperty("typeObj");
                if (box.isSelected()) {
                    if (!typesSelectionnes.contains(t)) typesSelectionnes.add(t);
                } else {
                    typesSelectionnes.remove(t);
                }
            }
            if (listener != null) listener.onSelectionChanged(typesSelectionnes);
        }
    }

    public void setVehicule(Vehicule v) {
        this.vehiculeCourant = v;
        reset(); // On vide la sélection précédente
        if (v != null) {
            infoLabel.setText(""); // On vide le label car l'info sera ailleurs
        }
    }

    public List<TypeIntervention> getTypesSelectionnes() {
        return typesSelectionnes;
    }

    public void reset() {
        typesSelectionnes.clear();
        if (listener != null) listener.onSelectionChanged(typesSelectionnes); // Vide la liste visuelle
    }
}