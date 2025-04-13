package com.schoolreport.persistence.util;

import com.schoolreport.core.model.Grade;
import com.schoolreport.core.model.School;
import com.schoolreport.core.model.SchoolClass;
import com.schoolreport.core.model.Student;
import com.schoolreport.core.model.Subject;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Générateur de données fictives pour tester l'application
 */
public class FakeDataGenerator {

    private static final Logger LOGGER = Logger.getLogger(FakeDataGenerator.class.getName());
    private static final Random RANDOM = new Random();
    
    private static final String[] FIRST_NAMES = {
        "Emma", "Léa", "Jade", "Louise", "Alice", "Chloé", "Lina", "Mila", "Inès", "Rose",
        "Gabriel", "Louis", "Raphaël", "Jules", "Adam", "Lucas", "Noah", "Hugo", "Maël", "Nathan",
        "Aya", "Sofia", "Liam", "Ethan", "Mohamed", "Yanis", "Rayan", "Ibrahim", "Karim", "Mariam",
        "Thomas", "Paul", "Victor", "Oscar", "Arthur", "Théo", "Léon", "Simon", "Joseph", "Samuel",
        "Anna", "Eva", "Maria", "Clara", "Sarah", "Zoé", "Camille", "Juliette", "Valentine", "Adèle",
        "Maxime", "Alexandre", "Antoine", "Martin", "Léo", "Pierre", "Mathis", "Valentin", "David", "Nolan"
    };
    
    private static final String[] LAST_NAMES = {
        "Martin", "Bernard", "Dubois", "Thomas", "Robert", "Richard", "Petit", "Durand", "Leroy", "Moreau",
        "Simon", "Laurent", "Lefebvre", "Michel", "Garcia", "David", "Bertrand", "Roux", "Vincent", "Fournier",
        "Morel", "Girard", "Andre", "Lefevre", "Mercier", "Dupont", "Lambert", "Bonnet", "Francois", "Martinez",
        "Legrand", "Garnier", "Faure", "Rousseau", "Blanc", "Guerin", "Muller", "Henry", "Roussel", "Nicolas",
        "Perrin", "Morin", "Mathieu", "Clement", "Gauthier", "Dumont", "Lopez", "Fontaine", "Chevalier", "Robin",
        "Masson", "Sanchez", "Gerard", "Nguyen", "Boyer", "Denis", "Lemaire", "Duval", "Joly", "Gautier"
    };

