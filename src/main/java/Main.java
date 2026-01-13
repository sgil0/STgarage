import back.*;
import back.EnumType.*;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- DÉMARRAGE DU GARAGE ---");
        GestionGarage garage = new GestionGarage();

        // CRÉATION DU CATALOGUE (Pièces & Types)
        System.out.println("\n--- Initialisation Catalogue ---");

        // On crée quelques pièces
        garage.creerPiece("HUILE-5W30", "Bidon Huile 5L", 50.0f, ZoneIntervention.BLOC_MOTEUR);
        garage.creerPiece("FILTRE-H", "Filtre à Huile", 15.0f, ZoneIntervention.BLOC_MOTEUR);
        garage.creerPiece("TURBO", "Turbo", 780.0f, ZoneIntervention.BLOC_MOTEUR);
        garage.creerPiece("COURROIE_DISTRIBUTION", "Courroie de distribution", 280.0f, ZoneIntervention.BLOC_MOTEUR);

        garage.creerPiece("PLAQUE-AV", "Plaquettes Avant", 60.0f, ZoneIntervention.TRAIN_AVANT);
        garage.creerPiece("DISQUE-AV", "Disques Avant", 120.0f, ZoneIntervention.TRAIN_AVANT);
        garage.creerPiece("PNEUS-AV", "Pneus Avant", 215.0f, ZoneIntervention.TRAIN_AVANT);
        

        garage.creerPiece("PLAQUE-AR", "Plaquettes Arrière", 45.0f, ZoneIntervention.TRAIN_ARRIERE);
        garage.creerPiece("DISQUE-AR", "Disques Arrière", 100.0f, ZoneIntervention.TRAIN_ARRIERE);
        garage.creerPiece("PNEUS-AR", "Pneus Arrière", 215.0f, ZoneIntervention.TRAIN_ARRIERE);
        

        // On crée un Type d'intervention : "Vidange Standard"
        List<Pieces> kitVidange = new ArrayList<>();
        kitVidange.add(garage.getPiece("HUILE-5W30"));
        kitVidange.add(garage.getPiece("FILTRE-H"));
        Entretien vidange = new Entretien("Vidange Standard", 60, kitVidange, 15000);
        garage.creerTypeIntervention(vidange);

        // On crée un Type d'intervention : "Changement courroie de distribution"
        List<Pieces> kitCourroie = new ArrayList<>();
        kitCourroie.add(garage.getPiece("COURROIE_DISTRIBUTION"));
        Entretien courroie_distr = new Entretien("Changement courroie de distribution", 120, kitCourroie, 120000);
        garage.creerTypeIntervention(courroie_distr);

        // On crée un Type d'intervention : "Changement turbo"
        List<Pieces> kitTurbo = new ArrayList<>();
        kitTurbo.add(garage.getPiece("TURBO"));
        Reparation cgt_turbo = new Reparation("Changement turbo", 150, kitTurbo);
        garage.creerTypeIntervention(cgt_turbo);

        // On crée un Type d'intervention : "Crevaison pneus avants"
        List<Pieces> kitCrevaisonAV = new ArrayList<>();
        kitCrevaisonAV.add(garage.getPiece("PNEUS-AV"));
        Reparation crevaison_AV = new Reparation("Crevaison pneus avants", 30, kitCrevaisonAV);

        garage.creerTypeIntervention(crevaison_AV);

        // On crée un Type d'intervention : "Crevaison pneus arrière"
        List<Pieces> kitCrevaisonAR = new ArrayList<>();
        kitCrevaisonAR.add(garage.getPiece("PNEUS-AR"));
        Reparation crevaison_AR = new Reparation("Crevaison pneus arrières", 30, kitCrevaisonAR);

        garage.creerTypeIntervention(crevaison_AR);

        // On crée un Type d'intervention : "Changement pneus"
        List<Pieces> kitRemplacementPneus = new ArrayList<>();
        kitRemplacementPneus.add(garage.getPiece("PNEUS-AV"));
        kitRemplacementPneus.add(garage.getPiece("PNEUS-AR"));
        Entretien remplacement_pneus = new Entretien("Remplacement pneus", 30, kitRemplacementPneus, 20000);
        remplacement_pneus.setNom("Remplacement pneus");
        remplacement_pneus.setDuree(30);

        garage.creerTypeIntervention(remplacement_pneus);


        List<Pieces> kitFreinsAV = new ArrayList<>();
        kitFreinsAV.add(garage.getPiece("PLAQUE-AV"));
        kitFreinsAV.add(garage.getPiece("DISQUE-AV"));
        // On crée le type "Réparation" (Taux horaire plus cher, défini dans la classe Reparation)
        Reparation repFreinsAV = new Reparation("Remplacement Freins AV", 90, kitFreinsAV);
        garage.creerTypeIntervention(repFreinsAV);

        List<Pieces> kitFreinsAR = new ArrayList<>();
        kitFreinsAR.add(garage.getPiece("PLAQUE-AR"));
        kitFreinsAR.add(garage.getPiece("DISQUE-AR"));
        // On crée le type "Réparation" (Taux horaire plus cher, défini dans la classe Reparation)
        Reparation repFreinsAR = new Reparation("Remplacement Freins AR", 90, kitFreinsAR);
        garage.creerTypeIntervention(repFreinsAR);

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

        // AJOUT D'INTERVENTIONS SUR LES VÉHICULES
        System.out.println("\n--- Ajout d'Interventions Historiques ---");

        garage.creerIntervention("CD-634-BH", "Vidange Standard", kitVidange, 42500);

        garage.creerIntervention("TC-654-ST", "Remplacement Freins AV", kitFreinsAV, 86000);

        garage.creerIntervention("YC-321-UV", "Vidange Standard", kitVidange, 61000);

        garage.creerIntervention("BY-147-VY", "Remplacement Freins AR", kitFreinsAR, 112000);

        garage.creerIntervention("KE-258-XP", "Vidange Standard", kitVidange, 31000);

        // 6. Une Vidange + Freins (Sur Mesure) sur la Yaris d'Isabelle (GM-789-HX)
        // On crée une liste spéciale combinant Vidange + Plaquettes seulement
        List<Pieces> kitComplet = new ArrayList<>(kitVidange);
        kitComplet.add(garage.getPiece("PLAQUE-AV"));

        garage.creerIntervention("GM-789-HX", "Vidange Standard", kitComplet, 23000);

        System.out.println("✅ Interventions ajoutées avec succès.");

        garage.fermer();
        System.out.println("\n--- FIN Création BDD ---");
    }
}