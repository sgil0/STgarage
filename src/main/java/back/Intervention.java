package back;

import back.EnumType.StatutIntervention;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Intervention {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private LocalDate date;
    private float kilometrage;
    private float prixTotal; // Somme des prix
    private int dureeTotale; // Somme des durées

    @Enumerated(EnumType.STRING)
    private StatutIntervention statut;

    @ManyToOne
    @JoinColumn(name = "immat_vehicule")
    private Vehicule vehicule;


    @ManyToMany
    @JoinTable(
            name = "intervention_composition",
            joinColumns = @JoinColumn(name = "id_intervention"),
            inverseJoinColumns = @JoinColumn(name = "id_type_intervention")
    )
    private List<TypeIntervention> typesIntervention;

    public Intervention() {
        this.typesIntervention = new ArrayList<>();
    }

    /**
     * Nouveau Constructeur : Une intervention est composée d'une LISTE de types.
     */
    public Intervention(Vehicule vehicule, float kilometrage, List<TypeIntervention> types) {
        this.date = LocalDate.now();
        this.kilometrage = kilometrage;
        this.vehicule = vehicule;
        this.typesIntervention = types;
        this.statut = StatutIntervention.PLANIFIEE;

        calculerTotaux(types);

        // MAJ kilométrage vehicule
        if(this.vehicule != null) {
            this.vehicule.setKilometrage(this.kilometrage);
        }
    }

    /**
     * Calcule le prix total et la durée totale en additionnant les sous-interventions.
     */
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

    // Getters et Setters
    public float getPrix() { return this.prixTotal; }
    public List<TypeIntervention> getTypesIntervention() { return typesIntervention; }
    public Vehicule getVehicule() { return vehicule; }
    public float getKilometrage() { return kilometrage; }

    public int getDureeTotale() {
        return dureeTotale;
    }

    public LocalDate getDate() {
        return date;
    }
}