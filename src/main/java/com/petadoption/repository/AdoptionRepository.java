package com.petadoption.repository;

import com.petadoption.model.Adoption;
import com.petadoption.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Adoption entities.
 * Extends JpaRepository for basic CRUD operations and pagination support.
 * Provides custom query methods related to adoption records.

 * Methods in this interface enable querying adoption records by user and pet IDs.
 */
@Repository
public interface AdoptionRepository extends JpaRepository<Adoption, Long> {

    List<Adoption> findByUserId(Long userId);
    List<Adoption> findByPetId(Long petId);

}
