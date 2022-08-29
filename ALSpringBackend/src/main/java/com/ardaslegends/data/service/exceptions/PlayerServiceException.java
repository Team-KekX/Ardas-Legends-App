package com.ardaslegends.data.service.exceptions;

import com.ardaslegends.data.domain.Player;

public class PlayerServiceException extends ServiceException {

    public static final String NOT_REGISTERED = "You are not registered! please register your account with /register !";
    private static final String NO_RP_CHAR = "You have no Roleplay Character!";

    //Heal start
    private static final String CANNOT_HEAL_CHAR_BECAUSE_NOT_INJURED = "Cannot heal character '%s' because it is not injured!";
    private static final String CANNOT_HEAL_NO_CB_WITH_HOH = "Cannot heal character '%s' because there is no claimbuild with a House of Healing in region '%s'! (Claimbuilds in region '%s': %s)";

    public static PlayerServiceException notRegistered() { return new PlayerServiceException(NOT_REGISTERED); }
    public static PlayerServiceException noRpChar() {
        return new PlayerServiceException(NO_RP_CHAR);
    }

    //Heal start
    public static PlayerServiceException cannotHealNotInjured(String charName) { return new PlayerServiceException(CANNOT_HEAL_CHAR_BECAUSE_NOT_INJURED.formatted(charName)); }
    public static PlayerServiceException cannotHealNoCbWithHoH(String charName, String regionId, String claimbuildsInRegion) {
        return new PlayerServiceException(CANNOT_HEAL_NO_CB_WITH_HOH.formatted(charName, regionId, regionId, claimbuildsInRegion));
    }

    protected PlayerServiceException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    protected PlayerServiceException(String message) {
        super(message);
    }
}
