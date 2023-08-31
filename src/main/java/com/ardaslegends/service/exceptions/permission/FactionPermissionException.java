package com.ardaslegends.service.exceptions.permission;

public class FactionPermissionException extends PermissionException{
    protected FactionPermissionException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    protected FactionPermissionException(String message) {
        super(message);
    }
}
