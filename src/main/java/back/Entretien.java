package back;

import back.EnumType.ZoneIntervention;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.util.List;

@Entity
@DiscriminatorValue("ENTRETIEN")
public class Entretien extends TypeIntervention {

    private int kilometrageMax; // Fréquence (ex: tous les 15000 km)

    public Entretien() {
        super();
    }

    public Entretien(String nom, int duree, List<Pieces> piecesUtilisees, int kilometrageMax, ZoneIntervention zone) {
        // On appelle le nouveau constructeur parent avec la Zone
        super(nom, duree, piecesUtilisees, zone);
        this.kilometrageMax = kilometrageMax;
    }

    public int getKilometrageMax() {
        return kilometrageMax;
    }

    public void setKilometrageMax(int kilometrageMax) {
        this.kilometrageMax = kilometrageMax;
    }
}