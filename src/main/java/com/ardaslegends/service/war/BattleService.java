package com.ardaslegends.service.war;

import com.ardaslegends.domain.*;
import com.ardaslegends.domain.war.Battle;
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

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

        if (!createBattleDto.FieldBattle())
            Objects.requireNonNull(createBattleDto.ClaimBuildName(), "Name of claim build attacked must not be null when creating a claim build battle");

        log.debug("Calling getPlayerByDiscordId with id: [{}]", createBattleDto.executorDiscordId());
        Player executorPlayer = playerService.getPlayerByDiscordId(createBattleDto.executorDiscordId());
        log.debug("Calling getArmyByName with name: [{}]", createBattleDto.attackingArmyName());
        Army attackingArmy = armyService.getArmyByName(createBattleDto.attackingArmyName());

//        System.out.println(executorPlayer.getActiveCharacter().get().getBoundTo().getName());
        System.out.println(attackingArmy.getName());
        if (!ServiceUtils.boundLordLeaderPermission(executorPlayer, attackingArmy)) {
            log.warn("Player [{}] does not have the permission to start a battle with the army [{}]!", executorPlayer.getIgn(), createBattleDto.attackingArmyName());
            throw ArmyServiceException.noPermissionToPerformThisAction();
        }

        log.debug("Setting attacking faction to: [{}]", attackingArmy.getFaction().getName());
        Faction attackingFaction = attackingArmy.getFaction();
        Faction defendingFaction;


        Set<Army> defendingArmies = new HashSet<>();
        List<PathElement> paths ;
        boolean movementisAble;

        if(createBattleDto.FieldBattle()){
            log.debug("Field battle boolean is set to true - fetching one defending army with name: [{}]", createBattleDto.defendingArmyName());
            log.trace("Calling getArmyByName with name: [{}]", createBattleDto.defendingArmyName());
            Army fetchedArmy = armyService.getArmyByName(createBattleDto.defendingArmyName());


            //pathfinder
            paths = pathfinder.findShortestWay(attackingArmy.getCurrentRegion(),fetchedArmy.getCurrentRegion(),executorPlayer,true);

            //checks if the defending army is further than 24 hours
            if(paths.size() > 1){
                log.warn("The defending army is to far, battle is not possible");
                throw BattleServiceException.battleNotAbleDueHours();
            }
            //checks if the defending army is moving
            if(fetchedArmy.getMovements().size() > 0){
                log.warn("Defending Army is in movement, battle is not possible!");
                throw BattleServiceException.defendingArmyIsMoving();
            }
            //checks if the attacking army has another movement
            if(attackingArmy.getMovements().size() > 0){
                log.warn("Attacking army has another active movement, battle is not possible!");
                throw BattleServiceException.attackingArmyHasAnotherMovement();
            }
            defendingArmies.add(fetchedArmy);
            log.debug("Setting defending Faction to: [{}]", fetchedArmy.getFaction().getName());
            defendingFaction = fetchedArmy.getFaction();
        }
        else {
            log.debug("Field battle boolean is set to false - fetching all stationed armies at claim build: [{}]", createBattleDto.ClaimBuildName());
            log.trace("Calling getClaimBuildByName with name: [{}]", createBattleDto.ClaimBuildName());
            ClaimBuild fetchedClaimBuild = claimBuildService.getClaimBuildByName(createBattleDto.ClaimBuildName());
            defendingFaction = fetchedClaimBuild.getOwnedBy();
            List<Army> stationedArmies = fetchedClaimBuild.getStationedArmies();

            //pathfinder
            paths = pathfinder.findShortestWay(attackingArmy.getCurrentRegion(), fetchedClaimBuild.getRegion(),executorPlayer,true);

            if(paths.size() > 1){
                log.warn("Battle is not possible");
                throw BattleServiceException.battleNotAbleDueHours();
            }

            if(stationedArmies.isEmpty())
                log.debug("No armies stationed at claim build with name: [{}]", createBattleDto.ClaimBuildName());
            else {
                log.debug("[{}] armies stationed at claim build with name: [{}]", stationedArmies.size(), createBattleDto.ClaimBuildName());
                defendingArmies.addAll(stationedArmies);
            }
        }

        boolean factionsAreAtWar = warRepository.isFactionAtWarWithOtherFaction(attackingFaction, defendingFaction);
        if(!factionsAreAtWar){
            log.warn("The attacking faction: [{}] and the defending faction: [{}] are not at war with each other", attackingFaction.getName(), defendingFaction.getName());
            throw BattleServiceException.factionsNotAtWar(attackingFaction.getName(), defendingFaction.getName());
        }



        //ToDo: Add proper War object when query exists that fetches a specific wars between two factions



        //ToDo: Add 24h in reach check, if the attacking army is in reach of the defending army / CB


        // Checking if army has enough health for the battle
        if(attackingArmy.getFreeTokens() <=0 ){
            log.warn("The attacking army: [{}] can not start a battle, because they don`t have enough health!");
            throw BattleServiceException.notEnoughHealth();
        }


        //ToDo: Add proper BattleLocation when the 24h check is available

        WarParticipant aggressors = WarParticipant.builder().warParticipant(attackingFaction).joiningDate(LocalDateTime.now()).initialParty(false).build();
        WarParticipant defenders = WarParticipant.builder().warParticipant(defendingFaction).joiningDate(LocalDateTime.now()).initialParty(false).build();

        Set<WarParticipant> aggressorsSet = new HashSet<>();
        Set<WarParticipant> defendersSet = new HashSet<>();

        aggressorsSet.add(aggressors);
        defendersSet.add(defenders);


        War war = warRepository.findWarByAggressorsAndDefenders(aggressorsSet,defendersSet);
        System.out.println(war.getName());


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
                null);

        battleRepository.save(battle);
        log.debug("Trying to persist the battle object");
        //battle = secureSave(battle, battleRepository);

        log.info("Successfully created army [{}]!", battle.getName());
        return battle;
    }
}
