package com.petadoption.customexceptions.user;

public class UserIdValidationException extends UserNotFoundException {

    public UserIdValidationException(String message) {
        super(message);
    }
}
