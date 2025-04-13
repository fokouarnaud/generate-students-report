package com.schoolreport.core.service;

import com.schoolreport.core.model.Grade;
import java.util.List;
import java.util.Optional;

/**
 * Interface de service pour la gestion des notes
 */
public interface GradeService {
    
    /**
     * Sauvegarde une note
     * @param grade la note à sauvegarder
     * @return la note sauvegardée avec son ID généré
     */
    Grade saveGrade(Grade grade);
    
    /**
     * Récupère une note par son ID
     * @param id l'ID de la note
     * @return Optional contenant la note ou vide si non trouvée
     */
    Optional<Grade> getGradeById(Long id);
    
    /**
     * Récupère toutes les notes d'un étudiant
     * @param studentId l'ID de l'étudiant
     * @return la liste des notes de cet étudiant
     */
    List<Grade> getGradesByStudentId(Long studentId);
    
    /**
     * Récupère toutes les notes d'un étudiant pour un trimestre spécifique
     * @param studentId l'ID de l'étudiant
     * @param trimester le numéro du trimestre
     * @return la liste des notes de cet étudiant pour ce trimestre
     */
    List<Grade> getGradesByStudentIdAndTrimester(Long studentId, int trimester);
    
    /**
     * Récupère toutes les notes des étudiants d'une classe pour un trimestre spécifique
     * @param classId l'ID de la classe
     * @param trimester le numéro du trimestre
     * @return la liste des notes pour les étudiants de cette classe et ce trimestre
     */
    List<Grade> getGradesByClassIdAndTrimester(Long classId, int trimester);
    
    /**
     * Met à jour une note existante
     * @param grade la note avec les informations mises à jour
     * @return la note mise à jour
     */
    Grade updateGrade(Grade grade);
    
    /**
     * Supprime une note
     * @param id l'ID de la note à supprimer
     */
    void deleteGrade(Long id);
    
    /**
     * Calcule la moyenne générale d'un étudiant pour un trimestre spécifique
     * @param studentId l'ID de l'étudiant
     * @param trimester le numéro du trimestre
     * @return la moyenne générale de l'étudiant
     */
    double calculateStudentAverage(Long studentId, int trimester);
    
    /**
     * Calcule la moyenne de classe pour une matière et un trimestre spécifiques
     * @param classId l'ID de la classe
     * @param subjectId l'ID de la matière
     * @param trimester le numéro du trimestre
     * @return la moyenne de la classe pour cette matière
     */
    double calculateClassAverageBySubject(Long classId, Long subjectId, int trimester);
}
