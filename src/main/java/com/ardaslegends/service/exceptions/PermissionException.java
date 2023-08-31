package com.ardaslegends.service.exceptions;

public class PermissionException extends ServiceException{
    protected PermissionException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    protected PermissionException(String message) {
        super(message);
    }
}
