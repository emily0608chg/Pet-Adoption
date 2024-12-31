package com.petadoption.controller;

import com.petadoption.dto.token.RefreshTokenRequestDTO;
import com.petadoption.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller responsible for handling token-related operations,
 * specifically for refreshing access tokens using a provided refresh token.

 * This controller exposes an endpoint that accepts a refresh token in the request body
 * and returns a new access token if the provided refresh token is valid and not expired.
 * It leverages the {@code JwtService} to perform the token-refresh logic.

 * This controller is mapped to the path "/api/token".
 */
@RestController
@RequestMapping("/api/token")
public class TokenController {

    private final JwtService jwtService;

    public TokenController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Handles the HTTP POST request for refreshing an access token using a provided refresh token.
     * Validates the refresh token from the request body, and if valid, generates and returns a new access token.
     * Returns an appropriate response status and message in case of invalid input, expired token, or unexpected errors.
     *
     * @param refreshTokenRequest the request body containing the refresh token required for generating a new access token.
     *                            The refresh token must not be null or blank, as it is essential for authorization.
     * @return a ResponseEntity containing a new access token in the response body if the refresh token is valid.
     *         Returns HTTP 400 (BAD_REQUEST) if the refresh token is missing or invalid.
     *         Returns HTTP 403 (FORBIDDEN) if the refresh token is expired or invalid.
     *         Returns HTTP 500 (INTERNAL_SERVER_ERROR) if an unexpected error occurs.
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();

        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Refresh token is required"));
        }

        try {
            // Generate a new Access Token from the Refresh Token
            String newAccessToken = jwtService.refreshAccessToken(refreshToken);

            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } catch (JwtException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Invalid or expired refresh token"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred"));
        }
    }
}
