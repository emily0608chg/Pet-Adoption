package com.petadoption.customexceptions.pet;

public class PetAgeValidationException extends PetNotFoundException {

    public PetAgeValidationException(String message) {
        super(message);
    }
}
