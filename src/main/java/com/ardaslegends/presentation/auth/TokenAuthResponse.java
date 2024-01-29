package com.ardaslegends.presentation.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenAuthResponse(
        String accessToken,
        String tokenType,
        String expiresIn,
        String refreshToken,
        String[] scope
) {

        public TokenAuthResponse(@JsonProperty("access_token") String accessToken,
                                 @JsonProperty("token_type") String tokenType,
                                 @JsonProperty("expires_in") String expiresIn,
                                 @JsonProperty("refresh_token") String refreshToken,
                                 @JsonProperty("scope") String scope) {
            this(
                    accessToken,
                    tokenType,
                    expiresIn,
                    refreshToken,
                    scope.split(" ")
            );
        }
}
