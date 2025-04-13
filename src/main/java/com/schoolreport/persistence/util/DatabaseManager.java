package com.schoolreport.persistence.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestionnaire de la base de données
 */
public class DatabaseManager {
    
    private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());
    private static final String PERSISTENCE_UNIT_NAME = "school-report-unit";
    private static EntityManagerFactory emf;
    
    /**
     * Initialise le gestionnaire de base de données
     * @param dbPath le chemin vers le fichier de base de données
     */
    public static void initialize(String dbPath) {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
        
        Map<String, String> properties = new HashMap<>();
        properties.put("javax.persistence.jdbc.url", "jdbc:h2:file:" + dbPath);
        properties.put("javax.persistence.jdbc.user", "sa");
        properties.put("javax.persistence.jdbc.password", "");
        properties.put("javax.persistence.jdbc.driver", "org.h2.Driver");
        
        // Pour le développement, nous pouvons créer la base de données automatiquement
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.put("hibernate.show_sql", "false");
        
        try {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);
            LOGGER.info("Database initialized successfully at: " + dbPath);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize database", e);
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
    
    /**
     * Obtient un EntityManager
     * @return un nouvel EntityManager
     */
    public static EntityManager getEntityManager() {
        if (emf == null || !emf.isOpen()) {
            throw new IllegalStateException("EntityManagerFactory is not initialized. Call initialize() first.");
        }
        return emf.createEntityManager();
    }
    
    /**
     * Ferme le gestionnaire de base de données
     */
    public static void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            LOGGER.info("Database connection closed");
        }
    }
    
    /**
     * Commence une transaction
     * @param em l'EntityManager à utiliser
     */
    public static void beginTransaction(EntityManager em) {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }
    
    /**
     * Commet une transaction
     * @param em l'EntityManager à utiliser
     */
    public static void commitTransaction(EntityManager em) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }
    
    /**
     * Annule une transaction
     * @param em l'EntityManager à utiliser
     */
    public static void rollbackTransaction(EntityManager em) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}
