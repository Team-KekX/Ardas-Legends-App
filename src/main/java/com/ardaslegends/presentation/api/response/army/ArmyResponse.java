package com.ardaslegends.presentation.api.response.army;

import com.ardaslegends.domain.Army;

public record ArmyResponse(
        long id,
        String nameOfArmy,
        String armyType,
        String nameOfFaction,
        String currentRegion
) {
    public ArmyResponse(Army army) {
        this(
                army.getId(),
                army.getName(),
                army.getArmyType().getName(),
                army.getFaction().getName(),
                army.getCurrentRegion().getId()
        );
    }
}
