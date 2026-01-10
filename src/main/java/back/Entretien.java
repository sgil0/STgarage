package back;

import jakarta.persistence.*;

@Entity
public class Entretien extends TypeIntervention {
    private float kilometrageMax;
    private int dureeMaxMois;

    public Entretien(String nom, float kilometrageMax) {
        super();
        this.setNom(nom);
        this.kilometrageMax = kilometrageMax;
        this.setTauxHoraire(90);
    }

    public Entretien() {
        super();
        this.kilometrageMax = 20000;
        this.setTauxHoraire(90);
    }

    public float getKilometrageMax() {
        return kilometrageMax;
    }

    public int getDureeMaxMois() {
        return dureeMaxMois;
    }
}
