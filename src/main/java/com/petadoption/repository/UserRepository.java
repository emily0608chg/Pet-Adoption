package com.petadoption.repository;

import com.petadoption.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for performing CRUD operations and custom queries
 * on the User entity.

 * This interface extends JpaRepository, which provides generic methods to
 * handle database interactions, such as saving, deleting, and finding entities.
 * It also includes custom query methods for retrieving users by username or email.

 * This repository is used as a dependency in services like UserService and
 * AdoptionService, enabling user management and supporting related business logic.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
