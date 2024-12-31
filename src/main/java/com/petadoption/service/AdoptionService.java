package com.petadoption.service;

import com.petadoption.customexceptions.adoption.AdoptionNotFoundException;
import com.petadoption.customexceptions.adoption.AdoptionStatusException;
import com.petadoption.customexceptions.pet.PetIdValidationException;
import com.petadoption.customexceptions.pet.PetNotFoundException;
import com.petadoption.customexceptions.user.UserIdValidationException;
import com.petadoption.customexceptions.user.UserNotFoundException;
import com.petadoption.model.Adoption;
import com.petadoption.model.Pet;
import com.petadoption.model.User;
import com.petadoption.repository.AdoptionRepository;
import com.petadoption.repository.PetRepository;
import com.petadoption.repository.UserRepository;
import com.petadoption.model.enums.PetStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

/**
 * Service class to manage Adoption operations.

 * This service encapsulates the business logic and facilitates
 * interaction between the application and the data persistence
 * layer for operations related to Adoption entities.

 * All methods in this service class are transactional to ensure
 * data consistency and integrity.
 */
@Service
@Transactional
public class AdoptionService {

    private static final Logger logger = LoggerFactory.getLogger(AdoptionService.class);

    private final AdoptionRepository adoptionRepository;
    private final PetRepository petRepository; // Agregar esta línea
    private final UserRepository userRepository;

    @Autowired
    public AdoptionService(AdoptionRepository adoptionRepository, PetRepository petRepository, UserRepository userRepository) {
        this.adoptionRepository = adoptionRepository;
        this.petRepository = petRepository;
        this.userRepository = userRepository;
    }

    public Adoption create(Adoption adoption) {
        validateAdoption(adoption);

        // Search for the associated user by ID
        Long userId = adoption.getUser().getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));

        // Search for the associated pet by ID
        Long petId = adoption.getPet().getId();
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetNotFoundException("Pet not found with id " + petId));

        // Assign the entities correctly
        adoption.setUser(user);
        adoption.setPet(pet);

        // Assigns current date if not present
        if (adoption.getAdoptionDate() == null) {
            adoption.setAdoptionDate(new Date());
        }

        Adoption createdAdoption = adoptionRepository.save(adoption);
        logger.info("Created adoption with id {}", createdAdoption.getAdoptionId());
        return createdAdoption;
    }

    @Transactional(readOnly = true)
    public Optional<Adoption> getAdoptionById(Long id) {
        Optional<Adoption> adoption = adoptionRepository.findByIdWithUser(id);
        if (adoption.isEmpty()) {
            logger.warn("Adoption with ID {} not found", id);
            throw new AdoptionNotFoundException("Adoption not found with ID " + id);
        }
        // Check if the authenticated user is the owner
        String username = SecurityContextHolder.getContext().getAuthentication().getName(); // Actual user
        if (!adoption.get().getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not authorized to access this adoption.");
        }

        logger.info("Retrieved adoption with ID {}", id);
        return adoption;
    }


    @Transactional(readOnly = true)
    public Iterable<Adoption> getAllAdoptions() {
        Iterable<Adoption> adoptions = adoptionRepository.findAll();
        logger.info("Retrieved {} adoptions", adoptions.spliterator().getExactSizeIfKnown());
        return adoptions;
    }


    public Adoption updateAdoption(Long id, Adoption updateAdoption) {
        validateAdoption(updateAdoption);
        return adoptionRepository.findById(id)
                .map(adoption -> {
                    adoption.setPet(updateAdoption.getPet());
                    adoption.setUser(updateAdoption.getUser());
                    adoption.setStatus(updateAdoption.getStatus());
                    Adoption updatedAdoption = adoptionRepository.save(adoption);
                    logger.info("Updated adoption with id {}", updateAdoption.getAdoptionId());
                    return updatedAdoption;
                })
                .orElseGet(() -> {
                    updateAdoption.setAdoptionId(id);
                    Adoption newAdoption = adoptionRepository.save(updateAdoption);
                    logger.info("New created adoption with id {}", newAdoption.getAdoptionId());
                    return newAdoption;
                });
    }

    public void deleteAdoptionById(Long id) {
        if (!adoptionRepository.existsById(id)) {
            logger.warn("Adoption with ID {} not found for deletion", id);
            throw new AdoptionNotFoundException("Adoption not found with ID " + id);
        }
        adoptionRepository.deleteById(id);
        logger.info("Deleted adoption with ID {}", id);
    }

    @Transactional
    public Adoption approveAdoption(Long id) {
        return adoptionRepository.findById(id)
                .map(adoption -> {
                    logger.info("Adoption retrieved: {}", adoption);

                    adoption.setStatus("APPROVED");

                    Pet pet = adoption.getPet();
                    if (pet != null) {
                        logger.info("Retrieved Pet: {}", pet);
                        pet.setStatus(PetStatus.ADOPTED);

                        logger.info("Saving updated pet status...");
                        petRepository.save(pet); // Aquí validamos el guardado
                        logger.info("Pet updated successfully");
                    } else {
                        logger.warn("Pet is null for adoption with id {}", id);
                    }

                    Adoption approvedAdoption = adoptionRepository.save(adoption);
                    logger.info("Approved adoption with id {}", approvedAdoption.getAdoptionId());
                    return approvedAdoption;
                })
                .orElseThrow(() -> new AdoptionNotFoundException("Adoption not found with id " + id));
    }

    @Transactional
    public Adoption rejectAdoption(Long id) {
        return adoptionRepository.findById(id)
                .map(adoption -> {
                    // Change adoption status to REJECTED
                    adoption.setStatus("REJECTED");
                    logger.info("Adoption with id {} marked as REJECTED", id);

                    // Change the status of the associated pet to AVAILABLE
                    Pet pet = adoption.getPet();
                    if (pet != null) {
                        logger.info("Updating pet with id {} to AVAILABLE", pet.getId());
                        pet.setStatus(PetStatus.AVAILABLE);
                        petRepository.save(pet); // Save the changes
                        logger.info("Pet with id {} updated to AVAILABLE", pet.getId());
                    } else {
                        logger.warn("No pet associated with adoption id {}", id);
                    }

                    // Save adoption changes
                    Adoption updatedAdoption = adoptionRepository.save(adoption);
                    logger.info("Rejected adoption with id {}", updatedAdoption.getAdoptionId());
                    return updatedAdoption;
                })
                .orElseThrow(() -> new AdoptionNotFoundException("Adoption not found with id " + id));
    }

    private void validateAdoption(Adoption adoption) {
        if (adoption == null) {
            throw new IllegalArgumentException("Adoption object must not be null.");
        }
        validateUser(adoption.getUser());
        validatePet(adoption.getPet());
        validateAdoptionStatus(adoption.getStatus());
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserIdValidationException("User object is null.");
        }
        if (user.getId() == null || user.getId() < 0) {
            throw new UserIdValidationException("User ID " + user.getId() + " must not be null or negative.");
        }
    }

    private void validatePet(Pet pet) {
        if (pet == null || pet.getId() == null || pet.getId() < 0) {
            throw new PetIdValidationException("Pet ID must not be null or negative");
        }
    }

    private void validateAdoptionStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new AdoptionStatusException("Status must not be null or empty");
        }
    }
}