package com.ardaslegends.data.service.exceptions.army;

import com.ardaslegends.data.domain.Army;
import com.ardaslegends.data.service.ArmyService;
import com.ardaslegends.data.service.exceptions.ServiceException;
import com.ardaslegends.data.service.exceptions.movement.MovementServiceException;

public class ArmyServiceException extends ServiceException{

    private static final String NO_ARMY_WITH_NAME = "No army with the name '%s' found!";
    private static final String NO_PERMISSION_TO_PERFORM_ACTION = "No permission to perform this action. You need to be either bound, have a lordship rank of that faction or be faction leader";
    private static final String CLAIMBUILD_IS_NOT_IN_THE_SAME_OR_ALLIED_FACTION = "Claimbuild '%s' is not in the same or allied faction of army";
    private static final String ARMY_IS_ALREADY_STATIONED = "Army '%s' is already stationed at Claimbuild '%s'!";
    private static final String ARMY_IS_NOT_STATIONED = "Army '%s' is not stationed at a Claimbuild!";
    private static final String PLAYER_NOT_FACTION_LEADER = "You are not the faction leader or a lord of '%s' and therefore cannot (un)bind other players!";
    private static final String ONLY_FACTION_LEADER_CAN_BIND_WANDERER = "Only the faction leader or a lord can bind wanderers to armies!";
    private static final String NOT_IN_SAME_REGION = "Army/Company '%s' is not in the same region as character '%s'!";
    private static final String NOT_SAME_FACTION = "You are not in the same faction as army/company '%s'! Your faction: '%s' - army's/company's faction: '%s'!";
    private static final String ALREADY_BOUND = "The army/company '%s' is already bound to the player '%s'!";
    private static final String NO_PLAYER_BOUND_TO_ARMY = "There is no player bound to the army '%s'!";
    private static final String TOO_HIGH_TOKEN_COUNT = "The army's token count exceeds the maximum of 30, it currently is '%s";
    private static final String CANNOT_MOVE_ARMY_DUE_TO_ALREADY_MOVING = "Cannot move army '%s' because it is already in a movement! Cancel its movement to submit a new one";
    private static final String MAX_ARMY_OR_COMPANIES_CREATED = "The claimbuild '%s' is already at maximum armies/companies created: %s";
    private static final String CANNOT_MOVE_ARMY_DUE_TO_PLAYER_AND_ARMY_BEING_IN_DIFFERENT_FACTIONS = "Army '%s' could not be moved since it is in a different faction!";
    private static final String NO_PERMISSION_TO_MOVE_ARMY = "You do not have the permission to move armies that you are not bound to!";
    private static final String CANNOT_MOVE_ARMY_ALREADY_IN_REGION = "Army '%s' is already in the desired region of '%s'";
    private static final String CANNOT_BIND_ARMY_IS_MOVING = "The army '%s' is currently moving to '%s' - cancel the movement before binding to it!";
    private static final String CANNOT_BIND_CHAR_IS_MOVING = "The character '%s' is currently moving to '%s' - cancel the movement before binding to an army!";
    private static final String CANNOT_CREATE_ARMY_WHEN_IN_DIFFERENT_FACTIONS = "You are in faction '%s' and the claimbuild is in faction '%s' - you can only create armies from claimbuilds of your own faction!";
    private static final String ARMY_AND_PLAYER_IN_DIFFERENT_FACTION = "The army '%s' and the player '%s' are not in the same faction - Cant execute command!";
    private static final String ARMY_MUST_BE_STATIONED_AT_A_CLAIMBUILD_WITH_HOUSE_OF_HEALING = "The army '%s' is not stationed at a CB with a House of Healing, cannot start healing - Please station the Army at CB";
    //Disband army
    private static final String NOT_ALLOWED_TO_DISBAND_NOT_IN_SAME_FACTION = "The army '%s' is part of the faction '%s' - only the faction leader can disband it!";
    private static final String NOT_ALLOWED_TO_DISBAND = "Only faction leaders and lords with permission are allowed to disband armies!";
    private static final String CANNOT_STOP_HEALING_IF_ARMY_IS_NOT_HEALING = "Army '%s' is not healing - Can't stop it";

    //Set Token
    private static final String TOKEN_NEGATIVE = "Armies cannot have less than 0 free tokens (you inputted: %d)!";
    private static final String TOKEN_ABOVE_30 = "Armies can only have a maximum of 30 free tokens (you inputted: %d)!";

    //pick siege
    private static final String SIEGE_ONLY_ARMY_CAN_PICK = "Only armies can pick sieges - '%s' is a trading/armed company!";
    private static final String SIEGE_NOT_FACTION_LEADER_OR_LORD = "Only faction leaders/lords of '%s' can pick siege for the army '%s' without being bound to it!";
    private static final String SIEGE_ARMY_NOT_IN_SAME_REGION_AS_CB = "The army '%s' is currently in region %s while the claimbuild '%s' is located in region %s. Move the army into the claimbuild's region in order to pick up siege from it!";
    private static final String SIEGE_NOT_AVAILABLE = "The siege equipment '%s' is not available in claimbuild '%s'. Available sieges are: '%s'";
    public static ArmyServiceException noArmyWithName(String armyName) { return new ArmyServiceException(NO_ARMY_WITH_NAME.formatted(armyName)); }
    public static ArmyServiceException noPermissionToPerformThisAction() { return new ArmyServiceException(NO_PERMISSION_TO_PERFORM_ACTION);}
    public static ArmyServiceException claimbuildNotInTheSameOrAlliedFaction(String claimbuildName) { return new ArmyServiceException(CLAIMBUILD_IS_NOT_IN_THE_SAME_OR_ALLIED_FACTION.formatted(claimbuildName)); }
    public static ArmyServiceException armyAlreadyStationed(String armyName, String claimbuildName) { return new ArmyServiceException(ARMY_IS_ALREADY_STATIONED.formatted(armyName, claimbuildName)); }
    public static ArmyServiceException notFactionLeader(String factionName) { return new ArmyServiceException(PLAYER_NOT_FACTION_LEADER.formatted(factionName)); }

