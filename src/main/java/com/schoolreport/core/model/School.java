package com.schoolreport.core.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Représente un établissement scolaire
 */
@Entity
@Table(name = "schools")
public class School implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column
    private String address;
    
    @Column
    private String city;
    
    @Column
    private String phone;
    
    @Column
    private String email;
    
    @Column
    private String logo;
    
    // Constructeurs
    public School() {
    }
    
    public School(String name, String address, String city, String phone, String email) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.phone = phone;
        this.email = email;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        School school = (School) o;
        return id != null && id.equals(school.id);
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
