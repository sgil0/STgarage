package back;

import back.EnumType.Energie;
import back.EnumType.ZoneIntervention;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

public class GestionGarage {

    // =========================================================================
    // Attributs
    // =========================================================================

    private final EntityManagerFactory emf;
    private final EntityManager em;

    // =========================================================================
    // Méthodes
    // =========================================================================

    // =========================================================================
    // Chargement de la configuration JPA et ouverture de la connexion à la BDD via l'EntityManager.
    // =========================================================================
    public GestionGarage() {
        this.emf = Persistence.createEntityManagerFactory("STgaragePU");
        this.em = emf.createEntityManager();
    }

    // =========================================================================
    // Fermeture de l'EntityManager et la Factory
    // =========================================================================
    public void fermer() {
        if (em.isOpen()) em.close();
        if (emf.isOpen()) emf.close();
    }

    // =========================================================================
    // Intéractions BDD
    // =========================================================================

    // =========================================================================
    // Création d'une pièce
    // =========================================================================
    public void creerPiece(String ref, String nom, float prix, ZoneIntervention zone) {
        if (em.find(Pieces.class, ref) != null) return;
        em.getTransaction().begin();
        em.persist(new Pieces(ref, nom, prix, zone));
        em.getTransaction().commit();
    }
    // =========================================================================
    // Récupérer une pièce avec sa ref
    // =========================================================================
    public Pieces getPiece(String ref) {
        return em.find(Pieces.class, ref);
    }

    // =========================================================================
    // Création d'un TypeIntervention
    // =========================================================================
    public void creerTypeIntervention(TypeIntervention type) {
        em.getTransaction().begin();
        em.persist(type);
        em.getTransaction().commit();
    }

    // =========================================================================
    // Création d'un véhicule
    // =========================================================================
    public void creerVehicule(Vehicule vehicule) {
        em.getTransaction().begin();
        em.persist(vehicule);
        em.getTransaction().commit();
    }

    // =========================================================================
    // Créer un client et l'associer à un véhicule
    // =========================================================================
    public void creerClientEtVehicule(Client c, Vehicule v) {
        em.getTransaction().begin();
        em.persist(c);
        v.setProprietaire(c);
        em.persist(v);
        em.getTransaction().commit();
    }

