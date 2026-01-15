package back;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Entity
public class Vehicule {

    // =========================================================================
    // Attributs
    // =========================================================================

    // Format immatriculation
    public static final String FORMAT_SIV = "^[A-Z]{2}-\\d{3}-[A-Z]{2}$";
    @Id
    @Column(length = 9, nullable = false)
    @NotNull
    @Pattern(regexp = "^[A-Z]{2}-\\d{3}-[A-Z]{2}$", message = "Format invalide. Attendu : AA-123-BB")
    private String immatriculation;

    private LocalDate miseCirc;
    private float kilometrage;

    // Client many to one
    @ManyToOne
    @JoinColumn(name = "id_proprietaire")
    private Client proprietaire;

    // Intervention one to many
    @OneToMany(mappedBy = "vehicule")
    private Collection<Intervention> listeInterventions;

    // TypeVehicule many to one
    @ManyToOne
    @JoinColumn(name = "id_type_vehicule")
    private TypeVehicule typeVehicule;

    // =========================================================================
    // Constructeurs
    // =========================================================================

    public Vehicule(String immatriculation, LocalDate miseCirc, float kilometrage, TypeVehicule typeVehicule, Client proprietaire) {
        if (immatriculation == null) {
            throw new IllegalArgumentException("L'immatriculation ne peut pas être nulle.");
        }
        // Normalisation (Trim et Majuscules)
        String immatClean = immatriculation.trim().toUpperCase();
        // Validation du format
        if (!immatClean.matches(FORMAT_SIV)) {
            throw new IllegalArgumentException("Format invalide. Attendu : AA-123-BB");
        }
        this.kilometrage = kilometrage;
        this.miseCirc = miseCirc;
        this.immatriculation = immatClean;
        this.typeVehicule = typeVehicule;
        this.listeInterventions = new ArrayList<>();
        this.proprietaire = proprietaire;
    }

    public Vehicule() {
        this.immatriculation = "AA-000-AA";
        this.miseCirc = LocalDate.of(2000, Month.JANUARY, 1);
        this.kilometrage = 1000;
        this.proprietaire = null;
        this.listeInterventions = new ArrayList<>();
        this.typeVehicule = null;
    }

    // =========================================================================
    // Setters
    // =========================================================================
    public void setKilometrage(float nouveauKilometrage) throws IllegalArgumentException {
        if (nouveauKilometrage < this.kilometrage) {
            throw new IllegalArgumentException("Erreur E.2 : Le nouveau kilométrage ne peut pas être inférieur à l'ancien.");
        }
        this.kilometrage = nouveauKilometrage;
    }


    public void setProprietaire(Client client) {
        this.proprietaire = client;
    }

    // =========================================================================
    // Getters
    // =========================================================================
    public TypeVehicule getTypeVehicule() {
        return typeVehicule;
    }

    public String getImmatriculation() {
        return immatriculation;
    }

    public Client getProprietaire() {
        return proprietaire;
    }

    public float getKilometrage() {
        return kilometrage;
    }

    // =========================================================================
    // Equals, Hashcode & toString
    // =========================================================================

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Vehicule vehicule)) return false;
        return Objects.equals(immatriculation, vehicule.immatriculation);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(immatriculation);
    }

    @Override
    public String toString() {
        return "Vehicule{" +
                "immatriculation='" + immatriculation + '\'' +
                ", miseCirc=" + miseCirc +
                ", kilometrage=" + kilometrage +
                ", prop=" + proprietaire.getMail() +
                ", listeInterventions=" + listeInterventions +
                ", typeVehicule=" + typeVehicule +
                '}';
    }
}
