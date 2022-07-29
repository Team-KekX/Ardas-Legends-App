package com.ardaslegends.data.service.exceptions.movement;

import com.ardaslegends.data.domain.RPChar;
import com.ardaslegends.data.service.exceptions.ServiceException;

import javax.validation.constraints.NotNull;

public class MovementServiceException extends ServiceException {

    private static final String NO_ACTIVE_MOVEMENT_FOUND_ARMY = "No active movement found for army '%s'!";
    private static final String NO_ACTIVE_MOVEMENT_CHAR = "There are no active movements for the character '%s'!";

    public static MovementServiceException noActiveMovementArmy(String armyName) { return new MovementServiceException(NO_ACTIVE_MOVEMENT_FOUND_ARMY.formatted(armyName)); }
    public static ServiceException noActiveMovementChar(String charName) { return new MovementServiceException(NO_ACTIVE_MOVEMENT_CHAR.formatted(charName)); }

    protected MovementServiceException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    protected MovementServiceException(String message) {
        super(message);
    }
}
