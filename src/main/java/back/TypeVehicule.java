package back;

import back.EnumType.BoiteVitesse;
import back.EnumType.Energie;
import jakarta.persistence.*;

import java.util.Collection;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "type_vehicule")
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


    // Constructeur vide (obligatoire pour JPA)
    public TypeVehicule() {}

    // Constructeur complet
    public TypeVehicule(String marque, String modele, Energie energie, BoiteVitesse boiteVitesse, int nbPlaces, int nbPortes, int puissance) {
        this.marque = marque;
        this.modele = modele;
        this.energie = energie;
        this.boiteVitesse = boiteVitesse;
        this.nbPlaces = nbPlaces;
        this.nbPortes = nbPortes;
        this.puissance = puissance;
    }

    public String getModele() {
        return modele;
    }

    public String getMarque() {
        return marque;
    }

    public Energie getEnergie() {
        return energie;
    }

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
                '}';
    }

}



