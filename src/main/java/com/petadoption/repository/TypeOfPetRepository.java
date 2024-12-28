package com.petadoption.repository;

import com.petadoption.model.TypeOfPet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link TypeOfPet} entities.
 * This interface extends {@link JpaRepository} to provide standard
 * CRUD operations and additional query methods for the TypeOfPet entity.

 * It enables interaction with the database and abstracts common persistence
 * functionalities such as saving, deleting, and retrieving TypeOfPet records.
 * Custom query methods can also be defined here if needed.

 * This repository should be used in service or business logic layers to
 * perform operations related to TypeOfPet objects.
 */
@Repository
public interface TypeOfPetRepository extends JpaRepository<TypeOfPet, Long> {
}
