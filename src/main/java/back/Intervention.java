package back;

import back.EnumType.StatutIntervention;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
public class Intervention {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private LocalDate date;
    private float kilometrage;
    private float prix;
    @Enumerated(EnumType.STRING)
    private StatutIntervention statut;

    // Vehicule many to one
    @ManyToOne
    @JoinColumn(name = "immatVehicule")
    private Vehicule vehicule;

    // TypeIntervention Many to one
    @ManyToOne
    @JoinColumn(name = "idTypeIntervention")
    private TypeIntervention typeIntervention;

    // Cette liste va stocker les pièces cochées sur le schéma 2D.
    // Au début, on peut la pré-remplir avec celles du TypeIntervention,
    // mais ensuite on est libre d'ajouter/supprimer ce qu'on veut.
    @ManyToMany
    @JoinTable(
            name = "intervention_pieces_reelles", // Table de jointure spécifique
            joinColumns = @JoinColumn(name = "id_intervention"),
            inverseJoinColumns = @JoinColumn(name = "ref_piece")
    )
    private List<Pieces> piecesReelles;

    public Intervention(Vehicule vehicule, TypeIntervention typeIntervention, float kilometrage) {
        this.date = LocalDate.now();
        this.kilometrage = kilometrage; // La vérif du kilometrage > kilometrage voiture se fait dans front avant l'envoie du formulaire
        this.prix = (((float) typeIntervention.getDuree() / 60) * typeIntervention.getTauxHoraire()) + calculerCoutPieces(typeIntervention.getPiecesUtilisees());
        this.vehicule = vehicule;
        this.typeIntervention = typeIntervention;
        this.statut = StatutIntervention.PLANIFIEE; // Par défaut
        this.piecesReelles = typeIntervention.getPiecesUtilisees();
    }

    public Intervention() {
        this.piecesReelles = new ArrayList<>();
    }

    // Constructeur pour le cas "Sur Mesure"
    public Intervention(LocalDate date, float duree, Vehicule vehicule, TypeIntervention type, List<Pieces> piecesDuSchema) {
        this.date = date;
        this.vehicule = vehicule;
        this.typeIntervention = type;

        // On enregistre ce que l'utilisateur a cliqué sur le schéma
        this.piecesReelles = piecesDuSchema;
        this.prix = (((float) type.getDuree() / 60) * type.getTauxHoraire()) + calculerCoutPieces(type.getPiecesUtilisees());

    }

    // Constructeur pour le cas "Standard" (Cas A - sans modif du schéma)
    public Intervention(LocalDate date, float duree, Vehicule v, TypeIntervention type) {
        this(date, duree, v, type, type.getPiecesUtilisees()); // On prend les pièces par défaut
    }

    public float calculerCoutPieces(List<Pieces> pieces) {
        float coutPieces = 0;
        for (Pieces p : pieces) {
            coutPieces += p.getPrix();
        }
        return coutPieces;
    }

    // Getter et Setter
    public List<Pieces> getPiecesReelles() {
        return piecesReelles;
    }

    public void setPiecesReelles(List<Pieces> piecesReelles) {
        this.piecesReelles = piecesReelles;
    }

    public void setStatut(StatutIntervention nouveauStatut) {
        this.statut = nouveauStatut;
    }

    public Float getPrix() {
        return this.prix;
    }

    public void setPrix(float prix) {
        this.prix = prix;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;
    }

    public void setTypeIntervention(TypeIntervention typeIntervention) {
        this.typeIntervention = typeIntervention;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Intervention that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Intervention{" +
                "id=" + id +
                ", date=" + date +
                ", kilometrage=" + kilometrage +
                ", prix=" + prix +
                ", vehicule=" + vehicule +
                ", typeIntervention=" + typeIntervention +
                '}';
    }
}

