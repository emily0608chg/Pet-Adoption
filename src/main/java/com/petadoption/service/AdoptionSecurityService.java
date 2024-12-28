package com.petadoption.service;

import com.petadoption.repository.AdoptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * AdoptionSecurityService is responsible for handling security-related aspects of the
 * adoption process. It ensures that only authorized users can access or manage specific
 * adoption, such as verifying ownership of an adoption entity.

 * This service uses the AdoptionRepository to interact with adoption data in the
 * persistence layer. It validates whether the authenticated user matches the owner of
 * a specific adoption.

 * Components:
 * - AdoptionRepository: Repository for accessing adoption-related data.
 * - isOwner: Method to check if an authenticated user is the owner of a given adoption.

 * Annotations:
 * - Service: Indicates that this class is a service component in the Spring framework.
 * - Autowired: Used to inject the AdoptionRepository dependency automatically.
 */
@Service
public class AdoptionSecurityService {

    private final AdoptionRepository adoptionRepository;

    @Autowired
    public AdoptionSecurityService(AdoptionRepository adoptionRepository) {
        this.adoptionRepository = adoptionRepository;
    }

    public boolean isOwner(Authentication authentication, Long adoptionId) {
        String username = authentication.getName(); // Current username
        return adoptionRepository.findById(adoptionId)
                .map(adoption -> adoption.getUser().getUsername().equals(username))
                .orElse(false);
    }
}
