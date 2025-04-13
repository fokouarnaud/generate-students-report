package com.schoolreport.persistence.repository;

import com.schoolreport.core.model.School;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'accès aux données des établissements scolaires
 */
public class SchoolRepository {
    
    private final EntityManager entityManager;
    
    public SchoolRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    public School save(School school) {
        if (school.getId() == null) {
            entityManager.persist(school);
            return school;
        } else {
            return entityManager.merge(school);
        }
    }
    
    public Optional<School> findById(Long id) {
        School school = entityManager.find(School.class, id);
        return Optional.ofNullable(school);
    }
    
    public List<School> findAll() {
        TypedQuery<School> query = entityManager.createQuery(
                "SELECT s FROM School s ORDER BY s.name", 
                School.class);
        return query.getResultList();
    }
    
    public Optional<School> findDefault() {
        // Dans une implémentation réelle, vous pourriez avoir une table de configuration
        // pour stocker l'ID de l'école par défaut
        // Pour simplifier, nous retournons la première école si elle existe
        TypedQuery<School> query = entityManager.createQuery(
                "SELECT s FROM School s ORDER BY s.id", 
                School.class);
        query.setMaxResults(1);
        
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    public void delete(Long id) {
        School school = entityManager.find(School.class, id);
        if (school != null) {
            entityManager.remove(school);
        }
    }
}
