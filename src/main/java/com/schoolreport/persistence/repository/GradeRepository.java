package com.schoolreport.persistence.repository;

import com.schoolreport.core.model.Grade;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'accès aux données des notes
 */
public class GradeRepository {
    
    private final EntityManager entityManager;
    
    public GradeRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    public Grade save(Grade grade) {
        if (grade.getId() == null) {
            entityManager.persist(grade);
            return grade;
        } else {
            return entityManager.merge(grade);
        }
    }
    
    public Optional<Grade> findById(Long id) {
        Grade grade = entityManager.find(Grade.class, id);
        return Optional.ofNullable(grade);
    }
    
    public List<Grade> findByStudentId(Long studentId) {
        TypedQuery<Grade> query = entityManager.createQuery(
                "SELECT g FROM Grade g WHERE g.student.id = :studentId", 
                Grade.class);
        query.setParameter("studentId", studentId);
        return query.getResultList();
    }
    
    public List<Grade> findByStudentIdAndTrimester(Long studentId, int trimester) {
        TypedQuery<Grade> query = entityManager.createQuery(
                "SELECT g FROM Grade g WHERE g.student.id = :studentId AND g.trimester = :trimester", 
                Grade.class);
        query.setParameter("studentId", studentId);
        query.setParameter("trimester", trimester);
        return query.getResultList();
    }
    
    public List<Grade> findByClassIdAndTrimester(Long classId, int trimester) {
        TypedQuery<Grade> query = entityManager.createQuery(
                "SELECT g FROM Grade g WHERE g.student.schoolClass.id = :classId AND g.trimester = :trimester", 
                Grade.class);
        query.setParameter("classId", classId);
        query.setParameter("trimester", trimester);
        return query.getResultList();
    }
    
    public void delete(Long id) {
        Grade grade = entityManager.find(Grade.class, id);
        if (grade != null) {
            entityManager.remove(grade);
        }
    }
}
