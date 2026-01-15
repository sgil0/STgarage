package back;

import back.EnumType.ZoneIntervention;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Pieces {

    // =========================================================================
    // Attributs
    // =========================================================================

    @Id
    private String ref;
    private String nom;
    private float prix;
    @Enumerated(EnumType.STRING)
    private ZoneIntervention zone;

    // TypeIntervention many to many
    @ManyToMany(mappedBy = "piecesUtilisees")
    private List<TypeIntervention> interventionsLiees;

    // =========================================================================
    // Constructeurs
    // =========================================================================

    public Pieces() {
    }

    public Pieces(String ref, String nom, float prix, ZoneIntervention zone) {
        this.ref = ref;
        this.nom = nom;
        this.prix = prix;
        this.zone = zone;
        this.interventionsLiees = new ArrayList<>();
    }

    // =========================================================================
    // Méthodes
    // =========================================================================



    // =========================================================================
    // Setters
    // =========================================================================

    public void setZone(ZoneIntervention zone) {
        this.zone = zone;
    }

    // =========================================================================
    // Getters
    // =========================================================================

    public String getNom() {
        return nom;
    }
    public float getPrix() {
        return this.prix;
    }
    public ZoneIntervention getZone() {
        return zone;
    }
}
