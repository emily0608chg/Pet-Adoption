package com.petadoption.repository;

import com.petadoption.model.Adoption;
import com.petadoption.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Adoption entities.
 * Extends JpaRepository for basic CRUD operations and pagination support.
 * Provides custom query methods related to adoption records.

 * Methods in this interface enable querying adoption records by user and pet IDs.
 */
@Repository
public interface AdoptionRepository extends JpaRepository<Adoption, Long> {

    @Query("SELECT a FROM Adoption a JOIN FETCH a.user WHERE a.adoptionId = :adoptionId")
    Optional<Adoption> findByIdWithUser(@Param("adoptionId") Long adoptionId);
    List<Adoption> findByPetId(Long petId);

}
