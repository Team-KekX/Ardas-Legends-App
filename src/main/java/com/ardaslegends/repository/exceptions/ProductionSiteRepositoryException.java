package com.ardaslegends.repository.exceptions;

import java.util.Objects;

public class ProductionSiteRepositoryException extends RuntimeException {
    private static final String ENTITY_TYPE_NAME = "Production Site";
    private static final String ENTITY_NOT_FOUND = "No %s found with %s=%s";

    public static ProductionSiteRepositoryException entityNotFound(String fieldName, String value) {
        Objects.requireNonNull(fieldName);
        Objects.requireNonNull(value);
        return new ProductionSiteRepositoryException(ENTITY_NOT_FOUND.formatted(ENTITY_TYPE_NAME, fieldName, value));
    }

    private ProductionSiteRepositoryException(String message) {
        super(message);
    }

    private ProductionSiteRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
