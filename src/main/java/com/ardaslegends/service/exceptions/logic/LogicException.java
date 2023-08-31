package com.ardaslegends.service.exceptions.logic;

import com.ardaslegends.service.exceptions.ServiceException;

public class LogicException extends ServiceException {
    protected LogicException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    protected LogicException(String message) {
        super(message);
    }
}
