package com.ardaslegends.service.war;

import com.ardaslegends.domain.Faction;
import com.ardaslegends.domain.war.War;
import com.ardaslegends.repository.WarRepository;
import com.ardaslegends.service.AbstractService;
import com.ardaslegends.service.FactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;


@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class WarService extends AbstractService<War, WarRepository> {

    private final FactionService factionService;

    @Transactional(readOnly = false)
    public War createWar(Faction attackerName, Faction defenderName, String executorDiscordId) {
        log.debug("Creating war with data [aggressor: {}, defender: {}]", attackerName, defenderName);

        Objects.requireNonNull(attackerName, "Attacker Faction name must not be null");
        Objects.requireNonNull(defenderName, "Defender Faction name must not be null");
        Objects.requireNonNull(executorDiscordId, "Discord ID of user who executed the command must not be null");


        return null;
    }

}
