package com.petadoption.model;

import com.petadoption.model.enums.PetStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

/**
 * Represents a Pet entity in the system, with details such as name, age, status,
 * type of pet, and location. This class is annotated as an entity for persistence.
 */
@Entity
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "The name of the pet must not be empty")
    private String name;

    @Min(value = 0, message = "Age must be greater or equal to 0")
    private Integer age;

    @Enumerated(EnumType.STRING)
    private PetStatus status;

    @NotBlank(message = "Location must be provided")
    private String location;


    @ManyToOne
    @JoinColumn(name = "type_of_pet_id")
    TypeOfPet typeOfPet;

    public TypeOfPet getTypeOfPet() {
        return typeOfPet;
    }

    public void setTypeOfPet(TypeOfPet typeOfPet) {
        this.typeOfPet = typeOfPet;
    }

    public Pet() {
    }

    public Pet(Long id, String name, Integer age, PetStatus status, TypeOfPet typeOfPet, String location) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.status = status;
        this.typeOfPet = typeOfPet;
        this.location = location;
    }

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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public PetStatus getStatus() {
        return status;
    }

    public void setStatus(PetStatus status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return Objects.equals(id, pet.id) && Objects.equals(name, pet.name) && Objects.equals(age, pet.age) && status == pet.status && Objects.equals(location, pet.location) && Objects.equals(typeOfPet, pet.typeOfPet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age, status, location, typeOfPet);
    }

    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", status=" + status +
                ", location='" + location + '\'' +
                ", typeOfPet=" + typeOfPet +
                '}';
    }
}
