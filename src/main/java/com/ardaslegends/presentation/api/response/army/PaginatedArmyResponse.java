package com.ardaslegends.presentation.api.response.army;

import com.ardaslegends.domain.Army;
import com.ardaslegends.domain.ArmyType;
import com.ardaslegends.presentation.api.response.war.PaginatedWarsResponse;

import jakarta.validation.constraints.NotNull;

public record PaginatedArmyResponse(
        long id,
        String nameOfArmy,
        ArmyType armyType,
        String nameOfFaction,
        String currentRegion
) {
    public PaginatedArmyResponse(Army army) {
        this(
                army.getId(),
                army.getName(),
                army.getArmyType(),
                army.getFaction().getName(),
                army.getCurrentRegion().getId()
        );
    }
}
