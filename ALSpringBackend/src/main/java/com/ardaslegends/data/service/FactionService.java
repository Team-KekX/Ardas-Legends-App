package com.ardaslegends.data.service;

import com.ardaslegends.data.domain.Faction;
import com.ardaslegends.data.domain.Player;
import com.ardaslegends.data.repository.FactionRepository;
import com.ardaslegends.data.repository.PlayerRepository;
import com.ardaslegends.data.service.dto.UpdateFactionLeaderDto;
import com.ardaslegends.data.service.dto.faction.UpdateStockpileDto;
import com.ardaslegends.data.service.exceptions.FactionServiceException;
import com.ardaslegends.data.service.exceptions.PlayerServiceException;
import com.ardaslegends.data.service.exceptions.ServiceException;
import com.ardaslegends.data.service.exceptions.army.ArmyServiceException;
import com.ardaslegends.data.service.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j

@Service
@Transactional(readOnly = true)
public class FactionService extends AbstractService<Faction, FactionRepository>{

    private final FactionRepository factionRepository;

    private final PlayerRepository playerRepository;

    @Transactional(readOnly = false)
    public Faction addToStockpile(UpdateStockpileDto dto) {
        log.debug("Updating stockpile of faction with data [{}]",dto);

        ServiceUtils.checkAllNulls(dto);
        ServiceUtils.checkBlanks(dto, List.of("factionName"));

        log.trace("Fetching faction with name [{}]", dto.factionName());
        Faction faction = getFactionByName(dto.factionName());
        log.trace("Fetched Faction [{}]", faction.getName());

        log.debug("Adding amount");
        faction.addFoodToStockpile(dto.amount());

        log.debug("Persisting faction [{}] with stockpile [{}]", faction.getName(), faction.getFoodStockpile());
        secureSave(faction, factionRepository);

        log.info("Returning faction [{}] with stockpile [{}]", faction.getName(), faction.getFoodStockpile());
        return faction;
    }

    @Transactional(readOnly = false)
    public Faction removeFromStockpile(UpdateStockpileDto dto) {
        log.debug("Updating stockpile of faction with data [{}]",dto);

        ServiceUtils.checkAllNulls(dto);
        ServiceUtils.checkBlanks(dto, List.of("factionName"));

        log.trace("Fetching faction with name [{}]", dto.factionName());
        Faction faction = getFactionByName(dto.factionName());
        log.trace("Fetched Faction [{}]", faction.getName());

        log.debug("Removing amount");
        faction.subtractFoodFromStockpile(dto.amount());

        log.debug("Persisting faction [{}] with stockpile [{}]", faction.getName(), faction.getFoodStockpile());
        secureSave(faction, factionRepository);

        log.info("Returning faction [{}] with stockpile [{}]", faction.getName(), faction.getFoodStockpile());
        return faction;
    }


    @Transactional(readOnly = false)
    public Faction setFactionLeader(UpdateFactionLeaderDto dto) {
        log.debug("Updating leader of faction [{}], discordId [{}]", dto.factionName(), dto.targetDiscordId() );

        ServiceUtils.checkAllNulls(dto);
        ServiceUtils.checkAllBlanks(dto);

        log.trace("Fetching faction, dto:[{}]", dto.factionName());
        Faction faction = getFactionByName(dto.factionName());
        log.trace("Fetched Faction [{}]", faction.getName());

        log.trace("Fetching player, dto [{}]", dto.targetDiscordId());
        Optional<Player> fetchedPlayer = playerRepository.findByDiscordID(dto.targetDiscordId());
        if(fetchedPlayer.isEmpty()) {
            log.warn("Could not find a player with discord id [{}]", dto.targetDiscordId());
            throw PlayerServiceException.noPlayerFound(dto.targetDiscordId());
        }
        Player player = fetchedPlayer.get();
        log.trace("Fetched Player, IGN:[{}], DiscordId: [{}]", player.getIgn(), player.getDiscordID());

        log.debug("Fetched relevant data!");

        log.debug("Checking if player is in the same faction");
        if(!faction.equals(player.getFaction())) {
            log.warn("Player [ign:{}] is not in the same faction - target [{}] ", player.getIgn(), faction.getName());
            throw FactionServiceException.factionLeaderMustBeOfSameFaction();
        }

        log.debug("Checking if player has an RpChar");
        if(player.getRpChar() == null) {
            log.warn("Player [ign:{}] does not have an RpChar and cannot be leader", player.getIgn());
            throw FactionServiceException.playerHasNoRpchar();
        }

        log.debug("Player [ign:{}] has an rpchar [name:{}]", player.getIgn(), player.getRpChar().getName());

        String oldLeaderIgn = faction.getLeader() == null ? "No Leader" : faction.getLeader().getIgn();
        log.debug("Faction [{}] current leader [ign:{}], setting it to new player [ign:{}]", faction.getName(),oldLeaderIgn, player.getIgn());
        faction.setLeader(player);

        log.debug("Faction [{}] leader is set to [ign:{}]", faction.getName(), faction.getLeader().getIgn());
        log.trace("Persisting faction object");
        faction = factionRepository.save(faction);

        log.info("Persisted faction [{}] object with new leader [ign:{}]", faction.getName(), faction.getLeader().getIgn());
        return faction;
    }

    public Faction getFactionByName(String name) {
        log.debug("Fetching Faction with name [{}]", name);
        Objects.requireNonNull(name, "Faction name must not be nulL!");

        if(name.isBlank()) {
            log.warn("Faction name is blank");
            throw new IllegalArgumentException("Faction name must not be blank!");
        }
        Optional<Faction> fetchedFaction = secureFind(name, factionRepository::findById);
        
        if (fetchedFaction.isEmpty()) {
            log.warn("No faction found with name {}", name);
            throw FactionServiceException.noFactionWithNameFound(name);
        }
        log.debug("Successfully fetched faction [{}]", fetchedFaction);

        return fetchedFaction.get();

    }


}
