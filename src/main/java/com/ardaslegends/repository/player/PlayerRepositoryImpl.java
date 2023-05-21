package com.ardaslegends.repository.player;

import com.ardaslegends.domain.Player;
import com.ardaslegends.domain.QPlayer;
import com.ardaslegends.repository.exceptions.PlayerRepositoryException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.lang.NonNull;

import java.util.Objects;
import java.util.Set;

@Slf4j
public class PlayerRepositoryImpl extends QuerydslRepositorySupport implements PlayerRepositoryCustom {
    public PlayerRepositoryImpl() {
        super(Player.class);
    }

    /**
     * Save QueryDSL Vairant
     * @param discordId which the queried player should have
     * @return a non-null player object
     * @throws NullPointerException if any parameter is null
     * @throws PlayerRepositoryException if no player was found
     */
    @Override
    public @NonNull Player queryByDiscordId(String discordId) {
        Objects.requireNonNull(discordId);

        val qplayer = QPlayer.player;

        val fetchedPlayer = from(qplayer)
                .where(qplayer.discordID.eq(discordId))
                .fetchFirst();

        if(fetchedPlayer == null) { throw PlayerRepositoryException.entityNotFound("discordId", discordId); }
        return fetchedPlayer;
    }

    @Override
    public Set<Player> queryByDiscordId(String[] discordIds) {
        return null;
    }
}
