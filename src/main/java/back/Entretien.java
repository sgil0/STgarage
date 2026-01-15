package back;

import back.EnumType.ZoneIntervention;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.List;

@Entity
@DiscriminatorValue("ENTRETIEN")
public class Entretien extends TypeIntervention {

    // =========================================================================
    // Attributs
    // =========================================================================

    private int kilometrageMax;

    // =========================================================================
    // Constructeurs
    // =========================================================================

    public Entretien() {
        super();
    }

    public Entretien(String nom, int duree, List<Pieces> piecesUtilisees, int kilometrageMax, ZoneIntervention zone) {
        super(nom, duree, piecesUtilisees, zone);
        this.kilometrageMax = kilometrageMax;
    }

    // =========================================================================
    // Méthodes
    // =========================================================================



    // =========================================================================
    // Setters
    // =========================================================================

    public void setKilometrageMax(int kilometrageMax) {
        this.kilometrageMax = kilometrageMax;
    }

    // =========================================================================
    // Getters
    // =========================================================================

    public int getKilometrageMax() {
        return kilometrageMax;
    }

}