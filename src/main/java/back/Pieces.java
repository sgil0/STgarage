package back;

import back.EnumType.ZoneIntervention;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
public class Pieces {
    @Id
    private String ref;
    private String nom;
    private float prix;
    @Enumerated(EnumType.STRING)
    private ZoneIntervention zone;

    // TypeIntervention many to many
    @ManyToMany(mappedBy = "piecesUtilisees")
    private List<TypeIntervention> interventionsLiees;

    @ManyToMany(mappedBy = "piecesReelles")
    private List<Intervention> interventions;

    public Pieces(){
    }

    public Pieces(String ref, String nom, float prix, ZoneIntervention zone){
        this.ref = ref;
        this.nom = nom;
        this.prix = prix;
        this.zone = zone;
        this.interventionsLiees = new ArrayList<>();;
        this.interventions = new ArrayList<>();;
    }

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

    public String getNom() {
        return nom;
    }
}
