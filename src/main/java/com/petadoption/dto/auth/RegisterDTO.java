package com.petadoption.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) used for registering a new user.
 * Encapsulates user data required during the registration process.
 * Provides validation annotations to ensure the correctness of the input fields.

 * Fields:
 * - username: Required, must not be blank.
 * - password: Required, must not be blank and should have a minimum of 8 characters.
 * - name: Required, must not be blank.
 * - email: Required, must be a valid email format.
 * - phone: Required, must not be blank.
 * - adminKey: Optional, used for administrative purposes.

 * This class uses validation constraints from the javax.validation package to enforce
 * validation rules for its fields.
 */
public class RegisterDTO {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "The password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "The name is required")
    private String name;

    @Email(message = "You must provide a valid email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Telephone number is required")
    private String phone;

    private String adminKey; // Optional, for administrative management.

    // Getters y Setters
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAdminKey() {
        return adminKey;
    }

    public void setAdminKey(String adminKey) {
        this.adminKey = adminKey;
    }

}
