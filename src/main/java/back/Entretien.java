package back;

import jakarta.persistence.*;

import java.util.List;

@Entity
@DiscriminatorValue("ENTRETIEN")
public class Entretien extends TypeIntervention {
    private float kilometrageMax;

    // Nouveau constructeur "Tout-en-un"
    public Entretien(String nom, int duree, List<Pieces> pieces, float kilometrageMax) {
        super(); // Initialise les listes vides du parent
        this.setNom(nom);
        this.setDuree(duree);
        this.setPiecesUtilisees(pieces);
        this.kilometrageMax = kilometrageMax;

        // Règles métier de l'Entretien
        this.setTauxHoraire(90);

        // Calcul automatique du prix (plus besoin de l'appeler dans le Main !)
        this.calculerPrixForfait();
    }

    public Entretien() {
        super();
        this.kilometrageMax = 20000;
        this.setTauxHoraire(90);
    }

    public float getKilometrageMax() {
        return kilometrageMax;
    }

    public void setKilometrageMax(float kilometrageMax) {
        this.kilometrageMax = kilometrageMax;
    }
}
