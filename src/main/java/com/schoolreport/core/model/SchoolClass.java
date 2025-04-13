package com.schoolreport.core.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Représente une classe scolaire (ex: "12e")
 */
@Entity
@Table(name = "school_classes")
public class SchoolClass implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(name = "academic_year", nullable = false)
    private String academicYear;
    
    @OneToMany(mappedBy = "schoolClass", cascade = CascadeType.ALL)
    private Set<Student> students = new HashSet<>();
    
    // Constructeurs
    public SchoolClass() {
    }
    
    public SchoolClass(String name, String academicYear) {
        this.name = name;
        this.academicYear = academicYear;
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

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public Set<Student> getStudents() {
        return students;
    }

    public void setStudents(Set<Student> students) {
        this.students = students;
    }
    
    // Méthodes utilitaires
    public void addStudent(Student student) {
        students.add(student);
        student.setSchoolClass(this);
    }
    
    public void removeStudent(Student student) {
        students.remove(student);
        student.setSchoolClass(null);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchoolClass that = (SchoolClass) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return name + " (" + academicYear + ")";
    }
}
