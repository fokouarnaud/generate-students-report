package com.schoolreport.core.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Représente une note d'un élève pour une matière donnée
 */
@Entity
@Table(name = "grades")
public class Grade implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
    
    @Column(name = "sequence_1")
    private Double sequence1Value;
    
    @Column(name = "sequence_2")
    private Double sequence2Value;
    
    @Column(name = "term_average")
    private Double termAverage;
    
    @Column(name = "trimester", nullable = false)
    private int trimester;
    
    @Column(name = "school_year", nullable = false)
    private String schoolYear;
    
    // Constructeurs
    public Grade() {
    }
    
    public Grade(Student student, Subject subject, Double sequence1Value, Double sequence2Value, 
                 int trimester, String schoolYear) {
        this.student = student;
        this.subject = subject;
        this.sequence1Value = sequence1Value;
        this.sequence2Value = sequence2Value;
        this.trimester = trimester;
        this.schoolYear = schoolYear;
        calculateAverage();
    }
    
    // Méthode pour calculer la moyenne - simple moyenne des deux séquences
    public void calculateAverage() {
        if (sequence1Value != null && sequence2Value != null) {
            this.termAverage = (sequence1Value + sequence2Value) / 2.0;
        } else if (sequence1Value != null) {
            this.termAverage = sequence1Value;
        } else if (sequence2Value != null) {
            this.termAverage = sequence2Value;
        } else {
            this.termAverage = null;
        }
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Double getSequence1Value() {
        return sequence1Value;
    }

    public void setSequence1Value(Double sequence1Value) {
        this.sequence1Value = sequence1Value;
        calculateAverage();
    }

    public Double getSequence2Value() {
        return sequence2Value;
    }

    public void setSequence2Value(Double sequence2Value) {
        this.sequence2Value = sequence2Value;
        calculateAverage();
    }

    public Double getTermAverage() {
        return termAverage;
    }

    public void setTermAverage(Double termAverage) {
        this.termAverage = termAverage;
    }

    public int getTrimester() {
        return trimester;
    }

    public void setTrimester(int trimester) {
        this.trimester = trimester;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grade grade = (Grade) o;
        return id != null && id.equals(grade.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
