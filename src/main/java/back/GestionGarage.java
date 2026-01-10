package back;

import back.EnumType.ZoneIntervention;
import jakarta.persistence.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class GestionGarage {

    private EntityManagerFactory emf;
    private EntityManager em;

    public GestionGarage() {
        // "GaragePU" doit correspondre au nom dans ton persistence.xml
        this.emf = Persistence.createEntityManagerFactory("GaragePU");
        this.em = emf.createEntityManager();
    }

    /**
     * Ferme la connexion proprement à la fin de l'application
     */
    public void fermer() {
        if (em.isOpen()) em.close();
        if (emf.isOpen()) emf.close();
    }

    // ============================================================
    // GESTION CATALOGUE & PIÈCES (Pour l'interface graphique)
    // ============================================================

    /**
     * Récupère toutes les pièces disponibles pour une zone du schéma 2D.
     * Ex: Si on clique sur "Train Avant", on ne veut que les plaquettes, cardans, etc.
     */
    public List<Pieces> getPiecesParZone(ZoneIntervention zone) {
        TypedQuery<Pieces> query = em.createQuery(
                "SELECT p FROM Pieces p WHERE p.zone = :zoneDemandee",
                Pieces.class
        );
        query.setParameter("zoneDemandee", zone);
        return query.getResultList();
    }

    /**
     * Récupère la liste des types d'intervention (le "Menu" du restaurant)
     * Pour remplir ta JComboBox "Type d'intervention"
     */
    public List<TypeIntervention> getTousLesTypesIntervention() {
        return em.createQuery("SELECT t FROM TypeIntervention t", TypeIntervention.class)
                .getResultList();
    }

    // ============================================================
    // GESTION CLIENTS & VÉHICULES
    // ============================================================

    public void creerClientEtVehicule(Client client, Vehicule vehicule) {
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();

            // On lie les objets Java entre eux
            vehicule.setProprietaire(client); // Setter à ajouter dans Vehicule si absent

            // On sauvegarde
            em.persist(client);
            em.persist(vehicule);

            trans.commit();
        } catch (Exception e) {
            trans.rollback();
            e.printStackTrace();
        }
    }

    public Vehicule trouverVehicule(String immatriculation) {
        return em.find(Vehicule.class, immatriculation);
    }

    // ============================================================
    // CŒUR DU SUJET : CRÉATION D'INTERVENTION
    // ============================================================

    /**
     * Crée une intervention validée par le formulaire.
     * * @param immat L'immatriculation du véhicule
     * @param nomTypeIntervention Le nom du type choisi (ex: "Vidange")
     * @param piecesChoisies La liste finale des pièces (celles du template + ajouts manuels)
     * @param nouveauKm Le kilométrage actuel à mettre à jour
     * @param dureeReelle La durée estimée ou réelle de l'intervention
     */
    public void creerIntervention(String immat, String nomTypeIntervention, List<Pieces> piecesChoisies, float nouveauKm, float dureeReelle) {
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();

            // 1. Récupération du véhicule
            Vehicule v = em.find(Vehicule.class, immat);
            if (v == null) throw new IllegalArgumentException("Véhicule inconnu : " + immat);

            // 2. Mise à jour du kilométrage (Sera sauvegardé au commit)
            if (nouveauKm < v.getKilometrage()) {
                System.out.println("Attention : Incohérence kilométrique (Nouveau < Ancien)");
            }
            v.setKilometrage(nouveauKm);

            // 3. Récupération du Type d'intervention (Le Template)
            // On suppose que le nom est unique dans la base
            TypedQuery<TypeIntervention> q = em.createQuery(
                    "SELECT t FROM TypeIntervention t WHERE t.nom = :nom",
                    TypeIntervention.class
            );
            q.setParameter("nom", nomTypeIntervention);
            TypeIntervention type = q.getSingleResult();

            // 4. Création de l'Intervention (Le Dossier)
            Intervention intervention = new Intervention();
            intervention.setDate(Date.valueOf(LocalDate.now()).toLocalDate()); // Date du jour
            intervention.setVehicule(v);
            intervention.setTypeIntervention(type);
            intervention.setPiecesReelles(piecesChoisies); // La liste spécifique du schéma 2D

            // Calcul du prix total
            float coutPieces = 0;
            for (Pieces p : piecesChoisies) {
                coutPieces += p.getPrix();
            }
            // Supposons que TypeIntervention a un attribut tauxHoraire, sinon prix fixe
            // float coutMainDoeuvre = type.getTauxHoraire() * dureeReelle;
            float prixTotal = coutPieces; // + coutMainDoeuvre;

            intervention.setPrix(prixTotal);

            // 5. Sauvegarde
            em.persist(intervention);

            trans.commit();
            System.out.println("Intervention créée avec succès pour le véhicule " + immat);

        } catch (Exception e) {
            if (trans.isActive()) trans.rollback();
            e.printStackTrace();
        }
    }

    /**
     * Pour afficher l'historique dans l'interface
     */
    public List<Intervention> getHistoriqueVehicule(String immat) {
        TypedQuery<Intervention> q = em.createQuery(
                "SELECT i FROM Intervention i WHERE i.vehicule.immatriculation = :immat ORDER BY i.date DESC",
                Intervention.class
        );
        q.setParameter("immat", immat);
        return q.getResultList();
    }
}