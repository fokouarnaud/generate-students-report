package com.schoolreport.ui;

import com.schoolreport.persistence.util.DatabaseManager;
import com.schoolreport.persistence.util.FakeDataGenerator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Point d'entrée principal de l'application
 */
public class SchoolReportApplication extends Application {
    
    private static final Logger LOGGER = Logger.getLogger(SchoolReportApplication.class.getName());
    private static final int NUM_STUDENTS = 112; // Nombre exact d'élèves à générer
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialiser la base de données
            String dbPath = System.getProperty("user.home") + File.separator + "schoolreport" + File.separator + "schooldb";
            // Assurer que le répertoire parent existe
            new File(dbPath).getParentFile().mkdirs();
            
            DatabaseManager.initialize(dbPath);
            
            // Demander à l'utilisateur s'il souhaite générer de nouvelles données
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Initialisation des données");
            alert.setHeaderText("Génération des données fictives");
            alert.setContentText("Voulez-vous générer " + NUM_STUDENTS + " élèves avec des données fictives ?\n" +
                                "Cela remplacera toutes les données existantes.");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    generateFakeDataWithProgress(primaryStage);
                } else {
                    // Charger directement l'interface sans générer de données
                    loadMainInterface(primaryStage);
                }
            });
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error starting application", e);
            showErrorAlert("Erreur de démarrage", "Une erreur est survenue au démarrage de l'application", 
                         e.getMessage());
        }
    }
    
    private void generateFakeDataWithProgress(Stage primaryStage) {
        try {
            // Créer une fenêtre de progression
            Stage progressStage = new Stage(StageStyle.UTILITY);
            progressStage.initModality(Modality.APPLICATION_MODAL);
            progressStage.setTitle("Génération des données");
            progressStage.setResizable(false);
            
            ProgressBar progressBar = new ProgressBar();
            progressBar.setPrefWidth(300);
            progressBar.setProgress(-1); // Indéterminé
            
            Label statusLabel = new Label("Génération de " + NUM_STUDENTS + " élèves en cours...");
            
            VBox root = new VBox(10);
            root.setStyle("-fx-padding: 20px; -fx-background-color: white;");
            root.getChildren().addAll(statusLabel, progressBar);
            
            Scene scene = new Scene(root);
            progressStage.setScene(scene);
            progressStage.show();
            
            // Créer une tâche en arrière-plan pour générer les données
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    // Générer les données fictives
                    FakeDataGenerator.generateFakeData(NUM_STUDENTS);
                    return null;
                }
            };
            
            task.setOnSucceeded(e -> {
                progressStage.close();
                loadMainInterface(primaryStage);
            });
            
            task.setOnFailed(e -> {
                progressStage.close();
                Throwable exception = task.getException();
                LOGGER.log(Level.SEVERE, "Error generating fake data", exception);
                showErrorAlert("Erreur de génération des données", 
                             "Une erreur est survenue lors de la génération des données fictives", 
                             exception.getMessage());
                
                // Charger quand même l'interface
                loadMainInterface(primaryStage);
            });
            
            // Démarrer la tâche
            new Thread(task).start();
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating fake data", e);
            showErrorAlert("Erreur de génération des données", 
                         "Une erreur est survenue lors de la génération des données fictives", 
                         e.getMessage());
            
            // Charger quand même l'interface
            loadMainInterface(primaryStage);
        }
    }
    
    private void loadMainInterface(Stage primaryStage) {
        try {
            // Charger l'interface principale
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainView.fxml"));
            
            // Charger le fichier CSS
            Scene scene = new Scene(root, 1024, 768);
            scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
            
            primaryStage.setTitle("Gestion des Bulletins Scolaires - 112 Élèves");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading main interface", e);
            showErrorAlert("Erreur de chargement", 
                         "Une erreur est survenue lors du chargement de l'interface", 
                         e.getMessage());
        }
    }
    
    private void showErrorAlert(String title, String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
    
    @Override
    public void stop() {
        // Fermer proprement la connexion à la base de données
        DatabaseManager.shutdown();
        LOGGER.info("Application stopped");
    }
    
    /**
     * Point d'entrée principal
     * @param args arguments de ligne de commande
     */
    public static void main(String[] args) {
        launch(args);
    }
}
