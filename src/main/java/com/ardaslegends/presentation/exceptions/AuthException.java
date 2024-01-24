package com.ardaslegends.presentation.exceptions;

public class AuthException extends RuntimeException {

    public AuthException(String message, Exception exception) { super(message, exception); }

}
