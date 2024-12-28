package com.petadoption.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Represents the type or category of a pet, such as dog, cat, bird, etc.
 * This class is an entity that can be persisted in a database.
 * It contains information about the type's unique identifier and name.
 */
@Entity
public class TypeOfPet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;

    public TypeOfPet() {}

    public TypeOfPet(String name, Long id) {
        this.name = name;
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeOfPet typeOfPet = (TypeOfPet) o;
        return Objects.equals(id, typeOfPet.id) && Objects.equals(name, typeOfPet.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "TypeOfPet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
