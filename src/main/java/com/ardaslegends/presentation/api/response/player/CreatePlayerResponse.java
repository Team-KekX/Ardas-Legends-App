package com.ardaslegends.presentation.api.response.player;

import com.ardaslegends.domain.Player;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Create Player Response", description = "Reponse body when creating/registering a new player")
public record CreatePlayerResponse(

        @Schema(description = "Player's Discord ID", example = "1015367754771083405")
        String discordId,
        @Schema(description = "Player's Minecraft IGN", example = "VernonRoche")
        String ign,
        @Schema(description = "Faction name of new player", example = "Gondor")
        String faction
) {

    public CreatePlayerResponse(Player player) {
        this(player.getDiscordID(), player.getIgn(), player.getFaction().getName());
        log.debug("Created CreatePlayerResponse: '{}'", this);
    }
}
