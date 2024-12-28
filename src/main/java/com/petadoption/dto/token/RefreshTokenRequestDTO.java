package com.petadoption.dto.token;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object (DTO) representing a request to refresh an access token.
 * This class encapsulates the refresh token required to generate a new access token.
 * It ensures that the provided refresh token is non-null and not blank, as validated by the `@NotBlank` annotation.

 * The refresh token is expected to be a valid and unexpired JWT used to authorize the renewal of an access token.
 * This DTO is typically used in conjunction with token renewal endpoints in an API.
 */
public class RefreshTokenRequestDTO {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
