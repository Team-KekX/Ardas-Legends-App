package com.ardaslegends.presentation.api.response.movement.path;

import com.ardaslegends.domain.PathElement;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record PathResponse(
    String region,
    Integer baseCost,
    Integer actualCost
) {

    public PathResponse(PathElement pathElement) {
        this(
                pathElement.getRegion().getId(),
                pathElement.getBaseCost(),
                pathElement.getActualCost()
        );
        log.debug("Created PathResponse {}", this);
    }
}
