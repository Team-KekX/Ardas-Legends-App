package com.ardaslegends.service.exceptions.logic.units;

import com.ardaslegends.service.exceptions.logic.LogicException;

public class UnitServiceException extends LogicException {

    private static final String UNIT_NOT_FOUND = "No Unit with name '%s' found!";

    public static UnitServiceException unitNotFound(String unitName) { return new UnitServiceException(UNIT_NOT_FOUND.formatted(unitName)); }

    protected UnitServiceException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    protected UnitServiceException(String message) {
        super(message);
    }
}
