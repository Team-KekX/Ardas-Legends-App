package com.ardaslegends.service.exceptions;

import com.ardaslegends.domain.war.WarInvolvement;
import lombok.val;

public class WarServiceException extends ServiceException {

    private static final String NO_WAR_DECLARATION_PERMISSIONS = "You are not a faction leader or lord with permission to declare wars!";
    private static final String THIS_BATTLE_IS_ALREADY_IN_THE_BATTLE_LIST = "Battle [%s] is already listed in the battles of this war";
    private static final String CANNOT_DECLARE_WAR_ON_YOUR_FACTION = "You cannot declare war on your own faction!";
    private static final String ALREADY_AT_WAR = "Your faction '%s' is already at war with '%s'!";

    public static WarServiceException noWarDeclarationPermissions() { return new WarServiceException(NO_WAR_DECLARATION_PERMISSIONS); }
    public static WarServiceException factionAlreadyJoinedTheWar(String factionName, WarInvolvement involvement) {
        val involvementString = involvement == WarInvolvement.ATTACKING ? "an attacking" : "a defending";
        return new WarServiceException("%s already joined the war as %s faction".formatted(factionName, involvementString));
    }
    public static WarServiceException battleAlreadyListed(String battleName) { return new WarServiceException(THIS_BATTLE_IS_ALREADY_IN_THE_BATTLE_LIST.formatted(battleName));}
    public static WarServiceException cannotDeclareWarOnYourFaction() { return new WarServiceException(CANNOT_DECLARE_WAR_ON_YOUR_FACTION); }
    public static WarServiceException alreadyAtWar(String executorFaction, String otherFaction) { return new WarServiceException(ALREADY_AT_WAR.formatted(executorFaction, otherFaction)); }

    public WarServiceException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public WarServiceException(String message) {
        super(message);
    }
}
