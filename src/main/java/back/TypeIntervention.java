package back;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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

    // Intervention one to many
    @OneToMany(mappedBy = "typeIntervention")
    private Collection<Intervention> interventions;

    // Pieces many to many
    @ManyToMany
    @JoinTable(
            name = "ref_intervention_pieces", // Nom de la table intermédiaire
            joinColumns = @JoinColumn(name = "id_intervention"),
            inverseJoinColumns = @JoinColumn(name = "ref_piece")
    )
    private List<Pieces> piecesUtilisees;

    public TypeIntervention() {
        this.nom = "default";
        this.duree = 120; // 120 minutes
        this.interventions = new ArrayList<>(); // Initialisation de la collection vide
        this.piecesUtilisees = new ArrayList<>(); // Très important aussi pour le ManyToMany
        this.tauxHoraire = 90;
        this.prix = ((float) this.duree / 60.0f) * this.tauxHoraire;
    }
    public TypeIntervention(String nom, int duree, List<Pieces> piecesUtilisees) {
        this.nom = nom;
        this.duree = duree; // 120 minutes
        this.interventions = new ArrayList<>(); // Initialisation de la collection vide
        this.piecesUtilisees = new ArrayList<>(); // Très important aussi pour le ManyToMany
        this.tauxHoraire = 90;
        this.prix = 0;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<Pieces> getPiecesUtilisees() {
        return piecesUtilisees;
    }

    public void setPiecesUtilisees(List<Pieces> piecesUtilisees) {
        this.piecesUtilisees = piecesUtilisees;
    }

    public Collection<Intervention> getInterventions() {
        return interventions;
    }

    public String getNom() {
        return nom;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) { this.duree = duree; }

    public long getId() {
        return id;
    }

    public float getTauxHoraire() {
        return tauxHoraire;
    }

    public void setTauxHoraire(float tauxHoraire) {
        this.tauxHoraire = tauxHoraire;
    }

    /**
     * Calcule le prix catalogue (Forfait) en fonction de la durée,
     * du taux horaire (qui change selon si c'est Entretien ou Réparation)
     * et des pièces configurées par défaut.
     */
    public void calculerPrixForfait() {
        // 1. Coût Main d'Oeuvre : (Durée en minutes / 60) * Taux Horaire
        float coutMainDoeuvre = (this.duree / 60.0f) * this.tauxHoraire;

        // 2. Coût des Pièces
        float coutPieces = 0;
        if (this.piecesUtilisees != null) {
            for (Pieces p : this.piecesUtilisees) {
                coutPieces += p.getPrix();
            }
        }

        // 3. Mise à jour de l'attribut stocké
        this.prix = coutMainDoeuvre + coutPieces;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TypeIntervention that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TypeIntervention{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", idInterventions=" + interventions +
                ", refPiecesUtilisees=" + piecesUtilisees +
                '}';
    }
}
