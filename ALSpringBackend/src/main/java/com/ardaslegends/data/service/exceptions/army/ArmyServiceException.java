package com.ardaslegends.data.service.exceptions.army;

import com.ardaslegends.data.domain.ArmyType;
import com.ardaslegends.data.service.exceptions.ServiceException;

public class ArmyServiceException extends ServiceException{

    private static final String NO_ARMYTYPE_WITH_NAME = "No %s with the name '%s' found!";
    private static final String NO_PERMISSION_TO_PERFORM_ACTION = "No permission to perform this action. You need to be either bound, have a lordship rank of that faction or be faction leader";
    private static final String CLAIMBUILD_IS_NOT_IN_THE_SAME_OR_ALLIED_FACTION = "Claimbuild '%s' is not in the same or allied faction of %s";
    private static final String ARMYTYPE_IS_ALREADY_STATIONED = "%s '%s' is already stationed at Claimbuild '%s'!";
    private static final String ARMYTYPE_IS_NOT_STATIONED = "%s '%s' is not stationed at a Claimbuild!";
    private static final String PLAYER_NOT_FACTION_LEADER = "You are not the faction leader or a lord of '%s' and therefore cannot (un)bind other players!";
    private static final String ONLY_FACTION_LEADER_CAN_BIND_WANDERER = "Only the faction leader or a lord can bind wanderers to %s!";
    private static final String NOT_IN_SAME_REGION = "%s '%s' is not in the same region as character '%s'!";
    private static final String NOT_SAME_FACTION = "You are not in the same faction as %s '%s'! Your faction: '%s' - %s faction: '%s'!";
    private static final String ALREADY_BOUND = "The %s '%s' is already bound to the player '%s'!";
    private static final String CANNOT_BIND_CHAR_INJURED = "The character '%s' is currently injured and cannot be bound to army '%s'!\nHeal the character in a House of Healing first!";
    private static final String CANNOT_BIND_CHAR_HEALING = "The character '%s' is currently healing and cannot be bound to army '%s'!\nStop healing the character first!";
    private static final String TRADING_COMPANIES_CANNOT_HEAL = "%s is a trading company. Trading companies cannot heal!";
    private static final String NO_PLAYER_BOUND_TO_ARMYTYPE = "There is no player bound to the %s '%s'!";
    private static final String TOO_HIGH_TOKEN_COUNT = "%s token count exceeds the maximum of 30, it currently is '%s";
    private static final String CANNOT_MOVE_ARMYTYPE_DUE_TO_ALREADY_MOVING = "Cannot move %s '%s' because it is already in a movement! Cancel its movement to submit a new one";
    private static final String MAX_ARMYTYPE_CREATED = "The claimbuild '%s' has already created the maximum amount of type %s: %s";
    private static final String CANNOT_MOVE_ARMYTYPE_DUE_TO_PLAYER_AND_ARMY_BEING_IN_DIFFERENT_FACTIONS = "%s '%s' could not be moved since it is in a different faction!";
    private static final String CANNOT_MOVE_ARMYTYPE_ALREADY_IN_REGION = "%s'%s' is already in the desired region of '%s'";
    private static final String CANNOT_MOVE_ARMY_JUST_CREATED = "The army '%s' was created less than 24 hours ago and cannot move for another '%d' hour(s)!";
    private static final String CANNOT_BIND_ARMYTYPE_IS_MOVING = "The %s '%s' is currently moving to '%s' - cancel the movement before binding to it!";
    private static final String CANNOT_BIND_CHAR_IS_MOVING = "The character '%s' is currently moving to '%s' - cancel the movement before binding to %s!";
    private static final String CANNOT_CREATE_ARMYTYPE_WHEN_IN_DIFFERENT_FACTIONS = "You are in faction '%s' and the claimbuild is in faction '%s' - you can only create a %s from claimbuilds of your own faction!";
    private static final String ARMYTYPE_AND_PLAYER_IN_DIFFERENT_FACTION = "The %s '%s' and the player '%s' are not in the same faction - Cant execute command!";
    private static final String ARMYTYPE_MUST_BE_STATIONED_AT_A_CLAIMBUILD_WITH_HOUSE_OF_HEALING = "The %s '%s' is not stationed at a CB with a House of Healing, cannot start healing - Please station the %s at CB";
    private static final String ARMY_ALREADY_FULLY_HEALED = "The %s '%s' is already fully healed!";
    private static final String ARMY_OR_COMPANY_WITH_NAME_ALREADY_EXISTS = "Army or company with name %s already exists, please choose a different name";
    private static final String INVALID_UNIT_STRING = "The string '%s' is not grammatically correct \n " +
            "A correct string would be: Gondorian Ranger:5-Mordor Orc:2 \n" +
            "Actual Grammar=[Unit name]:[Integer amount]-[Next Unit name]:[next integer amount]";
    //Disband army
    private static final String NOT_ALLOWED_TO_DISBAND_NOT_IN_SAME_FACTION = "The %s '%s' is part of the faction '%s' - only the faction leader can disband it!";
    private static final String NOT_ALLOWED_TO_DISBAND = "Only faction leaders and lords with permission are allowed to disband %s!";
    private static final String CANNOT_STOP_HEALING_IF_ARMYTYPE_IS_NOT_HEALING = "%s '%s' is not healing - Can't stop it";

