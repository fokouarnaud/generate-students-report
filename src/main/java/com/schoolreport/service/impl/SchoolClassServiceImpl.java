package com.schoolreport.service.impl;

import com.schoolreport.core.model.SchoolClass;
import com.schoolreport.core.service.SchoolClassService;
import com.schoolreport.persistence.repository.SchoolClassRepository;
import com.schoolreport.persistence.util.DatabaseManager;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implémentation du service de gestion des classes scolaires
 */
public class SchoolClassServiceImpl implements SchoolClassService {
    
    private static final Logger LOGGER = Logger.getLogger(SchoolClassServiceImpl.class.getName());
    private final SchoolClassRepository schoolClassRepository;
    
    public SchoolClassServiceImpl() {
        EntityManager em = DatabaseManager.getEntityManager();
        this.schoolClassRepository = new SchoolClassRepository(em);
    }
    
    @Override
    public SchoolClass saveSchoolClass(SchoolClass schoolClass) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            DatabaseManager.beginTransaction(em);
            SchoolClass savedClass = schoolClassRepository.save(schoolClass);
            DatabaseManager.commitTransaction(em);
            return savedClass;
        } catch (Exception e) {
            DatabaseManager.rollbackTransaction(em);
            LOGGER.log(Level.SEVERE, "Error saving school class", e);
            throw new RuntimeException("Error saving school class", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public Optional<SchoolClass> getSchoolClassById(Long id) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            return schoolClassRepository.findById(id);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<SchoolClass> getAllSchoolClasses() {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            return schoolClassRepository.findAll();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<SchoolClass> getSchoolClassesByAcademicYear(String academicYear) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            return schoolClassRepository.findByAcademicYear(academicYear);
        } finally {
            em.close();
        }
    }
    
    @Override
    public SchoolClass updateSchoolClass(SchoolClass schoolClass) {
        if (schoolClass.getId() == null) {
            throw new IllegalArgumentException("School class ID cannot be null for update operation");
        }
        return saveSchoolClass(schoolClass);
    }
    
    @Override
    public void deleteSchoolClass(Long id) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            DatabaseManager.beginTransaction(em);
            schoolClassRepository.delete(id);
            DatabaseManager.commitTransaction(em);
        } catch (Exception e) {
            DatabaseManager.rollbackTransaction(em);
            LOGGER.log(Level.SEVERE, "Error deleting school class", e);
            throw new RuntimeException("Error deleting school class", e);
        } finally {
            em.close();
        }
    }
}