    /**
     * Génère des données fictives pour un grand nombre d'élèves
     */
    public static void generateFakeData(int numberOfStudents) {
        EntityManager em = null;
        try {
            LOGGER.info("Début de la génération des données fictives pour " + numberOfStudents + " élèves...");
            
            em = DatabaseManager.getEntityManager();
            
            // Vérifier si des données existent déjà
            Long countSchools = (Long) em.createQuery("SELECT COUNT(s) FROM School s").getSingleResult();
            if (countSchools > 0) {
                LOGGER.info("Suppression des données existantes avant génération...");
                // Exécuter en dehors d'une transaction car peut être volumineux
                DatabaseManager.beginTransaction(em);
                em.createQuery("DELETE FROM Grade").executeUpdate();
                em.createQuery("DELETE FROM Student").executeUpdate();
                em.createQuery("DELETE FROM SchoolClass").executeUpdate();
                em.createQuery("DELETE FROM Subject").executeUpdate();
                em.createQuery("DELETE FROM School").executeUpdate();
                DatabaseManager.commitTransaction(em);
                LOGGER.info("Données existantes supprimées avec succès.");
            }
            
            // Commencer une nouvelle transaction
            DatabaseManager.beginTransaction(em);
            
            // Création d'une école
            School school = new School("LYCÉE DE NKOZOA DE YAOUNDÉ", "Rue Principale", "Yaoundé", "+237 123456789", "info@lycee-nkozoa.cm");
            em.persist(school);
            LOGGER.info("École créée: " + school.getName());
            
            // Création des classes (on divise les élèves en plusieurs classes)
            List<SchoolClass> classes = new ArrayList<>();
            
            // 5 classes maximum, avec environ 20-25 élèves par classe
            int numClasses = Math.min(5, (int) Math.ceil(numberOfStudents / 20.0));
            
            // Utiliser l'année scolaire actuelle ou celle spécifiée dans l'interface
            String currentYear = "2022/2023"; // Correspond à la valeur dans l'interface
            
            for (int i = 0; i < numClasses; i++) {
                SchoolClass schoolClass = new SchoolClass((i + 6) + "e", currentYear);
                em.persist(schoolClass);
                classes.add(schoolClass);
                LOGGER.info("Classe créée: " + schoolClass.getName() + " (" + currentYear + ")");
            }
            
            // Création des matières (sans coefficients)
            List<Subject> subjects = new ArrayList<>();
            String[] subjectNames = {
                "Français", "Anglais", "Allemand", "Mathématiques", "Physique", 
                "Chimie", "Histoire - Géographie", "Éducation civique", "Technologie", "EPS"
            };
            
            for (String subjectName : subjectNames) {
                Subject subject = new Subject(subjectName, 1); // Coefficient 1 pour tous
                em.persist(subject);
                subjects.add(subject);
            }
            LOGGER.info(subjects.size() + " matières créées");
            
            // Commit la première partie
            DatabaseManager.commitTransaction(em);
            LOGGER.info("École, classes et matières enregistrées avec succès.");
            
            // Création des étudiants et leurs notes - en lots
            int batchSize = 10; // Traitement par lots de 10 élèves
            
            // Créer les données pour le trimestre 1 (sélectionné dans l'interface)
            int trimester = 1;
            
            for (int i = 0; i < numberOfStudents; i++) {
                // Commencer une nouvelle transaction pour chaque lot
                if (i % batchSize == 0) {
                    if (em.getTransaction().isActive()) {
                        DatabaseManager.commitTransaction(em);
                    }
                    DatabaseManager.beginTransaction(em);
                    LOGGER.info("Traitement du lot " + (i / batchSize + 1) + " (élèves " + (i+1) + " à " + 
                            Math.min(i + batchSize, numberOfStudents) + ")");
                }
                
                // Déterminer la classe pour cet élève (répartition équilibrée)
                int classIndex = i % numClasses;
                SchoolClass studentClass = classes.get(classIndex);
                
                // Créer un étudiant avec nom/prénom aléatoire
                String firstName = FIRST_NAMES[RANDOM.nextInt(FIRST_NAMES.length)];
                String lastName = LAST_NAMES[RANDOM.nextInt(LAST_NAMES.length)];
                Student student = new Student(firstName, lastName, studentClass);
                em.persist(student);
                
                LOGGER.fine("Élève créé: " + lastName + " " + firstName + " (" + studentClass.getName() + ")");
                
                // Générer des notes pour chaque matière
                for (Subject subject : subjects) {
                    // Générer des notes aléatoires entre 5 et 20
                    double seq1 = Math.round((5 + RANDOM.nextDouble() * 15) * 10.0) / 10.0; // Entre 5 et 20 avec un décimal
                    double seq2 = Math.round((5 + RANDOM.nextDouble() * 15) * 10.0) / 10.0; // Entre 5 et 20 avec un décimal
                    
                    // Créer et sauvegarder la note pour le trimestre 1
                    Grade grade = new Grade(student, subject, seq1, seq2, trimester, currentYear);
                    em.persist(grade);
                }
                
                // Commit à la fin de chaque lot
                if ((i + 1) % batchSize == 0 || i == numberOfStudents - 1) {
                    DatabaseManager.commitTransaction(em);
                    LOGGER.info("Lot " + (i / batchSize + 1) + " enregistré avec succès (" + (i % batchSize + 1) + " élèves)");
                }
            }
            
            LOGGER.info("Génération des données terminée avec succès : " + numberOfStudents + " élèves créés pour le trimestre " + trimester);
            
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                DatabaseManager.rollbackTransaction(em);
            }
            LOGGER.log(Level.SEVERE, "Erreur lors de la génération des données fictives", e);
            e.printStackTrace();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
