package com.ardaslegends.service.exceptions;

public class BattleServiceException extends ServiceException {
    public static final String FACTIONS_NOT_AT_WAR = "The two factions with the names '%s' and '%s' are not at war with each other";

    public static BattleServiceException factionsNotAtWar(String factionName1, String factionName2) { return new BattleServiceException(FACTIONS_NOT_AT_WAR.formatted(factionName1, factionName2)); }


    protected BattleServiceException(String message, Throwable rootCause) {
        super(message, rootCause);
    }
    protected BattleServiceException(String message) {
        super(message);
    }
}
