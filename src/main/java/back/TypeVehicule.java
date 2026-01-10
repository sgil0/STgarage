package back;

import back.EnumType.BoiteVitesse;
import back.EnumType.Energie;
import jakarta.persistence.*;

import java.util.Collection;
import java.util.List;
import java.util.Objects;


@Entity
public class TypeVehicule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String marque;
    private String modele;
    @Enumerated(EnumType.STRING)
    private Energie energie;
    @Enumerated(EnumType.STRING)
    private BoiteVitesse boiteVitesse;
    private int nbPlaces;
    private int nbPortes;
    private int puissance;

    // Vehicule one to many
    @OneToMany(mappedBy = "typeVehicule")
    private Collection<Vehicule> vehicules;

    // Pièces many to many
    @ManyToMany
    @JoinTable(
            name = "lienTypeVehiculeEtPieces", // Nom de la table intermédiaire
            joinColumns = @JoinColumn(name = "immatTypeVehicule"),
            inverseJoinColumns = @JoinColumn(name = "refPiece")
    )
    private List<Pieces> pieces;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TypeVehicule that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TypeVehicule{" +
                "id=" + id +
                ", marque='" + marque + '\'' +
                ", modele='" + modele + '\'' +
                ", energie=" + energie +
                ", boiteVitesse=" + boiteVitesse +
                ", nbPlaces=" + nbPlaces +
                ", nbPortes=" + nbPortes +
                ", puissance=" + puissance +
                ", immatsVehicules=" + vehicules +
                ", pieces=" + pieces +
                '}';
    }
}



