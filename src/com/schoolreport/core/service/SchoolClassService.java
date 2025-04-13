package com.schoolreport.core.service;

import com.schoolreport.core.model.SchoolClass;
import java.util.List;
import java.util.Optional;

/**
 * Interface de service pour la gestion des classes scolaires
 */
public interface SchoolClassService {
    
    /**
     * Sauvegarde une classe
     * @param schoolClass la classe à sauvegarder
     * @return la classe sauvegardée avec son ID généré
     */
    SchoolClass saveSchoolClass(SchoolClass schoolClass);
    
    /**
     * Récupère une classe par son ID
     * @param id l'ID de la classe
     * @return Optional contenant la classe ou vide si non trouvée
     */
    Optional<SchoolClass> getSchoolClassById(Long id);
    
    /**
     * Récupère toutes les classes
     * @return la liste de toutes les classes
     */
    List<SchoolClass> getAllSchoolClasses();
    
    /**
     * Récupère toutes les classes pour une année académique spécifique
     * @param academicYear l'année académique (ex: "2024-2025")
     * @return la liste des classes pour cette année académique
     */
    List<SchoolClass> getSchoolClassesByAcademicYear(String academicYear);
    
    /**
     * Met à jour une classe existante
     * @param schoolClass la classe avec les informations mises à jour
     * @return la classe mise à jour
     */
    SchoolClass updateSchoolClass(SchoolClass schoolClass);
    
    /**
     * Supprime une classe
     * @param id l'ID de la classe à supprimer
     */
    void deleteSchoolClass(Long id);
}
