package com.schoolreport.service.impl;

import com.schoolreport.core.model.Subject;
import com.schoolreport.core.service.SubjectService;
import com.schoolreport.persistence.repository.SubjectRepository;
import com.schoolreport.persistence.util.DatabaseManager;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implémentation du service de gestion des matières
 */
public class SubjectServiceImpl implements SubjectService {
    
    private static final Logger LOGGER = Logger.getLogger(SubjectServiceImpl.class.getName());
    private final SubjectRepository subjectRepository;
    
    public SubjectServiceImpl() {
        EntityManager em = DatabaseManager.getEntityManager();
        this.subjectRepository = new SubjectRepository(em);
    }
    
    @Override
    public Subject saveSubject(Subject subject) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            DatabaseManager.beginTransaction(em);
            Subject savedSubject = subjectRepository.save(subject);
            DatabaseManager.commitTransaction(em);
            return savedSubject;
        } catch (Exception e) {
            DatabaseManager.rollbackTransaction(em);
            LOGGER.log(Level.SEVERE, "Error saving subject", e);
            throw new RuntimeException("Error saving subject", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public Optional<Subject> getSubjectById(Long id) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            return subjectRepository.findById(id);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Subject> getAllSubjects() {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            return subjectRepository.findAll();
        } finally {
            em.close();
        }
    }
    
    @Override
    public Optional<Subject> getSubjectByName(String name) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            return subjectRepository.findByName(name);
        } finally {
            em.close();
        }
    }
    
    @Override
    public Subject updateSubject(Subject subject) {
        if (subject.getId() == null) {
            throw new IllegalArgumentException("Subject ID cannot be null for update operation");
        }
        return saveSubject(subject);
    }
    
    @Override
    public void deleteSubject(Long id) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            DatabaseManager.beginTransaction(em);
            subjectRepository.delete(id);
            DatabaseManager.commitTransaction(em);
        } catch (Exception e) {
            DatabaseManager.rollbackTransaction(em);
            LOGGER.log(Level.SEVERE, "Error deleting subject", e);
            throw new RuntimeException("Error deleting subject", e);
        } finally {
            em.close();
        }
    }
}
