package back;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
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
            name = "refInterventionPieces", // Nom de la table intermédiaire
            joinColumns = @JoinColumn(name = "idIntervention"),
            inverseJoinColumns = @JoinColumn(name = "refPiece")
    )
    private List<Pieces> piecesUtilisees;

    public TypeIntervention() {
        this.nom = "default";
        this.duree = 120; // 120 minutes
        this.interventions = new ArrayList<>(); // Initialisation de la collection vide
        this.piecesUtilisees = new ArrayList<>(); // Très important aussi pour le ManyToMany
        this.tauxHoraire = 90;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<Pieces> getPiecesUtilisees() {
        return piecesUtilisees;
    }

    public void setPiecesUtilisees(List<Pieces> piecesUtilisees) { this.piecesUtilisees = piecesUtilisees;}

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

    // Calcule le prix théorique (Le Devis) C'est ce prix qu'on affiche quand on sélectionne "Vidange" avant de modifier les pièces.

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
