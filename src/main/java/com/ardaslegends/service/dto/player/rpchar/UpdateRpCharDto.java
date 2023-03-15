package com.ardaslegends.service.dto.player.rpchar;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateRpCharDto(
        @Schema(description = "Discord ID of the player", requiredMode = Schema.RequiredMode.REQUIRED, example = "261173268365443074")
        String discordId,
        @Schema(description = "Updated RpChar Name. Null if no update is wished", example = "Firyawe", nullable = true)
        String charName,
        @Schema(description = "Updated RpChar title. Null if no update is wished", example = "Star of Ithilien", nullable = true)
        String title,
        @Schema(description = "Updated RpChar region. Null if no update is wished", example = "200", nullable = true)
        String currentRegion,
        @Schema(description = "Updated bount army name. Null if no update is wished", example = "Gondor Army", nullable = true)
        String boundArmy,
        @Schema(description = "Updated RpChar gear. Null if no update is wished", example = "Gondorian Gear", nullable = true)
        String gear,
        @Schema(description = "Updated RpChar PvP. Null if no update is wished", example = "false", nullable = true)
        Boolean pvp
) { }
