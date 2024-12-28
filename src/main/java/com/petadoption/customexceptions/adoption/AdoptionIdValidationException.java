package com.petadoption.customexceptions.adoption;

public class AdoptionIdValidationException extends AdoptionNotFoundException{

    public AdoptionIdValidationException(String message) {
        super(message);
    }
}
