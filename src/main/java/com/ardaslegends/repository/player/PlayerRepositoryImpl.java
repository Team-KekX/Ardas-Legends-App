package com.ardaslegends.repository.player;

import com.ardaslegends.domain.Player;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.Set;

public class PlayerRepositoryImpl extends QuerydslRepositorySupport implements PlayerRepositoryCustom {
    public PlayerRepositoryImpl() {
        super(Player.class);
    }

    @Override
    public Player queryByDiscordId(String discordId) {

        return null;
    }

    @Override
    public Set<Player> queryByDiscordId(String[] discordIds) {
        return null;
    }
}
