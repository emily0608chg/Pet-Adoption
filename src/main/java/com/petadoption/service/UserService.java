package com.petadoption.service;

import com.petadoption.customexceptions.user.*;
import com.petadoption.model.User;
import com.petadoption.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Service class responsible for managing users and related operations.
 * This class provides methods for CRUD operations, authentication,
 * role assignment, and validation logic for user entities.
 * Additionally, it integrates with external services like PetService
 * to perform cross-functional operations.

 * The class is transactional and relies on Spring's dependency injection
 * mechanism for instantiating its dependencies.
 */
@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PetService petService;
    private final PasswordEncoder passwordEncoder;
    @Value("${app.admin-key}")
    private String adminSecretKey;

    @Autowired
    public UserService(UserRepository userRepository, PetService petService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.petService = petService;
        this.passwordEncoder = passwordEncoder;
    }

    public List<String> authenticate(String username, String password) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            throw new RuntimeException("User has no roles assigned");
        }

        logger.info("Authenticated user: {} with roles: {}", username, user.getRoles());


        for (String role : user.getRoles()) {
            System.out.println("Role: " + role);
        }

        return new ArrayList<>(user.getRoles());
    }

    private Set<String> determineRoles(Optional<String> adminKey) {
        if (adminKey.isPresent() && adminKey.get().equals(adminSecretKey)) {
            return Set.of("ROLE_ADMIN");
        }
        return Set.of("ROLE_USER");
    }


    public User createUser(User user, Optional<String> adminKey) {
        validateUser(user, false);

        user.setRoles(determineRoles(adminKey));

        // Encriptar la contraseña antes de guardar
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User createdUser = userRepository.save(user);
        logger.info("Created user with id {}", createdUser.getId());
        return createdUser;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        logger.info("Retrieved {} users", users.size());
        return users;
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            logger.warn("User not found with id {}", id);
            throw new UserNotFoundException("User not found with ID " + id);
        }
        logger.info("Retrieved user with ID {}", id);
        return user;
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    public User updateUser(User user) {
        User userToUpdate = userRepository.findById(user.getId()).orElseThrow(
                () -> new UserNotFoundException("User not found with ID " + user.getId())
        );
        validateUser(user, true);
        userToUpdate.setName(user.getName());
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setPhone(user.getPhone());

        User updatedUser = userRepository.save(userToUpdate);
        logger.info("Updated user with id {}", updatedUser.getId());
        return updatedUser;
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            logger.warn("User with ID {} not found for deletion", id);
            throw new UserNotFoundException("User not found with ID " + id);
        }
        userRepository.deleteById(id);
        logger.info("Deleted user with ID {}", id);
    }

    @Transactional(readOnly = true)
    public boolean hasAvailablePets(String location) {
        boolean available = petService.hasAvailablePets(location);
        if (available) {
            logger.info("There are pets available in this location {}", location);
        } else {
            logger.info("There are no pets available in this location {}", location);
        }
        return available;
    }

    private void validateUser(User user, boolean isUpdating) {
        if (user.getName() == null || user.getName().isBlank()) {
            throw new UserNameValidationException("Name must not be null or empty");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().matches("^[\\w-.]+@([\\w-]+.)+[\\w-]{2,4}$")) {
            throw new UserEmailValidationException("Email is invalid");
        }
        if (user.getPhone() == null || user.getPhone().isBlank()) {
            throw new UserPhoneValidationException("Phone must not be null or empty");
        }
        if (isUpdating && (user.getId() == null || user.getId() < 0)) {
            throw new UserIdValidationException("User ID must not be null or negative");
        }
    }
}
