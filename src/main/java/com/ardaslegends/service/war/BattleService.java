package com.ardaslegends.service.war;

import com.ardaslegends.domain.*;
import com.ardaslegends.domain.war.Battle;
import com.ardaslegends.domain.war.BattleLocation;
import com.ardaslegends.domain.war.War;
import com.ardaslegends.domain.war.WarParticipant;
import com.ardaslegends.repository.*;
import com.ardaslegends.repository.war.WarRepository;
import com.ardaslegends.service.*;
import com.ardaslegends.service.dto.war.CreateBattleDto;
import com.ardaslegends.service.exceptions.logic.war.BattleServiceException;
import com.ardaslegends.service.exceptions.logic.army.ArmyServiceException;
import com.ardaslegends.service.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class BattleService extends AbstractService<Battle, BattleRepository> {
    private final BattleRepository battleRepository;
    private final ArmyService armyService;
    private final PlayerService playerService;
    private final ClaimBuildService claimBuildService;
    private final WarRepository warRepository;
    private final Pathfinder pathfinder;

    public Battle createBattle(CreateBattleDto createBattleDto) {
        log.debug("Creating battle with data {}", createBattleDto);
        Objects.requireNonNull(createBattleDto, "CreateBattleDto must not be null");
        Objects.requireNonNull(createBattleDto.battleName(), "BattleName must not be null");

        if (!createBattleDto.isFieldBattle())
            Objects.requireNonNull(createBattleDto.claimBuildName(), "Name of claim build attacked must not be null when creating a claim build battle");

        log.debug("Calling getPlayerByDiscordId with id: [{}]", createBattleDto.executorDiscordId());
        Player executorPlayer = playerService.getPlayerByDiscordId(createBattleDto.executorDiscordId());
        log.debug("Calling getArmyByName with name: [{}]", createBattleDto.attackingArmyName());
        Army attackingArmy = armyService.getArmyByName(createBattleDto.attackingArmyName());

        log.debug("Checking if player has permission to start the battle");
        if (!ServiceUtils.boundLordLeaderPermission(executorPlayer, attackingArmy)) {
            log.warn("Player [{}] does not have the permission to start a battle with the army [{}]!", executorPlayer.getIgn(), createBattleDto.attackingArmyName());
            throw ArmyServiceException.noPermissionToPerformThisAction();
        }

        log.debug("Checking if army has enough health to start the battle");
        if(attackingArmy.getFreeTokens() <=0 ){
            log.warn("The attacking army: [{}] can not start a battle, because they don`t have enough health!", attackingArmy);
            throw BattleServiceException.notEnoughHealth();
        }

        log.debug("Checking if attacking army is currently in a movement");
        if(attackingArmy.getActiveMovement().isPresent()){
            log.warn("Attacking army is currently moving, cannot declare battle!");
            throw BattleServiceException.attackingArmyHasAnotherMovement();
        }

        log.debug("Setting attacking faction to: [{}]", attackingArmy.getFaction().getName());
        Faction attackingFaction = attackingArmy.getFaction();
        Faction defendingFaction; //Not initializing yet, defending faction is evaluated differently for field battles
        Region battleRegion;
        ClaimBuild attackedClaimbuild = null;

        Set<Army> defendingArmies = new HashSet<>();
        List<PathElement> path;

        if(createBattleDto.isFieldBattle()){
            log.debug("Declared battle is field battle");
            log.debug("Fetching single defending army with name: [{}]", createBattleDto.defendingArmyName());
            log.trace("Calling getArmyByName with name: [{}]", createBattleDto.defendingArmyName());
            Army defendingArmy = armyService.getArmyByName(createBattleDto.defendingArmyName());

            log.debug("Calling pathfinder to find shortest way from regions [{}] to [{}]", attackingArmy.getCurrentRegion(), defendingArmy.getCurrentRegion());
            path = pathfinder.findShortestWay(attackingArmy.getCurrentRegion(),defendingArmy.getCurrentRegion(), executorPlayer,false);
            log.debug("Path: [{}], duration: [{} days]", ServiceUtils.buildPathString(path), ServiceUtils.getTotalPathCost(path));

            log.debug("Checking if the defending army is reachable in 24h");
            if(ServiceUtils.getTotalPathCost(path) > 24) {
                log.warn("Cannot create battle because defending army is too far away ([{} hours])", ServiceUtils.getTotalPathCost(path));
                throw BattleServiceException.battleNotAbleDueHours();
            }
            log.debug("Defending army [{}] is in 24h reach of attacking army [{}]", defendingArmy, attackingArmy);

            log.debug("Checking if defending army is moving");
            if(defendingArmy.getActiveMovement().isPresent()) {
                var activeMovement = defendingArmy.getActiveMovement().get();
                log.debug("Defending army [{}] is moving [{}]", defendingArmy, activeMovement);
                log.debug("Hours until next region: [{}]", activeMovement.getHoursUntilNextRegion());
                if(activeMovement.getHoursUntilNextRegion() <= 24) {
                    log.warn("Cannot declare battle - defending army cannot be reached because it is moving away in [{}] hours!", activeMovement.getHoursUntilNextRegion());
                    throw BattleServiceException.defendingArmyIsMovingAway(activeMovement.getHoursUntilNextRegion());
                }
                log.debug("Defending army is moving but can be reached in 24h");
            }

            defendingArmies.add(defendingArmy);
            log.debug("Setting defending Faction to: [{}]", defendingArmy.getFaction().getName());
            defendingFaction = defendingArmy.getFaction();
            battleRegion = defendingArmy.getCurrentRegion();
        }
        else {
            log.debug("Declared battle is claimbuild battle");
            log.debug("Fetching all stationed armies at claimbuild: [{}]", createBattleDto.claimBuildName());
            log.trace("Calling getClaimBuildByName with name: [{}]", createBattleDto.claimBuildName());
            attackedClaimbuild = claimBuildService.getClaimBuildByName(createBattleDto.claimBuildName());
            defendingFaction = attackedClaimbuild.getOwnedBy();
            battleRegion = attackedClaimbuild.getRegion();
            List<Army> stationedArmies = attackedClaimbuild.getStationedArmies();

            log.debug("Calling pathfinder to find shortest way from regions [{}] to [{}]", attackingArmy.getCurrentRegion(), battleRegion);
            path = pathfinder.findShortestWay(attackingArmy.getCurrentRegion(), attackedClaimbuild.getRegion(),executorPlayer,true);
            log.debug("Path: [{}], duration: [{} days]", ServiceUtils.buildPathString(path), ServiceUtils.getTotalPathCost(path));

            log.debug("Checking if claimbuild is reachable in 24 hours");
            if(ServiceUtils.getTotalPathCost(path) > 24){
                log.warn("Cannot declare battle - Claimbuild [{}] is not in 24 hour reach of attacking army [{}]", attackedClaimbuild.getName(), attackingArmy.getName());
                throw BattleServiceException.battleNotAbleDueHours();
            }

            if(stationedArmies.isEmpty())
                log.debug("No armies stationed at claim build with name: [{}]", createBattleDto.claimBuildName());
            else {
                log.debug("[{}] armies stationed at claim build with name: [{}]", stationedArmies.size(), createBattleDto.claimBuildName());
                defendingArmies.addAll(stationedArmies);
            }
        }

        boolean factionsAreAtWar = warRepository.isFactionAtWarWithOtherFaction(attackingFaction, defendingFaction);
        if(!factionsAreAtWar){
            log.warn("Cannot create battle: The attacking faction [{}] and the defending faction [{}] are not at war with each other", attackingFaction.getName(), defendingFaction.getName());
            throw BattleServiceException.factionsNotAtWar(attackingFaction.getName(), defendingFaction.getName());
        }

        //ToDo: Add proper War object when query exists that fetches a specific wars between two factions



        //ToDo: Add 24h in reach check, if the attacking army is in reach of the defending army / CB

        //ToDo: Add proper BattleLocation when the 24h check is available

        WarParticipant aggressors = WarParticipant.builder().warParticipant(attackingFaction).joiningDate(LocalDateTime.now()).initialParty(false).build();
        WarParticipant defenders = WarParticipant.builder().warParticipant(defendingFaction).joiningDate(LocalDateTime.now()).initialParty(false).build();

        Set<WarParticipant> aggressorsSet = new HashSet<>();
        Set<WarParticipant> defendersSet = new HashSet<>();

        aggressorsSet.add(aggressors);
        defendersSet.add(defenders);

        log.debug("Creating BattleLocation");
        BattleLocation battleLocation = new BattleLocation(battleRegion, createBattleDto.isFieldBattle(), attackedClaimbuild);
        log.debug("Created BattleLocation [{}]", battleLocation);
        War war = warRepository.findWarByAggressorsAndDefenders(aggressorsSet,defendersSet);

        log.debug("War inforamtion: " + war);

        log.trace("Assembling Battle Object");
        Battle battle = new Battle(war,
                createBattleDto.battleName(),
                Set.of(attackingArmy),
                defendingArmies,
                LocalDateTime.now(),
                null,
                null,
                null,
                battleLocation);

        log.debug("Trying to persist the battle object");
        battle = secureSave(battle, battleRepository);

        log.info("Successfully created battle [{}]!", battle.getName());
        return battle;
    }
}
