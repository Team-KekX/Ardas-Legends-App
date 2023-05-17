package com.ardaslegends.repository.player;

import com.ardaslegends.domain.Player;

import java.util.Set;

public interface PlayerRepositoryCustom {
    Player queryByDiscordId(String discordId);
    Set<Player> queryByDiscordId(String[] discordIds);
}
