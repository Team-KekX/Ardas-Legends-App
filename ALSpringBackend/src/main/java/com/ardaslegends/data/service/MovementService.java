package com.ardaslegends.data.service;

import com.ardaslegends.data.domain.*;
import com.ardaslegends.data.repository.MovementRepository;
import com.ardaslegends.data.repository.RegionRepository;
import com.ardaslegends.data.service.dto.player.rpchar.MoveRpCharDto;
import com.ardaslegends.data.service.exceptions.ServiceException;
import com.ardaslegends.data.service.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor

@Slf4j
@Service
@Transactional(readOnly = true)
public class MovementService extends AbstractService<Movement, MovementRepository>{

    private final MovementRepository movementRepository;
    private final RegionRepository regionRepository;

    private final PlayerService playerService;
    private final Pathfinder pathfinder;

    @Transactional(readOnly = false)
    public Movement createMovement(Movement movement) {
        log.debug("Saving movement {}", movement);

        secureSave(movement, movementRepository);

        log.info("Successfully created movement: {}", movement);
        return movement;
    }

    //TODO: Check if RPChar is bound to an army
    @Transactional(readOnly = false)
    public Movement createRpCharMovement(MoveRpCharDto dto) {
        log.debug("Moving RpChar of player {} to Region {}", dto.discordId(), dto.toRegion());

        //Validating data

        log.trace("Validating Data");
        ServiceUtils.checkAllNulls(dto);
        ServiceUtils.checkAllBlanks(dto);

        log.trace("Getting the player");
        Player player = playerService.getPlayerByDiscordId(dto.discordId());

        log.trace("Setting the RPChar");
        RPChar rpChar = player.getRpChar();

        log.debug("Checking if the Player has a RP Char");
        if(rpChar == null) {
            log.warn("Player {} has no RP Char!", player);
            throw ServiceException.noRpChar();
        }

        log.debug("Checking if rpChar is bound to army");
        if(rpChar.getBoundTo() != null) {
            log.warn("RpChar is currently bound to army!");
            throw ServiceException.cannotMoveRpCharBoundToArmy(rpChar, rpChar.getBoundTo());
        }

        //Setting up Region Data

        log.trace("Find the region the player is moving to");
        Optional<Region> fetchedToRegion = secureFind(dto.toRegion(), regionRepository::findById);

        if(fetchedToRegion.isEmpty()) {
            log.warn("User inputed to Region does not exist [{}]", dto.toRegion());
            throw new IllegalArgumentException("The region %s does not exist!".formatted(dto.toRegion()));
        }

        Region toRegion = fetchedToRegion.get();

        log.trace("Getting the RPChar's current region");
        Region fromRegion = rpChar.getCurrentRegion();

        log.debug("Calling the pathfinder to find the fastest way from '{}' -> '{}'", fromRegion.getId(), toRegion.getId());
        Path shortestPath = pathfinder.findShortestWay(fromRegion, toRegion, player, true);

        log.trace("Getting the current time");
        LocalDateTime currentTime = LocalDateTime.now();

        log.trace("Building the movement object");
        Movement movement = Movement.builder().player(player).path(shortestPath).startTime(currentTime).endTime(currentTime.plusDays(shortestPath.getCost())).isCharMovement(true).isAccepted(false).build();

        log.trace("Saving the new movement");
        createMovement(movement);

        log.info("Successfully created new Movement for the RPChar '{}' of Player '{}'", rpChar.getName(), player);
        return movement;
    }
}
