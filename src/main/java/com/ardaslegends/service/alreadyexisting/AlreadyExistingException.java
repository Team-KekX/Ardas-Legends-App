package com.ardaslegends.service.alreadyexisting;

import com.ardaslegends.service.exceptions.ServiceException;

public class AlreadyExistingException extends ServiceException {
    protected AlreadyExistingException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    protected AlreadyExistingException(String message) {
        super(message);
    }
}
