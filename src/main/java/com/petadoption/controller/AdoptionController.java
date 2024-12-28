package com.petadoption.controller;

import com.petadoption.model.Adoption;
import com.petadoption.service.AdoptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Handles operations related to adoptions, such as creation, retrieval, updating, and deletion of adoption records.
 * Provides endpoints for authorized users and administrators to manage adoption processes.
 */
@RestController
@RequestMapping("api/adoption")
public class AdoptionController {

    private final AdoptionService adoptionService;

    public AdoptionController(AdoptionService adoptionService) {
        this.adoptionService = adoptionService;
    }

    // Register a new adoption
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<Adoption> registerAdoption(@Valid @RequestBody Adoption adoption) {
        Adoption createdAdoption = adoptionService.create(adoption);
        return new ResponseEntity<>(createdAdoption, HttpStatus.CREATED);
    }

    // Get an adoption by ID (only admin or owner can access this)
    @PreAuthorize("hasRole('ADMIN') or @adoptionSecurityService.isOwner(authentication, #id)")
    @GetMapping("/{id}")
    public ResponseEntity<Adoption> getAdoptionById(@PathVariable Long id) {
        Optional<Adoption> adoption = adoptionService.getAdoptionById(id);
        return adoption.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Get all adoptions (only admin can access this)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Iterable<Adoption>> getAllAdoptions() {
        Iterable<Adoption> adoptions = adoptionService.getAllAdoptions();
        return new ResponseEntity<>(adoptions, HttpStatus.OK);
    }

    // Update an existing adoption (only admin or owner can access this)
    @PreAuthorize("hasRole('ADMIN') or @adoptionSecurityService.isOwner(authentication, #id)")
    @PutMapping("/{id}")
    public ResponseEntity<Adoption> updateAdoption(@PathVariable Long id, @RequestBody Adoption updateAdoption) {
        Adoption updatedAdoption = adoptionService.updateAdoption(id, updateAdoption);
        return new ResponseEntity<>(updatedAdoption, HttpStatus.OK);
    }

    // Delete an adoption by ID (only admin can access this)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdoptionById(@PathVariable Long id) {
        adoptionService.deleteAdoptionById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Approve an adoption (only admin can access this)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<Adoption> approveAdoption(@PathVariable Long id) {
        Adoption approvedAdoption = adoptionService.approveAdoption(id);
        return new ResponseEntity<>(approvedAdoption, HttpStatus.OK);
    }

    // Reject an adoption (only admin can access this)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/reject")
    public ResponseEntity<Adoption> rejectAdoption(@PathVariable Long id) {
        Adoption rejectedAdoption = adoptionService.rejectAdoption(id);
        return new ResponseEntity<>(rejectedAdoption, HttpStatus.OK);
    }
}
