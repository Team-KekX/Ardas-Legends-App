package com.ardaslegends.service.exceptions;

public class StaffPermissionException extends PermissionException{
    protected StaffPermissionException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    protected StaffPermissionException(String message) {
        super(message);
    }
}
