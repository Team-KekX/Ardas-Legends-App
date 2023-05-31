package com.ardaslegends.repository.exceptions;

import java.util.Objects;

public class ClaimbuildRepositoryException extends RuntimeException {

    private static final String ENTITY_TYPE_NAME = "Claimbuild";
    private static final String ENTITY_NOT_FOUND = "No %s found with %s=%s";

    public static ClaimbuildRepositoryException entityNotFound(String fieldName, String value) {
        Objects.requireNonNull(fieldName);
        Objects.requireNonNull(value);
        return new ClaimbuildRepositoryException(ENTITY_NOT_FOUND.formatted(ENTITY_TYPE_NAME, fieldName, value));
    }

    private ClaimbuildRepositoryException(String message) {
        super(message);
    }

    private ClaimbuildRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
