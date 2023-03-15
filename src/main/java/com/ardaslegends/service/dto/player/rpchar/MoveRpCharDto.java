package com.ardaslegends.service.dto.player.rpchar;

import io.swagger.v3.oas.annotations.media.Schema;

public record MoveRpCharDto(
        @Schema(description = "Player's Discord ID", example = "261173268365443074")
        String discordId,
        @Schema(description = "Region to move to", example = "192")
        String toRegion
) { }
