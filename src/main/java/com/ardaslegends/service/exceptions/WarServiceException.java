package com.ardaslegends.service.exceptions;

public class WarServiceException extends ServiceException {

    private static final String NO_WAR_DECLARATION_PERMISSIONS = "You are not a faction leader or lord with permission to declare wars!";

    public static WarServiceException noWarDeclarationPermissions() { return new WarServiceException(NO_WAR_DECLARATION_PERMISSIONS); }

    public WarServiceException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public WarServiceException(String message) {
        super(message);
    }
}
