package back;

import back.EnumType.ZoneIntervention;
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
    @Enumerated(EnumType.STRING)
    private ZoneIntervention zone;

    // TypeVehicule many to many
    @ManyToMany(mappedBy = "pieces")
    private Collection<TypeVehicule> typesVehicules;

    // TypeIntervention many to many
    @ManyToMany(mappedBy = "refPiecesUtilisees")
    private List<TypeIntervention> interventionsLiees;

    @ManyToMany(mappedBy = "piecesReelles")
    private List<Intervention> interventions;

    public float getPrix() {
        return this.prix;
    }

    public ZoneIntervention getZone() {
        return zone;
    }

    public void setZone(ZoneIntervention zone) {
        this.zone = zone;
    }

    public List<Intervention> getInterventions() {
        return interventions;
    }
}
