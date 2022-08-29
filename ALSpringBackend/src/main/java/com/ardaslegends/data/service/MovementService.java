package com.ardaslegends.data.service;

import com.ardaslegends.data.domain.*;
import com.ardaslegends.data.repository.ArmyRepository;
import com.ardaslegends.data.repository.MovementRepository;
import com.ardaslegends.data.repository.PlayerRepository;
import com.ardaslegends.data.repository.RegionRepository;
import com.ardaslegends.data.service.dto.army.CreateArmyDto;
import com.ardaslegends.data.service.dto.army.MoveArmyDto;
import com.ardaslegends.data.service.dto.player.DiscordIdDto;
import com.ardaslegends.data.service.dto.player.rpchar.MoveRpCharDto;
import com.ardaslegends.data.service.exceptions.PlayerServiceException;
import com.ardaslegends.data.service.exceptions.ServiceException;
import com.ardaslegends.data.service.exceptions.army.ArmyServiceException;
import com.ardaslegends.data.service.exceptions.movement.MovementServiceException;
import com.ardaslegends.data.service.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor

@Slf4j
@Service
@Transactional(readOnly = true)
public class MovementService extends AbstractService<Movement, MovementRepository>{

    private final MovementRepository movementRepository;
    private final RegionRepository regionRepository;
    private final ArmyRepository armyRepository;
    private final ArmyService armyService;
    private final PlayerRepository playerRepository;
    private final PlayerService playerService;
    private final Pathfinder pathfinder;

    // TODO: Check if time is frozen -> if yes, cancel request
    // TODO: Check if army is in a battle -> if yes, cancel request
    // TODO: Check if army is healing -> if yes, ask to stop healing
    @Transactional(readOnly = false)
    public Movement createArmyMovement(MoveArmyDto dto) {
        log.debug("Trying to move Army [{}] executed by [{}] to Region [{}]", dto.armyName(), dto.executorDiscordId(), dto.toRegion());

        ServiceUtils.checkAllNulls(dto);
        ServiceUtils.checkAllBlanks(dto);

        log.debug("Fetching required data");

        log.trace("Fetching player");
        Player player = playerService.getPlayerByDiscordId(dto.executorDiscordId());

        log.trace("Fetching army entity");
        Army army = armyService.getArmyByName(dto.armyName());

        log.trace("Fetching region entity");
        Optional<Region> fetchedRegion = secureFind(dto.toRegion(), regionRepository::findById);

        if(fetchedRegion.isEmpty()) {
            log.warn("Desired region [{}] does not exist in the database", dto.toRegion());
            throw ServiceException.regionDoesNotExist(dto.toRegion());
        }
        Region region = fetchedRegion.get();

        log.debug("Checking if army is already in the desired region");
        if(dto.toRegion().equals(army.getCurrentRegion().getId())) {
            log.warn("Army is already in desired region [{}], no movement required", dto.toRegion());
            throw ArmyServiceException.cannotMoveArmyAlreadyInRegion(army.getArmyType(),army.toString(),dto.toRegion());
        }

        log.debug("Checking if army is currently performing a movement");
        if(secureFind(army, movementRepository::findMovementByArmyAndIsCurrentlyActiveTrue).isPresent()) {
            log.warn("Army [{}] is currently performing a movement", dto.armyName());
            throw ArmyServiceException.cannotMoveArmyDueToArmyBeingInMovement(army.getArmyType(),army.getName());
        }

        log.debug("Checking if army is older than 24h");
        if(LocalDateTime.now().isBefore(army.getCreatedAt().plusDays(1))) {
            log.warn("Army [{}] is younger than 24h and therefore cannot move!", army);
            long hoursUntilMove = 24 - Duration.between(army.getCreatedAt(), LocalDateTime.now()).toHours();
            log.debug("Army can move again in [{}] hours", hoursUntilMove);
            throw ArmyServiceException.cannotMoveArmyWasCreatedRecently(army.getName(), hoursUntilMove);
        }

        log.debug("Checking if executor is allowed to perform the movement");
        boolean isAllowed = ServiceUtils.boundLordLeaderPermission(player, army);

        if(!isAllowed) {
            log.warn("Player [{}] in Faction [{}] does not have permission to move armies", player.getIgn(), player.getFaction());
            throw ArmyServiceException.noPermissionToPerformThisAction();
        }

        log.debug("Player [{}] is allowed to move army [{}], executing pathfinder", player, army);
        Path path = pathfinder.findShortestWay(army.getCurrentRegion(),region,player, false);

        var currentTime = LocalDateTime.now();
        log.debug("Creating movement object");
        Movement movement = Movement.builder()
                .army(army)
                .player(army.getBoundTo())
                .isCharMovement(false)
                .isAccepted(false)
                .isCurrentlyActive(true)
                .startTime(currentTime)
                .endTime(currentTime.plusDays(path.getCost()))
                .path(path)
                .build();

        log.debug("Saving Movement to database");
        secureSave(movement, movementRepository);

        log.info("Successfully saved movement [{}]", movement);
        return movement;
    }

