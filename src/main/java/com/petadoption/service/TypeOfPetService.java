package com.petadoption.service;

import com.petadoption.model.TypeOfPet;
import com.petadoption.repository.TypeOfPetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing operations related to the {@link TypeOfPet} entity.
 * This class provides methods for retrieving, saving, and fetching all types of pets.
 * It acts as a business logic layer to interact with the {@link TypeOfPetRepository}.
 *
 * The service methods are designed to be utilized in controllers or other service layers
 * where interaction with {@link TypeOfPet} objects is required.
 */
@Service
public class TypeOfPetService {

    private final TypeOfPetRepository typeOfPetRepository;

    @Autowired
    public TypeOfPetService(TypeOfPetRepository typeOfPetRepository) {
        this.typeOfPetRepository = typeOfPetRepository;
    }

    public Optional<TypeOfPet> getTypeOfPetById(Long id) {
        return typeOfPetRepository.findById(id);
    }


    public TypeOfPet saveTypeOfPet(TypeOfPet typeOfPet) {
        return typeOfPetRepository.save(typeOfPet);
    }

    public List<TypeOfPet> getAllTypesOfPets() {
        return typeOfPetRepository.findAll();
    }
}
