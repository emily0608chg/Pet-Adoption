package com.petadoption.customexceptions.adoption;

public class AdoptionNotFoundException extends RuntimeException{
    public AdoptionNotFoundException(String message) {
        super(message);
    }
}
