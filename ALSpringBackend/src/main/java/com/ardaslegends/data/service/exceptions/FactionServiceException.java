package com.ardaslegends.data.service.exceptions;

public class FactionServiceException extends ServiceException {

    public static final String NO_FACTION_WITH_NAME_FOUND = "No faction with name '%s' found in database!";
    public static final String FACTION_LEADER_MUST_BE_OF_SAME_FACTION = "The faction leader must be of the same faction";

    public static final String NEGATIVE_STOCKPILE_ADD_NOT_SUPPORTED = "You are trying to add a negative amount to the stockpile. Please use /stockpile remove to lower food stockpiles";
    public static final String NEGATIVE_STOCKPILE_SUBTRACT_NOT_SUPPORTED = "You are trying to subtract a negative amount to the stockpile. Please use /stockpile add to increase food stockpiles";
    public static final String PLAYER_HAS_NO_RPCHAR= "The inputted player does not have a roleplay character.";
    public static FactionServiceException factionLeaderMustBeOfSameFaction() {return new FactionServiceException(FACTION_LEADER_MUST_BE_OF_SAME_FACTION);}
    public static FactionServiceException negativeStockpileAddNotSupported() {return new FactionServiceException((NEGATIVE_STOCKPILE_ADD_NOT_SUPPORTED));}
    public static FactionServiceException negativeStockpileSubtractNotSupported() {return new FactionServiceException((NEGATIVE_STOCKPILE_SUBTRACT_NOT_SUPPORTED));}
    public static FactionServiceException playerHasNoRpchar() {return new FactionServiceException(PLAYER_HAS_NO_RPCHAR);}

    public static FactionServiceException noFactionWithNameFound(String factionName) { return new FactionServiceException(NO_FACTION_WITH_NAME_FOUND.formatted(factionName)); }

    protected FactionServiceException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    protected FactionServiceException(String message) {
        super(message);
    }
}
