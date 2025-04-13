@echo off
echo ===============================================
echo Construction et packaging de SchoolReportManager
echo ===============================================

REM Vérification de Java
java -version || (
    echo Erreur: Java n'est pas installé ou n'est pas dans le PATH.
    exit /b 1
)

REM Vérification de Maven
mvn -version || (
    echo Erreur: Maven n'est pas installé ou n'est pas dans le PATH.
    exit /b 1
)

REM Nettoyage et génération du package
echo.
echo 1. Nettoyage et compilation du projet...
call mvn clean package || (
    echo Erreur lors de la compilation du projet.
    exit /b 1
)

REM Génération du runtime avec jlink
echo.
echo 2. Génération du runtime Java personnalisé...
call mvn javafx:jlink || (
    echo Erreur lors de la génération du runtime personnalisé.
    exit /b 1
)

REM Création de l'installateur
echo.
echo 3. Création de l'installateur Windows...
call mvn -P dist-win exec:exec || (
    echo Erreur lors de la création de l'installateur.
    exit /b 1
)

echo.
echo ============================================
echo Installation terminée avec succès!
echo.
echo Vous trouverez l'installateur dans le dossier:
echo %CD%\target\installer
echo ============================================
echo.
