package com.ardaslegends.data.service.exceptions.units;

import com.ardaslegends.data.service.exceptions.ServiceException;

public class UnitServiceException extends ServiceException {

    private static final String UNIT_NOT_FOUND = "No Unit with name '%s' found!";

    public static UnitServiceException unitNotFound(String unitName) { return new UnitServiceException(UNIT_NOT_FOUND.formatted(unitName)); }

    protected UnitServiceException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    protected UnitServiceException(String message) {
        super(message);
    }
}
