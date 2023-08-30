package com.ardaslegends.presentation.api.response.movement;

import com.ardaslegends.domain.Movement;
import com.ardaslegends.presentation.api.response.army.ArmyResponse;
import com.ardaslegends.presentation.api.response.movement.path.PathResponse;
import com.ardaslegends.presentation.api.response.player.rpchar.RpCharResponse;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public record MovementResponse(
    Long id,
    RpCharResponse rpChar,
    ArmyResponse army,
    Boolean isCharMovement,
    List<PathResponse> path,
    LocalDateTime startTime,
    LocalDateTime endTime,
    Boolean isCurrentlyActive,
    Integer hoursUntilComplete,
    Integer hoursAlreadyMoved,
    Integer hoursUntilNextRegion

) {

    public MovementResponse(Movement movement) {
        this(
                movement.getId(),
                movement.getRpChar() == null ? null : new RpCharResponse(movement.getRpChar()),
                movement.getArmy() == null ? null : new ArmyResponse(movement.getArmy()),
                movement.getIsCharMovement(),
                movement.getPath().stream().map(PathResponse::new).toList(),
                movement.getStartTime(),
                movement.getEndTime(),
                movement.getIsCurrentlyActive(),
                movement.getHoursUntilComplete(),
                movement.getHoursMoved(),
                movement.getHoursUntilComplete()
        );
        log.debug("Created MovementResponse {}", this);
    }
}
