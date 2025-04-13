package com.schoolreport.core.service;

import com.schoolreport.core.model.School;
import java.util.List;
import java.util.Optional;

/**
 * Interface de service pour la gestion des établissements scolaires
 */
public interface SchoolService {
    
    /**
     * Sauvegarde un établissement
     * @param school l'établissement à sauvegarder
     * @return l'établissement sauvegardé avec son ID généré
     */
    School saveSchool(School school);
    
    /**
     * Récupère un établissement par son ID
     * @param id l'ID de l'établissement
     * @return Optional contenant l'établissement ou vide si non trouvé
     */
    Optional<School> getSchoolById(Long id);
    
    /**
     * Récupère tous les établissements
     * @return la liste de tous les établissements
     */
    List<School> getAllSchools();
    
    /**
     * Récupère l'établissement par défaut actuel
     * @return Optional contenant l'établissement par défaut ou vide si non défini
     */
    Optional<School> getDefaultSchool();
    
    /**
     * Définit un établissement comme l'établissement par défaut
     * @param id l'ID de l'établissement à définir comme défaut
     * @return l'établissement défini comme défaut
     */
    School setDefaultSchool(Long id);
    
    /**
     * Met à jour un établissement existant
     * @param school l'établissement avec les informations mises à jour
     * @return l'établissement mis à jour
     */
    School updateSchool(School school);
    
    /**
     * Supprime un établissement
     * @param id l'ID de l'établissement à supprimer
     */
    void deleteSchool(Long id);
}
