package com.ardaslegends.repository.exceptions;

public class NotFoundException extends DataAccessException{


    private final String GENERIC_COULD_NOT_FIND = "Could not find %s with %s %s!";

    protected NotFoundException(String message) {
        super(message);
    }

    protected NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
