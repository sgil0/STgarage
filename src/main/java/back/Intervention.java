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
    @JoinColumn(name = "immat_vehicule")
    private Vehicule vehicule;

    // TypeIntervention Many to one
    @ManyToOne
    @JoinColumn(name = "id_type_intervention")
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

    /**
     * Constructeur STANDARD (Cas où l'utilisateur ne modifie pas le schéma)
     */
    public Intervention(Vehicule vehicule, TypeIntervention typeIntervention, float kilometrage) {
        this.date = LocalDate.now();
        this.kilometrage = kilometrage;
        this.vehicule = vehicule;
        this.typeIntervention = typeIntervention;
        this.statut = StatutIntervention.PLANIFIEE;

        // SÉCURITÉ : On crée une NOUVELLE liste copiée depuis le modèle
        this.piecesReelles = new ArrayList<>(typeIntervention.getPiecesUtilisees());

        // Calcul du prix initial
        calculerEtMettreAJourPrix();

        // MAJ kilométrage vehicule
        this.vehicule.setKilometrage(this.kilometrage);
    }

    public Intervention() {
        this.piecesReelles = new ArrayList<>();
    }

    /**
     * Constructeur SUR MESURE (Cas où l'utilisateur a modifié le schéma 2D)
     */
    public Intervention(Vehicule vehicule, TypeIntervention type, float kilometrage, List<Pieces> piecesDuSchema) {
        this.date = LocalDate.now();
        this.kilometrage = kilometrage;
        this.vehicule = vehicule;
        this.typeIntervention = type;
        this.statut = StatutIntervention.PLANIFIEE;

        // On prend la liste spécifique envoyée par le formulaire
        this.piecesReelles = piecesDuSchema; // Ici c'est ok car le DAO nous envoie déjà une nouvelle liste

        // Calcul du prix basé sur CES pièces là
        calculerEtMettreAJourPrix();

        // MAJ kilométrage vehicule
        this.vehicule.setKilometrage(this.kilometrage);
    }

    /**
     * Méthode centrale pour calculer le prix.
     * À appeler dès que la liste des pièces change ou à la création.
     */
    public void calculerEtMettreAJourPrix() {
        if (this.typeIntervention == null) return;

        // 1. Coût Main d'Oeuvre (Durée en minutes / 60 * Taux)
        // Assure-toi que getDuree() dans TypeIntervention retourne bien des minutes (int)
        float coutMO = ((float) this.typeIntervention.getDuree() / 60.0f) * this.typeIntervention.getTauxHoraire();

        // 2. Coût des pièces RÉELLES
        float coutPieces = 0;
        if (this.piecesReelles != null) {
            for (Pieces p : this.piecesReelles) {
                coutPieces += p.getPrix();
            }
        }
        this.prix = coutMO + coutPieces;
    }



    // Getter et Setter
    public void setPiecesReelles(List<Pieces> piecesReelles) {
        this.piecesReelles = piecesReelles;
        // Si on change les pièces manuellement via le setter, on pense à recalculer le prix !
        calculerEtMettreAJourPrix();
    }

    public List<Pieces> getPiecesReelles() { return piecesReelles; }
    public Float getPrix() { return this.prix; }
    public void setPrix(float prix) { this.prix = prix; } // Utile si on veut faire une remise manuelle
    public void setDate(LocalDate date) { this.date = date; }
    public void setVehicule(Vehicule vehicule) { this.vehicule = vehicule; }
    public void setTypeIntervention(TypeIntervention typeIntervention) { this.typeIntervention = typeIntervention; }
    public void setStatut(StatutIntervention s) { this.statut = s; }

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

