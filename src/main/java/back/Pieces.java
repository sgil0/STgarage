package back;

import jakarta.persistence.*;

import java.util.Collection;
import java.util.List;

@Entity
public class Pieces {
    @Id
    private String ref;
    private String nom;
    private String constructeur;
    private float prix;

    // TypeVehicule many to many
    @ManyToMany
    @JoinColumn(name = "pieces")
    private Collection<TypeVehicule> TypesVehicules;

    // TypeIntervention many to many
    @ManyToMany(mappedBy = "refPiecesUtilisees")
    private List<TypeIntervention> idInterventionsLiees;
}
