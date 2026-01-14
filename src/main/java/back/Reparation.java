package back;

import back.EnumType.ZoneIntervention;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.util.List;

@Entity
@DiscriminatorValue("REPARATION")
public class Reparation extends TypeIntervention {

    public Reparation() {
        super();
    }

    public Reparation(String nom, int duree, List<Pieces> piecesUtilisees, ZoneIntervention zone) {
        // 1. Le super constructeur calcule avec le taux de 90€ (par défaut)
        super(nom, duree, piecesUtilisees, zone);

        // 2. CORRECTION : On applique le taux "Réparation" et on recalcule
        this.setTauxHoraire(120);
        this.calculerPrixForfait();
    }
}