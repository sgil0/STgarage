package back;

import jakarta.persistence.*;

@Entity
public class Reparation extends TypeIntervention{

    public Reparation() {
        super();
        this.setTauxHoraire(120);
    }
}
