package com.ardaslegends.presentation.api.response.army;

import com.ardaslegends.domain.Army;
import com.ardaslegends.domain.ArmyType;
import com.ardaslegends.presentation.api.response.army.unit.UnitResponse;
import com.ardaslegends.presentation.api.response.player.PlayerResponse;
import com.ardaslegends.presentation.api.response.player.PlayerRpCharResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Schema(name = "ArmyResponse", description = "Army response containing all information")
public record ArmyResponse(
        @Schema(description = "ID", example = "5")
        long id,
        @Schema(description = "Army name", example = "Army of Gondor")
        String name,
        @Schema(description = "Type of army", example = "ARMY")
        ArmyType armyType,
        @Schema(description = "Army's faction", example = "Gondor")
        String faction,
        UnitResponse[] units,
        @Schema(description = "Army's current region", example = "99")
        String currentRegion,
        @Schema(description = "Name of Claimbuild the army is currently stationed at. Null if not stationed anywhere",
                example = "Minas Tirith", nullable = true)
        String stationedAt,
        @Schema(description = "Player that is bound to the army. Null if no player is bound",
                nullable = true)
        PlayerResponse boundTo,
        @Schema(description = "List of siege gear of the army", example = "[\"Trebuchet\", \"Ram\"]")
        String[] sieges,
        @Schema(description = "Specifies if the army is paid already", example = "true")
        Boolean isPaid,
        @Schema(description = "Remaining tokens of the army. 0 means the army cannot hold any more units.",
                example = "0", minimum = "0", maximum = "30")
        Double freeTokens,
        @Schema(description = "Time when healing of army ends. Null if army is not healing",
            nullable = true)
        LocalDateTime healEnds,
        @Schema(description = "Name of Claimbuild the army was created at",
                example = "Minas Tirith")
        String originalClaimbuild
) {
    public ArmyResponse(Army army) {
        this(
                army.getId(),
                army.getName(),
                army.getArmyType(),
                army.getFaction().getName(),
                army.getUnits().stream().map(UnitResponse::new).toArray(UnitResponse[]::new),
                army.getCurrentRegion().getId(),
                army.getStationedAt() == null ? null : army.getStationedAt().getName(),
                army.getBoundTo() == null ? null : new PlayerResponse(army.getBoundTo()),
                army.getSieges().toArray(String[]::new),
                army.getIsPaid(),
                army.getFreeTokens(),
                army.getHealEnd(),
                army.getOriginalClaimbuild().getName()
        );
    }
}
