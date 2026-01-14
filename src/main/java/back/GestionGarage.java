package back;

import back.EnumType.*;
import jakarta.persistence.*;
import java.util.List;

public class GestionGarage {

    private EntityManagerFactory emf;
    private EntityManager em;

    public GestionGarage() {
        // "GaragePU" doit correspondre au nom dans ton persistence.xml
        this.emf = Persistence.createEntityManagerFactory("STgaragePU");
        this.em = emf.createEntityManager();
    }

    public void fermer() {
        em.close();
        emf.close();
    }

    // --- 1. OUTILS DE TEST (Pour remplir la base) ---
    public List<Pieces> getPiecesParZone(ZoneIntervention zone) {
        return em.createQuery("SELECT p FROM Pieces p WHERE p.zone = :zone", Pieces.class)
                .setParameter("zone", zone)
                .getResultList();
    }

    public void creerPiece(String ref, String nom, float prix, ZoneIntervention zone) {
        if (em.find(Pieces.class, ref) != null) return; // Déjà existante

        em.getTransaction().begin();
        Pieces p = new Pieces(ref, nom, prix, zone); // Assure-toi d'avoir ce constructeur dans Pieces
        em.persist(p);
        em.getTransaction().commit();
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
        v.setProprietaire(c); // On lie les objets
        em.persist(v);
        em.getTransaction().commit();
    }

    // --- 2. CŒUR DU SUJET : Créer une Intervention ---

    public Intervention creerIntervention(String immat, String nomType, List<Pieces> piecesDuSchema, float kmActuel) {
        em.getTransaction().begin();
        try {
            // A. Récupérations
            Vehicule v = em.find(Vehicule.class, immat);

            TypedQuery<TypeIntervention> q = em.createQuery("SELECT t FROM TypeIntervention t WHERE t.nom = :nom", TypeIntervention.class);
            q.setParameter("nom", nomType);
            TypeIntervention type = q.getSingleResult();

            // B. Création (Appel du constructeur "Sur Mesure" que nous avons fait)
            Intervention interv = new Intervention(v, type, kmActuel, piecesDuSchema);

            // C. Sauvegarde
            em.persist(interv);
            em.getTransaction().commit();

            System.out.println("✅ Intervention créée : " + nomType + " pour " + immat);
            System.out.println("💰 Prix Facturé : " + interv.getPrix() + "€");

            return interv;

        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            return null;
        }
    }

    // Recherche partielle (LIKE)
    public List<Vehicule> rechercherVehicules(String recherche) {
        // Si la recherche est vide, on renvoie tout (ou rien, selon votre choix).
        // Ici on renvoie tout pour réinitialiser le tableau.
        if (recherche == null || recherche.isEmpty()) {
            return em.createQuery("SELECT v FROM Vehicule v", Vehicule.class).getResultList();
        }

        TypedQuery<Vehicule> q = em.createQuery(
                "SELECT v FROM Vehicule v WHERE v.immatriculation LIKE :recherche",
                Vehicule.class
        );
        // Les % permettent de chercher "n'importe où" dans la chaîne (contient)
        q.setParameter("recherche", "%" + recherche + "%");

        return q.getResultList();
    }

    // Pour vérifier dans le test
    public Pieces getPiece(String ref) {
        return em.find(Pieces.class, ref);
    }

    public void creerTypeVehicule(TypeVehicule type) {
        // Vérifie si un type similaire existe déjà (optionnel mais conseillé)
        // Ici on fait simple pour le TP : on persiste directement
        em.getTransaction().begin();
        em.persist(type);
        em.getTransaction().commit();
    }

    public List<String> getNomsTypesIntervention() {
        return em.createQuery("SELECT t.nom FROM TypeIntervention t", String.class).getResultList();
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
            return null; // Pas trouvé
        }
    }

    public List<Client> getTousLesClients() {
        // Récupère tous les clients triés par nom
        return em.createQuery("SELECT c FROM Client c ORDER BY c.nom ASC", Client.class)
                .getResultList();
    }

    public void creerClient(Client c) {
        em.getTransaction().begin();
        em.persist(c);
        em.getTransaction().commit();
    }
}