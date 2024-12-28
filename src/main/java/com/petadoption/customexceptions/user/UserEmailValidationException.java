package com.petadoption.customexceptions.user;

public class UserEmailValidationException extends  UserNotFoundException {

    public UserEmailValidationException(String message) {
        super(message);
    }
}
