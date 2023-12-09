package com.ardaslegends.service.exceptions.permission;

import com.ardaslegends.service.exceptions.ServiceException;

public class PermissionException extends ServiceException {
    protected PermissionException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    protected PermissionException(String message) {
        super(message);
    }
}
