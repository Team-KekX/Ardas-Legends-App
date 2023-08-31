package com.ardaslegends.service.exceptions;

public class AlreadyExistingException extends ServiceException{
    protected AlreadyExistingException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    protected AlreadyExistingException(String message) {
        super(message);
    }
}
