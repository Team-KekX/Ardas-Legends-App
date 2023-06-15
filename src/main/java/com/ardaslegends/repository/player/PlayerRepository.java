package com.ardaslegends.repository.player;

import com.ardaslegends.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Repository
public interface PlayerRepository extends JpaRepository<Player, String>, PlayerRepositoryCustom {

    Optional<Player> findPlayerByIgn(String ign);
    Optional<Player> findByDiscordID(String discordId);
}
