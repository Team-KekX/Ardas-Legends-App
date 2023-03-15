package com.ardaslegends.presentation.api.response.player;

import com.ardaslegends.domain.Player;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Schema(name = "Update Discord ID Response", description = "Player response when updating DiscordId")
public record PlayerUpdateDiscordIdResponse(
        @Schema(description = "Player's new Discord ID", example = "1015367754771083405")
        String discordId,
        @Schema(description = "Player's previous Discord ID", example = "261173268365443074")
        String oldDiscordId,
        @Schema(description = "Player's Minecraft IGN", example = "VernonRoche")
        String ign,
        @Schema(description = "Faction name of new player", example = "Gondor")
        String faction
) {

    public PlayerUpdateDiscordIdResponse(Player player, String oldDiscordId) {
        this(player.getDiscordID(), oldDiscordId, player.getIgn(), player.getFaction().getName());
        log.debug("Created PlayerUpdateDiscordIdResponse: '{}'", this);
    }
}
