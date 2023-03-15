package com.ardaslegends.presentation.api.response.player;

import com.ardaslegends.domain.Player;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Schema(name = "Player", description = "Player response containing basic information")
public record PlayerResponse(

        @Schema(description = "Player's Discord ID", example = "1015367754771083405")
        String discordId,
        @Schema(description = "Player's Minecraft IGN", example = "VernonRoche")
        String ign,
        @Schema(description = "Faction name of new player", example = "Gondor")
        String faction
) {

    public PlayerResponse(Player player) {
        this(player.getDiscordID(), player.getIgn(), player.getFaction().getName());
        log.debug("Created PlayerResponse: '{}'", this);
    }
}
