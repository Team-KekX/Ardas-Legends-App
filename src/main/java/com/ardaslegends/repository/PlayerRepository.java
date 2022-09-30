package com.ardaslegends.repository;

import com.ardaslegends.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, String> {

    Optional<Player> findPlayerByIgn(String ign);

    Optional<Player> findByDiscordID(String discordId);
    List<Player> findPlayerByRpCharIsHealingTrue();

    @Query("FROM Player WHERE rpChar.name = ?1")
    Optional<Player> findPlayerByRpChar(String name);
}
