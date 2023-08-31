package com.ardaslegends.service.exceptions.logic.player;

import com.ardaslegends.service.exceptions.logic.LogicException;

public class PlayerServiceException extends LogicException {

    public static final String IGN_ALREADY_USED = "Another player already registered with this ign '%s'. If you own this minecraft account then please contact staff!";
    public static final String ALREADY_REGISTERED = "Your discord account is already linked/registered in our roleplay-system!";
    public static final String NOT_REGISTERED = "You are not registered! please register your account with /register !";
    private static final String NO_RP_CHAR = "You have no Roleplay Character!";
    public static final String PLAYER_HAS_NO_RPCHAR= "The inputted player does not have a roleplay character.";
    public static final String NO_PLAYER_FOUND = "No Player found for Discord User %s - please register first if you haven't already!";
    public static final String RPCHAR_ALREADY_EXISTS = "This rpchar (%s) is already registered";
    private static final String UNEXPECTED_ERROR_WHIlE_SAVING_NEW_CHARACTER = "Could not update player %s with new character because of an unexpected issue during save";


    //Heal start
    private static final String CANNOT_HEAL_CHAR_BECAUSE_NOT_INJURED = "Cannot heal character '%s' because it is not injured!";
    private static final String CANNOT_HEAL_NO_CB_WITH_HOH = "Cannot heal character '%s' because there is no claimbuild with a House of Healing in region '%s'! (Claimbuilds in region '%s': %s)";

    //Heal stop
    private static final String CANNOT_STOP_HEAL_NOT_HEALING = "Cannot stop healing of character '%s' since it is not healing at the moment!";

    public static PlayerServiceException rpcharAlreadyExists(String rpcharName) {return new PlayerServiceException(RPCHAR_ALREADY_EXISTS.formatted(rpcharName));}
    public static PlayerServiceException ignAlreadyUsed(String ign) {return new PlayerServiceException(IGN_ALREADY_USED.formatted(ign));}
    public static PlayerServiceException alreadyRegistered() {return new PlayerServiceException((ALREADY_REGISTERED)); }
    public static PlayerServiceException notRegistered() { return new PlayerServiceException(NOT_REGISTERED); }
    public static PlayerServiceException noRpChar() {
        return new PlayerServiceException(NO_RP_CHAR);
    }
    public static PlayerServiceException noPlayerFound(String discordId) {return new PlayerServiceException(NO_PLAYER_FOUND.formatted(discordId));}

    public static PlayerServiceException unexpectedErrorSavingCharacter(String ign) {return new PlayerServiceException(UNEXPECTED_ERROR_WHIlE_SAVING_NEW_CHARACTER.formatted(ign));}
    public static PlayerServiceException playerHasNoRpchar() {return new PlayerServiceException(PLAYER_HAS_NO_RPCHAR);}
    //Heal start
    public static PlayerServiceException cannotHealNotInjured(String charName) { return new PlayerServiceException(CANNOT_HEAL_CHAR_BECAUSE_NOT_INJURED.formatted(charName)); }
    public static PlayerServiceException cannotHealNoCbWithHoH(String charName, String regionId, String claimbuildsInRegion) {
        return new PlayerServiceException(CANNOT_HEAL_NO_CB_WITH_HOH.formatted(charName, regionId, regionId, claimbuildsInRegion));
    }

    //Heal stop
    public static PlayerServiceException cannotStopHealBecauseCharNotHealing(String charName) { return new PlayerServiceException(CANNOT_STOP_HEAL_NOT_HEALING.formatted(charName)); }

    protected PlayerServiceException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    protected PlayerServiceException(String message) {
        super(message);
    }
}
