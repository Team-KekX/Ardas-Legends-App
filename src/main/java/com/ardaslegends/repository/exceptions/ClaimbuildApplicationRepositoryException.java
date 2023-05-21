package com.ardaslegends.repository.exceptions;

import java.util.Objects;

public class ClaimbuildApplicationRepositoryException extends RuntimeException {

    private static final String ENTITY_TYPE_NAME = "Claimbuild Application ";
    private static final String ENTITY_NOT_FOUND = "No %s found with %s=%s";

    public static ClaimbuildApplicationRepositoryException entityNotFound(String fieldName, String value) {
        Objects.requireNonNull(fieldName);
        Objects.requireNonNull(value);
        return new ClaimbuildApplicationRepositoryException(ENTITY_NOT_FOUND.formatted(ENTITY_TYPE_NAME, fieldName, value));
    }

    private ClaimbuildApplicationRepositoryException(String message) {
        super(message);
    }

    private ClaimbuildApplicationRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
