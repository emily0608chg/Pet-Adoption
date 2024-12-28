package com.petadoption.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Service class responsible for generating, verifying, and refreshing JSON Web Tokens (JWTs).

 * This class encapsulates the logic for creating access and refresh tokens,
 * validating refresh tokens, and generating access tokens based on valid refresh tokens.
 * It uses RSA-based encryption to ensure secure signing and verification of tokens.

 * The class depends on `JwtEncoder` for signing tokens and `JwtDecoder` for verifying tokens.
 */
@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    /*
    Used to sign tokens with the RSA private key
     */
    private final JwtEncoder jwtEncoder;
    /*
    Used to verify tokens with the RSA public key
     */
    private final JwtDecoder jwtDecoder;

    public JwtService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    /**
     * Generates a JWT access token for a given user with their respective roles.
     *
     * @param username the username of the user for whom the token is being generated.
     * @param roles the list of roles associated with the user. Duplicate roles will be sanitized,
     *              and the prefix "ROLE_" (if present) will be removed.
     * @return the generated JWT access token as a String.
     * @throws RuntimeException if there is an error during the token generation process.
     */
    public String generateToken(String username, List<String> roles) {
        try {
            logger.info("Generating access token for the user: {}", username);

            List<String> sanitizedRoles = roles.stream()
                    .map(role -> role.replace("ROLE_", ""))
                    .distinct()
                    .toList();

            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer("self")
                    .issuedAt(Instant.now())
                    .expiresAt(Instant.now().plusSeconds(3600)) // Expire in one hour
                    .subject(username)
                    .claim("roles", sanitizedRoles)
                    .build();

            return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        } catch (Exception e) {
            logger.error("Error generating JWT token: {}", e.getMessage(), e);
            throw new RuntimeException("Internal error generating token");
        }
    }

    /**
     * Generates a JWT refresh token for a specified user with their associated roles.
     *
     * @param username the username of the user for whom the refresh token is being generated.
     * @param roles the list of roles associated with the user.
     * @return the generated JWT refresh token as a String.
     * @throws RuntimeException if an error occurs during the token generation process.
     */
    public String generateRefreshToken(String username, List<String> roles) {
        try {
            logger.info("Generating refresh token for user: {}", username);

            Instant now = Instant.now();
            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer("self")
                    .issuedAt(now)
                    .expiresAt(now.plusSeconds(604800)) // Expira en 7 días
                    .subject(username)
                    .claim("roles", roles)
                    .build();

            return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        } catch (Exception e) {
            logger.error("Error generating JWT refresh token: {}", e.getMessage(), e);
            throw new RuntimeException("Internal error generating refresh token");
        }
    }

    /**
     * Refreshes the access token using the provided refresh token.
     *
     * @param refreshToken the refresh token to be used for generating a new access token.
     *                      It must be a valid and non-expired JWT containing the username and roles.
     * @return the new access token as a String.
     * @throws JwtException if the refresh token is invalid, expired, or lacks necessary claims.
     * @throws RuntimeException if an unexpected error occurs during the process.
     */
    public String refreshAccessToken(String refreshToken) {
        try {
            logger.info("Trying to refresh access token...");

            var jwt = jwtDecoder.decode(refreshToken);

            String username = jwt.getSubject();
            if (username == null || username.isBlank()) {
                throw new JwtException("Refresh token does not contain a valid user");
            }

            Instant expiration = jwt.getExpiresAt();
            if (expiration == null || Instant.now().isAfter(expiration)) {
                throw new JwtException("Refresh token has expired");
            }

            List<String> roles = jwt.getClaim("roles");
            if (roles == null || roles.isEmpty()) {
                throw new JwtException("No roles found in refresh token");
            }

            return generateToken(username, roles);

        } catch (JwtException e) {
            logger.error("Error refreshing the token: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error updating access token: {}", e.getMessage(), e);
            throw new RuntimeException("Error inesperado al refrescar el access token.");
        }
    }
}