    @Transactional(readOnly = false)
    public Movement cancelArmyMovement(MoveArmyDto dto) {
        log.debug("Trying to cancel movement of army [{}] (executed by player [{}])", dto.armyName(), dto.executorDiscordId());

        log.trace("Validating data");
        ServiceUtils.checkNulls(dto, List.of("armyName", "executorDiscordId"));
        ServiceUtils.checkBlanks(dto, List.of("armyName", "executorDiscordId"));

        log.trace("Getting the army instance");
        Army army = armyService.getArmyByName(dto.armyName());

        log.trace("Getting the player instance");
        Player player = playerService.getPlayerByDiscordId(dto.executorDiscordId());

        boolean isAllowed = ServiceUtils.boundLordLeaderPermission(player, army);

        if(!isAllowed) {
            log.warn("Player [{}] is not allowed to cancel movements of army [{}]", player, army);
            throw MovementServiceException.notAllowedToCancelMove();
        }

        log.trace("Getting active movement for army [{}]", army);
        Movement movement = getActiveMovementByArmy(army);

        log.debug("Setting the movement to inactive");
        movement.setIsCurrentlyActive(false);

        log.debug("Persisting the movement");
        movementRepository.save(movement);

        log.info("Cancelled movement of army [{}] - Start: [{}] ([{}]) - End: [{}] ([{}])", army, movement.getStartRegionId(), movement.getStartTime(), movement.getDestinationRegionId(), movement.getEndTime());
        return movement;
    }

