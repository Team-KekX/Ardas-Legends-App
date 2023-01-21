package com.ardaslegends.service.dto.player;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdatePlayerIgnDto(
        @Schema(description = "New IGN", example = "mirak441")
        String ign,
        @Schema(description = "Discord ID of the player that should change IGN", example = "261173268365443074")
        String discordId
) { }
