package com.ardaslegends.repository.player;

import com.ardaslegends.domain.Player;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PlayerRepositoryCustom {
    Player queryByDiscordId(String discordId);
    Player queryByIgn(String ign);
    Set<Player> queryAllByDiscordIds(String[] discordIds);
    Set<Player> queryAllByIgns(String[] igns);
    Optional<Player> queryPlayerByRpChar(String name);
    List<Player> queryPlayersWithHealingRpchars();
}
