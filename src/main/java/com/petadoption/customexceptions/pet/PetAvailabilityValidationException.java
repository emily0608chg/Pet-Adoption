package com.petadoption.customexceptions.pet;

public class PetAvailabilityValidationException extends PetNotFoundException {

    public PetAvailabilityValidationException(String message) {
        super(message);
    }
}
