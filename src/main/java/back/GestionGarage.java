package back;

import back.EnumType.*;
import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

public class GestionGarage {

    private EntityManagerFactory emf;
    private EntityManager em;

    public GestionGarage() {
        // Assurez-vous que le nom correspond à votre persistence.xml
        this.emf = Persistence.createEntityManagerFactory("STgaragePU");
        this.em = emf.createEntityManager();
    }

    public void fermer() {
        if (em.isOpen()) em.close();
        if (emf.isOpen()) emf.close();
    }

    // =========================================================================
    // 1. GESTION DES DONNÉES DE BASE (CRUD)
    // =========================================================================

    public void creerPiece(String ref, String nom, float prix, ZoneIntervention zone) {
        if (em.find(Pieces.class, ref) != null) return;
        em.getTransaction().begin();
        em.persist(new Pieces(ref, nom, prix, zone));
        em.getTransaction().commit();
    }

    public Pieces getPiece(String ref) {
        return em.find(Pieces.class, ref);
    }

    public void creerTypeIntervention(TypeIntervention type) {
        em.getTransaction().begin();
        em.persist(type);
        em.getTransaction().commit();
    }

    public void creerVehicule(Vehicule vehicule){
        em.getTransaction().begin();
        em.persist(vehicule);
        em.getTransaction().commit();
    }

    public void creerClientEtVehicule(Client c, Vehicule v) {
        em.getTransaction().begin();
        em.persist(c);
        v.setProprietaire(c);
        em.persist(v);
        em.getTransaction().commit();
    }

    public void creerTypeVehicule(TypeVehicule type) {
        em.getTransaction().begin();
        em.persist(type);
        em.getTransaction().commit();
    }

    public void creerClient(Client c) {
        em.getTransaction().begin();
        em.persist(c);
        em.getTransaction().commit();
    }

    // =========================================================================
    // 2. LOGIQUE MÉTIER : INTERVENTIONS & COMPATIBILITÉ
    // =========================================================================

    /**
     * Récupère les TYPES d'intervention possibles pour une zone donnée et un véhicule donné.
     * Utilisé par le Schéma 2D pour afficher les checkboxes.
     */
    public List<TypeIntervention> getTypesParZoneEtEnergie(ZoneIntervention zone, Energie energieVehicule) {
        // 1. On récupère tout ce qui est dans la bonne zone
        List<TypeIntervention> tousDansZone = em.createQuery(
                        "SELECT t FROM TypeIntervention t WHERE t.zone = :zone", TypeIntervention.class)
                .setParameter("zone", zone)
                .getResultList();

        // 2. On filtre selon l'énergie du véhicule
        List<TypeIntervention> result = new ArrayList<>();
        for (TypeIntervention t : tousDansZone) {
            if (t.estCompatible(energieVehicule)) {
                result.add(t);
            }
        }
        return result;
    }

    /**
     * CRÉATION D'UNE INTERVENTION (Refactorisé)
     * Prend désormais une LISTE de types d'intervention choisis.
     */
    public Intervention creerIntervention(String immat, List<TypeIntervention> typesChoisis, float kmActuel) {
        em.getTransaction().begin();
        try {
            Vehicule v = em.find(Vehicule.class, immat);
            if (v == null) throw new IllegalArgumentException("Véhicule introuvable : " + immat);

            Energie energie = (v.getTypeVehicule() != null) ? v.getTypeVehicule().getEnergie() : null;

            if (energie != null) {
                for (TypeIntervention t : typesChoisis) {
                    if (!t.estCompatible(energie)) {
                        throw new IllegalArgumentException("Le forfait '" + t.getNom() + "' est incompatible avec un moteur " + energie);
                    }
                }
            }

            Intervention interv = new Intervention(v, kmActuel, typesChoisis);

            em.persist(interv);
            em.getTransaction().commit();

            System.out.println("✅ Intervention créée pour " + immat + " | Total : " + interv.getPrix() + "€");

            return interv;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            // On propage l'erreur pour l'afficher dans le JOptionPane du Front
            throw new RuntimeException(e.getMessage());
        }
    }

