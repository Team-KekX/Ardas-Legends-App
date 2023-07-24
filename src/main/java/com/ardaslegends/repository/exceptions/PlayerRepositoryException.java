package com.ardaslegends.repository.exceptions;

import lombok.val;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

public class PlayerRepositoryException extends RuntimeException{

    private static final String ENTITY_TYPE_NAME = "Player";
    private static final String ENTITY_NOT_FOUND = "No %s found with %s=%s";

    public static PlayerRepositoryException entityNotFound(String fieldName, String value) {
        Objects.requireNonNull(fieldName);
        Objects.requireNonNull(value);
        return new PlayerRepositoryException(ENTITY_NOT_FOUND.formatted(ENTITY_TYPE_NAME, fieldName, value));
    }

    private PlayerRepositoryException(String message) {
        super(message);
    }

    private PlayerRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
