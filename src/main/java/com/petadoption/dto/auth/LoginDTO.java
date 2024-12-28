package com.petadoption.dto.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object (DTO) for user login operations.
 * This class is used to encapsulate the required login details such as
 * username and password for authentication processes.

 * Validation annotations ensure that both `username` and `password` fields are mandatory.
 * They provide error messages if the requirements are not met.
 */
public class LoginDTO {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
