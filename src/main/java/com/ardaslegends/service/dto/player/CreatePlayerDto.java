package com.ardaslegends.service.dto.player;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Create Player", description = "Data for creating a player")
public record CreatePlayerDto(
        @Schema(description = "Minecraft IGN of the Player", example = "Luktronic")
        String ign,
        @Schema(description = "Discord Account ID of the Player", example = "1015367754771083405")
        String discordID,
        @Schema(description = "The faction the player is joining", example = "Gondor")
        String faction
) { }
