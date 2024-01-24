package com.ardaslegends.presentation.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenAuthResponse(
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("token_type")
        String tokenType,
        @JsonProperty("expires_in")
        String expiresIn,
        @JsonProperty("refresh_token")
        String refreshToken,
        String[] scope
) {
}
