package com.ardaslegends.presentation.api.response.army.unit;

import com.ardaslegends.domain.Unit;

public record UnitResponse(
        String name,
        Double baseCost,
        Boolean isMounted,
        Integer count,
        Integer alive,
        Double totalCost


) {

    public UnitResponse(Unit unit) {
        this(
                unit.getUnitType().getUnitName(),
                unit.getUnitType().getTokenCost(),
                unit.getIsMounted(),
                unit.getCount(),
                unit.getAmountAlive(),
                unit.getCost()
        );
    }
}
