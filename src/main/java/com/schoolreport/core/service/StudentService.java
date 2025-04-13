package com.schoolreport.core.service;

import com.schoolreport.core.model.Student;
import java.util.List;
import java.util.Optional;

/**
 * Interface de service pour la gestion des étudiants
 */
public interface StudentService {
    
    /**
     * Sauvegarde un étudiant
     * @param student l'étudiant à sauvegarder
     * @return l'étudiant sauvegardé avec son ID généré
     */
    Student saveStudent(Student student);
    
    /**
     * Récupère un étudiant par son ID
     * @param id l'ID de l'étudiant
     * @return Optional contenant l'étudiant ou vide si non trouvé
     */
    Optional<Student> getStudentById(Long id);
    
    /**
     * Récupère tous les étudiants
     * @return la liste de tous les étudiants
     */
    List<Student> getAllStudents();
    
    /**
     * Récupère tous les étudiants d'une classe
     * @param classId l'ID de la classe
     * @return la liste des étudiants de cette classe
     */
    List<Student> getStudentsByClassId(Long classId);
    
    /**
     * Récupère les étudiants d'une classe avec pagination
     * @param classId l'ID de la classe
     * @param page la page à récupérer (commence à 0)
     * @param size le nombre d'étudiants par page
     * @return la liste des étudiants pour cette page
     */
    List<Student> getStudentsByClassIdPaginated(Long classId, int page, int size);
    
    /**
     * Compte le nombre total d'étudiants dans une classe
     * @param classId l'ID de la classe
     * @return le nombre d'étudiants
     */
    long countStudentsByClassId(Long classId);
    
    /**
     * Supprime un étudiant
     * @param id l'ID de l'étudiant à supprimer
     */
    void deleteStudent(Long id);
    
    /**
     * Met à jour un étudiant existant
     * @param student l'étudiant avec les informations mises à jour
     * @return l'étudiant mis à jour
     */
    Student updateStudent(Student student);
}
