package com.petadoption.customexceptions.pet;

public class PetNotFoundException extends RuntimeException  {

    public PetNotFoundException(String message) {
        super(message);
    }
}
