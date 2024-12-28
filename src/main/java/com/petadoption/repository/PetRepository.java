package com.petadoption.repository;

import com.petadoption.model.Pet;
import com.petadoption.model.enums.PetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Pet entities in the database.
 * Extends JpaRepository to provide generic CRUD operations and query methods.
 * Includes custom query methods for retrieving pets based on location or status.

 * This interface is used primarily in services such as PetService and AdoptionService,
 * where business logic related to Pets interacts with the database layer.
 */
@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByLocation(String location);
    List<Pet> findByStatus(PetStatus status);
}
