package com.ardaslegends.repository.exceptions;

import java.util.Objects;

public class FactionRepositoryException extends RuntimeException {

    private static final String ENTITY_TYPE_NAME = "Faction";
    private static final String ENTITY_NOT_FOUND = "No %s found with %s=%s";

    public static FactionRepositoryException entityNotFound(String fieldName, String value) {
        Objects.requireNonNull(fieldName);
        Objects.requireNonNull(value);
        return new FactionRepositoryException(ENTITY_NOT_FOUND.formatted(ENTITY_TYPE_NAME, fieldName, value));
    }

    private FactionRepositoryException(String message) {
        super(message);
    }

    private FactionRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
