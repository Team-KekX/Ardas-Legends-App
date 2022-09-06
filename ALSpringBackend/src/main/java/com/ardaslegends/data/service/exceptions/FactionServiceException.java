package com.ardaslegends.data.service.exceptions;

public class FactionServiceException extends ServiceException {

    public static final String NO_FACTION_WITH_NAME_FOUND = "Could not find Faction with name '%s'.\nAvailable factions are: %s";
    public static final String FACTION_LEADER_MUST_BE_OF_SAME_FACTION = "The faction leader must be of the same faction";

    public static final String NEGATIVE_STOCKPILE_ADD_NOT_SUPPORTED = "You are trying to add a negative amount to the stockpile. Please use /stockpile remove to lower food stockpiles";
    public static final String NEGATIVE_STOCKPILE_SUBTRACT_NOT_SUPPORTED = "You are trying to subtract a negative amount to the stockpile. Please use /stockpile add to increase food stockpiles";
    public static final String NOT_ENOUGH_FOOD_IN_STOCKPILE_TO_COVER_COST = "There is not enough food in the stockpile of '%s' \n'%d' Stacks are in stockpile, cannot subtract/pay '%d' Stacks because the amount would fall below zero!";
    public static FactionServiceException notEnoughFoodInStockpile(String faction, int amountInStockpile, int amountToRemove) {return new FactionServiceException(NOT_ENOUGH_FOOD_IN_STOCKPILE_TO_COVER_COST.formatted(faction, amountInStockpile, amountToRemove));}
    public static FactionServiceException factionLeaderMustBeOfSameFaction() {return new FactionServiceException(FACTION_LEADER_MUST_BE_OF_SAME_FACTION);}
    public static FactionServiceException negativeStockpileAddNotSupported() {return new FactionServiceException((NEGATIVE_STOCKPILE_ADD_NOT_SUPPORTED));}
    public static FactionServiceException negativeStockpileSubtractNotSupported() {return new FactionServiceException((NEGATIVE_STOCKPILE_SUBTRACT_NOT_SUPPORTED));}

    public static FactionServiceException noFactionWithNameFound(String factionName, String allFactions) { return new FactionServiceException(NO_FACTION_WITH_NAME_FOUND.formatted(factionName, allFactions)); }

    protected FactionServiceException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    protected FactionServiceException(String message) {
        super(message);
    }
}
