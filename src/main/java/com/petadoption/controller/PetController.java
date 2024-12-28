package com.petadoption.controller;

import com.petadoption.customexceptions.pet.PetInvalidedType;
import com.petadoption.customexceptions.pet.PetNotFoundException;
import com.petadoption.model.Pet;
import com.petadoption.model.TypeOfPet;
import com.petadoption.service.PetService;
import com.petadoption.service.TypeOfPetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller for managing pet-related operations. Provides endpoints for creating, retrieving,
 * updating, and deleting pets, as well as querying available pets based on location or status.

 * This controller interacts with the {@link PetService} and {@link TypeOfPetService} to handle
 * business logic and persistence layer operations. It also includes authorization for specific
 * operations using role-based access control.

 * Endpoints:
 * - Register a new pet
 * - Retrieve a list of pets (with optional filtering by location)
 * - Retrieve a specific pet by ID
 * - Update pet details
 * - Delete a pet

 * Role-Based Access Control:
 * - Specific endpoints require the ADMIN role to perform certain operations such as
 *   registering, updating, or deleting pets.

 * Validation:
 * - Ensures that the `typeOfPet` provided during pet creation has a valid ID.
 * - Throws appropriate exceptions for invalid or missing data, such as `PetInvalidedType` or
 *   `RuntimeException` for invalid `TypeOfPet` IDs.
 */
@RestController
@RequestMapping("api/pets")
public class PetController {

    private final PetService petService;
    private final TypeOfPetService typeOfPetService;

    @Autowired
    public PetController(PetService petService, TypeOfPetService typeOfPetService) {
        this.petService = petService;
        this.typeOfPetService = typeOfPetService;
    }

    //Register a new pet
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Pet> registerPet(@RequestBody Pet pet) {

        // Check if `typeOfPet` has a valid ID
        if (pet.getTypeOfPet() == null || pet.getTypeOfPet().getId() == null) {
            throw new PetInvalidedType("TypeOfPet must be provided with a valid ID.");
        }

        // Search the TypeOfPet in the database
        TypeOfPet typeOfPet = typeOfPetService
                .getTypeOfPetById(pet.getTypeOfPet().getId())
                .orElseThrow(() -> new RuntimeException("Invalid TypeOfPet ID")); // Throw exception if it does not exist
        pet.setTypeOfPet(typeOfPet); // Associate the existing TypeOfPet

        // Create the pet
        Pet createdPet = petService.create(pet);
        return new ResponseEntity<>(createdPet, HttpStatus.CREATED);
    }

    //Consult pet availability for adoption
    @GetMapping
    public ResponseEntity<List<Pet>> getPets(@RequestParam(required = false) String location) {
        List<Pet> pets;

        if (location != null && !location.isBlank()) {
            pets = petService.getEnabledPetsByLocation(location);
        } else {
            pets = petService.getAllAvailablePets();
        }
        return new ResponseEntity<>(pets, HttpStatus.OK);
    }

    //Get pet by ID
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Pet> getPetById(@PathVariable Long id) {
        Optional<Pet> pet = petService.getPetById(id);
        return pet.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    //Update pet details
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Pet> updatePet(@PathVariable Long id, @RequestBody Pet petDetails) {
        try {
            Pet updatedPet = petService.updatePet(id, petDetails);
            return new ResponseEntity<>(updatedPet, HttpStatus.OK);
        } catch (PetNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //Delete pet
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Pet> deletePet(@PathVariable Long id) {
        try {
            petService.deletePetById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (PetNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
