package back;

import jakarta.persistence.*;

import java.util.List;

@Entity
@DiscriminatorValue("REPARATION")
public class Reparation extends TypeIntervention{

    public Reparation(String nom, int duree, List<Pieces> pieces) {
        super(); // Initialise les listes vides du parent
        this.setNom(nom);
        this.setDuree(duree);
        this.setPiecesUtilisees(pieces);

        // Règles métier de l'Entretien
        this.setTauxHoraire(120);

        // Calcul automatique du prix
        this.calculerPrixForfait();
    }

    public Reparation() {

    }
}
