package com.petadoption.service;

import com.petadoption.customexceptions.pet.*;
import com.petadoption.model.Pet;
import com.petadoption.repository.PetRepository;
import com.petadoption.model.enums.PetStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


/**
 * Service class for managing Pet entities. Provides business logic for operations
 * such as creating, retrieving, updating, and deleting pets, as well as handling
 * pet-specific validations. This class interacts with the database layer through
 * the PetRepository interface.

 * Key functionalities include:
 * - Creating new pets with default status and validations
 * - Retrieving pets based on availability or location
 * - Updating pet details with validations
 * - Deleting pets by ID
 * - Managing pet-specific validation logic

 * Marked as a transactional service to ensure atomicity and consistency during
 * database operations.
 */
@Service
@Transactional
public class PetService {

    private static final Logger logger = LoggerFactory.getLogger(PetService.class);

    private final PetRepository petRepository;

    @Autowired
    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public Pet create(Pet pet) {
        if (pet.getStatus() == null) {
            pet.setStatus(PetStatus.AVAILABLE);
        }

        validatePetOnCreate(pet);
        Pet createdPet = petRepository.save(pet);
        logger.info("Created pet with id {}", createdPet.getId());
        return createdPet;
    }

    @Transactional(readOnly = true)
    public List<Pet> getAllAvailablePets() {
        return petRepository.findByStatus(PetStatus.AVAILABLE);
    }

    //TODO add endpoint to check list of adopted pets
    @Transactional(readOnly = true)
    public List<Pet> getAllAdoptedPets() {
        return petRepository.findByStatus(PetStatus.ADOPTED);
    }

    @Transactional(readOnly = true)
    public Optional<Pet> getPetById(Long id) {
        Optional<Pet> pet = petRepository.findById(id);
        if (pet.isEmpty()) {
            logger.warn("Pet not found with id {}", id);
            throw new PetIdValidationException("Pet not found with ID " + id);
        }
        logger.info("Retrieve pet with ID {}", id);
        return pet;
    }

    public List<Pet> getEnabledPetsByLocation(String location) {
        List<Pet> pets = petRepository.findByLocation(location);
        logger.info("Retrieved pets by location");
        return pets;
    }

    public boolean hasAvailablePets(String location) {
        List<Pet> pets = petRepository.findByLocation(location);
        return !pets.isEmpty();
    }

    public Pet updatePet(Long id, Pet petDetails) {
        Pet petToUpdate = petRepository.findById(id).orElseThrow(
                () -> new PetIdValidationException("Pet not found with ID " + id)
        );
        validatePet(petDetails);
        petToUpdate.setName(petDetails.getName());
        petToUpdate.setTypeOfPet(petDetails.getTypeOfPet());
        petToUpdate.setAge(petDetails.getAge());
        petToUpdate.setStatus(petDetails.getStatus());
        petToUpdate.setLocation(petDetails.getLocation());

        Pet updatedPet = petRepository.save(petToUpdate);
        logger.info("Updated pet with id {}", id);
        return updatedPet;
    }

    public void deletePetById(Long id) {
        if (!petRepository.existsById(id)) {
            throw new PetNotFoundException("Pet not found with ID " + id);
        }
        petRepository.deleteById(id);
    }

    private void validatePetOnCreate(Pet pet) {
        validateName(pet.getName());
        validateAge(pet.getAge());
        validateAvailability(pet.getStatus());
    }

    private void validatePet(Pet pet) {
        validateName(pet.getName());
        validateAge(pet.getAge());
        validateId(pet.getId());

        validateAvailability(pet.getStatus());
    }

    private void validateId(Long id) {
        if (id == null || id < 0) {
            throw new PetIdValidationException("Pet ID must not be null or negative");
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new PetNameValidationException("Pet name must not be null or empty");
        }
    }

    private void validateAge(int age) {
        if (age < 0) {
            throw new PetAgeValidationException("Pet age must not be less than 0");
        }
    }

    private void validateAvailability(PetStatus status) {
        if (status == null) {
            throw new PetAvailabilityValidationException("Pet is not available for adoption");
        } else if (status != PetStatus.AVAILABLE) {
            throw new PetAvailabilityValidationException("Pet is not available for adoption");
        }
    }

    private void validateLocationAvailability(String location, Boolean enable) {
        if (enable == null || !enable) {
            throw new PetAvailabilityValidationException("Pet is not available for adoption");
        }
    }
}