    // =========================================================================
    // Création d'un nouveau Type de véhicule
    // =========================================================================
    public void creerTypeVehicule(TypeVehicule t) {
        try {
            em.getTransaction().begin();
            em.persist(t);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    // =========================================================================
    // Créer un client
    // =========================================================================
    public void creerClient(Client c) {
        try {
            em.getTransaction().begin();
            em.persist(c);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        }
    }
    // =========================================================================
    // Recherche des clients par mot-clé (Nom, Prénom ou Email).
    // =========================================================================
    public List<Client> rechercherClients(String recherche) {
        try {
            if (recherche == null || recherche.trim().isEmpty()) {
                return em.createQuery("SELECT c FROM Client c", Client.class).getResultList();
            }
            return em.createQuery("SELECT c FROM Client c WHERE LOWER(c.nom) LIKE :r OR LOWER(c.prenom) LIKE :r OR LOWER(c.mail) LIKE :r", Client.class)
                    .setParameter("r", "%" + recherche.toLowerCase() + "%")
                    .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // =========================================================================
    // Associe un client existant à un véhicule identifié par son immatriculation.
    // =========================================================================
    public void associerClientAVehicule(String immat, Client c) {
        try {
            em.getTransaction().begin();
            Vehicule v = em.find(Vehicule.class, immat);
            if (v != null) {
                v.setProprietaire(c);
                em.merge(v);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    // =========================================================================
    // Récupérer les TYPES d'intervention possibles pour une zone donnée et un véhicule donné.
    // =========================================================================
    public List<TypeIntervention> getTypesParZoneEtEnergie(ZoneIntervention zone, Energie energieVehicule) {
        // Récupération de ce qui se trouve dans la zone
        List<TypeIntervention> tousDansZone = em.createQuery(
                        "SELECT t FROM TypeIntervention t WHERE t.zone = :zone", TypeIntervention.class)
                .setParameter("zone", zone)
                .getResultList();

        // Filtrage selon l'énergie du véhicule
        List<TypeIntervention> result = new ArrayList<>();
        for (TypeIntervention t : tousDansZone) {
            if (t.estCompatible(energieVehicule)) {
                result.add(t);
            }
        }
        return result;
    }

    // =========================================================================
    // Création d'une intervention
    // =========================================================================
    public Intervention creerIntervention(String immat, List<TypeIntervention> typesChoisis, float kmActuel) {
        em.getTransaction().begin();
        try {
            Vehicule v = em.find(Vehicule.class, immat);
            if (v == null) throw new IllegalArgumentException("Véhicule introuvable : " + immat);

            // Récupération de l'énergie du véhicule, null si TypeVehicule inconnu
            Energie energie = (v.getTypeVehicule() != null) ? v.getTypeVehicule().getEnergie() : null;

            // Vérification compatibilitée (vidange incompatible avec electrique)
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

            System.out.println("Intervention créée pour " + immat + " | Total : " + interv.getPrix() + "€");

            return interv;

        } catch (Exception e) {
            // Annulation transaction si erreur
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            // On propage l'erreur pour l'afficher dans le JOptionPane du Front
            throw new RuntimeException(e.getMessage());
        }
    }

    // =========================================================================
    // Analyse l'état des entretiens pour un véhicule donné
    // Filtre les entretiens incompatibles (ex: Vidange sur Électrique).
    // =========================================================================
    public List<String> analyserUrgences(String immat) {
        List<String> rapport = new ArrayList<>();

        Vehicule v = em.find(Vehicule.class, immat);
        if (v == null) return rapport;

        Energie energieVehicule = null;
        if (v.getTypeVehicule() != null) {
            energieVehicule = v.getTypeVehicule().getEnergie();
        }

        // Récupérer les entretiens
        List<Entretien> typesEntretien = em.createQuery("SELECT e FROM Entretien e", Entretien.class).getResultList();

        //
        for (Entretien type : typesEntretien) {
            // si incompatible alors on ignore (ex: Vidange sur Renault Zoe)
            if (energieVehicule != null && !type.estCompatible(energieVehicule)) {
                continue;
            }


            // Interrogation de la BDD pour avoir la dernière intervention de ce type
            List<Intervention> historique = em.createQuery(
                            "SELECT i FROM Intervention i JOIN i.typesIntervention t " +
                                    "WHERE i.vehicule = :v AND t = :type " +
                                    "ORDER BY i.kilometrage DESC", Intervention.class)
                    .setParameter("v", v)
                    .setParameter("type", type)
                    .setMaxResults(1)
                    .getResultList();

            // Calcul des kilomètres restants
            float dernierKmFait = 0;
            if (!historique.isEmpty()) {
                dernierKmFait = historique.get(0).getKilometrage();
            }

            float prochainKm = dernierKmFait + type.getKilometrageMax();
            float kmRestants = prochainKm - v.getKilometrage();

            StringBuilder sb = new StringBuilder();
            sb.append(type.getNom());
            sb.append(" : ");

            if (kmRestants <= 0) {
                sb.append("URGENT (Depasse de ");
                sb.append(Math.abs(kmRestants));
                sb.append(" km)");
            } else {
                sb.append("Dans ");
                sb.append(kmRestants);
                sb.append(" km");
            }

            rapport.add(sb.toString());
        }

        return rapport;
    }

    // =========================================================================
    // Recherches Véhicule par immatriculation
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

    // =========================================================================
    // Recherches TypeVehicule par marque et nom
    // =========================================================================
    public TypeVehicule trouverTypeVehicule(String marque, String modele) {
        List<TypeVehicule> res = em.createQuery(
                        "SELECT t FROM TypeVehicule t WHERE LOWER(t.marque) = :marque AND LOWER(t.modele) = :modele",
                        TypeVehicule.class)
                .setParameter("marque", marque.toLowerCase())
                .setParameter("modele", modele.toLowerCase())
                .getResultList();

        if(res.size() > 0) {
            return res.get(0);
        } else {
            return null;
        }
    }

    // =========================================================================
    // Lister les clients
    // =========================================================================
    public List<Client> getTousLesClients() {
        return em.createQuery("SELECT c FROM Client c ORDER BY c.nom ASC", Client.class)
                .getResultList();
    }

    // =========================================================================
    // Lister les interventions concernant un véhicule
    // =========================================================================
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