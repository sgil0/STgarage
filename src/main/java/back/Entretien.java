package back;

import jakarta.persistence.*;

@Entity
public class Entretien extends TypeIntervention {
    private float kilometrageMax;

    public Entretien(String nom, float kilometrageMax) {
        super();
        this.setNom(nom);
        this.kilometrageMax = kilometrageMax;
    }

    public Entretien() {
        super();
        this.kilometrageMax = 20000;
    }

    public float getKilometrageMax() {
        return kilometrageMax;
    }
}
