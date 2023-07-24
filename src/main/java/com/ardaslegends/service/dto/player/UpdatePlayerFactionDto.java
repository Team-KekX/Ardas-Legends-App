package com.ardaslegends.service.dto.player;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdatePlayerFactionDto(
        @Schema(description = "Player's Discord ID", example = "261173268365443074")
        String discordId,
        @Schema(description = "Name of the new Facton", example = "Mordor")
        String factionName
) { }