    //Set Token
    private static final String TOKEN_NEGATIVE = "Armies cannot have less than 0 free tokens (you inputted: %f)!";
    private static final String TOKEN_ABOVE_30 = "Armies can only have a maximum of 30 free tokens (you inputted: %f)!";

    //pick siege
    private static final String SIEGE_ONLY_ARMY_CAN_PICK = "Only armies can pick sieges - '%s' is a trading/armed company!";
    private static final String SIEGE_NOT_FACTION_LEADER_OR_LORD = "Only faction leaders/lords of '%s' can pick siege for the army '%s' without being bound to it!";
    private static final String SIEGE_ARMY_NOT_IN_SAME_REGION_AS_CB = "The army '%s' is currently in region %s while the claimbuild '%s' is located in region %s. Move the army into the claimbuild's region in order to pick up siege from it!";
    private static final String SIEGE_NOT_AVAILABLE = "The siege equipment '%s' is not available in claimbuild '%s'. Available sieges are: '%s'";
    public static ArmyServiceException noArmyWithName(String armyType, String armyName) { return new ArmyServiceException(NO_ARMYTYPE_WITH_NAME.formatted(armyType, armyName)); }
    public static ArmyServiceException armyOrCompanyWithNameAlreadyExists(String name) {return new ArmyServiceException(ARMY_OR_COMPANY_WITH_NAME_ALREADY_EXISTS.formatted(name));}
    public static ArmyServiceException tradingCompaniesCannotHeal(String name) {return new ArmyServiceException((TRADING_COMPANIES_CANNOT_HEAL.formatted(name)));}
    public static ArmyServiceException noPermissionToPerformThisAction() { return new ArmyServiceException(NO_PERMISSION_TO_PERFORM_ACTION);}
    public static ArmyServiceException claimbuildNotInTheSameOrAlliedFaction(ArmyType armyType, String claimbuildName) { return new ArmyServiceException(CLAIMBUILD_IS_NOT_IN_THE_SAME_OR_ALLIED_FACTION.formatted(claimbuildName, armyType.getName())); }
    public static ArmyServiceException armyAlreadyStationed(ArmyType armyType, String armyName, String claimbuildName) { return new ArmyServiceException(ARMYTYPE_IS_ALREADY_STATIONED.formatted(armyType.getName(), armyName, claimbuildName)); }
    public static ArmyServiceException notFactionLeader(String factionName) { return new ArmyServiceException(PLAYER_NOT_FACTION_LEADER.formatted(factionName)); }
    public static ArmyServiceException invalidUnitString(String unitString) { return new ArmyServiceException(INVALID_UNIT_STRING.formatted(unitString)); }
    public static ArmyServiceException notInSameRegion(ArmyType armyType, String armyName, String charName) { return new ArmyServiceException(NOT_IN_SAME_REGION.formatted(armyType.getName(), armyName, charName)); }
    public static ArmyServiceException notSameFaction(ArmyType armyType, String armyName, String playerFactionName, String armyFactionName) { return new ArmyServiceException(NOT_SAME_FACTION.formatted(armyType.getName(), armyName, playerFactionName, armyType.getName(), armyFactionName)); }
    public static ArmyServiceException alreadyBound(ArmyType armyType, String armyName, String playerName) { return new ArmyServiceException(ALREADY_BOUND.formatted(armyType.getName(), armyName, playerName)); }
    public static ArmyServiceException cannotBindCharInjured(String charName, String armyName) { return new ArmyServiceException(CANNOT_BIND_CHAR_INJURED.formatted(charName, armyName)); }
    public static ArmyServiceException cannotBindCharHealing(String charName, String armyName) { return new ArmyServiceException(CANNOT_BIND_CHAR_HEALING.formatted(charName, armyName)); }
    public static ArmyServiceException noPlayerBoundToArmy(ArmyType armyType, String armyName) { return new ArmyServiceException(NO_PLAYER_BOUND_TO_ARMYTYPE.formatted(armyType.getName(), armyName)); }
    public static ArmyServiceException tooHighTokenCount(ArmyType armyType, double tokenCount) {return new ArmyServiceException(TOO_HIGH_TOKEN_COUNT.formatted(armyType.getName(), tokenCount)); };
    public static ArmyServiceException maxArmyOrCompany(ArmyType armyType, String claimbuild, String units) {return new ArmyServiceException(MAX_ARMYTYPE_CREATED.formatted(claimbuild,armyType.getName(), units)); }
    public static ArmyServiceException onlyLeaderCanBindWanderer(ArmyType armyType) {return new ArmyServiceException(ONLY_FACTION_LEADER_CAN_BIND_WANDERER.formatted(armyType.getName()));}
    public static ArmyServiceException cannotMoveArmyDueToArmyBeingInMovement(ArmyType armyType, String armyName) {return new ArmyServiceException(CANNOT_MOVE_ARMYTYPE_DUE_TO_ALREADY_MOVING.formatted(armyType.getName(), armyName));}
    public static ArmyServiceException cannotMoveArmyDueToPlayerAndArmyBeingInDifferentFactions(ArmyType armyType, String armyName) {return new ArmyServiceException(CANNOT_MOVE_ARMYTYPE_DUE_TO_PLAYER_AND_ARMY_BEING_IN_DIFFERENT_FACTIONS.formatted(armyType.getName(), armyName)); }
    public static ArmyServiceException cannotMoveArmyWasCreatedRecently(String armyName, long hoursUntilMove) { return new ArmyServiceException(CANNOT_MOVE_ARMY_JUST_CREATED.formatted(armyName, hoursUntilMove)); }
    public static ArmyServiceException cannotMoveArmyAlreadyInRegion(ArmyType armyType, String armyName, String region) {return new ArmyServiceException(CANNOT_MOVE_ARMYTYPE_ALREADY_IN_REGION.formatted(armyType.getName(), armyName,region));}
    public static ArmyServiceException cannotBindArmyIsMoving(ArmyType armyType, String armyName, String region) {return new ArmyServiceException((CANNOT_BIND_ARMYTYPE_IS_MOVING).formatted(armyType.getName(), armyName, region));}
    public static ArmyServiceException cannotBindCharIsMoving(ArmyType armyType, String charName, String region) {return new ArmyServiceException((CANNOT_BIND_CHAR_IS_MOVING).formatted(charName, region, armyType.getName()));}
    public static ArmyServiceException cannotCreateArmyFromClaimbuildInDifferentFaction(String playerFaction, String claimbuildFaction, ArmyType armyType) {return new ArmyServiceException((CANNOT_CREATE_ARMYTYPE_WHEN_IN_DIFFERENT_FACTIONS).formatted(playerFaction, claimbuildFaction, armyType.getName()));}
    public static ArmyServiceException armyAndPlayerInDifferentFaction(ArmyType armyType, String playerFaction, String armyFaction) {return new ArmyServiceException(ARMYTYPE_AND_PLAYER_IN_DIFFERENT_FACTION.formatted(armyType.getName(), playerFaction, armyFaction));}
    public static ArmyServiceException needToStationArmyAtCbWithHouseOfHealing(ArmyType armyType, String armyName) {return new ArmyServiceException(ARMYTYPE_MUST_BE_STATIONED_AT_A_CLAIMBUILD_WITH_HOUSE_OF_HEALING.formatted(armyType.getName(), armyName, armyType.getName()));}
    public static ArmyServiceException alreadyFullyHealed(ArmyType armyType, String armyName) { return new ArmyServiceException(ARMY_ALREADY_FULLY_HEALED.formatted(armyType.getName(), armyName)); }
    public static ArmyServiceException armyIsNotHealing(ArmyType armyType, String armyName) {return new ArmyServiceException((CANNOT_STOP_HEALING_IF_ARMYTYPE_IS_NOT_HEALING.formatted(armyType.getName(), armyName)));}
    //Disband army
    public static ArmyServiceException notAllowedToDisbandNotSameFaction(ArmyType armyType, String armyName, String factionName) { return new ArmyServiceException(NOT_ALLOWED_TO_DISBAND_NOT_IN_SAME_FACTION.formatted(armyType.getName(), armyName, factionName)); }
    public static ArmyServiceException notAllowedToDisband(ArmyType armyType) { return new ArmyServiceException(NOT_ALLOWED_TO_DISBAND.formatted(armyType.getName())); }
    public static ArmyServiceException armyNotStationed(ArmyType armyType, String armyName) { return new ArmyServiceException((ARMYTYPE_IS_NOT_STATIONED.formatted(armyType.getName(), armyName))); }

