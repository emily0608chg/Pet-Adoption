package com.petadoption.customexceptions;

import com.petadoption.customexceptions.adoption.AdoptionIdValidationException;
import com.petadoption.customexceptions.adoption.AdoptionNotFoundException;
import com.petadoption.customexceptions.adoption.AdoptionStatusException;
import com.petadoption.customexceptions.pet.*;
import com.petadoption.customexceptions.user.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler is a centralized exception handling class annotated with
 * @RestControllerAdvice to manage specific exceptions in an application.
 *
 * This class handles exceptions thrown by different modules such as user management,
 * pet management, adoption module, as well as generic and security-related exceptions.
 * Each handler maps a specific exception type to an appropriate
 * HTTP status code and response body.
 *
 * Features:
 * - Consistent error handling across the entire application.
 * - Maps custom exceptions, such as UserNotFoundException or PetNotFoundException,
 *   to specific HTTP status codes.
 * - Provides detailed responses for validation errors, including field-specific details.
 * - Handles generic exceptions and ensures an internal server error response for unexpected exceptions.
 * - Enhances security by responding adequately to access-denied exceptions.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    //User Exception Handler

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNameValidationException.class)
    public ResponseEntity<String> handleUserNameValidationException(UserNameValidationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserEmailValidationException.class)
    public ResponseEntity<String> handleUserEmailValidationException(UserEmailValidationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserPhoneValidationException.class)
    public ResponseEntity<String> handleUserPhoneValidationException(UserPhoneValidationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserIdValidationException.class)
    public ResponseEntity<String> handleUserIdValidationException(UserIdValidationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //Pet Exception Handler

    @ExceptionHandler(PetNotFoundException.class)
    public ResponseEntity<String> handlePetNotFoundException(PetNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PetIdValidationException.class)
    public ResponseEntity<String> handlePetIdValidationException(PetIdValidationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PetNameValidationException.class)
    public ResponseEntity<String> handlePetNameValidationException(PetNameValidationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PetAgeValidationException.class)
    public ResponseEntity<String> handlePetAgeValidationException(PetAgeValidationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PetAvailabilityValidationException.class)
    public ResponseEntity<String> handlePetAvailabilityValidationException(PetAvailabilityValidationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AdoptionNotFoundException.class)
    public ResponseEntity<String> handleAdoptionNotFoundException(AdoptionNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    //Adoption Exception Handler

    @ExceptionHandler(AdoptionIdValidationException.class)
    public ResponseEntity<String> handleAdoptionIdValidationException(AdoptionIdValidationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AdoptionStatusException.class)
    public ResponseEntity<String> handleAdoptionStatusException(AdoptionStatusException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //Generic Exception Handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // Collect all validation errors
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException e) {
        return new ResponseEntity<>("You are not authorized", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleAuthenticationException(RuntimeException ex) {
        return ResponseEntity.status(401)
                .body(Map.of("error", ex.getMessage()));
    }
}
