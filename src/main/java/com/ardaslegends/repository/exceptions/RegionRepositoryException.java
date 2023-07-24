package com.ardaslegends.repository.exceptions;

import java.util.Objects;

public class RegionRepositoryException extends RuntimeException {
    private static final String ENTITY_TYPE_NAME = "Region";
    private static final String ENTITY_NOT_FOUND = "No %s found with %s=%s";

    public static RegionRepositoryException entityNotFound(String fieldName, String value) {
        Objects.requireNonNull(fieldName);
        Objects.requireNonNull(value);
        return new RegionRepositoryException(ENTITY_NOT_FOUND.formatted(ENTITY_TYPE_NAME, fieldName, value));
    }

    private RegionRepositoryException(String message) {
        super(message);
    }

    private RegionRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
