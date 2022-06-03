package com.ardaslegends.data.service.exceptions;

public class ServiceException extends RuntimeException {

    private ServiceException(String message, Throwable rootCause) { super(message, rootCause);}

}
