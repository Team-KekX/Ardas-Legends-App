package com.ardaslegends.repository.player;

import com.ardaslegends.domain.Player;
import lombok.val;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Set;

public class PlayerRepositoryImpl extends QuerydslRepositorySupport implements PlayerRepositoryCustom {
    public PlayerRepositoryImpl() {
        super(Player.class);
    }

    @Override
    public Player queryByDiscordId(String discordId) {
        Objects.requireNonNull(discordId);

        return null;
    }

    @Override
    public Set<Player> queryByDiscordId(String[] discordIds) {
        return null;
    }
}
