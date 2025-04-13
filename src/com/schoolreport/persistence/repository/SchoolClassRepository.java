package com.schoolreport.persistence.repository;

import com.schoolreport.core.model.SchoolClass;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'accès aux données des classes scolaires
 */
public class SchoolClassRepository {
    
    private final EntityManager entityManager;
    
    public SchoolClassRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    public SchoolClass save(SchoolClass schoolClass) {
        if (schoolClass.getId() == null) {
            entityManager.persist(schoolClass);
            return schoolClass;
        } else {
            return entityManager.merge(schoolClass);
        }
    }
    
    public Optional<SchoolClass> findById(Long id) {
        SchoolClass schoolClass = entityManager.find(SchoolClass.class, id);
        return Optional.ofNullable(schoolClass);
    }
    
    public List<SchoolClass> findAll() {
        TypedQuery<SchoolClass> query = entityManager.createQuery(
                "SELECT c FROM SchoolClass c ORDER BY c.name", 
                SchoolClass.class);
        return query.getResultList();
    }
    
    public List<SchoolClass> findByAcademicYear(String academicYear) {
        TypedQuery<SchoolClass> query = entityManager.createQuery(
                "SELECT c FROM SchoolClass c WHERE c.academicYear = :academicYear ORDER BY c.name", 
                SchoolClass.class);
        query.setParameter("academicYear", academicYear);
        return query.getResultList();
    }
    
    public Optional<SchoolClass> findByName(String name) {
        TypedQuery<SchoolClass> query = entityManager.createQuery(
                "SELECT c FROM SchoolClass c WHERE c.name = :name", 
                SchoolClass.class);
        query.setParameter("name", name);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    public void delete(Long id) {
        SchoolClass schoolClass = entityManager.find(SchoolClass.class, id);
        if (schoolClass != null) {
            entityManager.remove(schoolClass);
        }
    }
}
