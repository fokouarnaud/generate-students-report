package com.schoolreport.service.impl;

import com.schoolreport.core.model.School;
import com.schoolreport.core.service.SchoolService;
import com.schoolreport.persistence.repository.SchoolRepository;
import com.schoolreport.persistence.util.DatabaseManager;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implémentation du service de gestion des établissements scolaires
 */
public class SchoolServiceImpl implements SchoolService {
    
    private static final Logger LOGGER = Logger.getLogger(SchoolServiceImpl.class.getName());
    private final SchoolRepository schoolRepository;
    
    public SchoolServiceImpl() {
        EntityManager em = DatabaseManager.getEntityManager();
        this.schoolRepository = new SchoolRepository(em);
    }
    
    @Override
    public School saveSchool(School school) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            DatabaseManager.beginTransaction(em);
            School savedSchool = schoolRepository.save(school);
            DatabaseManager.commitTransaction(em);
            return savedSchool;
        } catch (Exception e) {
            DatabaseManager.rollbackTransaction(em);
            LOGGER.log(Level.SEVERE, "Error saving school", e);
            throw new RuntimeException("Error saving school", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public Optional<School> getSchoolById(Long id) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            return schoolRepository.findById(id);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<School> getAllSchools() {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            return schoolRepository.findAll();
        } finally {
            em.close();
        }
    }
    
    @Override
    public Optional<School> getDefaultSchool() {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            return schoolRepository.findDefault();
        } finally {
            em.close();
        }
    }
    
    @Override
    public School setDefaultSchool(Long id) {
        // Dans une implémentation réelle, vous auriez une table de configuration
        // pour stocker l'ID de l'école par défaut
        // Pour simplifier, nous supposons que l'école avec cet ID devient la nouvelle école par défaut
        Optional<School> schoolOpt = getSchoolById(id);
        if (schoolOpt.isPresent()) {
            return schoolOpt.get();
        } else {
            throw new IllegalArgumentException("School with ID " + id + " not found");
        }
    }
    
    @Override
    public School updateSchool(School school) {
        if (school.getId() == null) {
            throw new IllegalArgumentException("School ID cannot be null for update operation");
        }
        return saveSchool(school);
    }
    
    @Override
    public void deleteSchool(Long id) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            DatabaseManager.beginTransaction(em);
            schoolRepository.delete(id);
            DatabaseManager.commitTransaction(em);
        } catch (Exception e) {
            DatabaseManager.rollbackTransaction(em);
            LOGGER.log(Level.SEVERE, "Error deleting school", e);
            throw new RuntimeException("Error deleting school", e);
        } finally {
            em.close();
        }
    }
}
