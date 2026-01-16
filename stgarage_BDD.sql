-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : ven. 16 jan. 2026 à 12:37
-- Version du serveur : 8.3.0
-- Version de PHP : 8.2.18

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `stgarage`
--

-- --------------------------------------------------------

--
-- Structure de la table `client`
--

DROP TABLE IF EXISTS `client`;
CREATE TABLE IF NOT EXISTS `client` (
  `idClient` bigint NOT NULL AUTO_INCREMENT,
  `adresse` varchar(255) DEFAULT NULL,
  `mail` varchar(255) DEFAULT NULL,
  `nom` varchar(255) DEFAULT NULL,
  `prenom` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`idClient`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `client`
--

INSERT INTO `client` (`idClient`, `adresse`, `mail`, `nom`, `prenom`) VALUES
(1, '12 Rue des Fleurs, Paris', 'paul.martin@mail.fr', 'Martin', 'Paul'),
(2, '45 Avenue de la République, Lyon', 's.bernard@yahoo.com', 'Bernard', 'Sophie'),
(3, '8 Boulevard Haussmann, Paris', 'l.thomas@tech.com', 'Thomas', 'Luc'),
(4, '3 Impasse des Lilas, Bordeaux', 'marie.petit@orange.fr', 'Petit', 'Marie'),
(5, '90 Route Nationale, Marseille', 'm.robert@vitesse.net', 'Robert', 'Michel'),
(6, '7 Place du Marché, Lille', 'isa.richard@free.fr', 'Richard', 'Isabelle'),
(7, '55 Rue Verte, Nantes', 'pierrot@gmail.com', 'Durand', 'Pierre'),
(8, '101 Allée des Pins, Nice', 'j.leroy@sfr.fr', 'Leroy', 'Julie'),
(9, '22 Rue du Port, Brest', 'alain.moreau@club.com', 'Moreau', 'Alain'),
(10, '6 Bis Rue Haute, Strasbourg', 'claire.simon@laposte.net', 'Simon', 'Claire');

-- --------------------------------------------------------

--
-- Structure de la table `intervention`
--

DROP TABLE IF EXISTS `intervention`;
CREATE TABLE IF NOT EXISTS `intervention` (
  `date` date DEFAULT NULL,
  `dureeTotale` int NOT NULL,
  `kilometrage` float NOT NULL,
  `prixTotal` float NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `immat_vehicule` varchar(9) DEFAULT NULL,
  `statut` enum('EN_COURS','PAYEE','PLANIFIEE','TERMINEE') DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1lsb5r9wo1wnom66eo6ptndpa` (`immat_vehicule`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `intervention`
--

INSERT INTO `intervention` (`date`, `dureeTotale`, `kilometrage`, `prixTotal`, `id`, `immat_vehicule`, `statut`) VALUES
('2026-01-14', 105, 42500, 465, 1, 'CD-634-BH', 'PLANIFIEE'),
('2026-01-14', 90, 16000, 360, 2, 'DK-987-HJ', 'PLANIFIEE'),
('2026-01-14', 45, 6000, 370, 3, 'BE-456-LK', 'PLANIFIEE'),
('2026-01-14', 195, 61200, 825, 4, 'YC-321-UV', 'PLANIFIEE'),
('2026-01-14', 300, 86000, 1415, 5, 'TC-654-ST', 'PLANIFIEE'),
('2026-01-14', 60, 22500, 155, 6, 'GM-789-HX', 'PLANIFIEE'),
('2026-01-14', 240, 110500, 705, 7, 'BY-147-VY', 'PLANIFIEE'),
('2026-01-14', 90, 56000, 680, 8, 'ZP-369-TQ', 'PLANIFIEE'),
('2026-01-14', 240, 30500, 800, 9, 'BE-456-LK', 'PLANIFIEE'),
('2026-01-15', 240, 42500, 705, 10, 'CD-634-BH', 'PLANIFIEE'),
('2026-01-15', 135, 110000, 670, 11, 'TC-654-ST', 'PLANIFIEE'),
('2026-01-15', 90, 8000, 680, 12, 'KD-741-FG', 'PLANIFIEE'),
('2026-01-15', 135, 110000, 670, 13, 'TC-654-ST', 'PLANIFIEE'),
('2026-01-15', 540, 68600, 2215, 14, 'BE-456-LK', 'PLANIFIEE'),
('2026-01-15', 180, 42500, 550, 15, 'CD-634-BH', 'PLANIFIEE'),
('2026-01-16', 210, 70000, 770, 16, 'BE-456-LK', NULL),
('2026-01-16', 240, 92778, 800, 17, 'OP-785-BV', NULL);

-- --------------------------------------------------------

--
-- Structure de la table `intervention_composition`
--

DROP TABLE IF EXISTS `intervention_composition`;
CREATE TABLE IF NOT EXISTS `intervention_composition` (
  `id_intervention` bigint NOT NULL,
  `id_type_intervention` bigint NOT NULL,
  KEY `FKfltcg37lil97tc8mvfix4a7k8` (`id_type_intervention`),
  KEY `FKixtf30m4b28qsb0gr4sfehrmr` (`id_intervention`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `intervention_composition`
--

INSERT INTO `intervention_composition` (`id_intervention`, `id_type_intervention`) VALUES
(1, 2),
(1, 6),
(2, 5),
(3, 9),
(4, 2),
(4, 5),
(4, 6),
(5, 4),
(5, 2),
(6, 2),
(7, 3),
(7, 2),
(8, 6),
(8, 9),
(9, 1),
(10, 2),
(10, 3),
(11, 9),
(11, 6),
(12, 9),
(12, 6),
(13, 5),
(13, 6),
(14, 6),
(14, 1),
(14, 7),
(14, 8),
(14, 9),
(15, 3),
(16, 5),
(16, 7),
(17, 1);

-- --------------------------------------------------------

--
-- Structure de la table `pieces`
--

DROP TABLE IF EXISTS `pieces`;
CREATE TABLE IF NOT EXISTS `pieces` (
  `prix` float NOT NULL,
  `nom` varchar(255) DEFAULT NULL,
  `ref` varchar(255) NOT NULL,
  `zone` enum('BLOC_MOTEUR','TRAIN_ARRIERE','TRAIN_AVANT') DEFAULT NULL,
  PRIMARY KEY (`ref`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `pieces`
--

INSERT INTO `pieces` (`prix`, `nom`, `ref`, `zone`) VALUES
(85, 'Amortisseur Arrière', 'AMORTISSEUR-AR', 'TRAIN_ARRIERE'),
(280, 'Courroie Distribution', 'COURROIE-DIST', 'BLOC_MOTEUR'),
(100, 'Jeu Disques AR', 'DISQUE-AR', 'TRAIN_ARRIERE'),
(120, 'Jeu Disques AV', 'DISQUE-AV', 'TRAIN_AVANT'),
(15, 'Filtre à Huile', 'FILTRE-H', 'BLOC_MOTEUR'),
(50, 'Bidon Huile 5L', 'HUILE-5W30', 'BLOC_MOTEUR'),
(45, 'Jeu Plaquettes AR', 'PLAQUE-AR', 'TRAIN_ARRIERE'),
(60, 'Jeu Plaquettes AV', 'PLAQUE-AV', 'TRAIN_AVANT'),
(140, 'Pneu Michelin 19p', 'PNEU-AR', 'TRAIN_ARRIERE'),
(110, 'Pneu Michelin 17p', 'PNEU-AV', 'TRAIN_AVANT'),
(780, 'Turbo Garett', 'TURBO', 'BLOC_MOTEUR');

-- --------------------------------------------------------

--
-- Structure de la table `ref_intervention_pieces`
--

DROP TABLE IF EXISTS `ref_intervention_pieces`;
CREATE TABLE IF NOT EXISTS `ref_intervention_pieces` (
  `id_type_intervention` bigint NOT NULL,
  `ref_piece` varchar(255) NOT NULL,
  KEY `FKmmvybymwc34giov7191r3tlrg` (`ref_piece`),
  KEY `FK3jybitwyyewdddjbm08wl62sw` (`id_type_intervention`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `ref_intervention_pieces`
--

INSERT INTO `ref_intervention_pieces` (`id_type_intervention`, `ref_piece`) VALUES
(2, 'HUILE-5W30'),
(2, 'FILTRE-H'),
(3, 'COURROIE-DIST'),
(4, 'TURBO'),
(5, 'PLAQUE-AV'),
(5, 'DISQUE-AV'),
(6, 'PNEU-AV'),
(6, 'PNEU-AV'),
(7, 'AMORTISSEUR-AR'),
(7, 'AMORTISSEUR-AR'),
(8, 'PLAQUE-AR'),
(8, 'DISQUE-AR'),
(9, 'PNEU-AR'),
(9, 'PNEU-AR');

-- --------------------------------------------------------

--
-- Structure de la table `type_intervention`
--

DROP TABLE IF EXISTS `type_intervention`;
CREATE TABLE IF NOT EXISTS `type_intervention` (
  `duree` int NOT NULL,
  `kilometrageMax` int DEFAULT NULL,
  `prix` float NOT NULL,
  `tauxHoraire` float NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `categorie_type` varchar(31) NOT NULL,
  `nom` varchar(255) DEFAULT NULL,
  `zone` enum('BLOC_MOTEUR','TRAIN_ARRIERE','TRAIN_AVANT') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ;

--
-- Déchargement des données de la table `type_intervention`
--

INSERT INTO `type_intervention` (`duree`, `kilometrageMax`, `prix`, `tauxHoraire`, `id`, `categorie_type`, `nom`, `zone`) VALUES
(240, 30000, 800, 200, 1, 'ENTRETIEN', 'Diagnostic Santé Batterie (SOH)', 'BLOC_MOTEUR'),
(60, 20000, 155, 90, 2, 'ENTRETIEN', 'Vidange Complète', 'BLOC_MOTEUR'),
(180, 120000, 550, 90, 3, 'ENTRETIEN', 'Kit Distribution', 'BLOC_MOTEUR'),
(240, NULL, 1260, 120, 4, 'REPARATION', 'Remplacement Turbo', 'BLOC_MOTEUR'),
(90, NULL, 360, 120, 5, 'REPARATION', 'Freins Avant (Disques+Plaquettes)', 'TRAIN_AVANT'),
(45, NULL, 310, 120, 6, 'REPARATION', 'Changement 2 Pneus AV', 'TRAIN_AVANT'),
(120, NULL, 410, 120, 7, 'REPARATION', 'Amortisseurs Arrière', 'TRAIN_ARRIERE'),
(90, NULL, 325, 120, 8, 'REPARATION', 'Freins Arrière (Disques+Plaquettes)', 'TRAIN_ARRIERE'),
(45, NULL, 370, 120, 9, 'REPARATION', 'Changement 2 Pneus AR', 'TRAIN_ARRIERE');

-- --------------------------------------------------------

--
-- Structure de la table `type_intervention_energies`
--

DROP TABLE IF EXISTS `type_intervention_energies`;
CREATE TABLE IF NOT EXISTS `type_intervention_energies` (
  `id_type_intervention` bigint NOT NULL,
  `energie_compatible` enum('DIESEL','ELECTRIQUE','ESSENCE','HYBRID') DEFAULT NULL,
  KEY `FK7gxm2487c5a669dor8eof3qc` (`id_type_intervention`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `type_intervention_energies`
--

INSERT INTO `type_intervention_energies` (`id_type_intervention`, `energie_compatible`) VALUES
(1, 'ELECTRIQUE'),
(1, 'HYBRID'),
(2, 'ESSENCE'),
(2, 'DIESEL'),
(2, 'HYBRID'),
(3, 'ESSENCE'),
(3, 'DIESEL'),
(3, 'HYBRID'),
(4, 'ESSENCE'),
(4, 'DIESEL');

-- --------------------------------------------------------

--
-- Structure de la table `type_vehicule`
--

DROP TABLE IF EXISTS `type_vehicule`;
CREATE TABLE IF NOT EXISTS `type_vehicule` (
  `nbPlaces` int NOT NULL,
  `nbPortes` int NOT NULL,
  `puissance` int NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `marque` varchar(255) DEFAULT NULL,
  `modele` varchar(255) DEFAULT NULL,
  `boiteVitesse` enum('AUTOMATIQUE','MANUELLE') DEFAULT NULL,
  `energie` enum('DIESEL','ELECTRIQUE','ESSENCE','HYBRID') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `type_vehicule`
--

INSERT INTO `type_vehicule` (`nbPlaces`, `nbPortes`, `puissance`, `id`, `marque`, `modele`, `boiteVitesse`, `energie`) VALUES
(5, 5, 90, 1, 'Renault', 'Clio V', 'MANUELLE', 'ESSENCE'),
(5, 5, 135, 2, 'Renault', 'Zoe', 'AUTOMATIQUE', 'ELECTRIQUE'),
(5, 4, 300, 3, 'Tesla', 'Model 3', 'AUTOMATIQUE', 'ELECTRIQUE'),
(5, 5, 130, 4, 'Peugeot', '3008', 'AUTOMATIQUE', 'DIESEL'),
(4, 2, 450, 5, 'Ford', 'Mustang GT', 'AUTOMATIQUE', 'ESSENCE'),
(5, 5, 116, 6, 'Toyota', 'Yaris', 'AUTOMATIQUE', 'HYBRID');

-- --------------------------------------------------------

--
-- Structure de la table `vehicule`
--

DROP TABLE IF EXISTS `vehicule`;
CREATE TABLE IF NOT EXISTS `vehicule` (
  `kilometrage` float NOT NULL,
  `miseCirc` date DEFAULT NULL,
  `id_proprietaire` bigint DEFAULT NULL,
  `id_type_vehicule` bigint DEFAULT NULL,
  `immatriculation` varchar(9) NOT NULL,
  PRIMARY KEY (`immatriculation`),
  KEY `FK8yec76kmmgxlphob1faf9gsxo` (`id_proprietaire`),
  KEY `FKhe63s4ae6vjyb55cj7jjxkhfy` (`id_type_vehicule`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `vehicule`
--

INSERT INTO `vehicule` (`kilometrage`, `miseCirc`, `id_proprietaire`, `id_type_vehicule`, `immatriculation`) VALUES
(70000, '2023-01-15', 3, 3, 'BE-456-LK'),
(110500, '2015-09-18', 7, 1, 'BY-147-VY'),
(42500, '2019-05-12', 1, 1, 'CD-634-BH'),
(16000, '2021-08-20', 2, 2, 'DK-987-HJ'),
(22500, '2022-06-05', 6, 6, 'GM-789-HX'),
(8000, '2023-05-25', 10, 2, 'KD-741-FG'),
(30000, '2021-12-01', 8, 4, 'KE-258-XP'),
(92778, '2020-06-12', 2, 3, 'OP-785-BV'),
(110000, '2018-03-10', 5, 5, 'TC-654-ST'),
(61200, '2020-11-30', 4, 4, 'YC-321-UV'),
(56000, '2017-07-07', 9, 5, 'ZP-369-TQ');

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `intervention`
--
ALTER TABLE `intervention`
  ADD CONSTRAINT `FK1lsb5r9wo1wnom66eo6ptndpa` FOREIGN KEY (`immat_vehicule`) REFERENCES `vehicule` (`immatriculation`);

--
-- Contraintes pour la table `intervention_composition`
--
ALTER TABLE `intervention_composition`
  ADD CONSTRAINT `FKfltcg37lil97tc8mvfix4a7k8` FOREIGN KEY (`id_type_intervention`) REFERENCES `type_intervention` (`id`),
  ADD CONSTRAINT `FKixtf30m4b28qsb0gr4sfehrmr` FOREIGN KEY (`id_intervention`) REFERENCES `intervention` (`id`);

--
-- Contraintes pour la table `ref_intervention_pieces`
--
ALTER TABLE `ref_intervention_pieces`
  ADD CONSTRAINT `FK3jybitwyyewdddjbm08wl62sw` FOREIGN KEY (`id_type_intervention`) REFERENCES `type_intervention` (`id`),
  ADD CONSTRAINT `FKmmvybymwc34giov7191r3tlrg` FOREIGN KEY (`ref_piece`) REFERENCES `pieces` (`ref`);

--
-- Contraintes pour la table `type_intervention_energies`
--
ALTER TABLE `type_intervention_energies`
  ADD CONSTRAINT `FK7gxm2487c5a669dor8eof3qc` FOREIGN KEY (`id_type_intervention`) REFERENCES `type_intervention` (`id`);

--
-- Contraintes pour la table `vehicule`
--
ALTER TABLE `vehicule`
  ADD CONSTRAINT `FK8yec76kmmgxlphob1faf9gsxo` FOREIGN KEY (`id_proprietaire`) REFERENCES `client` (`idClient`),
  ADD CONSTRAINT `FKhe63s4ae6vjyb55cj7jjxkhfy` FOREIGN KEY (`id_type_vehicule`) REFERENCES `type_vehicule` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
