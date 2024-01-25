package com.ardaslegends.repository.exceptions;

public class NotFoundException extends DataAccessException{


    private static final String GENERIC_COULD_NOT_FIND = "Could not find %s with %s %s!";

    protected NotFoundException(String message) {
        super(message);
    }

    protected NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Results in an error with format "Could not find %s with %s %s!"
     */
    public static NotFoundException genericNotFound(String couldNotFind, String with, String value) {
        return new NotFoundException(GENERIC_COULD_NOT_FIND.formatted(couldNotFind, with, value));
    }

}
