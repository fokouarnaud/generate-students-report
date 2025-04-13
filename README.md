# School Report Manager

Application de gestion des bulletins scolaires pour les établissements d'enseignement.

![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)
![License](https://img.shields.io/badge/license-Proprietary-red.svg)

## Présentation

School Report Manager est une application JavaFX complète pour la gestion des bulletins scolaires. Elle permet de:

- Gérer les élèves et les classes
- Saisir et calculer les notes des élèves
- Générer des bulletins individuels ou collectifs
- Exporter les bulletins au format PDF (individuel ou fusionné) ou au format ZIP

L'application est conçue avec une interface utilisateur moderne et intuitive.

## Captures d'écran

![Interface principale](docs/images/main-interface.png)

## Prérequis système

- Windows 10/11 (64 bits) ou macOS 11+ ou Linux
- Processeur 1.5 GHz minimum
- 4 Go de RAM minimum
- 100 Mo d'espace disque disponible

## Installation

### Windows

1. Téléchargez le fichier d'installation `SchoolReportManager-Setup.exe` depuis la section [Releases](https://github.com/votre-organisation/SchoolReportManager/releases)
2. Exécutez le fichier et suivez les instructions d'installation
3. Lancez l'application depuis le menu Démarrer ou le raccourci sur le bureau

### Compilation depuis les sources

Si vous préférez compiler l'application vous-même :

1. Assurez-vous d'avoir installé :
   - JDK 17 ou supérieur
   - Apache Maven 3.8 ou supérieur

2. Clonez le dépôt :
   ```
   git clone https://github.com/votre-organisation/SchoolReportManager.git
   cd SchoolReportManager
   ```

3. Compilation et exécution :
   ```
   mvn clean javafx:run
   ```

4. Génération d'un installateur Windows (optionnel) :
   ```
   build-installer.bat
   ```

## Guide de démarrage rapide

1. Lors du premier démarrage, l'application vous proposera de générer des données de test
2. Sélectionnez une classe dans le menu déroulant en haut
3. La liste des élèves s'affichera dans le tableau de gauche
4. Sélectionnez un élève pour afficher ses notes dans le tableau de droite
5. Utilisez les boutons d'action pour :
   - Générer un bulletin individuel (PDF)
   - Générer tous les bulletins de la classe (dossier avec PDFs)
   - Exporter tous les bulletins en PDF unique (PDF combiné)
   - Exporter tous les bulletins en ZIP (archive compressée)

## Structure des données

L'application stocke par défaut les données dans une base H2 située dans le dossier :
- Windows : `%USERPROFILE%\schoolreport\schooldb`
- macOS/Linux : `~/schoolreport/schooldb`

Les bulletins générés sont enregistrés dans le dossier :
- Windows : `%USERPROFILE%\bulletins`
- macOS/Linux : `~/bulletins`

## Fonctionnalités détaillées

### Gestion des bulletins

- **Génération individuelle** : Génère un bulletin PDF pour l'élève sélectionné
- **Génération par classe** : Génère tous les bulletins d'une classe dans un dossier
- **Export PDF combiné** : Combine tous les bulletins d'une classe en un seul fichier PDF
- **Export ZIP** : Archive tous les bulletins d'une classe dans un fichier ZIP

### Recherche et filtrage

- Utilisation du champ "Rechercher" pour filtrer les élèves par nom
- Navigation par pages pour les classes avec beaucoup d'élèves

## Architecture technique

L'application est construite selon une architecture en couches :

- **UI** (JavaFX) : Interface utilisateur avec FXML et CSS
- **Services** : Logique métier et traitement des données
- **Persistence** : Gestion de la base de données avec JPA/Hibernate
- **Export** : Génération de PDF avec iText

## Maintenance et dépannage

### Problèmes courants

- **Erreur de démarrage** : Vérifiez que Java est correctement installé
- **Erreur d'exportation PDF** : Vérifiez les permissions d'écriture dans le dossier cible
- **Base de données inaccessible** : Vérifiez que le dossier de la base de données existe et est accessible

### Sauvegarde des données

Pour sauvegarder les données de l'application :
1. Fermez l'application
2. Copiez le dossier `%USERPROFILE%\schoolreport` (Windows) ou `~/schoolreport` (macOS/Linux)
3. Stockez la copie dans un emplacement sécurisé

## Développement

### Structure du projet

```
SchoolReportManager/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── schoolreport/
│   │   │           ├── core/        # Modèles et services de base
│   │   │           ├── export/      # Utilitaires d'export
│   │   │           ├── persistence/ # Couche de persistance
│   │   │           ├── service/     # Implémentations des services
│   │   │           └── ui/          # Interface utilisateur
│   │   └── resources/
│   │       ├── css/                 # Styles CSS
│   │       ├── fxml/                # Fichiers FXML (interface)
│   │       └── META-INF/            # Configuration JPA
│   └── test/                        # Tests unitaires
├── pom.xml                          # Configuration Maven
└── README.md                        # Ce fichier
```

### Technologies utilisées

- **JavaFX** : Framework d'interface utilisateur
- **Hibernate/JPA** : ORM pour la persistance des données
- **H2** : Base de données embarquée
- **iText** : Génération de documents PDF
- **Maven** : Gestion des dépendances et build

## Licence et mentions légales

© 2025 SchoolReportManager. Tous droits réservés.

Cette application est distribuée sous licence propriétaire. Toute redistribution, modification ou utilisation non autorisée est strictement interdite.

## Contact et support

Pour toute question ou demande d'assistance, veuillez contacter :

- Support technique : support@schoolreportmanager.example.com
- Site web : https://www.schoolreportmanager.example.com
