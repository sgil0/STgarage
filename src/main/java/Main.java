import back.*;
import back.EnumType.BoiteVitesse;
import back.EnumType.Energie;
import back.EnumType.ZoneIntervention;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // =========================================================================
        // Création d'un jeu de données pour exploiter et tester l'application
        // =========================================================================

        System.out.println("--- DÉMARRAGE DU GARAGE ---");
        GestionGarage garage = new GestionGarage();

        System.out.println("\n--- 1. Initialisation des Pièces ---");
        // MOTEUR
        garage.creerPiece("HUILE-5W30", "Bidon Huile 5L", 50.0f, ZoneIntervention.BLOC_MOTEUR);
        garage.creerPiece("FILTRE-H", "Filtre à Huile", 15.0f, ZoneIntervention.BLOC_MOTEUR);
        garage.creerPiece("COURROIE-DIST", "Courroie Distribution", 280.0f, ZoneIntervention.BLOC_MOTEUR);
        garage.creerPiece("TURBO", "Turbo Garett", 780.0f, ZoneIntervention.BLOC_MOTEUR);

        // TRAIN AVANT
        garage.creerPiece("PLAQUE-AV", "Jeu Plaquettes AV", 60.0f, ZoneIntervention.TRAIN_AVANT);
        garage.creerPiece("DISQUE-AV", "Jeu Disques AV", 120.0f, ZoneIntervention.TRAIN_AVANT);
        garage.creerPiece("PNEU-AV", "Pneu Michelin 17p", 110.0f, ZoneIntervention.TRAIN_AVANT);

        // TRAIN ARRIERE
        garage.creerPiece("PLAQUE-AR", "Jeu Plaquettes AR", 45.0f, ZoneIntervention.TRAIN_ARRIERE);
        garage.creerPiece("DISQUE-AR", "Jeu Disques AR", 100.0f, ZoneIntervention.TRAIN_ARRIERE);
        garage.creerPiece("AMORTISSEUR-AR", "Amortisseur Arrière", 85.0f, ZoneIntervention.TRAIN_ARRIERE);
        garage.creerPiece("PNEU-AR", "Pneu Michelin 19p", 140.0f, ZoneIntervention.TRAIN_ARRIERE);


        System.out.println("\n--- 2. Création des Forfaits (Types Intervention) ---");

        // Entretien pour voitures électriques
        Entretien entretienBatterie = new Entretien("Diagnostic Santé Batterie (SOH)", 240, Collections.emptyList(), 30000, ZoneIntervention.BLOC_MOTEUR);
        entretienBatterie.setTauxHoraire(200);
        entretienBatterie.calculerPrixForfait();
        entretienBatterie.ajouterEnergieCompatible(Energie.ELECTRIQUE);
        entretienBatterie.ajouterEnergieCompatible(Energie.HYBRID);
        garage.creerTypeIntervention(entretienBatterie);
        // ZONE MOTEUR
        List<Pieces> kitVidange = Arrays.asList(garage.getPiece("HUILE-5W30"), garage.getPiece("FILTRE-H"));
        Entretien vidange = new Entretien("Vidange Complète", 60, kitVidange, 20000, ZoneIntervention.BLOC_MOTEUR);
        vidange.ajouterEnergieCompatible(Energie.ESSENCE);
        vidange.ajouterEnergieCompatible(Energie.DIESEL);
        vidange.ajouterEnergieCompatible(Energie.HYBRID);
        garage.creerTypeIntervention(vidange);

        List<Pieces> kitCourroie = Collections.singletonList(garage.getPiece("COURROIE-DIST"));
        Entretien courroie = new Entretien("Kit Distribution", 180, kitCourroie, 120000, ZoneIntervention.BLOC_MOTEUR);
        courroie.ajouterEnergieCompatible(Energie.ESSENCE);
        courroie.ajouterEnergieCompatible(Energie.DIESEL);
        courroie.ajouterEnergieCompatible(Energie.HYBRID);
        garage.creerTypeIntervention(courroie);

        List<Pieces> kitTurbo = Collections.singletonList(garage.getPiece("TURBO"));
        Reparation turbo = new Reparation("Remplacement Turbo", 240, kitTurbo, ZoneIntervention.BLOC_MOTEUR);
        turbo.ajouterEnergieCompatible(Energie.ESSENCE);
        turbo.ajouterEnergieCompatible(Energie.DIESEL);
        vidange.ajouterEnergieCompatible(Energie.HYBRID);
        garage.creerTypeIntervention(turbo);

        // ZONE AVANT
        List<Pieces> kitFreinsAV = Arrays.asList(garage.getPiece("PLAQUE-AV"), garage.getPiece("DISQUE-AV"));
        Reparation freinsAV = new Reparation("Freins Avant (Disques+Plaquettes)", 90, kitFreinsAV, ZoneIntervention.TRAIN_AVANT);
        garage.creerTypeIntervention(freinsAV);

        List<Pieces> kitPneusAV = Arrays.asList(garage.getPiece("PNEU-AV"), garage.getPiece("PNEU-AV"));
        Reparation pneusAV = new Reparation("Changement 2 Pneus AV", 45, kitPneusAV, ZoneIntervention.TRAIN_AVANT);
        garage.creerTypeIntervention(pneusAV);

        // ZONE ARRIERE
        List<Pieces> kitAmorto = Arrays.asList(garage.getPiece("AMORTISSEUR-AR"), garage.getPiece("AMORTISSEUR-AR"));
        Reparation amortisseurs = new Reparation("Amortisseurs Arrière", 120, kitAmorto, ZoneIntervention.TRAIN_ARRIERE);
        garage.creerTypeIntervention(amortisseurs);

        List<Pieces> kitFreinsAR = Arrays.asList(garage.getPiece("PLAQUE-AR"), garage.getPiece("DISQUE-AR"));
        Reparation freinsAR = new Reparation("Freins Arrière (Disques+Plaquettes)", 90, kitFreinsAR, ZoneIntervention.TRAIN_ARRIERE);
        garage.creerTypeIntervention(freinsAR);

        List<Pieces> kitPneusAR = Arrays.asList(garage.getPiece("PNEU-AR"), garage.getPiece("PNEU-AR"));
        Reparation pneusAR = new Reparation("Changement 2 Pneus AR", 45, kitPneusAR, ZoneIntervention.TRAIN_ARRIERE);
        garage.creerTypeIntervention(pneusAR);

        // CRÉATION TYPEVEHICULE
        TypeVehicule clio = new TypeVehicule("Renault", "Clio V", Energie.ESSENCE, BoiteVitesse.MANUELLE, 5, 5, 90);
        garage.creerTypeVehicule(clio);

        TypeVehicule zoe = new TypeVehicule("Renault", "Zoe", Energie.ELECTRIQUE, BoiteVitesse.AUTOMATIQUE, 5, 5, 135);
        garage.creerTypeVehicule(zoe);

        TypeVehicule model3 = new TypeVehicule("Tesla", "Model 3", Energie.ELECTRIQUE, BoiteVitesse.AUTOMATIQUE, 5, 4, 300);
        garage.creerTypeVehicule(model3);

        TypeVehicule t3008 = new TypeVehicule("Peugeot", "3008", Energie.DIESEL, BoiteVitesse.AUTOMATIQUE, 5, 5, 130);
        garage.creerTypeVehicule(t3008);

        TypeVehicule mustang = new TypeVehicule("Ford", "Mustang GT", Energie.ESSENCE, BoiteVitesse.AUTOMATIQUE, 4, 2, 450);
        garage.creerTypeVehicule(mustang);

        TypeVehicule yaris = new TypeVehicule("Toyota", "Yaris", Energie.HYBRID, BoiteVitesse.AUTOMATIQUE, 5, 5, 116);
        garage.creerTypeVehicule(yaris);

        // CRÉATION CLIENT & VÉHICULE
        Client c1 = new Client("Martin", "Paul", "paul.martin@mail.fr", "12 Rue des Fleurs, Paris");
        Vehicule v1 = new Vehicule("CD-634-BH", LocalDate.of(2019, 5, 12), 42000, clio, null);
        garage.creerClientEtVehicule(c1, v1);

        Client c2 = new Client("Bernard", "Sophie", "s.bernard@yahoo.com", "45 Avenue de la République, Lyon");
        Vehicule v2 = new Vehicule("DK-987-HJ", LocalDate.of(2021, 8, 20), 15000, zoe, null);
        garage.creerClientEtVehicule(c2, v2);

        Client c3 = new Client("Thomas", "Luc", "l.thomas@tech.com", "8 Boulevard Haussmann, Paris");
        Vehicule v3 = new Vehicule("BE-456-LK", LocalDate.of(2023, 1, 15), 5000, model3, null);
        garage.creerClientEtVehicule(c3, v3);

        Client c4 = new Client("Petit", "Marie", "marie.petit@orange.fr", "3 Impasse des Lilas, Bordeaux");
        Vehicule v4 = new Vehicule("YC-321-UV", LocalDate.of(2020, 11, 30), 60500, t3008, null);
        garage.creerClientEtVehicule(c4, v4);

        Client c5 = new Client("Robert", "Michel", "m.robert@vitesse.net", "90 Route Nationale, Marseille");
        Vehicule v5 = new Vehicule("TC-654-ST", LocalDate.of(2018, 3, 10), 85000, mustang, null);
        garage.creerClientEtVehicule(c5, v5);

        Client c6 = new Client("Richard", "Isabelle", "isa.richard@free.fr", "7 Place du Marché, Lille");
        Vehicule v6 = new Vehicule("GM-789-HX", LocalDate.of(2022, 6, 5), 22000, yaris, null);
        garage.creerClientEtVehicule(c6, v6);

        Client c7 = new Client("Durand", "Pierre", "pierrot@gmail.com", "55 Rue Verte, Nantes");
        Vehicule v7 = new Vehicule("BY-147-VY", LocalDate.of(2015, 9, 18), 110000, clio, null);
        garage.creerClientEtVehicule(c7, v7);

        Client c8 = new Client("Leroy", "Julie", "j.leroy@sfr.fr", "101 Allée des Pins, Nice");
        Vehicule v8 = new Vehicule("KE-258-XP", LocalDate.of(2021, 12, 1), 30000, t3008, null);
        garage.creerClientEtVehicule(c8, v8);

        Client c9 = new Client("Moreau", "Alain", "alain.moreau@club.com", "22 Rue du Port, Brest");
        Vehicule v9 = new Vehicule("ZP-369-TQ", LocalDate.of(2017, 7, 7), 55000, mustang, null);
        garage.creerClientEtVehicule(c9, v9);

        Client c10 = new Client("Simon", "Claire", "claire.simon@laposte.net", "6 Bis Rue Haute, Strasbourg");
        Vehicule v10 = new Vehicule("KD-741-FG", LocalDate.of(2023, 5, 25), 8000, zoe, null);
        garage.creerClientEtVehicule(c10, v10);

        System.out.println("\n--- 5. Peuplement de la BDD (Interventions Réalistes) ---");

        garage.creerIntervention("CD-634-BH", Arrays.asList(vidange, pneusAV), 42500);

        garage.creerIntervention("DK-987-HJ", List.of(freinsAV), 16000);

        garage.creerIntervention("BE-456-LK", List.of(pneusAR), 6000);

        garage.creerIntervention("YC-321-UV", Arrays.asList(vidange, freinsAV, pneusAV), 61200);

        garage.creerIntervention("TC-654-ST", Arrays.asList(turbo, vidange), 86000);

        garage.creerIntervention("GM-789-HX", List.of(vidange), 22500);

        garage.creerIntervention("BY-147-VY", Arrays.asList(courroie, vidange), 110500);

        garage.creerIntervention("ZP-369-TQ", Arrays.asList(pneusAV, pneusAR), 56000);

        garage.creerIntervention("BE-456-LK", List.of(entretienBatterie), 30500);

        System.out.println("Ajout des interventions terminé.");
        garage.fermer();
        System.out.println("\n--- FIN Création BDD ---");
    }
}