package com.ardaslegends.presentation.api.response.army.unit;

import com.ardaslegends.domain.Unit;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Schema(name = "UnitResponse", description = "Unit response containing all information")
public record UnitResponse(
        @Schema(description = "Name of the Unit", example = "Gondor Archer")
        String name,
        @Schema(description = "The base cost of the unit (how many tokens one of that unit costs)",
                example = "1.5")
        Double baseCost,
        @Schema(description = "If the unit is mounted")
        Boolean isMounted,
        @Schema(description = "How many of that Unit is in the army. This number is the" +
                "'maximum' amount of that unit in the army.", example = "10")
        Integer count,
        @Schema(description = "How many of that unit are alive. I.e. if the 'count' is 10, then a max. of 10 units can be alive",
                example = "5", maximum = "same number as 'count'", minimum = "0")
        Integer alive,
        @Schema(description = "The total amount of tokens that this unit occupies. TotalCost = baseCost * count",
            example = "15", minimum = "baseCost * count")
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
