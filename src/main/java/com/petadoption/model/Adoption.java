package com.petadoption.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.Date;
import java.util.Objects;

/**
 * Represents an adoption in the system.
 * Maps the relationship between a user and a pet through an adoption process.
 * This entity is annotated for persistence and stores details such as the
 * adopted pet, the adopting user, the adoption date, and the adoption status.
 * It includes functionality for comparing and displaying adoption.
 */
@Entity
public class Adoption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adoptionId;

    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    Pet pet;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    Date adoptionDate;

    @NotBlank
    String status;

    public Adoption() {}

    public Adoption(Long id, Pet pet, User user, Date adoptionDate, String status) {
        this.adoptionId = id;
        this.pet = pet;
        this.user = user;
        this.adoptionDate = adoptionDate;
        this.status = status;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }


    public void setAdoptionId(Long id) {
        this.adoptionId = id;
    }

    public Long getAdoptionId() {
        return adoptionId;
    }

    public Date getAdoptionDate() {
        return adoptionDate;
    }

    public void setAdoptionDate(Date adoptionDate) {
        this.adoptionDate = adoptionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Adoption{" +
                "id=" + adoptionId +
                ", pet=" + pet +
                ", user=" + user +
                ", adoptionDate=" + adoptionDate +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Adoption adoption = (Adoption) o;
        return Objects.equals(adoptionId, adoption.adoptionId) && Objects.equals(pet, adoption.pet) && Objects.equals(user, adoption.user) && Objects.equals(adoptionDate, adoption.adoptionDate) && Objects.equals(status, adoption.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(adoptionId, pet, user, adoptionDate, status);
    }
}
