package com.ardaslegends.service.exceptions.logic.applications;

import com.ardaslegends.service.exceptions.logic.LogicException;

public class ApplicationException extends LogicException  {

    private static final String NO_VOTE_NEEDED_TO_BE_REMOVED = "No vote needed to be removed since none was cast by '%s'!";

    public static ApplicationException noVoteNeededToBeRemoved(String playerName) { return new ApplicationException(NO_VOTE_NEEDED_TO_BE_REMOVED.formatted(playerName)); }

    private ApplicationException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    private ApplicationException(String message) {
        super(message);
    }
}
