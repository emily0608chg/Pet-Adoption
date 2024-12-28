package com.petadoption.customexceptions.pet;

public class PetNameValidationException extends PetNotFoundException {

    public PetNameValidationException(String message) {
        super(message);
    }
}