    public static ArmyServiceException notInSameRegion(String armyName, String charName) { return new ArmyServiceException(NOT_IN_SAME_REGION.formatted(armyName, charName)); }
    public static ArmyServiceException notSameFaction(String armyName, String playerFactionName, String armyFactionName) { return new ArmyServiceException(NOT_SAME_FACTION.formatted(armyName, playerFactionName, armyFactionName)); }
    public static ArmyServiceException alreadyBound(String armyName, String playerName) { return new ArmyServiceException(ALREADY_BOUND.formatted(armyName, playerName)); }
    public static ArmyServiceException noPlayerBoundToArmy(String armyName) { return new ArmyServiceException(NO_PLAYER_BOUND_TO_ARMY.formatted(armyName)); }
    public static ArmyServiceException tooHighTokenCount(int tokenCount) {return new ArmyServiceException(TOO_HIGH_TOKEN_COUNT.formatted(tokenCount)); };
    public static ArmyServiceException maxArmyOrCompany(String claimbuild, String units) {return new ArmyServiceException(MAX_ARMY_OR_COMPANIES_CREATED.formatted(claimbuild, units)); }
    public static ArmyServiceException onlyLeaderCanBindWanderer() {return new ArmyServiceException(ONLY_FACTION_LEADER_CAN_BIND_WANDERER);}
    public static ArmyServiceException cannotMoveArmyDueToArmyBeingInMovement(String armyName) {return new ArmyServiceException(CANNOT_MOVE_ARMY_DUE_TO_ALREADY_MOVING.formatted(armyName));}
    public static ArmyServiceException cannotMoveArmyDueToPlayerAndArmyBeingInDifferentFactions(String armyName) {return new ArmyServiceException(CANNOT_MOVE_ARMY_DUE_TO_PLAYER_AND_ARMY_BEING_IN_DIFFERENT_FACTIONS.formatted(armyName)); }
    public static ArmyServiceException notAllowedToMoveArmiesThatAreNotBoundToYou() {return new ArmyServiceException(NO_PERMISSION_TO_MOVE_ARMY);}
    public static ArmyServiceException cannotMoveArmyAlreadyInRegion(String armyName, String region) {return new ArmyServiceException(CANNOT_MOVE_ARMY_ALREADY_IN_REGION.formatted(armyName,region));}
    public static ArmyServiceException cannotBindArmyIsMoving(String armyName, String region) {return new ArmyServiceException((CANNOT_BIND_ARMY_IS_MOVING).formatted(armyName, region));}
    public static ArmyServiceException cannotBindCharIsMoving(String charName, String region) {return new ArmyServiceException((CANNOT_BIND_CHAR_IS_MOVING).formatted(charName, region));}
    public static ArmyServiceException cannotCreateArmyFromClaimbuildInDifferentFaction(String playerFaction, String claimbuildFaction) {return new ArmyServiceException((CANNOT_CREATE_ARMY_WHEN_IN_DIFFERENT_FACTIONS).formatted(playerFaction, claimbuildFaction));}
    public static ArmyServiceException armyAndPlayerInDifferentFaction(String playerFaction, String armyFaction) {return new ArmyServiceException(ARMY_AND_PLAYER_IN_DIFFERENT_FACTION.formatted(playerFaction, armyFaction));}
    public static ArmyServiceException needToStationArmyAtCbWithHouseOfHealing(String armyName) {return new ArmyServiceException(ARMY_MUST_BE_STATIONED_AT_A_CLAIMBUILD_WITH_HOUSE_OF_HEALING.formatted(armyName));}
    public static ArmyServiceException armyIsNotHealing(String armyName) {return new ArmyServiceException((CANNOT_STOP_HEALING_IF_ARMY_IS_NOT_HEALING.formatted(armyName)));}
    //Disband army
    public static ArmyServiceException notAllowedToDisbandNotSameFaction(String armyName, String factionName) { return new ArmyServiceException(NOT_ALLOWED_TO_DISBAND_NOT_IN_SAME_FACTION.formatted(armyName, factionName)); }
    public static ArmyServiceException notAllowedToDisband() { return new ArmyServiceException(NOT_ALLOWED_TO_DISBAND); }
    public static ArmyServiceException armyNotStationed(String armyName) { return new ArmyServiceException((ARMY_IS_NOT_STATIONED.formatted(armyName))); }

    //Set free tokens
    public static ArmyServiceException tokenNegative(int tokenAmount) { return new ArmyServiceException(TOKEN_NEGATIVE.formatted(tokenAmount)); }
    public static ArmyServiceException tokenAbove30(int tokenAmount) { return new ArmyServiceException(TOKEN_ABOVE_30.formatted(tokenAmount)); }

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
