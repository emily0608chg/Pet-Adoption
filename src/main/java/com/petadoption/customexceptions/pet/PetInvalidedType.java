package com.petadoption.customexceptions.pet;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PetInvalidedType extends RuntimeException{

    public PetInvalidedType(String message) {
        super(message);
    }
}
