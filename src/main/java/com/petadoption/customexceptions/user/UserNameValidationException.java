package com.petadoption.customexceptions.user;

public class UserNameValidationException extends UserNotFoundException {

    public UserNameValidationException(String message) {
        super(message);
    }
}
