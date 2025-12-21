package back;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
public abstract class TypeIntervention {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nom;

    // Intervention one to many
    @OneToMany(mappedBy = "typeIntervention")
    private Collection<Intervention> idInterventions;

    // Pieces many to many
    @ManyToMany
    @JoinTable(
            name = "refInterventionPieces", // Nom de la table intermédiaire
            joinColumns = @JoinColumn(name = "idIntervention"),
            inverseJoinColumns = @JoinColumn(name = "refPiece")
    )
    private List<Pieces> refPiecesUtilisees;

    public TypeIntervention() {
        this.nom = "default";
        this.idInterventions = new ArrayList<>(); // Initialisation de la collection vide
        this.refPiecesUtilisees = new ArrayList<>(); // Très important aussi pour le ManyToMany
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<Pieces> getRefPiecesUtilisees() {
        return refPiecesUtilisees;
    }

    public Collection<Intervention> getIdInterventions() {
        return idInterventions;
    }

    public String getNom() {
        return nom;
    }

    public long getId() {
        return id;
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
                ", idInterventions=" + idInterventions +
                ", refPiecesUtilisees=" + refPiecesUtilisees +
                '}';
    }
}
