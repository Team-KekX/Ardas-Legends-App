package com.ardaslegends.presentation.api.response.army;

import com.ardaslegends.domain.Army;

public record ArmyResponse(
        long id,
        String name,
        String armyType,
        String faction,
        String currentRegion,
        String boundTo
) {
    public ArmyResponse(Army army) {
        this(
                army.getId(),
                army.getName(),
                army.getArmyType().getName(),
                army.getFaction().getName(),
                army.getCurrentRegion().getId(),
                army.getBoundTo() == null ? null : army.getBoundTo().getName()
        );
    }
}
