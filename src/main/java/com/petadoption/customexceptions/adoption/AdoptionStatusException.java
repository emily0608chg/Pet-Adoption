package com.petadoption.customexceptions.adoption;

public class AdoptionStatusException extends AdoptionNotFoundException{

    public AdoptionStatusException(String message) {
        super(message);
    }
}
