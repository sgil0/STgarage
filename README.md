# STgarage

STgarage est une application de gestion complète pour garages automobiles. 
Elle permet le suivi du parc automobile, la gestion des clients et la planification des interventions via une interface graphique interactive.

## Prérequis

Avant de commencer, assurez-vous que votre environnement dispose des éléments suivants :

* **Java JDK 22** : Nécessaire pour compiler le projet.
* **Apache Maven** (version 3.9 ou supérieure) : Installé et accessible dans votre PATH.
* **MySQL Server** (version 8.0) : En cours d'exécution.

## Installation, configuration et lancement

### 1. Cloner le projet
Récupérez le code source sur votre machine locale :

```bash
git clone <https://github.com/sgil0/STgarage.git>
cd STgarage
```

### 2. Préparer la Base de Données
Le projet nécessite une base de données MySQL nommée stgarage.
Connectez-vous à votre serveur MySQL, créez une base de données nommée "stgarage". 
Importez "stgarage.sql" disponible à la racine du projet.

Pour la remplir, vous pouvez soit : 
- importer la bdd "stgarage_BDD.sql" disponible à la racine du projet directement.
- utiliser le script utilitaire Main.java pour insérer un jeu de données de test (clients, véhicules, pièces, forfaits)

### 3. Configurer l'accès BDD
Vous devez renseigner vos identifiants de connexion MySQL dans le projet.

Ouvrez le fichier src/main/resources/META-INF/persistence.xml et modifiez les propriétés suivantes :

```XML
<properties>
    <property name="jakarta.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/stgarage"/>
    <property name="jakarta.persistence.jdbc.user" value="root"/>
    <property name="jakarta.persistence.jdbc.password" value="VOTRE_MOT_DE_PASSE"/>
</properties>
```

### 4. Lancement de l'Application
Au premier démarrage et après set-up de la base de données, il faut d'abord modifier le fichier :
```/src/main/java/resources/persistence.xml```

Il faut modifier ces propriétés et les passer à "drop-and-create".

```xml
<property name="jakarta.persistence.schema-generation.database.action" value="drop-and-create"/>
<property name="jakarta.persistence.schema-generation.scripts.action" value="drop-and-create"/>
``` 

Il faut ensuite éxécuter "FenetrePrincipale.java".
Après ce premier démarage, il est primordial de remettre les valeurs de persistence.xml à "none"

```xml
<property name="jakarta.persistence.schema-generation.database.action" value="none"/>
<property name="jakarta.persistence.schema-generation.scripts.action" value="none"/>
```

Vous pouvez maintenant utiliser l'application normalement en exécutant "FenetrePrincipale.java".

## Fonctionnalités Clés

### Gestion du Parc Automobile
* **Suivi complet** : Enregistrement des véhicules avec immatriculation (format FR), kilométrage et propriétaire.
* **Détection de modèle** : Création dynamique de nouveaux modèles et marques lors de l'ajout d'un véhicule inconnu.
* **Historique centralisé** : Accès rapide à l'historique des réparations et des coûts par véhicule.

### Atelier Mécanique Interactif
* **Visualisation 2D** : Sélection intuitive des zones d'intervention via un schéma interactif du véhicule (Moteur, Train Avant, Train Arrière).
* **Filtrage Intelligent** : Le système propose uniquement les forfaits compatibles avec l'énergie du véhicule (ex: pas de vidange ou de filtre à huile proposés pour un véhicule électrique).
* **Devis en Temps Réel** : Calcul automatique du coût total (Pièces + Main d'œuvre) au fur et à mesure de la sélection des prestations.

### Gestion Proactive des Urgences
* **Algorithme d'analyse** : Comparaison automatique du kilométrage actuel avec les préconisations constructeur.
* **Alertes Visuelles** : Notification immédiate des entretiens critiques (ex: "Distribution à faire d'urgence").

### Gestion de la Clientèle
* **Base de données clients** : Gestion des coordonnées et liaison avec les véhicules.
* **Recherche rapide** : Moteur de recherche multicritères (Nom, Prénom, Email).


## Architecture Technique

Le projet repose sur une architecture **MVC (Modèle-Vue-Contrôleur)** robuste, assurant une séparation claire entre la logique métier et l'interface.

* **Back-end** :
    * **Langage** : Java 22
    * **ORM** : Hibernate Core 7.0.4 pour la persistance des données.
    * **JPA** : Jakarta Persistence API pour la standardisation des échanges avec la BDD.
* **Front-end** :
    * **Technologie** : Java Swing.
    * **Design** : Thème personnalisé sombre ("Dark Mode") avec accentuation Orange.
* **Base de Données** :
    * **SGBD** : MySQL 8.0.
    * **Scripting** : Initialisation via script SQL complet (`stgarage.sql`).
  
## Structure du Projet


```txt
STgarage/
├── src/main/java/
│   ├── Main.java                        # Script de peuplement de la BDD
│   │
│   ├── back/                            # COUCHE MÉTIER (Backend)
│   │   ├── EnumType/                    # Énumérations (BoiteVitesse, Energie...)
│   │   ├── Client.java
│   │   ├── Entretien.java
│   │   ├── GestionGarage.java           # Façade de gestion (EntityManager)
│   │   ├── Intervention.java
│   │   ├── Pieces.java
│   │   ├── Reparation.java
│   │   ├── TypeIntervention.java
│   │   ├── TypeVehicule.java
│   │   └── Vehicule.java
│   │
│   └── front/                           # COUCHE PRÉSENTATION (Swing)
│       ├── Listener/                    # Listeners (SchemaListener...)
│       ├── DialogAjoutClient.java
│       ├── DialogAjoutTypeVehicule.java
│       ├── DialogAjoutVehicule.java
│       ├── DialogSelectionClient.java
│       ├── FenetrePrincipale.java       # Point d'entrée de l'application
│       ├── PanneauGestionInterventions.java
│       ├── PanneauGestionVehicules.java
│       ├── PanneauSchema2D.java         # Dessin interactif du véhicule
│       └── PanneauUrgences.java         # Barre latérale de notifications
│
├── src/main/resources/META-INF/
│   ├── persistence.xml                  # Configuration JPA
│   └── validation.xml
│
├── pom.xml                              # Configuration Maven
└── stgarage_BDD.sql                     # Script SQL
```

## Auteurs
Ce projet a été développé par DINOIRD Victor et GILLON Samuel.
