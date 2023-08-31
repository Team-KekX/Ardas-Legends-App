package com.ardaslegends.service.exceptions;

public class LogicException extends ServiceException{
    protected LogicException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    protected LogicException(String message) {
        super(message);
    }
}
