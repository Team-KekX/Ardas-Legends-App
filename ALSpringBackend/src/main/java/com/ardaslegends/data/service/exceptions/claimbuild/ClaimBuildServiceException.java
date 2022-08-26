package com.ardaslegends.data.service.exceptions.claimbuild;

import com.ardaslegends.data.service.exceptions.ServiceException;

public class ClaimBuildServiceException extends ServiceException {

    private static final String NO_CB_WITH_NAME = "Found no claimbuild with name '%s'!";
    private static final String DIFFERENT_FACTION = "The claimbuild '%s' is part of a different faction ('%s') - you cannot interact with it!";
    private static final String DIFFERENT_FACTION_NOT_ALLIED = "The claimbuild '%s' is part of the faction '%s' - you are not allied with '%s' and therefore cannot interact with it!";

    //Create claimbuild
    private static final String CB_ALREADY_EXISTS = "A claimbuild with the name '%s' already exists in Region '%s' (owned by %s)";

    public static ClaimBuildServiceException noCbWithName(String cbName) { return new ClaimBuildServiceException(NO_CB_WITH_NAME.formatted(cbName)); }
    public static ClaimBuildServiceException differentFaction(String cbName, String factionName) { return new ClaimBuildServiceException(DIFFERENT_FACTION.formatted(cbName, factionName)); }
    public static ClaimBuildServiceException differentFactionNotAllied(String cbName, String factionName) { return new ClaimBuildServiceException(DIFFERENT_FACTION_NOT_ALLIED.formatted(cbName, factionName, factionName)); }

    //Create claimbuild
    public static ClaimBuildServiceException cbAlreadyExists(String cbName, String regionId, String factionName) { return new ClaimBuildServiceException(CB_ALREADY_EXISTS.formatted(cbName, regionId, factionName)); }

    protected ClaimBuildServiceException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    protected ClaimBuildServiceException(String message) {
        super(message);
    }
}
