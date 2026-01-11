package back;

import jakarta.persistence.*;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
public class Vehicule{
    public static final String FORMAT_SIV = "^[A-Z]{2}-\\d{3}-[A-Z]{2}$";

    @Id
    @Column(length = 9, nullable = false)
    @NotNull
    // Regex stricte pour le format SIV (ex: AA-123-BB)
    @Pattern(regexp = "^[A-Z]{2}-\\d{3}-[A-Z]{2}$", message = "Format invalide. Attendu : AA-123-BB")
    private String immatriculation;

    private LocalDate miseCirc;
    private float kilometrage;

    @ManyToOne
    @JoinColumn(name = "idProprietaire")
    private Client proprietaire;

    @OneToMany(mappedBy = "vehicule")
    private Collection<Intervention> listeInterventions;

    @ManyToOne
    @JoinColumn(name = "idTypeVehicule")
    private TypeVehicule typeVehicule;

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
        this.miseCirc = miseCirc;
        this.immatriculation = immatClean;
        this.typeVehicule = typeVehicule;
        this.listeInterventions = new ArrayList<>();
        this.proprietaire = proprietaire;
    }

    public Vehicule() {
        this.immatriculation = "AA-000-AA";
        this.miseCirc = LocalDate.of(2000, Month.JANUARY, 1); // par défaut 01/01/2000
        this.kilometrage = 1000;
        this.proprietaire = null;
        this.listeInterventions = new ArrayList<>();
        this.typeVehicule = null;
    }

    public Vehicule(String s, LocalDate date, int i, Client client) {
        this.immatriculation = s;
        this.miseCirc = date;
        this.kilometrage = (float) i;
        this.proprietaire = client;
        this.listeInterventions = new ArrayList<>();
        this.typeVehicule = null;
    }

    public void setKilometrage(float nouveauKilometrage) throws IllegalArgumentException {
        if (nouveauKilometrage < this.kilometrage) {
            throw new IllegalArgumentException("Erreur E.2 : Le nouveau kilométrage ne peut pas être inférieur à l'ancien.");
        }
        this.kilometrage = nouveauKilometrage;
    }

    public float getKilometrage() {
        return kilometrage;
    }

    public void setProprietaire(Client client) {
        this.proprietaire = client;
    }

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
                ", prop=" + proprietaire +
                ", listeInterventions=" + listeInterventions +
                ", typeVehicule=" + typeVehicule +
                '}';
    }
}
