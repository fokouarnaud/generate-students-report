package com.schoolreport.core.service;

import com.schoolreport.core.model.Grade;
import com.schoolreport.core.model.SchoolClass;
import com.schoolreport.core.model.Student;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Interface de service pour la génération des bulletins
 */
public interface ReportCardService {
    
    /**
     * Génère un bulletin pour un étudiant spécifique et un trimestre donné
     * @param student l'étudiant pour lequel générer le bulletin
     * @param trimester le numéro du trimestre
     * @param schoolYear l'année scolaire
     * @return le fichier PDF du bulletin généré
     */
    File generateStudentReportCard(Student student, int trimester, String schoolYear);
    
    /**
     * Génère des bulletins pour tous les étudiants d'une classe et un trimestre donné
     * @param schoolClass la classe pour laquelle générer les bulletins
     * @param trimester le numéro du trimestre
     * @param schoolYear l'année scolaire
     * @return un dossier contenant tous les bulletins générés
     */
    File generateClassReportCards(SchoolClass schoolClass, int trimester, String schoolYear);
    
    /**
     * Génère un bulletin pour un étudiant avec les notes fournies
     * @param student l'étudiant pour lequel générer le bulletin
     * @param grades la liste des notes pour cet étudiant
     * @param trimester le numéro du trimestre
     * @param schoolYear l'année scolaire
     * @return le fichier PDF du bulletin généré
     */
    File generateStudentReportCardWithGrades(Student student, List<Grade> grades, int trimester, String schoolYear);
    
    /**
     * Exporte les bulletins d'une classe en un seul fichier PDF
     * @param schoolClass la classe pour laquelle exporter les bulletins
     * @param trimester le numéro du trimestre
     * @param schoolYear l'année scolaire
     * @return le fichier PDF contenant tous les bulletins fusionnés
     */
    File exportClassReportCardsAsSinglePDF(SchoolClass schoolClass, int trimester, String schoolYear);
    
    /**
     * Exporte les bulletins d'une classe vers un format spécifique
     * @param schoolClass la classe pour laquelle exporter les bulletins
     * @param trimester le numéro du trimestre
     * @param schoolYear l'année scolaire
     * @param exportType le type d'export (PDF, ZIP)
     * @return le fichier contenant les bulletins exportés
     */
    File exportClassReportCards(SchoolClass schoolClass, int trimester, String schoolYear, String exportType);
    
    /**
     * Calcule les statistiques de la classe pour un trimestre donné
     * @param schoolClass la classe pour laquelle calculer les statistiques
     * @param trimester le numéro du trimestre
     * @return un objet contenant les statistiques (moyennes, minimums, maximums, etc.)
     */
    Map<String, Object> calculateClassStatistics(SchoolClass schoolClass, int trimester);
    
    /**
     * Configure le template utilisé pour les bulletins
     * @param templateFile le fichier de template
     * @return true si la configuration a réussi, false sinon
     */
    boolean configureReportCardTemplate(File templateFile);
}
