package com.schoolreport.service.impl;

import com.schoolreport.core.model.Grade;
import com.schoolreport.core.model.Subject;
import com.schoolreport.core.service.GradeService;
import com.schoolreport.persistence.repository.GradeRepository;
import com.schoolreport.persistence.util.DatabaseManager;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implémentation du service de gestion des notes
 */
public class GradeServiceImpl implements GradeService {
    
    private static final Logger LOGGER = Logger.getLogger(GradeServiceImpl.class.getName());
    private final GradeRepository gradeRepository;
    
    public GradeServiceImpl() {
        EntityManager em = DatabaseManager.getEntityManager();
        this.gradeRepository = new GradeRepository(em);
    }
    
    @Override
    public Grade saveGrade(Grade grade) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            DatabaseManager.beginTransaction(em);
            // Calcul automatique de la moyenne
            grade.calculateAverage();
            Grade savedGrade = gradeRepository.save(grade);
            DatabaseManager.commitTransaction(em);
            return savedGrade;
        } catch (Exception e) {
            DatabaseManager.rollbackTransaction(em);
            LOGGER.log(Level.SEVERE, "Error saving grade", e);
            throw new RuntimeException("Error saving grade", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public Optional<Grade> getGradeById(Long id) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            return gradeRepository.findById(id);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Grade> getGradesByStudentId(Long studentId) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            return gradeRepository.findByStudentId(studentId);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Grade> getGradesByStudentIdAndTrimester(Long studentId, int trimester) {
        LOGGER.info("Recherche des notes pour l'étudiant ID=" + studentId + ", trimestre=" + trimester);
        
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            // Requête directe pour diagnostic
            TypedQuery<Grade> query = em.createQuery(
                "SELECT g FROM Grade g WHERE g.student.id = :studentId AND g.trimester = :trimester",
                Grade.class);
            query.setParameter("studentId", studentId);
            query.setParameter("trimester", trimester);
            
            List<Grade> grades = query.getResultList();
            
            LOGGER.info("Résultat de la requête directe: " + grades.size() + " notes trouvées");
            
            if (grades.isEmpty()) {
                // Vérifier si l'étudiant a des notes en général
                TypedQuery<Long> countQuery = em.createQuery(
                    "SELECT COUNT(g) FROM Grade g WHERE g.student.id = :studentId",
                    Long.class);
                countQuery.setParameter("studentId", studentId);
                long totalGrades = countQuery.getSingleResult();
                
                LOGGER.info("Total des notes pour cet étudiant (tous trimestres): " + totalGrades);
                
                // Vérifier les trimestres disponibles
                if (totalGrades > 0) {
                    TypedQuery<Integer> trimesterQuery = em.createQuery(
                        "SELECT DISTINCT g.trimester FROM Grade g WHERE g.student.id = :studentId ORDER BY g.trimester",
                        Integer.class);
                    trimesterQuery.setParameter("studentId", studentId);
                    List<Integer> availableTrimester = trimesterQuery.getResultList();
                    
                    StringBuilder trimesters = new StringBuilder();
                    for (Integer t : availableTrimester) {
                        trimesters.append(t).append(", ");
                    }
                    
                    LOGGER.info("Trimestres disponibles pour cet étudiant: " + trimesters);
                    
                    // Si l'étudiant a des notes pour d'autres trimestres, on peut essayer de les récupérer
                    if (!availableTrimester.isEmpty()) {
                        int firstAvailableTrimester = availableTrimester.get(0);
                        LOGGER.info("Essai de récupération des notes du trimestre " + firstAvailableTrimester + " à la place");
                        
                        query = em.createQuery(
                            "SELECT g FROM Grade g WHERE g.student.id = :studentId AND g.trimester = :trimester",
                            Grade.class);
                        query.setParameter("studentId", studentId);
                        query.setParameter("trimester", firstAvailableTrimester);
                        
                        grades = query.getResultList();
                        LOGGER.info("Notes trouvées pour le trimestre " + firstAvailableTrimester + ": " + grades.size());
                    }
                }
            }
            
            return grades;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des notes", e);
            return List.of(); // Retourner une liste vide en cas d'erreur
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Grade> getGradesByClassIdAndTrimester(Long classId, int trimester) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            return gradeRepository.findByClassIdAndTrimester(classId, trimester);
        } finally {
            em.close();
        }
    }
    
    @Override
    public Grade updateGrade(Grade grade) {
        if (grade.getId() == null) {
            throw new IllegalArgumentException("Grade ID cannot be null for update operation");
        }
        return saveGrade(grade);
    }
    
    @Override
    public void deleteGrade(Long id) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            DatabaseManager.beginTransaction(em);
            gradeRepository.delete(id);
            DatabaseManager.commitTransaction(em);
        } catch (Exception e) {
            DatabaseManager.rollbackTransaction(em);
            LOGGER.log(Level.SEVERE, "Error deleting grade", e);
            throw new RuntimeException("Error deleting grade", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public double calculateStudentAverage(Long studentId, int trimester) {
        List<Grade> grades = getGradesByStudentIdAndTrimester(studentId, trimester);
        if (grades.isEmpty()) {
            return 0.0;
        }
        
        // Calcul de la moyenne (moyenne simple, sans coefficient)
        double totalScore = 0.0;
        int countWithGrades = 0;
        
        for (Grade grade : grades) {
            if (grade.getTermAverage() != null) {
                totalScore += grade.getTermAverage();
                countWithGrades++;
            }
        }
        
        return countWithGrades > 0 ? totalScore / countWithGrades : 0.0;
    }
    
    @Override
    public double calculateClassAverageBySubject(Long classId, Long subjectId, int trimester) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            // Dans une implémentation réelle, vous pourriez optimiser ceci avec une requête SQL directe
            List<Grade> grades = em.createQuery(
                    "SELECT g FROM Grade g WHERE g.student.schoolClass.id = :classId AND " +
                            "g.subject.id = :subjectId AND g.trimester = :trimester AND g.termAverage IS NOT NULL",
                    Grade.class)
                    .setParameter("classId", classId)
                    .setParameter("subjectId", subjectId)
                    .setParameter("trimester", trimester)
                    .getResultList();
            
            if (grades.isEmpty()) {
                return 0.0;
            }
            
            double sum = 0.0;
            for (Grade grade : grades) {
                sum += grade.getTermAverage();
            }
            
            return sum / grades.size();
        } finally {
            em.close();
        }
    }
}
