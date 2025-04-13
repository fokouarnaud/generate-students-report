package com.schoolreport.core.service;

import com.schoolreport.core.model.Subject;
import java.util.List;
import java.util.Optional;

/**
 * Interface de service pour la gestion des matières
 */
public interface SubjectService {
    
    /**
     * Sauvegarde une matière
     * @param subject la matière à sauvegarder
     * @return la matière sauvegardée avec son ID généré
     */
    Subject saveSubject(Subject subject);
    
    /**
     * Récupère une matière par son ID
     * @param id l'ID de la matière
     * @return Optional contenant la matière ou vide si non trouvée
     */
    Optional<Subject> getSubjectById(Long id);
    
    /**
     * Récupère toutes les matières
     * @return la liste de toutes les matières
     */
    List<Subject> getAllSubjects();
    
    /**
     * Récupère une matière par son nom
     * @param name le nom de la matière
     * @return Optional contenant la matière ou vide si non trouvée
     */
    Optional<Subject> getSubjectByName(String name);
    
    /**
     * Met à jour une matière existante
     * @param subject la matière avec les informations mises à jour
     * @return la matière mise à jour
     */
    Subject updateSubject(Subject subject);
    
    /**
     * Supprime une matière
     * @param id l'ID de la matière à supprimer
     */
    void deleteSubject(Long id);
}