    //Set free tokens
    public static ArmyServiceException tokenNegative(double tokenAmount) { return new ArmyServiceException(TOKEN_NEGATIVE.formatted(tokenAmount)); }
    public static ArmyServiceException tokenAbove30(double tokenAmount) { return new ArmyServiceException(TOKEN_ABOVE_30.formatted(tokenAmount)); }

    //pick siege
    public static ArmyServiceException siegeOnlyArmyCanPick(String armyName) { return new ArmyServiceException(SIEGE_ONLY_ARMY_CAN_PICK.formatted(armyName)); }
    public static ArmyServiceException siegeNotFactionLeaderOrLord(String factionName, String armyName) { return new ArmyServiceException(SIEGE_NOT_FACTION_LEADER_OR_LORD.formatted(factionName, armyName)); }
    public static ArmyServiceException siegeArmyNotInSameRegionAsCB(String armyName, String armyRegion, String cbName, String cbRegion) { return new ArmyServiceException(SIEGE_ARMY_NOT_IN_SAME_REGION_AS_CB.formatted(armyName, armyRegion, cbName, cbRegion)); }
    public static ArmyServiceException siegeNotAvailable(String inputtedSiege, String cbName, String availableSiege) { return new ArmyServiceException(SIEGE_NOT_AVAILABLE.formatted(inputtedSiege, cbName, availableSiege)); }
    protected ArmyServiceException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    protected ArmyServiceException(String message) {
        super(message);
    }
}
