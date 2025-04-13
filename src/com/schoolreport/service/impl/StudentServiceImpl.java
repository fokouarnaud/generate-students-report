package com.schoolreport.service.impl;

import com.schoolreport.core.model.Student;
import com.schoolreport.core.service.StudentService;
import com.schoolreport.persistence.repository.StudentRepository;
import com.schoolreport.persistence.util.DatabaseManager;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implémentation du service de gestion des étudiants
 */
public class StudentServiceImpl implements StudentService {
    
    private static final Logger LOGGER = Logger.getLogger(StudentServiceImpl.class.getName());
    private final StudentRepository studentRepository;
    
    public StudentServiceImpl() {
        EntityManager em = DatabaseManager.getEntityManager();
        this.studentRepository = new StudentRepository(em);
    }
    
    @Override
    public Student saveStudent(Student student) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            DatabaseManager.beginTransaction(em);
            Student savedStudent = studentRepository.save(student);
            DatabaseManager.commitTransaction(em);
            return savedStudent;
        } catch (Exception e) {
            DatabaseManager.rollbackTransaction(em);
            LOGGER.log(Level.SEVERE, "Error saving student", e);
            throw new RuntimeException("Error saving student", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public Optional<Student> getStudentById(Long id) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            return studentRepository.findById(id);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Student> getAllStudents() {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            return studentRepository.findAll();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Student> getStudentsByClassId(Long classId) {
        LOGGER.info("Récupération de tous les étudiants pour la classe " + classId);
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            List<Student> students = studentRepository.findByClassId(classId);
            LOGGER.info("Nombre d'étudiants récupérés: " + students.size());
            return students;
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Student> getStudentsByClassIdPaginated(Long classId, int page, int size) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            return studentRepository.findByClassIdWithPagination(classId, page, size);
        } finally {
            em.close();
        }
    }
    
    @Override
    public long countStudentsByClassId(Long classId) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            return studentRepository.countByClassId(classId);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void deleteStudent(Long id) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            DatabaseManager.beginTransaction(em);
            studentRepository.delete(id);
            DatabaseManager.commitTransaction(em);
        } catch (Exception e) {
            DatabaseManager.rollbackTransaction(em);
            LOGGER.log(Level.SEVERE, "Error deleting student", e);
            throw new RuntimeException("Error deleting student", e);
        } finally { 
            em.close();
        }
    }
    
    @Override
    public Student updateStudent(Student student) {
        if (student.getId() == null) {
            throw new IllegalArgumentException("Student ID cannot be null for update operation");
        }
        return saveStudent(student);
    }
}
