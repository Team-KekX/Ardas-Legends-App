package com.ardaslegends.service.war;

import com.ardaslegends.domain.Faction;
import com.ardaslegends.domain.Player;
import com.ardaslegends.domain.war.War;
import com.ardaslegends.repository.FactionRepository;
import com.ardaslegends.repository.PlayerRepository;
import com.ardaslegends.repository.WarRepository;
import com.ardaslegends.service.AbstractService;
import com.ardaslegends.service.FactionService;
import com.ardaslegends.service.PlayerService;
import com.ardaslegends.service.exceptions.FactionServiceException;
import com.ardaslegends.service.exceptions.PlayerServiceException;
import com.ardaslegends.service.exceptions.WarServiceException;
import com.ardaslegends.service.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class WarService extends AbstractService<War, WarRepository> {

    private final FactionRepository factionRepository;
    private final PlayerRepository playerRepository;

    @Transactional(readOnly = false)
    public War createWar(String defenderName, String executorDiscordId) {
        log.debug("Creating war with data [defender: {}]", defenderName);

        Objects.requireNonNull(defenderName, "Defender Faction name must not be null");
        Objects.requireNonNull(executorDiscordId, "Discord ID of user who executed the command must not be null");

        log.trace("Fetching player with discordId [{}]", executorDiscordId);
        var fetchedPlayer = secureFind(executorDiscordId, playerRepository::findByDiscordID);

        if(fetchedPlayer.isEmpty()) {
            log.warn("Player with discordID [{}] does not exist", executorDiscordId);
            throw PlayerServiceException.noPlayerFound(executorDiscordId);
        }

        Player executorPlayer = fetchedPlayer.get();

        Faction attackingFaction = executorPlayer.getFaction();
        log.trace("Attacking faction is [{}]", attackingFaction.getName());

        // TODO: This should include lords who also can declare wars
        if(!attackingFaction.getLeader().equals(executorPlayer)) {
            log.warn("Player [{}] does not have the permission to declare war!", executorPlayer.getIgn());
            throw WarServiceException.noWarDeclarationPermissions();
        }

        log.trace("Fetching defending Faction with name [{}]", defenderName);
        var fetchedDefendingFaction = secureFind(defenderName, factionRepository::findFactionByName);

        if(fetchedDefendingFaction.isEmpty()) {
            log.warn("No defending faction found with name [{}]", defenderName);
            // TODO: This is stupid as fuck, find other solution
            List<Faction> allFactions = secureFind(factionRepository::findAll);
            String allFactionString = allFactions.stream().map(Faction::getName).collect(Collectors.joining(", "));
            throw FactionServiceException.noFactionWithNameFound(defenderName,allFactionString);
        }

        Faction defendingFaction = fetchedDefendingFaction.get();

        War war = new War();

        return null;
    }

}
