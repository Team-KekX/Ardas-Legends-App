package com.ardaslegends.service.exceptions.logic.rpchar;

import com.ardaslegends.service.exceptions.logic.LogicException;

import java.util.Arrays;

public class RpCharServiceException extends LogicException {
    private final static String NO_RPCHAR_FOUND = "No RP Char found with name '%s'";
    private final static String NO_RPCHARS_FOUND = "No RP Chars found with names '%s'";

    protected RpCharServiceException(String message) {
        super(message);
    }

    public static RpCharServiceException noRpCharFound(String name) {
        return new RpCharServiceException(NO_RPCHAR_FOUND.formatted(name));
    }

    public static RpCharServiceException noRpCharsFound(String[] names) {
        return new RpCharServiceException(NO_RPCHARS_FOUND.formatted(Arrays.toString(names)));
    }
}
