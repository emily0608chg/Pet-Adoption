package com.petadoption.customexceptions.user;

public class UserPhoneValidationException extends UserNotFoundException {

    public UserPhoneValidationException(String message) {
        super(message);
    }
}
