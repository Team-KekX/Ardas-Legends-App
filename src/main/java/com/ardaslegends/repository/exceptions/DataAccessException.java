package com.ardaslegends.repository.exceptions;

public class DataAccessException extends RuntimeException{

    protected DataAccessException(String message) {
        super(message);
    }

    protected DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
