package com.ardaslegends.presentation.auth.exceptions;

import java.util.Objects;

public class NoPlayerRegisteredAuthException extends RuntimeException {

    /**
     * This token is used to register a new player.
     * There is a limit to when you can use this token, namely it is cached only for a short amount of time
     */
    private final String registrationToken;

    public NoPlayerRegisteredAuthException(String token) {
        super("No player was found with the given discordID");
        Objects.requireNonNull(token, "RegistrationToken must not be null!");
        this.registrationToken = token;
    }
}
