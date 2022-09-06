package com.ardaslegends.data.service.exceptions.movement;

import com.ardaslegends.data.domain.RPChar;
import com.ardaslegends.data.service.exceptions.ServiceException;

import javax.validation.constraints.NotNull;

public class MovementServiceException extends ServiceException {

    private static final String NO_ACTIVE_MOVEMENT_FOUND_ARMY = "No active movement found for army '%s'!";
    private static final String NO_ACTIVE_MOVEMENT_CHAR = "There are no active movements for the character '%s'!";

    //Cancel army move
    private static final String NOT_ALLOWED_TO_CANCEL_MOVE = "You are not allowed to cancel movements of armies you are not bound to!";
    private static final String NOT_ALLOWED_TO_CANCEL_MOVE_NOT_IN_SAME_FACTION = "The army '%s' is part of the faction '%s' - you cannot cancel its movement unless you are bound to it!";

    //Create rp char move
    private static final String CANNOT_MOVE_CHAR_IS_HEALING = "The character '%s' is currently healing and therefore cannot move!";

    public static MovementServiceException noActiveMovementArmy(String armyName) { return new MovementServiceException(NO_ACTIVE_MOVEMENT_FOUND_ARMY.formatted(armyName)); }
    public static MovementServiceException noActiveMovementChar(String charName) { return new MovementServiceException(NO_ACTIVE_MOVEMENT_CHAR.formatted(charName)); }

    //Cancel army move
    public static MovementServiceException notAllowedToCancelMove() { return new MovementServiceException(NOT_ALLOWED_TO_CANCEL_MOVE); }
    public static MovementServiceException notAllowedToCancelMoveNotSameFaction(String armyName, String factionName) { return new MovementServiceException(NOT_ALLOWED_TO_CANCEL_MOVE_NOT_IN_SAME_FACTION.formatted(armyName, factionName)); }

    //Create rp char move
    public static MovementServiceException cannotMoveCharIsHealing(String charName) { return new MovementServiceException(CANNOT_MOVE_CHAR_IS_HEALING.formatted(charName)); }

    protected MovementServiceException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    protected MovementServiceException(String message) {
        super(message);
    }
}
