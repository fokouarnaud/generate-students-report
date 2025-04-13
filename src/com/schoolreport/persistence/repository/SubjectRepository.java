package com.schoolreport.persistence.repository;

import com.schoolreport.core.model.Subject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'accès aux données des matières
 */
public class SubjectRepository {
    
    private final EntityManager entityManager;
    
    public SubjectRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    public Subject save(Subject subject) {
        if (subject.getId() == null) {
            entityManager.persist(subject);
            return subject;
        } else {
            return entityManager.merge(subject);
        }
    }
    
    public Optional<Subject> findById(Long id) {
        Subject subject = entityManager.find(Subject.class, id);
        return Optional.ofNullable(subject);
    }
    
    public List<Subject> findAll() {
        TypedQuery<Subject> query = entityManager.createQuery(
                "SELECT s FROM Subject s ORDER BY s.name", 
                Subject.class);
        return query.getResultList();
    }
    
    public Optional<Subject> findByName(String name) {
        TypedQuery<Subject> query = entityManager.createQuery(
                "SELECT s FROM Subject s WHERE s.name = :name", 
                Subject.class);
        query.setParameter("name", name);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    public void delete(Long id) {
        Subject subject = entityManager.find(Subject.class, id);
        if (subject != null) {
            entityManager.remove(subject);
        }
    }
}
