package back;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Intervention {

    // =========================================================================
    // Attributs
    // =========================================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private LocalDate date;
    private float kilometrage;
    private float prixTotal;
    private int dureeTotale;

    // Many To One Vehicule
    @ManyToOne
    @JoinColumn(name = "immat_vehicule")
    private Vehicule vehicule;

    // Many To Many TypeIntervention
    @ManyToMany
    @JoinTable(
            name = "intervention_composition",
            joinColumns = @JoinColumn(name = "id_intervention"),
            inverseJoinColumns = @JoinColumn(name = "id_type_intervention")
    )
    private List<TypeIntervention> typesIntervention;

    // =========================================================================
    // Constructeurs
    // =========================================================================

    public Intervention() {
        this.typesIntervention = new ArrayList<>();
    }

    public Intervention(Vehicule vehicule, float kilometrage, List<TypeIntervention> types) {
        this.date = LocalDate.now();
        this.kilometrage = kilometrage;
        this.vehicule = vehicule;
        this.typesIntervention = new ArrayList<>(types);
        calculerTotaux(types);

        if (this.vehicule != null) {
            this.vehicule.setKilometrage(this.kilometrage);
        }
    }

    // =========================================================================
    // Méthodes
    // =========================================================================

    // =========================================================================
    // Calculer le prix total et la durée totale en additionnant les sous-interventions
    // =========================================================================
    public void calculerTotaux(List<TypeIntervention> types) {
        this.prixTotal = 0;
        this.dureeTotale = 0;

        if (types != null) {
            for (TypeIntervention t : types) {
                this.prixTotal += t.getPrix();
                this.dureeTotale += t.getDuree();
            }
        }
    }

    // =========================================================================
    // Getters
    // =========================================================================

    public float getPrix() {
        return this.prixTotal;
    }

    public List<TypeIntervention> getTypesIntervention() {
        return typesIntervention;
    }

    public Vehicule getVehicule() {
        return vehicule;
    }

    public float getKilometrage() {
        return kilometrage;
    }

    public int getDureeTotale() {
        return dureeTotale;
    }

    public LocalDate getDate() {
        return date;
    }
}