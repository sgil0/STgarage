import back.*;
import back.EnumType.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- DÉMARRAGE DU GARAGE ---");
        GestionGarage garage = new GestionGarage();

        // 1. CRÉATION DU CATALOGUE (Pièces & Types)
        System.out.println("\n--- Initialisation Catalogue ---");

        // On crée quelques pièces
        garage.creerPiece("HUILE-5W30", "Bidon Huile 5L", 50.0f, ZoneIntervention.BLOC_MOTEUR);
        garage.creerPiece("FILTRE-H", "Filtre à Huile", 15.0f, ZoneIntervention.BLOC_MOTEUR);
        garage.creerPiece("PLAQUE-AV", "Plaquettes Avant", 80.0f, ZoneIntervention.TRAIN_AVANT);
        garage.creerPiece("DISQUE-AV", "Disques Avant", 120.0f, ZoneIntervention.TRAIN_AVANT);

        // On crée un Type d'intervention : "Vidange Standard"
        // Supposons que Entretien est la classe fille
        Entretien vidange = new Entretien();
        vidange.setNom("Vidange Standard");
        vidange.setTauxHoraire(60.0f); // 60€ de l'heure
        vidange.setDuree(60);          // 1 heure

        // On lui associe les pièces par défaut (Huile + Filtre)
        List<Pieces> kitVidange = new ArrayList<>();
        kitVidange.add(garage.getPiece("HUILE-5W30"));
        kitVidange.add(garage.getPiece("FILTRE-H"));
        vidange.setPiecesUtilisees(kitVidange);

        garage.creerTypeIntervention(vidange);


        // 2. CRÉATION CLIENT & VÉHICULE
        System.out.println("\n--- Création Client ---");
        Client c = new Client("Dupont", "Jean", "jean@mail.com", "1 rue de la Paix");
        // Adapte selon ton constructeur Vehicule
        // TypeVehicule doit exister, ici je simplifie, assure-toi d'en avoir un ou null si autorisé
        Vehicule v = new Vehicule("AB-123-CD", LocalDate.of(2020, 1, 1), 50000, null);

        garage.creerClientEtVehicule(c, v);


        // 3. SCÉNARIO : CRÉATION D'UNE INTERVENTION
        System.out.println("\n--- Test 1 : Vidange Standard (Sans modif) ---");
        // Le schéma envoie les pièces par défaut
        garage.creerIntervention("AB-123-CD", "Vidange Standard", kitVidange, 50500);
        // Prix attendu : 60€ (MO) + 50€ (Huile) + 15€ (Filtre) = 125€


        System.out.println("\n--- Test 2 : Vidange + Plaquettes (Sur Mesure) ---");
        // L'utilisateur clique sur "Plaquettes" en plus sur le schéma
        List<Pieces> listeModifiee = new ArrayList<>(kitVidange);
        listeModifiee.add(garage.getPiece("PLAQUE-AV")); // +80€

        garage.creerIntervention("AB-123-CD", "Vidange Standard", listeModifiee, 60000);
        // Prix attendu : 125€ (Base) + 80€ (Plaquettes) = 205€

        garage.fermer();
        System.out.println("\n--- FIN DU TEST ---");
    }
}