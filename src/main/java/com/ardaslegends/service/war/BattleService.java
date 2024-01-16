package com.ardaslegends.service.war;

import com.ardaslegends.domain.Army;
import com.ardaslegends.domain.ClaimBuild;
import com.ardaslegends.domain.Faction;
import com.ardaslegends.domain.Player;
import com.ardaslegends.domain.war.Battle;
import com.ardaslegends.domain.war.War;
import com.ardaslegends.repository.*;
import com.ardaslegends.service.AbstractService;
import com.ardaslegends.service.ArmyService;
import com.ardaslegends.service.ClaimBuildService;
import com.ardaslegends.service.PlayerService;
import com.ardaslegends.service.dto.war.CreateBattleDto;
import com.ardaslegends.service.exceptions.logic.war.BattleServiceException;
import com.ardaslegends.service.exceptions.logic.army.ArmyServiceException;
import com.ardaslegends.service.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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


        if (!ServiceUtils.boundLordLeaderPermission(executorPlayer, attackingArmy)) {
            log.warn("Player [{}] does not have the permission to start a battle with the army [{}]!", executorPlayer.getIgn(), createBattleDto.attackingArmyName());
            throw ArmyServiceException.noPermissionToPerformThisAction();
        }

        log.debug("Setting attacking faction to: [{}]", attackingArmy.getFaction().getName());
        Faction attackingFaction = attackingArmy.getFaction();
        Faction defendingFaction;


        Set<Army> defendingArmies = new HashSet<>();

        if(createBattleDto.FieldBattle()){
            log.debug("Field battle boolean is set to true - fetching one defending army with name: [{}]", createBattleDto.defendingArmyName());
            log.trace("Calling getArmyByName with name: [{}]", createBattleDto.defendingArmyName());
            Army fetchedArmy = armyService.getArmyByName(createBattleDto.defendingArmyName());
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
        //ToDo: Add proper BattleLocation when the 24h check is available

        log.trace("Assembling Battle Object");
        Battle battle = new Battle(new War(),
                "Battle name",
                Set.of(attackingArmy),
                defendingArmies,
                OffsetDateTime.now(),
                null,
                null,
                null,
                null);

        log.debug("Trying to persist the battle object");
        battle = secureSave(battle, battleRepository);

        log.info("Successfully created army [{}]!", battle.getName());
        return battle;
    }
}
