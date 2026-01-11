package back;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("REPARATION")
public class Reparation extends TypeIntervention{

    public Reparation() {
        super();
        this.setTauxHoraire(120);
    }
}
