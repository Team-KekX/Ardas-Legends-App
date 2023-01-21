package com.ardaslegends.service.dto.player;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

public record DiscordIdDto(
        @Schema(description = "Discord ID of the player", example = "1015367754771083405")
        String discordId
) { }
