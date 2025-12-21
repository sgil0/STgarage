package back;

import jakarta.persistence.*;

import java.sql.Date;
import java.util.Collection;
import java.util.Objects;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class Vehicule{
    public static final String FORMAT_SIV = "^[A-Z]{2}-\\d{3}-[A-Z]{2}$";

    @Id
    @Column(length = 9, nullable = false)
    @NotNull
    // Regex stricte pour le format SIV (ex: AA-123-BB)
    @Pattern(regexp = "^[A-Z]{2}-\\d{3}-[A-Z]{2}$", message = "Format invalide. Attendu : AA-123-BB")
    private String immatriculation;

    private Date miseCirc;
    private float kilometrage;

    @ManyToOne
    @JoinColumn(name = "idProprietaire")
    private Client idProp;

    @OneToMany(mappedBy = "immatVehicule")
    private Collection<Intervention> idInterventions;

    @ManyToOne
    @JoinColumn(name = "idTypeVehicule")
    private TypeVehicule idTypeVehicule;

    public Vehicule(String immatriculation, Date miseCirc, float kilometrage, TypeVehicule idTypeVehicule) {
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

    }

    public Vehicule() {

    }
}