    @Transactional(readOnly = false)
    public Movement createRpCharMovement(MoveRpCharDto dto) {
        log.debug("Moving RpChar of player {} to Region {}", dto.discordId(), dto.toRegion());

        //Validating data

        log.trace("Validating Data");
        ServiceUtils.checkAllNulls(dto);
        ServiceUtils.checkAllBlanks(dto);

        log.trace("Getting the player");
        Optional<Player> fetchedPlayer = secureFind(dto.discordId(), playerRepository::findByDiscordID);

        if(fetchedPlayer.isEmpty()) {
            log.warn("No player found with discordId [{}]", dto.discordId());
            throw new IllegalArgumentException("No player found with discordId [%s]".formatted(dto.discordId()));
        }
        Player player = fetchedPlayer.get();
        log.trace("Setting the RPChar");
        RPChar rpChar = player.getRpChar();

        log.debug("Checking if the Player has a RP Char");
        if(rpChar == null) {
            log.warn("Player {} has no RP Char!", player);
            throw PlayerServiceException.noRpChar();
        }

        log.debug("Checking if destination is the current region");
        if(dto.toRegion().equals(rpChar.getCurrentRegion().getId())) {
            log.warn("Character is already in region {}!", dto.toRegion());
            throw ServiceException.cannotMoveRpCharAlreadyInRegion(rpChar, rpChar.getCurrentRegion());
        }

        log.debug("Checking if rpChar is bound to army");
        if(rpChar.getBoundTo() != null) {
            log.warn("RpChar is currently bound to army!");
            throw ServiceException.cannotMoveRpCharBoundToArmy(rpChar, rpChar.getBoundTo());
        }

        log.debug("Checking if rpchar is currently healing");
        if(rpChar.getIsHealing()) {
            log.warn("RpChar [{}] is currently healing and therefore cannot move", rpChar);
            throw MovementServiceException.cannotMoveCharIsHealing(rpChar.getName());
        }

        log.debug("Checking if rpChar is already in a movement");
        List<Movement> playerMovements = secureFind(player, movementRepository::findMovementsByPlayer);
        if(playerMovements.stream().anyMatch(Movement::getIsCurrentlyActive)) { //Checking if there are any active movements
            log.warn("Player {} is already involved in a movement!", player);
            throw ServiceException.cannotMoveRpCharAlreadyMoving(player.getRpChar());
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
        Movement movement = Movement.builder().player(player).path(shortestPath).startTime(currentTime).endTime(currentTime.plusDays(shortestPath.getCost())).isCharMovement(true).isAccepted(false).isCurrentlyActive(true).build();

        log.trace("Saving the new movement");
        movement = secureSave(movement, movementRepository);

        log.info("Successfully created new Movement for the RPChar '{}' of Player '{}'", rpChar.getName(), player);
        return movement;
    }

    @Transactional(readOnly = false)
    public Movement cancelRpCharMovement(DiscordIdDto dto) {
        log.debug("Cancelling the rp char movement of player {}", dto.discordId());

        //validating data
        log.trace("Validating Data");
        ServiceUtils.checkAllNulls(dto);
        ServiceUtils.checkAllBlanks(dto);

        log.trace("Getting the player");
        Optional<Player> fetchedPlayer = secureFind(dto.discordId(), playerRepository::findByDiscordID);

        if(fetchedPlayer.isEmpty()) {
            log.warn("No player found with discordId [{}]", dto.discordId());
            throw new IllegalArgumentException("No player found with discordId [%s]".formatted(dto.discordId()));
        }
        Player player = fetchedPlayer.get();
        RPChar rpChar = player.getRpChar();

        log.debug("Checking if the Player has a RP Char");
        if(rpChar == null) {
            log.warn("Player {} has no RP Char!", player);
            throw PlayerServiceException.noRpChar();
        }

        log.trace("Searching for active movements of this player");
        Movement movement = getActiveMovementByChar(player);

        log.debug("Setting movement to inactive");
        movement.setIsCurrentlyActive(false);

        log.debug("Persisting movement");
        movement = secureSave(movement, movementRepository);

        return movement;
    }

    public Movement getActiveMovementByArmy(Army army) {
        log.debug("Trying to get an active Movement for the army [{}]", army);

        log.trace("Executing the secureFind");
        Optional<Movement> fetchedMove = secureFind(army, movementRepository::findMovementByArmyAndIsCurrentlyActiveTrue);

        log.debug("Checking if a movement was found");
        if(fetchedMove.isEmpty()) {
            log.warn("No active movement was found for the army [{}]!", army);
            throw MovementServiceException.noActiveMovementArmy(army.getName());
        }

        Movement movement = fetchedMove.get();
        log.debug("Found a movement from region [{}] to [{}]!", movement.getPath().getStart(), movement.getPath().getDestination());

        return movement;
    }

    public Movement getActiveMovementByChar(Player player) {
        log.debug("Trying to get an active Movement for the player [{}]", player);

        log.trace("Executing the secureFind");
        Optional<Movement> fetchedMove = secureFind(player, movementRepository::findMovementByPlayerAndIsCurrentlyActiveTrue);

        log.debug("Checking if a movement was found");
        if(fetchedMove.isEmpty()) {
            log.warn("No active movement was found for the player [{}]!", player);
            throw MovementServiceException.noActiveMovementChar(player.getRpChar().getName());
        }

        Movement movement = fetchedMove.get();
        log.debug("Found a movement from region [{}] to [{}]!", movement.getPath().getStart(), movement.getPath().getDestination());

        return movement;
    }

}
