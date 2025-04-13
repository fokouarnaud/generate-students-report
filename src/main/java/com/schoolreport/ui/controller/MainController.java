package com.schoolreport.ui.controller;

import com.schoolreport.core.model.Grade;
import com.schoolreport.core.model.SchoolClass;
import com.schoolreport.core.model.Student;
import com.schoolreport.core.service.GradeService;
import com.schoolreport.core.service.ReportCardService;
import com.schoolreport.core.service.SchoolClassService;
import com.schoolreport.core.service.StudentService;
import com.schoolreport.service.impl.GradeServiceImpl;
import com.schoolreport.service.impl.ReportCardServiceImpl;
import com.schoolreport.service.impl.SchoolClassServiceImpl;
import com.schoolreport.service.impl.SchoolServiceImpl;
import com.schoolreport.service.impl.StudentServiceImpl;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Contrôleur principal de l'application
 */
public class MainController implements Initializable {
    
    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());
    private static final int PAGE_SIZE = 20; // Nombre d'élèves par page
    
    @FXML
    private ComboBox<SchoolClass> classComboBox;
    
    @FXML
    private ComboBox<Integer> trimesterComboBox;
    
    @FXML
    private ComboBox<String> schoolYearComboBox;
    
    @FXML
    private Label studentCountLabel;
    
    @FXML
    private TableView<Student> studentTableView;
    
    @FXML
    private TableColumn<Student, String> studentNameColumn;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private Button prevPageButton;
    
    @FXML
    private Label paginationLabel;
    
    @FXML
    private Button nextPageButton;
    
    @FXML
    private TableView<Grade> gradeTableView;
    
    @FXML
    private TableColumn<Grade, String> subjectColumn;
    
    @FXML
    private TableColumn<Grade, String> sequence1Column;
    
    @FXML
    private TableColumn<Grade, String> sequence2Column;
    
    @FXML
    private TableColumn<Grade, String> averageColumn;
    
    @FXML
    private Button generateReportButton;
    
    @FXML
    private Button generateClassReportsButton;
    
    @FXML
    private Button exportZipButton;
    
    @FXML
    private Button exportSinglePdfButton;
    
    @FXML
    private ProgressBar progressBar;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private VBox mainContainer;
    
    private final StudentService studentService;
    private final SchoolClassService schoolClassService;
    private final GradeService gradeService;
    private final ReportCardService reportCardService;
    
    // Variables pour la pagination
    private int currentPage = 0;
    private int totalPages = 0;
    private long totalStudents = 0;
    private List<Student> allStudents;
    
    public MainController() {
        // Initialiser les services
        studentService = new StudentServiceImpl();
        schoolClassService = new SchoolClassServiceImpl();
        gradeService = new GradeServiceImpl();
        reportCardService = new ReportCardServiceImpl(
                gradeService, 
                studentService, 
                new SchoolServiceImpl()
        );
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialiser les composants UI
        setupComboBoxes();
        setupTableViews();
        setupSearchField();
        setupEventHandlers();
        setupPagination();
        loadInitialData();
    }
    
    private void setupComboBoxes() {
        // Configurer les ComboBox
        trimesterComboBox.setItems(FXCollections.observableArrayList(1, 2, 3));
        trimesterComboBox.getSelectionModel().selectFirst();
        
        // Années scolaires (les 5 dernières années)
        ObservableList<String> schoolYears = FXCollections.observableArrayList();
        int currentYear = java.time.Year.now().getValue();
        
        // Ajouter l'année 2022/2023 en premier pour qu'elle soit sélectionnée par défaut
        schoolYears.add("2022/2023");
        
        for (int i = 0; i < 5; i++) {
            String yearStr = (currentYear - i) + "/" + (currentYear - i + 1);
            if (!yearStr.equals("2022/2023")) { // Éviter les doublons
                schoolYears.add(yearStr);
            }
        }
        
        schoolYearComboBox.setItems(schoolYears);
        schoolYearComboBox.getSelectionModel().selectFirst();
    }
    
    private void setupTableViews() {
        // Configurer les colonnes du TableView des étudiants
        studentNameColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getLastName() + " " + cellData.getValue().getFirstName()));
        
        // Configurer les colonnes du TableView des notes
        subjectColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getSubject().getName()));
        
        sequence1Column.setCellValueFactory(cellData -> {
            Double value = cellData.getValue().getSequence1Value();
            return new SimpleStringProperty(value != null ? String.format("%.2f", value) : "-");
        });
        
        sequence2Column.setCellValueFactory(cellData -> {
            Double value = cellData.getValue().getSequence2Value();
            return new SimpleStringProperty(value != null ? String.format("%.2f", value) : "-");
        });
        
        averageColumn.setCellValueFactory(cellData -> {
            Double value = cellData.getValue().getTermAverage();
            return new SimpleStringProperty(value != null ? String.format("%.2f", value) : "-");
        });
        
        // Ajouter un listener pour charger les notes lorsqu'un étudiant est sélectionné
        studentTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadStudentGrades(newVal);
            } else {
                gradeTableView.getItems().clear();
            }
        });
    }
    
    private void setupSearchField() {
        // Configurer le champ de recherche
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (allStudents != null) {
                filterStudents(newVal);
            }
        });
    }
    
    private void setupEventHandlers() {
        // Ajouter des listeners pour les ComboBox
        classComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadStudentsForClass(newVal);
            }
        });
        
        trimesterComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            Student selectedStudent = studentTableView.getSelectionModel().getSelectedItem();
            if (selectedStudent != null) {
                loadStudentGrades(selectedStudent);
            }
        });
        
        schoolYearComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            // Recharger les données si l'année scolaire change
            SchoolClass selectedClass = classComboBox.getValue();
            if (selectedClass != null) {
                loadStudentsForClass(selectedClass);
            }
        });
    }
    
    private void setupPagination() {
        // Initialiser les contrôles de pagination
        prevPageButton.setDisable(true);
        nextPageButton.setDisable(true);
        paginationLabel.setText("Page 0 / 0");
    }
    
    private void loadInitialData() {
        try {
            showStatus("Chargement des classes...");
            
            // Charger les classes disponibles
            List<SchoolClass> classes = schoolClassService.getAllSchoolClasses();
            
            if (classes.isEmpty()) {
                showStatus("Aucune classe trouvée. Veuillez générer des données d'abord.");
                LOGGER.warning("Aucune classe trouvée dans la base de données");
                return;
            }
            
            LOGGER.info("Classes disponibles: " + classes.size());
            for (SchoolClass cls : classes) {
                LOGGER.info(" - " + cls.getName() + " (" + cls.getAcademicYear() + ")");
            }
            
            classComboBox.setItems(FXCollections.observableArrayList(classes));
            
            // Sélectionner la première classe si disponible
            if (!classes.isEmpty()) {
                classComboBox.getSelectionModel().selectFirst();
            }
            
            showStatus("Prêt");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading initial data", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement des données", 
                    "Une erreur est survenue lors du chargement des données initiales: " + e.getMessage());
        }
    }
    
    private void loadStudentsForClass(SchoolClass schoolClass) {
        try {
            showStatus("Chargement des élèves de la classe " + schoolClass.getName() + "...");
            
            // Utiliser l'année scolaire sélectionnée
            String selectedYear = schoolYearComboBox.getValue();
            LOGGER.info("Année scolaire sélectionnée: " + selectedYear);
            LOGGER.info("Année académique de la classe: " + schoolClass.getAcademicYear());
            
            // Charger tous les étudiants de la classe (sans pagination)
            allStudents = studentService.getStudentsByClassId(schoolClass.getId());
            totalStudents = allStudents.size();
            
            LOGGER.info("Nombre d'élèves trouvés pour la classe " + schoolClass.getName() + ": " + totalStudents);
            
            // Configurer la pagination
            totalPages = (int) Math.ceil((double) totalStudents / PAGE_SIZE);
            currentPage = 0;
            
            updatePaginationControls();
            displayCurrentPage();
            
            // Mettre à jour le label du nombre d'étudiants
            studentCountLabel.setText(totalStudents + " élèves");
            
            showStatus("Prêt - " + totalStudents + " élèves chargés");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading students for class", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement des étudiants", 
                    "Une erreur est survenue lors du chargement des étudiants: " + e.getMessage());
        }
    }
    
    private void displayCurrentPage() {
        if (allStudents == null || allStudents.isEmpty()) {
            studentTableView.setItems(FXCollections.observableArrayList());
            return;
        }
        
        int fromIndex = currentPage * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, allStudents.size());
        
        if (fromIndex < toIndex) {
            List<Student> pageStudents = allStudents.subList(fromIndex, toIndex);
            studentTableView.setItems(FXCollections.observableArrayList(pageStudents));
            
            // Sélectionner le premier étudiant si disponible
            if (!pageStudents.isEmpty()) {
                studentTableView.getSelectionModel().selectFirst();
            } else {
                gradeTableView.getItems().clear();
            }
        } else {
            studentTableView.setItems(FXCollections.observableArrayList());
            gradeTableView.getItems().clear();
        }
        
        // Mettre à jour le texte de pagination
        paginationLabel.setText("Page " + (currentPage + 1) + " / " + totalPages);
    }
    
    private void updatePaginationControls() {
        prevPageButton.setDisable(currentPage <= 0);
        nextPageButton.setDisable(currentPage >= totalPages - 1 || totalPages == 0);
    }
    
    private void filterStudents(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            // Aucun filtre, afficher la page normale
            displayCurrentPage();
            return;
        }
        
        // Filtre case insensitive
        String filter = searchText.toLowerCase();
        
        List<Student> filteredStudents = allStudents.stream()
                .filter(student -> 
                    student.getFirstName().toLowerCase().contains(filter) || 
                    student.getLastName().toLowerCase().contains(filter))
                .collect(Collectors.toList());
        
        studentTableView.setItems(FXCollections.observableArrayList(filteredStudents));
        
        // Mettre à jour les contrôles de pagination
        paginationLabel.setText("Recherche: " + filteredStudents.size() + " résultats");
        prevPageButton.setDisable(true);
        nextPageButton.setDisable(true);
    }
    
    @FXML
    private void handlePrevPageAction(ActionEvent event) {
        if (currentPage > 0) {
            currentPage--;
            displayCurrentPage();
            updatePaginationControls();
        }
    }
    
    @FXML
    private void handleNextPageAction(ActionEvent event) {
        if (currentPage < totalPages - 1) {
            currentPage++;
            displayCurrentPage();
            updatePaginationControls();
        }
    }
    
    private void loadStudentGrades(Student student) {
        try {
            int trimester = trimesterComboBox.getValue();
            String schoolYear = schoolYearComboBox.getValue();
            
            LOGGER.info("Chargement des notes pour l'élève ID=" + student.getId() + 
                        ", " + student.getLastName() + " " + student.getFirstName() + 
                        ", Trimestre=" + trimester + 
                        ", Année=" + schoolYear);
            
            // Afficher les informations de l'année académique de la classe
            LOGGER.info("Année académique de la classe de l'élève: " + student.getSchoolClass().getAcademicYear());
            
            List<Grade> grades = gradeService.getGradesByStudentIdAndTrimester(student.getId(), trimester);
            
            if (grades.isEmpty()) {
                LOGGER.warning("Aucune note trouvée pour cet élève au trimestre " + trimester);
                showStatus("Aucune note trouvée pour " + student.getLastName() + " " + student.getFirstName() + 
                          " au trimestre " + trimester);
            } else {
                LOGGER.info("Nombre de notes trouvées: " + grades.size());
                for (Grade grade : grades) {
                    LOGGER.fine(" - " + grade.getSubject().getName() + ": SEQ1=" + grade.getSequence1Value() + 
                              ", SEQ2=" + grade.getSequence2Value() + 
                              ", MOY=" + grade.getTermAverage());
                }
                showStatus("Notes chargées: " + grades.size() + " matières");
            }
            
            // Rafraîchir le tableau des notes
            gradeTableView.setItems(FXCollections.observableArrayList(grades));
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading grades for student", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement des notes", 
                    "Une erreur est survenue lors du chargement des notes: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleGenerateReportButtonAction(ActionEvent event) {
        Student selectedStudent = studentTableView.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Aucun étudiant sélectionné", 
                    "Veuillez sélectionner un étudiant pour générer son bulletin.");
            return;
        }
        
        try {
            progressBar.setVisible(true);
            progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            showStatus("Génération du bulletin en cours...");
            
            int trimester = trimesterComboBox.getValue();
            String schoolYear = schoolYearComboBox.getValue();
            
            // Exécuter dans un thread séparé pour ne pas bloquer l'interface
            Task<File> task = new Task<File>() {
                @Override
                protected File call() throws Exception {
                    return reportCardService.generateStudentReportCard(selectedStudent, trimester, schoolYear);
                }
            };
            
            task.setOnSucceeded(e -> {
                File reportFile = task.getValue();
                showStatus("Bulletin généré avec succès: " + reportFile.getAbsolutePath());
                progressBar.setVisible(false);
                
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Bulletin généré", 
                        "Le bulletin a été généré avec succès: " + reportFile.getAbsolutePath());
                
                // Ouvrir le fichier PDF avec l'application par défaut
                openFile(reportFile);
            });
            
            task.setOnFailed(e -> {
                progressBar.setVisible(false);
                showStatus("Erreur lors de la génération du bulletin");
                
                Throwable exception = task.getException();
                LOGGER.log(Level.SEVERE, "Error generating report card", exception);
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de génération du bulletin", 
                        "Une erreur est survenue lors de la génération du bulletin: " + exception.getMessage());
            });
            
            new Thread(task).start();
            
        } catch (Exception e) {
            progressBar.setVisible(false);
            LOGGER.log(Level.SEVERE, "Error generating report card", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de génération du bulletin", 
                    "Une erreur est survenue lors de la génération du bulletin: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleGenerateClassReportsButtonAction(ActionEvent event) {
        SchoolClass selectedClass = classComboBox.getValue();
        if (selectedClass == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Aucune classe sélectionnée", 
                    "Veuillez sélectionner une classe pour générer les bulletins.");
            return;
        }
        
        try {
            progressBar.setVisible(true);
            progressBar.setProgress(0);
            showStatus("Préparation de la génération des bulletins...");
            
            int trimester = trimesterComboBox.getValue();
            String schoolYear = schoolYearComboBox.getValue();
            
            // Désactiver les boutons pendant le traitement
            setButtonsEnabled(false);
            
            // Exécuter dans un thread séparé
            Task<File> task = new Task<File>() {
                @Override
                protected File call() throws Exception {
                    // Hooks pour mise à jour de la progression
                    AtomicInteger processed = new AtomicInteger(0);
                    
                    // Mise à jour de la progression toutes les 500ms
                    Thread progressThread = new Thread(() -> {
                        while (!isCancelled() && !isDone()) {
                            try {
                                Thread.sleep(500);
                                final int currentProcessed = processed.get();
                                final double progress = (double) currentProcessed / totalStudents;
                                
                                Platform.runLater(() -> {
                                    progressBar.setProgress(progress);
                                    showStatus("Génération en cours: " + currentProcessed + "/" + totalStudents + " bulletins");
                                });
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                    });
                    progressThread.setDaemon(true);
                    progressThread.start();
                    
                    // Générer les bulletins
                    File reportDir = reportCardService.generateClassReportCards(selectedClass, trimester, schoolYear);
                    
                    // Arrêter le thread de progression
                    progressThread.interrupt();
                    
                    return reportDir;
                }
            };
            
            task.setOnSucceeded(e -> {
                File reportDir = task.getValue();
                progressBar.setVisible(false);
                showStatus("Bulletins générés avec succès dans: " + reportDir.getAbsolutePath());
                setButtonsEnabled(true);
                
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Bulletins générés", 
                        "Les bulletins ont été générés avec succès dans le répertoire: " + reportDir.getAbsolutePath());
                
                // Ouvrir le répertoire
                openFile(reportDir);
            });
            
            task.setOnFailed(e -> {
                progressBar.setVisible(false);
                showStatus("Erreur lors de la génération des bulletins");
                setButtonsEnabled(true);
                
                Throwable exception = task.getException();
                LOGGER.log(Level.SEVERE, "Error generating class report cards", exception);
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de génération des bulletins", 
                        "Une erreur est survenue lors de la génération des bulletins: " + exception.getMessage());
            });
            
            new Thread(task).start();
            
        } catch (Exception e) {
            progressBar.setVisible(false);
            setButtonsEnabled(true);
            LOGGER.log(Level.SEVERE, "Error generating class report cards", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de génération des bulletins", 
                    "Une erreur est survenue lors de la génération des bulletins: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleExportZipButtonAction(ActionEvent event) {
        SchoolClass selectedClass = classComboBox.getValue();
        if (selectedClass == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Aucune classe sélectionnée", 
                    "Veuillez sélectionner une classe pour exporter les bulletins.");
            return;
        }
        
        try {
            progressBar.setVisible(true);
            progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            showStatus("Exportation des bulletins en ZIP en cours...");
            
            int trimester = trimesterComboBox.getValue();
            String schoolYear = schoolYearComboBox.getValue();
            
            // Désactiver les boutons pendant le traitement
            setButtonsEnabled(false);
            
            Task<File> task = new Task<File>() {
                @Override
                protected File call() throws Exception {
                    return reportCardService.exportClassReportCards(selectedClass, trimester, schoolYear, "ZIP");
                }
            };
            
            task.setOnSucceeded(e -> {
                File exportFile = task.getValue();
                progressBar.setVisible(false);
                showStatus("Bulletins exportés avec succès: " + exportFile.getAbsolutePath());
                setButtonsEnabled(true);
                
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Bulletins exportés", 
                        "Les bulletins ont été exportés avec succès: " + exportFile.getAbsolutePath());
                
                // Ouvrir le répertoire contenant le fichier ZIP
                openFile(exportFile.getParentFile());
            });
            
            task.setOnFailed(e -> {
                progressBar.setVisible(false);
                showStatus("Erreur lors de l'exportation des bulletins");
                setButtonsEnabled(true);
                
                Throwable exception = task.getException();
                LOGGER.log(Level.SEVERE, "Error exporting report cards", exception);
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur d'exportation des bulletins", 
                        "Une erreur est survenue lors de l'exportation des bulletins: " + exception.getMessage());
            });
            
            new Thread(task).start();
            
        } catch (Exception e) {
            progressBar.setVisible(false);
            setButtonsEnabled(true);
            LOGGER.log(Level.SEVERE, "Error exporting report cards", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur d'exportation des bulletins", 
                    "Une erreur est survenue lors de l'exportation des bulletins: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleExportSinglePdfButtonAction(ActionEvent event) {
        SchoolClass selectedClass = classComboBox.getValue();
        if (selectedClass == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Aucune classe sélectionnée", 
                    "Veuillez sélectionner une classe pour exporter les bulletins en PDF unique.");
            return;
        }
        
        try {
            progressBar.setVisible(true);
            progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            showStatus("Exportation des bulletins en PDF unique en cours...");
            
            int trimester = trimesterComboBox.getValue();
            String schoolYear = schoolYearComboBox.getValue();
            
            // Désactiver les boutons pendant le traitement
            setButtonsEnabled(false);
            
            Task<File> task = new Task<File>() {
                @Override
                protected File call() throws Exception {
                    return reportCardService.exportClassReportCardsAsSinglePDF(selectedClass, trimester, schoolYear);
                }
            };
            
            task.setOnSucceeded(e -> {
                File exportFile = task.getValue();
                progressBar.setVisible(false);
                showStatus("Bulletins exportés avec succès en un seul PDF: " + exportFile.getAbsolutePath());
                setButtonsEnabled(true);
                
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Bulletins exportés en PDF unique", 
                        "Les bulletins ont été exportés avec succès en un seul fichier PDF: " + exportFile.getAbsolutePath());
                
                // Ouvrir le PDF avec l'application par défaut
                openFile(exportFile);
            });
            
            task.setOnFailed(e -> {
                progressBar.setVisible(false);
                showStatus("Erreur lors de l'exportation des bulletins en PDF unique");
                setButtonsEnabled(true);
                
                Throwable exception = task.getException();
                LOGGER.log(Level.SEVERE, "Error exporting report cards as single PDF", exception);
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur d'exportation des bulletins en PDF unique", 
                        "Une erreur est survenue lors de l'exportation des bulletins: " + exception.getMessage());
            });
            
            new Thread(task).start();
            
        } catch (Exception e) {
            progressBar.setVisible(false);
            setButtonsEnabled(true);
            LOGGER.log(Level.SEVERE, "Error exporting report cards as single PDF", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur d'exportation des bulletins en PDF unique", 
                    "Une erreur est survenue lors de l'exportation des bulletins: " + e.getMessage());
        }
    }
    
    private void setButtonsEnabled(boolean enabled) {
        generateReportButton.setDisable(!enabled);
        generateClassReportsButton.setDisable(!enabled);
        exportZipButton.setDisable(!enabled);
        exportSinglePdfButton.setDisable(!enabled);
    }
    
    private void showStatus(String message) {
        statusLabel.setText(message);
        LOGGER.info(message);
    }
    
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
    
    private void openFile(File file) {
        try {
            if (file.exists()) {
                new ProcessBuilder("explorer.exe", file.getAbsolutePath()).start();
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not open file", e);
        }
    }
}
