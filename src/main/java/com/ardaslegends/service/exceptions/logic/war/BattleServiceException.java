package com.ardaslegends.service.exceptions.logic.war;

import com.ardaslegends.domain.Army;
import com.ardaslegends.service.exceptions.logic.LogicException;
import com.ardaslegends.service.war.BattleService;
import lombok.val;

public class BattleServiceException extends LogicException {
    private static final String FACTIONS_NOT_AT_WAR = "Cannot declare a battle because %s and %s are not at war with each other!";
    private static final String BATTLE_NOT_ABLE_DUE_HOURS = "Region can not be reached within 24 hours";
    private static final String NOT_ENOUGH_HEALTH = "Army does not have enough health";
    private static final String DEFENDING_ARMY_IS_MOVING_AWAY = "The army you are trying to attack is moving away from you and will already reach the next region in %d hours!";
    private static final String NOT_IN_SAME_REGION = "The army you are trying to attack (%s) is not in the same region as your army (%s)!";
    private static final String ATTACKING_ARMY_HAS_ANOTHER_MOVEMENT = "Attacking army has another ongoing movement!";
    private static final String CANNOT_ATTACK_STARTER_HAMLET = "Starter hamlets cannot be attacked!";
    private static final String NO_PLAYER_BOUND = "The army '%s' must have a player bound to it in order to attack!";
    private static final String ARMY_YOUNGER_THAN_24H = "The army '%s' was created less than 24 hours ago!";

    public static BattleServiceException factionsNotAtWar(String factionName1, String factionName2) { return new BattleServiceException(FACTIONS_NOT_AT_WAR.formatted(factionName1, factionName2)); }

    public static BattleServiceException battleNotAbleDueHours (){ return  new BattleServiceException(BATTLE_NOT_ABLE_DUE_HOURS.formatted());}
    public static BattleServiceException defendingArmyIsMovingAway (Army armyMovingAway){
        val hoursUntilNextRegion = armyMovingAway.getActiveMovement().orElseThrow(() -> new IllegalArgumentException("Army %s has no movement! PLEASE CONTACT DEVS".formatted(armyMovingAway.getName()))).getHoursUntilNextRegion();
        return new BattleServiceException(DEFENDING_ARMY_IS_MOVING_AWAY.formatted(hoursUntilNextRegion));}
    public static BattleServiceException attackingArmyHasAnotherMovement(){return new BattleServiceException(ATTACKING_ARMY_HAS_ANOTHER_MOVEMENT.formatted());}
    public static BattleServiceException notEnoughHealth(){ return new BattleServiceException(NOT_ENOUGH_HEALTH.formatted());}
    public static BattleServiceException notInSameRegion(Army attacker, Army defender){ return new BattleServiceException(NOT_IN_SAME_REGION.formatted(defender.getName(), attacker.getName()));}
    public static BattleServiceException cannotAttackStarterHamlet() {return new BattleServiceException(CANNOT_ATTACK_STARTER_HAMLET);}
    public static BattleServiceException noPlayerBound(String armyName) {return new BattleServiceException(NO_PLAYER_BOUND.formatted(armyName));}
    public static BattleServiceException armyYoungerThan24h(String armyName) {return new BattleServiceException(ARMY_YOUNGER_THAN_24H.formatted(armyName));}
    protected BattleServiceException(String message, Throwable rootCause) {
        super(message, rootCause);
    }
    protected BattleServiceException(String message) {
        super(message);
    }
}
