package back;

import back.EnumType.BoiteVitesse;
import back.EnumType.Energie;
import jakarta.persistence.*;

import java.util.Collection;
import java.util.List;


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
    @OneToMany(mappedBy = "idTypeVehicule")
    private Collection<Vehicule> immatsVehicules;

    // Pièces many to many
    @ManyToMany
    @JoinTable(
            name = "lienTypeVehiculeEtPieces", // Nom de la table intermédiaire
            joinColumns = @JoinColumn(name = "immatTypeVehicule"),
            inverseJoinColumns = @JoinColumn(name = "refPiece")
    )
    private List<Pieces> pieces;
}



