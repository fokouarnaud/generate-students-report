package com.schoolreport.service.impl;

import com.schoolreport.core.model.Grade;
import com.schoolreport.core.model.School;
import com.schoolreport.core.model.SchoolClass;
import com.schoolreport.core.model.Student;
import com.schoolreport.core.service.GradeService;
import com.schoolreport.core.service.ReportCardService;
import com.schoolreport.core.service.SchoolService;
import com.schoolreport.core.service.StudentService;
import com.schoolreport.export.util.PDFGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Implémentation du service de génération des bulletins
 */
public class ReportCardServiceImpl implements ReportCardService {
    
    private static final Logger LOGGER = Logger.getLogger(ReportCardServiceImpl.class.getName());
    
    private final GradeService gradeService;
    private final StudentService studentService;
    private final SchoolService schoolService;
    private String outputDirectory;
    private File templateFile;
    
    public ReportCardServiceImpl(GradeService gradeService, StudentService studentService, SchoolService schoolService) {
        this.gradeService = gradeService;
        this.studentService = studentService;
        this.schoolService = schoolService;
        this.outputDirectory = System.getProperty("user.home") + File.separator + "bulletins";
        
        // Créer le répertoire de sortie s'il n'existe pas
        File dir = new File(outputDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    /**
     * Définit le répertoire de sortie pour les bulletins
     * @param outputDirectory le répertoire de sortie
     */
    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
        File dir = new File(outputDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    @Override
    public File generateStudentReportCard(Student student, int trimester, String schoolYear) {
        List<Grade> grades = gradeService.getGradesByStudentIdAndTrimester(student.getId(), trimester);
        return generateStudentReportCardWithGrades(student, grades, trimester, schoolYear);
    }
    
    @Override
    public File generateClassReportCards(SchoolClass schoolClass, int trimester, String schoolYear) {
        LOGGER.info("Début de la génération des bulletins pour la classe: " + schoolClass.getName());
        
        // Créer un répertoire spécifique pour cette classe
        String classDir = outputDirectory + File.separator + 
                         "classe_" + schoolClass.getName() + 
                         "_T" + trimester + 
                         "_" + schoolYear.replace("/", "-");
        
        File outputDir = new File(classDir);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        
        // Récupérer l'école
        Optional<School> schoolOpt = schoolService.getDefaultSchool();
        School school = schoolOpt.orElse(new School("Lycée par défaut", "", "", "", ""));
        
        // Calculer les statistiques de classe
        Map<Long, Map<String, Object>> statisticsMap = new HashMap<>();
        statisticsMap.put(schoolClass.getId(), calculateClassStatistics(schoolClass, trimester));
        
        // Récupérer tous les élèves par lots
        int batchSize = 20; // Traiter 20 élèves à la fois
        List<Student> allStudents = studentService.getStudentsByClassId(schoolClass.getId());
        LOGGER.info("Nombre total d'élèves dans la classe: " + allStudents.size());
        
        // Traiter les élèves par lots pour éviter les problèmes de mémoire
        int totalProcessed = 0;
        Map<Long, List<Grade>> gradesMap = new HashMap<>();
        
        for (int i = 0; i < allStudents.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, allStudents.size());
            List<Student> batchStudents = allStudents.subList(i, endIndex);
            LOGGER.info("Traitement du lot " + (i/batchSize + 1) + ": élèves " + (i+1) + " à " + endIndex);
            
            // Récupérer les notes pour ce lot d'étudiants
            gradesMap.clear(); // Vider la map avant de traiter un nouveau lot
            
            for (Student student : batchStudents) {
                try {
                    List<Grade> grades = gradeService.getGradesByStudentIdAndTrimester(student.getId(), trimester);
                    gradesMap.put(student.getId(), grades);
                    
                    // Générer le bulletin individuel
                    String fileName = "bulletin_" + student.getLastName() + "_" + student.getFirstName() + 
                                    "_T" + trimester + ".pdf";
                    File outputFile = new File(outputDir, fileName);
                    
                    PDFGenerator.generateReportCard(student, grades, trimester, schoolYear, school, 
                                                 statisticsMap.get(student.getSchoolClass().getId()), outputFile);
                    
                    totalProcessed++;
                    LOGGER.fine("Bulletin généré pour: " + student.getLastName() + " " + student.getFirstName());
                    
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Erreur lors de la génération du bulletin pour l'étudiant: " + 
                              student.getLastName() + " " + student.getFirstName(), e);
                }
            }
            
            // Forcer le garbage collector pour libérer la mémoire
            gradesMap.clear();
            System.gc();
            
            LOGGER.info("Lot " + (i/batchSize + 1) + " terminé. " + totalProcessed + "/" + allStudents.size() + " bulletins générés.");
        }
        
        LOGGER.info("Génération des bulletins terminée. " + totalProcessed + " bulletins générés au total.");
        return outputDir;
    }
    
    @Override
    public File generateStudentReportCardWithGrades(Student student, List<Grade> grades, int trimester, String schoolYear) {
        // Créer le nom du fichier
        String fileName = outputDirectory + File.separator + 
                         "bulletin_" + student.getLastName() + "_" + student.getFirstName() + 
                         "_T" + trimester + ".pdf";
        
        File outputFile = new File(fileName);
        
        // Récupérer l'école
        Optional<School> schoolOpt = schoolService.getDefaultSchool();
        School school = schoolOpt.orElse(new School("Lycée par défaut", "", "", "", ""));
        
        // Récupérer les statistiques de classe
        Map<String, Object> statistics = calculateClassStatistics(student.getSchoolClass(), trimester);
        
        // Générer le PDF
        return PDFGenerator.generateReportCard(student, grades, trimester, schoolYear, school, 
                                             statistics, outputFile);
    }
    
    @Override
    public File exportClassReportCardsAsSinglePDF(SchoolClass schoolClass, int trimester, String schoolYear) {
        LOGGER.info("Début de l'exportation des bulletins en un seul PDF pour la classe: " + schoolClass.getName());
        
        // Générer d'abord tous les bulletins individuels
        File bulletinsDir = generateClassReportCards(schoolClass, trimester, schoolYear);
        
        // Créer le nom du fichier pour le PDF combiné
        String combinedPdfFileName = outputDirectory + File.separator + 
                                    "bulletins_combines_" + schoolClass.getName() + 
                                    "_T" + trimester + 
                                    "_" + schoolYear.replace("/", "-") + ".pdf";
        
        File combinedPdfFile = new File(combinedPdfFileName);
        
        try {
            // Trouver tous les fichiers PDF dans le répertoire
            File[] pdfFiles = bulletinsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
            
            if (pdfFiles != null && pdfFiles.length > 0) {
                LOGGER.info("Fusion de " + pdfFiles.length + " bulletins en un seul PDF");
                
                // Trier les fichiers par nom pour un ordre cohérent
                Arrays.sort(pdfFiles, (f1, f2) -> f1.getName().compareTo(f2.getName()));
                
                // Fusionner tous les PDF en un seul
                PDFGenerator.mergePDFs(Arrays.asList(pdfFiles), combinedPdfFile);
                
                LOGGER.info("PDF combiné créé avec succès: " + combinedPdfFile.getAbsolutePath());
                return combinedPdfFile;
            } else {
                LOGGER.warning("Aucun fichier PDF trouvé dans le répertoire: " + bulletinsDir.getAbsolutePath());
                throw new RuntimeException("Aucun bulletin à combiner n'a été trouvé");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la fusion des PDF", e);
            throw new RuntimeException("Erreur lors de la création du PDF combiné: " + e.getMessage(), e);
        }
    }
    
    @Override
    public File exportClassReportCards(SchoolClass schoolClass, int trimester, String schoolYear, String exportType) {
        // Générer d'abord tous les bulletins
        File bulletinsDir = generateClassReportCards(schoolClass, trimester, schoolYear);
        
        if ("ZIP".equalsIgnoreCase(exportType)) {
            // Créer un fichier ZIP contenant tous les bulletins
            String zipFileName = outputDirectory + File.separator + 
                               "bulletins_" + schoolClass.getName() + 
                               "_T" + trimester + 
                               "_" + schoolYear.replace("/", "-") + ".zip";
            
            try {
                LOGGER.info("Création du fichier ZIP: " + zipFileName);
                FileOutputStream fos = new FileOutputStream(zipFileName);
                ZipOutputStream zos = new ZipOutputStream(fos);
                
                File[] files = bulletinsDir.listFiles();
                int count = 0;
                
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile() && file.getName().toLowerCase().endsWith(".pdf")) {
                            ZipEntry zipEntry = new ZipEntry(file.getName());
                            zos.putNextEntry(zipEntry);
                            
                            byte[] bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                            zos.write(bytes, 0, bytes.length);
                            zos.closeEntry();
                            count++;
                            
                            if (count % 20 == 0) {
                                LOGGER.info(count + " fichiers ajoutés au ZIP...");
                            }
                        }
                    }
                }
                
                zos.close();
                LOGGER.info("Fichier ZIP créé avec succès. " + count + " bulletins inclus.");
                return new File(zipFileName);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erreur lors de la création du fichier ZIP", e);
                throw new RuntimeException("Erreur lors de la création du fichier ZIP", e);
            }
        } else {
            // Par défaut, retourner le répertoire contenant les PDF
            return bulletinsDir;
        }
    }
    
    @Override
    public Map<String, Object> calculateClassStatistics(SchoolClass schoolClass, int trimester) {
        Map<String, Object> statistics = new HashMap<>();
        
        try {
            // Calculer la moyenne générale de la classe
            List<Student> students = studentService.getStudentsByClassId(schoolClass.getId());
            double totalAverage = 0.0;
            int count = 0;
            
            // Si trop d'élèves, échantillonner pour performance
            List<Student> sampleStudents = students.size() > 30 ? 
                                         new ArrayList<>(students.subList(0, 30)) : 
                                         students;
            
            for (Student student : sampleStudents) {
                try {
                    double studentAverage = gradeService.calculateStudentAverage(student.getId(), trimester);
                    if (studentAverage > 0) {
                        totalAverage += studentAverage;
                        count++;
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Erreur de calcul de moyenne pour l'étudiant " + student.getId(), e);
                }
            }
            
            double classAverage = count > 0 ? totalAverage / count : 0.0;
            statistics.put("classAverage", classAverage);
            statistics.put("sampleSize", count);
            statistics.put("totalStudents", students.size());
            
            LOGGER.info("Statistiques calculées pour la classe " + schoolClass.getName() + 
                       ": moyenne=" + classAverage + ", échantillon=" + count + "/" + students.size());
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur lors du calcul des statistiques pour la classe " + schoolClass.getId(), e);
            statistics.put("classAverage", 0.0);
            statistics.put("sampleSize", 0);
            statistics.put("totalStudents", 0);
        }
        
        return statistics;
    }
    
    @Override
    public boolean configureReportCardTemplate(File templateFile) {
        if (templateFile != null && templateFile.exists() && templateFile.isFile()) {
            this.templateFile = templateFile;
            return true;
        }
        return false;
    }
}
