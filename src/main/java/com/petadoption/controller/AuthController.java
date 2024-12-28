package com.petadoption.controller;

import com.petadoption.dto.auth.LoginDTO;
import com.petadoption.dto.auth.RegisterDTO;
import com.petadoption.model.User;
import com.petadoption.service.JwtService;
import com.petadoption.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A REST controller that handles authentication requests, such as user registration and login.
 * It provides endpoints for registering new users and authenticating existing users, leveraging
 * JWT (JSON Web Token) for access and refresh token generation.

 * This controller interacts with the `JwtService` to generate tokens and the `UserService`
 * to manage user authentication and registration logic.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO payload) {
        try {
            // Create a new User object from the DTO
            User user = new User();
            user.setUsername(payload.getUsername());
            user.setPassword(payload.getPassword());
            user.setName(payload.getName());
            user.setEmail(payload.getEmail());
            user.setPhone(payload.getPhone());

            // Check if there is adminKey
            Optional<String> adminKey = Optional.ofNullable(payload.getAdminKey());

            // Create user using service
            User createdUser = userService.createUser(user, adminKey);

            // Build structure response
            Map<String, Object> response = Map.of(
                    "id", createdUser.getId(),
                    "username", createdUser.getUsername(),
                    "roles", createdUser.getRoles() != null ? createdUser.getRoles() : List.of()
            );

            return ResponseEntity.status(201).body(response);
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginDTO payload) {
        String username = payload.getUsername();
        String password = payload.getPassword();

        try {
            // Validate credentials with UserService
            List<String> roles = userService.authenticate(username, password);

            // Generate Access and Refresh Tokens
            String accessToken = jwtService.generateToken(username, roles);
            String refreshToken = jwtService.generateRefreshToken(username, roles);

            return ResponseEntity.ok(Map.of(
                    "accessToken", accessToken,
                    "refreshToken", refreshToken
            ));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(401).body(Map.of("error", ex.getMessage()));
        }
    }
}