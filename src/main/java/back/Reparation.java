package back;

import back.EnumType.ZoneIntervention;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.List;

@Entity
@DiscriminatorValue("REPARATION")
public class Reparation extends TypeIntervention {

    // =========================================================================
    // Attributs
    // =========================================================================



    // =========================================================================
    // Constructeurs
    // =========================================================================

    public Reparation() {
        super();
    }

    public Reparation(String nom, int duree, List<Pieces> piecesUtilisees, ZoneIntervention zone) {
        super(nom, duree, piecesUtilisees, zone);
        this.setTauxHoraire(120);
        this.calculerPrixForfait();
    }

    // =========================================================================
    // Méthodes
    // =========================================================================



    // =========================================================================
    // Getters, Setters, equals etc...
    // =========================================================================


}