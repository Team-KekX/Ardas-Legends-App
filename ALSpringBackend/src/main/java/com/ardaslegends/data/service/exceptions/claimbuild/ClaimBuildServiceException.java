package com.ardaslegends.data.service.exceptions.claimbuild;

import com.ardaslegends.data.service.exceptions.ServiceException;

public class ClaimBuildServiceException extends ServiceException {

    private static final String NO_CB_WITH_NAME = "Found no claimbuild with name '%s'!";
    private static final String DIFFERENT_FACTION = "The claimbuild '%s' is part of a different faction ('%s') - you cannot interact with it!";
    private static final String DIFFERENT_FACTION_NOT_ALLIED = "The claimbuild '%s' is part of the faction '%s' - you are not allied with '%s' and therefore cannot interact with it!";

    public static ClaimBuildServiceException noCbWithName(String cbName) { return new ClaimBuildServiceException(NO_CB_WITH_NAME.formatted(cbName)); }
    public static ClaimBuildServiceException differentFaction(String cbName, String factionName) { return new ClaimBuildServiceException(DIFFERENT_FACTION.formatted(cbName, factionName)); }
    public static ClaimBuildServiceException differentFactionNotAllied(String cbName, String factionName) { return new ClaimBuildServiceException(DIFFERENT_FACTION_NOT_ALLIED.formatted(cbName, factionName, factionName)); }

    protected ClaimBuildServiceException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    protected ClaimBuildServiceException(String message) {
        super(message);
    }
}
