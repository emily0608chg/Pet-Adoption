package com.petadoption.customexceptions.pet;

public class PetIdValidationException extends PetNotFoundException {

    public PetIdValidationException(String message) {
        super(message);
    }
}