    // =========================================================================
    // 3. LOGIQUE MÉTIER : URGENCES & MAINTENANCE
    // =========================================================================

    /**
     * Analyse l'état des entretiens pour un véhicule donné.
     * Filtre les entretiens incompatibles (ex: Vidange sur Électrique).
     */
    public List<String> analyserUrgences(String immat) {
        List<String> rapport = new ArrayList<>();

        // 1. Récupérer le véhicule
        Vehicule v = em.find(Vehicule.class, immat);
        if (v == null) return rapport;

        // Récupérer son énergie
        Energie energieVehicule = null;
        if (v.getTypeVehicule() != null) {
            energieVehicule = v.getTypeVehicule().getEnergie();
        }

        // 2. Récupérer TOUS les entretiens (Entretien extends TypeIntervention)
        List<Entretien> typesEntretien = em.createQuery("SELECT e FROM Entretien e", Entretien.class).getResultList();

        // 3. Pour chaque type, vérifier
        for (Entretien type : typesEntretien) {

            // --- FILTRE ÉNERGIE ---
            if (energieVehicule != null && !type.estCompatible(energieVehicule)) {
                continue; // On ignore (ex: Vidange sur Zoe)
            }

            // On cherche la dernière intervention de ce type
            // Attention : Intervention a maintenant une liste. On doit faire une jointure.
            List<Intervention> historique = em.createQuery(
                            "SELECT i FROM Intervention i JOIN i.typesIntervention t " +
                                    "WHERE i.vehicule = :v AND t = :type " +
                                    "ORDER BY i.kilometrage DESC", Intervention.class)
                    .setParameter("v", v)
                    .setParameter("type", type)
                    .setMaxResults(1)
                    .getResultList();

            float dernierKmFait = 0;
            if (!historique.isEmpty()) {
                dernierKmFait = historique.get(0).getKilometrage();
            }

            // Calculs
            float prochainKm = dernierKmFait + type.getKilometrageMax();
            float kmRestants = prochainKm - v.getKilometrage();

            StringBuilder sb = new StringBuilder();
            sb.append(type.getNom()).append(" : ");

            if (kmRestants <= 0) {
                sb.append("URGENT (Dépassé de ").append(Math.abs(kmRestants)).append(" km)");
            } else {
                sb.append("Dans ").append(kmRestants).append(" km");
            }

            rapport.add(sb.toString());
        }

        return rapport;
    }

    // =========================================================================
    // 4. RECHERCHES & UTILITAIRES
    // =========================================================================

    public List<Vehicule> rechercherVehicules(String recherche) {
        if (recherche == null || recherche.isEmpty()) {
            return em.createQuery("SELECT v FROM Vehicule v", Vehicule.class).getResultList();
        }
        TypedQuery<Vehicule> q = em.createQuery(
                "SELECT v FROM Vehicule v WHERE v.immatriculation LIKE :recherche",
                Vehicule.class
        );
        q.setParameter("recherche", "%" + recherche + "%");
        return q.getResultList();
    }

    public TypeVehicule trouverTypeVehicule(String marque, String modele) {
        try {
            return em.createQuery(
                            "SELECT t FROM TypeVehicule t WHERE LOWER(t.marque) = :marque AND LOWER(t.modele) = :modele",
                            TypeVehicule.class)
                    .setParameter("marque", marque.toLowerCase())
                    .setParameter("modele", modele.toLowerCase())
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Client> getTousLesClients() {
        return em.createQuery("SELECT c FROM Client c ORDER BY c.nom ASC", Client.class)
                .getResultList();
    }

    public List<Intervention> getHistoriqueVehicule(String immat) {
        Vehicule v = em.find(Vehicule.class, immat);
        if (v == null) return new ArrayList<>();
        return em.createQuery(
                        "SELECT DISTINCT i FROM Intervention i " +
                                "LEFT JOIN FETCH i.typesIntervention " +
                                "WHERE i.vehicule = :v " +
                                "ORDER BY i.date DESC", Intervention.class)
                .setParameter("v", v)
                .getResultList();
    }
}