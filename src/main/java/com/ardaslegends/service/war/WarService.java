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
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.permission.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class WarService extends AbstractService<War, WarRepository> {
    private final WarRepository warRepository;
    private final FactionRepository factionRepository;
    private final PlayerRepository playerRepository;
    private final DiscordApi discordApi;

    public Page<War> getWars(Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable getWarsBody must not be null");
        var page = secureFind(pageable, warRepository::findAll);
        return page;
    }

    @Transactional(readOnly = false)
    public War createWar(CreateWarDto createWarDto) {
        log.debug("Creating war with data [defender: {}]", createWarDto);

        Objects.requireNonNull(createWarDto, "CreateWarDto must not be null");
        Objects.requireNonNull(createWarDto.executorDiscordId(), "ExecutorDiscordId must not be null");
        Objects.requireNonNull(createWarDto.nameOfWar(), "Name of War must not be null");
        Objects.requireNonNull(createWarDto.defendingFactionName(), "Defending Faction Name must not be null");

        log.trace("Fetching player with discordId [{}]", createWarDto.executorDiscordId());
        var fetchedPlayer = secureFind(createWarDto.executorDiscordId(), playerRepository::findByDiscordID);

        if(fetchedPlayer.isEmpty()) {
            log.warn("Player with discordID [{}] does not exist", createWarDto.executorDiscordId());
            throw PlayerServiceException.noPlayerFound(createWarDto.executorDiscordId());
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
            throw FactionServiceException.noFactionWithNameFoundAndAll(createWarDto.defendingFactionName(),allFactionString);
        }

        var defendingFaction = fetchedDefendingFaction.get();

        if(attackingFaction.equals(defendingFaction)) {
            log.warn("Player [{}] tried to declare war on his faction", executorPlayer.getIgn());
            throw WarServiceException.cannotDeclareWarOnYourFaction();
        }

        boolean alreadyAtWar = secureFind(attackingFaction, defendingFaction, warRepository::isFactionAtWarWithOtherFaction);

        if(alreadyAtWar) {
            log.warn("The factions '{}' and '{}' are already at war!", attackingFaction.getName(), defendingFaction.getName());
            throw WarServiceException.alreadyAtWar(attackingFaction.getName(), defendingFaction.getName());
        }

        // Get Roles
        if(attackingFaction.getFactionRole() == null) {
            attackingFaction.setFactionRole(fetchFactionRole(attackingFaction));
        }
        if(defendingFaction.getFactionRole() == null) {
            defendingFaction.setFactionRole(fetchFactionRole(defendingFaction));
        }

        War war = new War(createWarDto.nameOfWar(), attackingFaction, defendingFaction);

        log.debug("Saving War Entity");
        war = secureSave(war, warRepository);

        log.info("Successfully executed and saved new war {}", war.getName());
        return war;
    }

    public Set<War> getWarsOfFaction(String factionName) {
        // TODO, not yet implemented
        return null;
    }

    public Set<War> getWarsOfFaction(Faction faction) {
        Set<War> wars = secureFind(faction, warRepository::findAllWarsWithFaction);
        return wars;
    }

    private Role fetchFactionRole(Faction faction) {
        Long roleId = faction.getFactionRoleId();

        if(roleId == null) {
            throw new IllegalArgumentException("CONTACT STAFF -> Faction '%s' does not have a faction role set!".formatted(faction.getName()));
        }

        return discordApi.getRoleById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("CONTACT STAFF -> Faction '%s' has a broken roleId! [%s]"
                                .formatted(faction.getName(), faction.getFactionRoleId())));
    }

}
