package com.schoolreport.persistence.repository;

import com.schoolreport.core.model.Student;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'accès aux données des étudiants
 */
public class StudentRepository {
    
    private final EntityManager entityManager;
    
    public StudentRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    public Student save(Student student) {
        if (student.getId() == null) {
            entityManager.persist(student);
            return student;
        } else {
            return entityManager.merge(student);
        }
    }
    
    public Optional<Student> findById(Long id) {
        Student student = entityManager.find(Student.class, id);
        return Optional.ofNullable(student);
    }
    
    public List<Student> findAll() {
        TypedQuery<Student> query = entityManager.createQuery("SELECT s FROM Student s", Student.class);
        return query.getResultList();
    }
    
    public List<Student> findByClassId(Long classId) {
        TypedQuery<Student> query = entityManager.createQuery(
                "SELECT s FROM Student s WHERE s.schoolClass.id = :classId ORDER BY s.lastName, s.firstName", 
                Student.class);
        query.setParameter("classId", classId);
        return query.getResultList();
    }
    
    /**
     * Trouve les étudiants d'une classe avec pagination
     * @param classId l'ID de la classe
     * @param page la page à récupérer (commence à 0)
     * @param size nombre d'étudiants par page
     * @return liste des étudiants pour cette page
     */
    public List<Student> findByClassIdWithPagination(Long classId, int page, int size) {
        TypedQuery<Student> query = entityManager.createQuery(
                "SELECT s FROM Student s WHERE s.schoolClass.id = :classId ORDER BY s.lastName, s.firstName", 
                Student.class);
        query.setParameter("classId", classId);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }
    
    /**
     * Compte le nombre total d'étudiants dans une classe
     * @param classId l'ID de la classe
     * @return le nombre d'étudiants
     */
    public long countByClassId(Long classId) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(s) FROM Student s WHERE s.schoolClass.id = :classId", 
                Long.class);
        query.setParameter("classId", classId);
        return query.getSingleResult();
    }
    
    public void delete(Long id) {
        Student student = entityManager.find(Student.class, id);
        if (student != null) {
            entityManager.remove(student);
        }
    }
}
