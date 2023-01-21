package com.ardaslegends.service.dto.player.rpchar;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateRPCharDto(
        @Schema(description = "Discord ID of the player", example = "261173268365443074")
        String discordId,
        @Schema(description = "Name of the RpChar", example = "Belegorn Arnorion")
        String rpCharName,
        @Schema(description = "RpChar title", example = "King of Gondor")
        String title,
        @Schema(description = "RpChar gear", example = "Gondor Weapons, Blackroot Vale Armor")
        String gear,
        @Schema(description = "Is the char participating in pvp", example = "true")
        boolean pvp
) { }
