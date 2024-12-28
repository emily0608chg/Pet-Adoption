package com.petadoption.model.enums;

/**
 * Enum representing the status of a pet within the system.
 * This enum is used to indicate the current state of a pet, particularly
 * in scenarios such as adoption processing or pet management.

 * The possible statuses are:
 * - AVAILABLE: The pet is available for adoption.
 * - ADOPTED: The pet has been successfully adopted.
 * - DISABLED: The pet is no longer available for adoption or has been removed.
 */
public enum PetStatus {
    AVAILABLE,
    ADOPTED,
    DISABLED
}
