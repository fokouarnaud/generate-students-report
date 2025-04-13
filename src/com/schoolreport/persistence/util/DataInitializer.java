package com.schoolreport.persistence.util;

import com.schoolreport.core.model.Grade;
import com.schoolreport.core.model.School;
import com.schoolreport.core.model.SchoolClass;
import com.schoolreport.core.model.Student;
import com.schoolreport.core.model.Subject;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;

/**
 * Classe pour initialiser des données de test
 */
public class DataInitializer {
    
    private static final Logger LOGGER = Logger.getLogger(DataInitializer.class.getName());
    
    /**
     * Initialise des données de test dans la base de données
     */
    public static void initializeTestData() {
        EntityManager em = null;
        try {
            em = DatabaseManager.getEntityManager();
            DatabaseManager.beginTransaction(em);
            
            // Vérifier si des données existent déjà
            Long countSchools = (Long) em.createQuery("SELECT COUNT(s) FROM School s").getSingleResult();
            if (countSchools > 0) {
                LOGGER.info("Test data already exists. Skipping initialization.");
                DatabaseManager.rollbackTransaction(em);
                return;
            }
            
            // Création d'une école
            School school = new School("LYCÉE DE NKOZOA DE YAOUNDÉ", "Rue Principale", "Yaoundé", "+237 123456789", "info@lycee-nkozoa.cm");
            em.persist(school);
            
            // Création des classes
            SchoolClass class12e = new SchoolClass("12e", "2024-2025");
            SchoolClass class11e = new SchoolClass("11e", "2024-2025");
            em.persist(class12e);
            em.persist(class11e);
            
            // Création des matières
            Subject francais = new Subject("Français", 5);
            Subject anglais = new Subject("Anglais", 4);
            Subject allemand = new Subject("Allemand", 2);
            Subject maths = new Subject("Mathématiques", 6);
            Subject physique = new Subject("Physique", 4);
            Subject chimie = new Subject("Chimie", 4);
            Subject histoire = new Subject("Histoire - Géographie", 3);
            Subject education = new Subject("Éducation civique", 2);
            Subject technologie = new Subject("Technologie", 2);
            Subject eps = new Subject("EPS", 2);
            
            em.persist(francais);
            em.persist(anglais);
            em.persist(allemand);
            em.persist(maths);
            em.persist(physique);
            em.persist(chimie);
            em.persist(histoire);
            em.persist(education);
            em.persist(technologie);
            em.persist(eps);
            
            // Création des étudiants
            Student emile = new Student("Emile", "MOUKAM", class12e);
            Student paul = new Student("Paul", "BIYA", class12e);
            Student marie = new Student("Marie", "EKOTO", class12e);
            Student jean = new Student("Jean", "MENDOZA", class12e);
            
            em.persist(emile);
            em.persist(paul);
            em.persist(marie);
            em.persist(jean);
            
            // Création des notes pour Emile (Trimestre 2)
            createGrade(em, emile, francais, 12.0, 10.5, 2, "2024-2025");
            createGrade(em, emile, anglais, 13.0, 15.0, 2, "2024-2025");
            createGrade(em, emile, allemand, 9.5, 8.0, 2, "2024-2025");
            createGrade(em, emile, maths, 11.5, 12.5, 2, "2024-2025");
            createGrade(em, emile, physique, 10.0, 9.5, 2, "2024-2025");
            createGrade(em, emile, chimie, 9.0, 13.0, 2, "2024-2025");
            createGrade(em, emile, histoire, 15.0, 14.0, 2, "2024-2025");
            createGrade(em, emile, education, 13.0, 14.0, 2, "2024-2025");
            createGrade(em, emile, technologie, 13.0, 13.0, 2, "2024-2025");
            createGrade(em, emile, eps, 11.0, 14.0, 2, "2024-2025");
            
            // Création des notes pour Paul (Trimestre 2)
            createGrade(em, paul, francais, 14.0, 13.5, 2, "2024-2025");
            createGrade(em, paul, anglais, 16.0, 15.5, 2, "2024-2025");
            createGrade(em, paul, allemand, 12.5, 11.0, 2, "2024-2025");
            createGrade(em, paul, maths, 17.5, 16.5, 2, "2024-2025");
            createGrade(em, paul, physique, 14.0, 15.5, 2, "2024-2025");
            createGrade(em, paul, chimie, 15.0, 14.0, 2, "2024-2025");
            createGrade(em, paul, histoire, 13.0, 14.0, 2, "2024-2025");
            createGrade(em, paul, education, 17.0, 16.0, 2, "2024-2025");
            createGrade(em, paul, technologie, 15.0, 16.0, 2, "2024-2025");
            createGrade(em, paul, eps, 14.0, 13.0, 2, "2024-2025");
            
            // Création des notes pour Marie (Trimestre 2)
            createGrade(em, marie, francais, 15.0, 16.5, 2, "2024-2025");
            createGrade(em, marie, anglais, 14.0, 13.5, 2, "2024-2025");
            createGrade(em, marie, allemand, 13.5, 14.0, 2, "2024-2025");
            createGrade(em, marie, maths, 12.5, 13.5, 2, "2024-2025");
            createGrade(em, marie, physique, 13.0, 12.5, 2, "2024-2025");
            createGrade(em, marie, chimie, 14.0, 15.0, 2, "2024-2025");
            createGrade(em, marie, histoire, 16.0, 17.0, 2, "2024-2025");
            createGrade(em, marie, education, 18.0, 17.0, 2, "2024-2025");
            createGrade(em, marie, technologie, 14.0, 15.0, 2, "2024-2025");
            createGrade(em, marie, eps, 16.0, 15.0, 2, "2024-2025");
            
            // Création des notes pour Jean (Trimestre 2)
            createGrade(em, jean, francais, 11.0, 12.5, 2, "2024-2025");
            createGrade(em, jean, anglais, 12.0, 11.5, 2, "2024-2025");
            createGrade(em, jean, allemand, 10.5, 9.0, 2, "2024-2025");
            createGrade(em, jean, maths, 9.5, 10.5, 2, "2024-2025");
            createGrade(em, jean, physique, 8.0, 9.5, 2, "2024-2025");
            createGrade(em, jean, chimie, 10.0, 11.0, 2, "2024-2025");
            createGrade(em, jean, histoire, 13.0, 12.0, 2, "2024-2025");
            createGrade(em, jean, education, 14.0, 13.0, 2, "2024-2025");
            createGrade(em, jean, technologie, 11.0, 12.0, 2, "2024-2025");
            createGrade(em, jean, eps, 15.0, 16.0, 2, "2024-2025");
            
            // Création des étudiants de la classe 11e
            Student thomas = new Student("Thomas", "EKONG", class11e);
            Student sophie = new Student("Sophie", "MBALA", class11e);
            
            em.persist(thomas);
            em.persist(sophie);
            
            // Création des notes pour Thomas (Trimestre 2)
            createGrade(em, thomas, francais, 13.0, 14.5, 2, "2024-2025");
            createGrade(em, thomas, anglais, 15.0, 14.5, 2, "2024-2025");
            createGrade(em, thomas, allemand, 11.5, 12.0, 2, "2024-2025");
            createGrade(em, thomas, maths, 16.5, 15.5, 2, "2024-2025");
            createGrade(em, thomas, physique, 15.0, 14.5, 2, "2024-2025");
            createGrade(em, thomas, chimie, 14.0, 13.0, 2, "2024-2025");
            createGrade(em, thomas, histoire, 12.0, 13.0, 2, "2024-2025");
            createGrade(em, thomas, education, 16.0, 15.0, 2, "2024-2025");
            createGrade(em, thomas, technologie, 13.0, 14.0, 2, "2024-2025");
            createGrade(em, thomas, eps, 15.0, 16.0, 2, "2024-2025");
            
            // Création des notes pour Sophie (Trimestre 2)
            createGrade(em, sophie, francais, 17.0, 16.5, 2, "2024-2025");
            createGrade(em, sophie, anglais, 16.0, 17.5, 2, "2024-2025");
            createGrade(em, sophie, allemand, 15.5, 14.0, 2, "2024-2025");
            createGrade(em, sophie, maths, 18.5, 17.5, 2, "2024-2025");
            createGrade(em, sophie, physique, 16.0, 17.5, 2, "2024-2025");
            createGrade(em, sophie, chimie, 17.0, 18.0, 2, "2024-2025");
            createGrade(em, sophie, histoire, 16.0, 17.0, 2, "2024-2025");
            createGrade(em, sophie, education, 18.0, 19.0, 2, "2024-2025");
            createGrade(em, sophie, technologie, 17.0, 18.0, 2, "2024-2025");
            createGrade(em, sophie, eps, 16.0, 17.0, 2, "2024-2025");
            
            // Tout sauvegarder
            DatabaseManager.commitTransaction(em);
            LOGGER.info("Test data initialized successfully");
            
        } catch (Exception e) {
            if (em != null) {
                DatabaseManager.rollbackTransaction(em);
            }
            LOGGER.log(Level.SEVERE, "Error initializing test data", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * Crée une note pour un étudiant
     */
    private static void createGrade(EntityManager em, Student student, Subject subject, 
                                   Double seq1Value, Double seq2Value, int trimester, String schoolYear) {
        Grade grade = new Grade(student, subject, seq1Value, seq2Value, trimester, schoolYear);
        em.persist(grade);
    }
}
