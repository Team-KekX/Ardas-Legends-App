package com.ardaslegends.service.war;

import com.ardaslegends.domain.Faction;
import com.ardaslegends.domain.Player;
import com.ardaslegends.domain.war.War;
import com.ardaslegends.repository.FactionRepository;
import com.ardaslegends.repository.PlayerRepository;
import com.ardaslegends.repository.WarRepository;
import com.ardaslegends.service.AbstractService;
import com.ardaslegends.service.dto.war.CreateWarDto;
import com.ardaslegends.service.exceptions.FactionServiceException;
import com.ardaslegends.service.exceptions.PlayerServiceException;
import com.ardaslegends.service.exceptions.WarServiceException;
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
    private final WarRepository warRepository;

    private final FactionRepository factionRepository;
    private final PlayerRepository playerRepository;

    @Transactional(readOnly = false)
    public War createWar(CreateWarDto createWarDto) {
        log.debug("Creating war with data [defender: {}]", createWarDto);

        Objects.requireNonNull(createWarDto, "CreateWarDto must not be null");
        Objects.requireNonNull(createWarDto.execturDiscordId(), "ExecutorDiscordId must not be null");
        Objects.requireNonNull(createWarDto.nameOfWar(), "Name of War must not be null");
        Objects.requireNonNull(createWarDto.defendingFactionName(), "Defending Faction Name must not be null");

        log.trace("Fetching player with discordId [{}]", createWarDto.execturDiscordId());
        var fetchedPlayer = secureFind(createWarDto.execturDiscordId(), playerRepository::findByDiscordID);

        if(fetchedPlayer.isEmpty()) {
            log.warn("Player with discordID [{}] does not exist", createWarDto.execturDiscordId());
            throw PlayerServiceException.noPlayerFound(createWarDto.execturDiscordId());
        }

        Player executorPlayer = fetchedPlayer.get();

        Faction attackingFaction = executorPlayer.getFaction();
        log.trace("Attacking faction is [{}]", attackingFaction.getName());

        // TODO: This should include lords who also can declare wars
        if(!attackingFaction.getLeader().equals(executorPlayer)) {
            log.warn("Player [{}] does not have the permission to declare war!", executorPlayer.getIgn());
            throw WarServiceException.noWarDeclarationPermissions();
        }

        log.trace("Fetching defending Faction with name [{}]", createWarDto.defendingFactionName());
        var fetchedDefendingFaction = secureFind(createWarDto.defendingFactionName(), factionRepository::findFactionByName);

        if(fetchedDefendingFaction.isEmpty()) {
            log.warn("No defending faction found with name [{}]", createWarDto.defendingFactionName());
            // TODO: This is stupid as fuck, find other solution
            List<Faction> allFactions = secureFind(factionRepository::findAll);
            String allFactionString = allFactions.stream().map(Faction::getName).collect(Collectors.joining(", "));
            throw FactionServiceException.noFactionWithNameFound(createWarDto.defendingFactionName(),allFactionString);
        }

        War war = new War(createWarDto.nameOfWar(), attackingFaction, fetchedDefendingFaction.get());

        log.debug("Saving War Entity");
        war = secureSave(war, warRepository);

        log.info("Successfully executed and saved new war {}", war.getName());
        return war;
    }

}
