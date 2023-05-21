package com.ardaslegends.repository.exceptions;

import java.util.Objects;

public class ClaimbuildApplicationException extends RuntimeException {

    private static final String ENTITY_TYPE_NAME = "Claimbuild Application ";
    private static final String ENTITY_NOT_FOUND = "No %s found with %s=%s";

    public static ClaimbuildApplicationException entityNotFound(String fieldName, String value) {
        Objects.requireNonNull(fieldName);
        Objects.requireNonNull(value);
        return new ClaimbuildApplicationException(ENTITY_NOT_FOUND.formatted(ENTITY_TYPE_NAME, fieldName, value));
    }

    private ClaimbuildApplicationException(String message) {
        super(message);
    }

    private ClaimbuildApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
