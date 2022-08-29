package com.ardaslegends.data.service.exceptions;

import com.ardaslegends.data.domain.Player;

public class PlayerServiceException extends ServiceException {

    public static final String NOT_REGISTERED = "You are not registered! please register your account with /register !";
    private static final String NO_RP_CHAR = "You have no Roleplay Character!";

    public static PlayerServiceException notRegistered() { return new PlayerServiceException(NOT_REGISTERED); }
    public static PlayerServiceException noRpChar() {
        return new PlayerServiceException(NO_RP_CHAR);
    }

    protected PlayerServiceException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    protected PlayerServiceException(String message) {
        super(message);
    }
}
