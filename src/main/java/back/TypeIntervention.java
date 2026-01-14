package back;

import back.EnumType.Energie;
import back.EnumType.ZoneIntervention;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "type_intervention")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "categorie_type")
public abstract class TypeIntervention {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nom;
    private int duree;
    private float tauxHoraire;
    private float prix;

    // NOUVEAU : Une intervention appartient à une zone (pour le clic sur le schéma)
    @Enumerated(EnumType.STRING)
    private ZoneIntervention zone;

    // Relation Inverse ManyToMany (Une liste de types peut appartenir à plusieurs interventions)
    @ManyToMany(mappedBy = "typesIntervention")
    private Collection<Intervention> interventions;

    @ManyToMany
    @JoinTable(
            name = "ref_intervention_pieces",
            joinColumns = @JoinColumn(name = "id_type_intervention"),
            inverseJoinColumns = @JoinColumn(name = "ref_piece")
    )
    private List<Pieces> piecesUtilisees;

    @ElementCollection(targetClass = Energie.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "type_intervention_energies", joinColumns = @JoinColumn(name = "id_type_intervention"))
    @Column(name = "energie_compatible")
    private List<Energie> energiesCompatibles = new ArrayList<>();

    public TypeIntervention() {
        this.nom = "default";
        this.duree = 60;
        this.interventions = new ArrayList<>();
        this.piecesUtilisees = new ArrayList<>();
        this.tauxHoraire = 90;
        this.zone = ZoneIntervention.BLOC_MOTEUR; // Valeur par défaut
    }

    public TypeIntervention(String nom, int duree, List<Pieces> piecesUtilisees, ZoneIntervention zone) {
        this.nom = nom;
        this.duree = duree;
        this.interventions = new ArrayList<>();
        this.piecesUtilisees = piecesUtilisees;
        this.tauxHoraire = 90;
        this.zone = zone;
        this.prix = 0;
        calculerPrixForfait();
    }

    public void ajouterEnergieCompatible(Energie e) {
        if (!energiesCompatibles.contains(e)) energiesCompatibles.add(e);
    }

    public boolean estCompatible(Energie energieVehicule) {
        if (energiesCompatibles.isEmpty()) return true;
        return energiesCompatibles.contains(energieVehicule);
    }

    public void calculerPrixForfait() {
        float coutMainDoeuvre = (this.duree / 60.0f) * this.tauxHoraire;
        float coutPieces = 0;
        if (this.piecesUtilisees != null) {
            for (Pieces p : this.piecesUtilisees) {
                coutPieces += p.getPrix();
            }
        }
        this.prix = coutMainDoeuvre + coutPieces;
    }

    // Getters / Setters
    public ZoneIntervention getZone() { return zone; }
    public void setZone(ZoneIntervention zone) { this.zone = zone; }
    public float getPrix() { return prix; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public int getDuree() { return duree; }
    public void setDuree(int duree) { this.duree = duree; }
    public float getTauxHoraire() { return tauxHoraire; }
    public void setTauxHoraire(float tauxHoraire) { this.tauxHoraire = tauxHoraire; }
    public List<Pieces> getPiecesUtilisees() { return piecesUtilisees; }

    @Override
    public String toString() { return this.nom + " (" + this.prix + "€)"; }
}