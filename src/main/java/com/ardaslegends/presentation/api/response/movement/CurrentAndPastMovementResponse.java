package com.ardaslegends.presentation.api.response.movement;

import com.ardaslegends.domain.Movement;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Optional;

public record CurrentAndPastMovementResponse(
        MovementResponse currentMovement,
        List<MovementResponse> pastMovements
) {
    public CurrentAndPastMovementResponse(Movement currentMovement, List<Movement> pastMovements) {
        this(
                currentMovement == null ? null : new MovementResponse(currentMovement),
                pastMovements.stream().map(MovementResponse::new).toList()
        );
    }
}
