package com.schoolreport.core.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Représente une matière scolaire (ex: "Mathématiques", "Français")
 */
@Entity
@Table(name = "subjects")
public class Subject implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(nullable = false)
    private int coefficient;
    
    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL)
    private Set<Grade> grades = new HashSet<>();
    
    // Constructeurs
    public Subject() {
    }
    
    public Subject(String name, int coefficient) {
        this.name = name;
        this.coefficient = coefficient;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(int coefficient) {
        this.coefficient = coefficient;
    }

    public Set<Grade> getGrades() {
        return grades;
    }

    public void setGrades(Set<Grade> grades) {
        this.grades = grades;
    }
    
    // Méthodes utilitaires
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subject subject = (Subject) o;
        return id != null && id.equals(subject.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return name;
    }
}
