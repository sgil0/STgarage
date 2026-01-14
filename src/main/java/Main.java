import back.*;
import back.EnumType.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
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

        Entretien entretienBatterie = new Entretien("Diagnostic Santé Batterie (SOH)", 240, Collections.emptyList(), 30000, ZoneIntervention.BLOC_MOTEUR);

        // On applique un tarif spécial "Expert Electrique" pour atteindre le prix sans pièces
        entretienBatterie.setTauxHoraire(200);
        entretienBatterie.calculerPrixForfait(); // Important de recalculer après changement du taux

        entretienBatterie.ajouterEnergieCompatible(Energie.ELECTRIQUE);
        entretienBatterie.ajouterEnergieCompatible(Energie.HYBRID); // Les hybrides ont aussi une batterie HT

        garage.creerTypeIntervention(entretienBatterie);
        // --- ZONE MOTEUR ---
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

        // --- ZONE AVANT ---
        List<Pieces> kitFreinsAV = Arrays.asList(garage.getPiece("PLAQUE-AV"), garage.getPiece("DISQUE-AV"));
        Reparation freinsAV = new Reparation("Freins Avant (Disques+Plaquettes)", 90, kitFreinsAV, ZoneIntervention.TRAIN_AVANT);
        garage.creerTypeIntervention(freinsAV);

        List<Pieces> kitPneusAV = Arrays.asList(garage.getPiece("PNEU-AV"), garage.getPiece("PNEU-AV"));
        Reparation pneusAV = new Reparation("Changement 2 Pneus AV", 45, kitPneusAV, ZoneIntervention.TRAIN_AVANT);
        garage.creerTypeIntervention(pneusAV);

        // --- ZONE ARRIERE ---
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
        // 1. Martin Paul - Clio
        Client c1 = new Client("Martin", "Paul", "paul.martin@mail.fr", "12 Rue des Fleurs, Paris");
        // Plaque réaliste
        Vehicule v1 = new Vehicule("CD-634-BH", LocalDate.of(2019, 5, 12), 42000, clio, null);
        garage.creerClientEtVehicule(c1, v1);

        // 2. Bernard Sophie - Zoe (Electrique)
        Client c2 = new Client("Bernard", "Sophie", "s.bernard@yahoo.com", "45 Avenue de la République, Lyon");
        Vehicule v2 = new Vehicule("DK-987-HJ", LocalDate.of(2021, 8, 20), 15000, zoe, null);
        garage.creerClientEtVehicule(c2, v2);

        // 3. Thomas Luc - Tesla Model 3
        Client c3 = new Client("Thomas", "Luc", "l.thomas@tech.com", "8 Boulevard Haussmann, Paris");
        Vehicule v3 = new Vehicule("BE-456-LK", LocalDate.of(2023, 1, 15), 5000, model3, null);
        garage.creerClientEtVehicule(c3, v3);

        // 4. Petit Marie - Peugeot 3008 (SUV)
        Client c4 = new Client("Petit", "Marie", "marie.petit@orange.fr", "3 Impasse des Lilas, Bordeaux");
        Vehicule v4 = new Vehicule("YC-321-UV", LocalDate.of(2020, 11, 30), 60500, t3008, null);
        garage.creerClientEtVehicule(c4, v4);

        // 5. Robert Michel - Mustang (Sport)
        Client c5 = new Client("Robert", "Michel", "m.robert@vitesse.net", "90 Route Nationale, Marseille");
        Vehicule v5 = new Vehicule("TC-654-ST", LocalDate.of(2018, 3, 10), 85000, mustang, null);
        garage.creerClientEtVehicule(c5, v5);

        // 6. Richard Isabelle - Yaris (Hybride)
        Client c6 = new Client("Richard", "Isabelle", "isa.richard@free.fr", "7 Place du Marché, Lille");
        Vehicule v6 = new Vehicule("GM-789-HX", LocalDate.of(2022, 6, 5), 22000, yaris, null);
        garage.creerClientEtVehicule(c6, v6);

        // 7. Durand Pierre - Une autre Clio
        Client c7 = new Client("Durand", "Pierre", "pierrot@gmail.com", "55 Rue Verte, Nantes");
        Vehicule v7 = new Vehicule("BY-147-VY", LocalDate.of(2015, 9, 18), 110000, clio, null);
        garage.creerClientEtVehicule(c7, v7);

        // 8. Leroy Julie - Un autre 3008
        Client c8 = new Client("Leroy", "Julie", "j.leroy@sfr.fr", "101 Allée des Pins, Nice");
        Vehicule v8 = new Vehicule("KE-258-XP", LocalDate.of(2021, 12, 1), 30000, t3008, null);
        garage.creerClientEtVehicule(c8, v8);

        // 9. Moreau Alain - Une autre Mustang
        Client c9 = new Client("Moreau", "Alain", "alain.moreau@club.com", "22 Rue du Port, Brest");
        Vehicule v9 = new Vehicule("ZP-369-TQ", LocalDate.of(2017, 7, 7), 55000, mustang, null);
        garage.creerClientEtVehicule(c9, v9);

        // 10. Simon Claire - Une autre Zoe
        Client c10 = new Client("Simon", "Claire", "claire.simon@laposte.net", "6 Bis Rue Haute, Strasbourg");
        Vehicule v10 = new Vehicule("KD-741-FG", LocalDate.of(2023, 5, 25), 8000, zoe, null);
        garage.creerClientEtVehicule(c10, v10);

        System.out.println("\n--- 5. Peuplement de la BDD (Interventions Réalistes) ---");

        // 1. La Clio de Martin (v1) : Révision classique des 40.000 km
        // Intervention combinée : Vidange + Filtres (Forfait Vidange) + Changement Pneus Avant
        garage.creerIntervention("CD-634-BH", Arrays.asList(vidange, pneusAV), 42500);

        // 2. La Zoe de Sophie (v2 - Electrique) : Entretien des trains roulants
        // IMPORTANT : Pas de vidange possible (bloqué par ton code). On change juste les plaquettes.
        garage.creerIntervention("DK-987-HJ", Arrays.asList(freinsAV), 16000);

        // 3. La Tesla de Luc (v3 - Electrique) : Problème de pneu
        // Une simple réparation de pneu (ou changement)
        garage.creerIntervention("BE-456-LK", Arrays.asList(pneusAR), 6000);

        // 4. Le 3008 de Marie (v4 - SUV) : Grosse maintenance des 60.000 km
        // Vidange + Disques et Plaquettes à l'avant + Pneus arrière
        garage.creerIntervention("YC-321-UV", Arrays.asList(vidange, freinsAV, pneusAV), 61200);

        // 5. La Mustang de Michel (v5 - Sport) : Casse mécanique !
        // Le turbo a lâché (Reparation) + on fait la vidange en même temps
        garage.creerIntervention("TC-654-ST", Arrays.asList(turbo, vidange), 86000);

        // 6. La Yaris Hybride d'Isabelle (v6) : Entretien annuel
        // Hybride = Moteur thermique = Vidange autorisée
        garage.creerIntervention("GM-789-HX", Arrays.asList(vidange), 22500);

        // 7. La vieille Clio de Pierre (v7 - 110.000 km) : Kit Distribution
        // C'est l'entretien le plus critique et le plus cher
        garage.creerIntervention("BY-147-VY", Arrays.asList(courroie, vidange), 110500);

        // 8. La Mustang d'Alain (v9) : Changement des trains de pneus (Voiture sport)
        // Changement Pneus Avant ET Arrière
        garage.creerIntervention("ZP-369-TQ", Arrays.asList(pneusAV, pneusAR), 56000);

        // 9. La Tesla de Luc (v3) : Les pneus c'est fait, maintenant le gros entretien des 30.000
        garage.creerIntervention("BE-456-LK", Arrays.asList(entretienBatterie), 30500);

        System.out.println("✅ Ajout des interventions terminé.");

        garage.fermer();
        System.out.println("\n--- FIN Création BDD ---");
    }
}