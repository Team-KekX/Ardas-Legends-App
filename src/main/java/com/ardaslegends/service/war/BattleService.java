package com.ardaslegends.service.war;

import com.ardaslegends.domain.*;
import com.ardaslegends.domain.war.Battle;
import com.ardaslegends.domain.war.BattleLocation;
import com.ardaslegends.domain.war.BattlePhase;
import com.ardaslegends.repository.*;
import com.ardaslegends.repository.war.WarRepository;
import com.ardaslegends.repository.war.QueryWarStatus;
import com.ardaslegends.service.*;
import com.ardaslegends.service.discord.DiscordService;
import com.ardaslegends.service.discord.messages.war.BattleMessages;
import com.ardaslegends.service.dto.war.CreateBattleDto;
import com.ardaslegends.service.exceptions.logic.war.BattleServiceException;
import com.ardaslegends.service.exceptions.logic.army.ArmyServiceException;
import com.ardaslegends.service.time.TimeFreezeService;
import com.ardaslegends.service.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
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
    private final Pathfinder pathfinder;
    private final TimeFreezeService timeFreezeService;
    private final DiscordService discordService;

    @Transactional(readOnly = false)
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

        log.debug("Checking if army is older than 24h");
        if(attackingArmy.isYoungerThan24h()) {
            log.warn("Army [{}] cannot declare battle because it was created less than 24h ago!", attackingArmy.getName());
            throw BattleServiceException.armyYoungerThan24h(attackingArmy.getName());
        }

        log.debug("Checking if player has permission to start the battle");
        if (!ServiceUtils.boundLordLeaderPermission(executorPlayer, attackingArmy)) {
            log.warn("Player [{}] does not have the permission to start a battle with the army [{}]!", executorPlayer.getIgn(), createBattleDto.attackingArmyName());
            throw ArmyServiceException.noPermissionToPerformThisAction();
        }

        log.debug("Checking if army has a player bound to it");
        if(attackingArmy.getBoundTo() == null) {
            log.warn("Cannot declare battle because attacking army [{}] is not bound to a player", attackingArmy.getName());
            throw BattleServiceException.noPlayerBound(attackingArmy.getName());
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

            Region defenderRegion = defendingArmy.getCurrentRegion();
            Region attackerRegion = attackingArmy.getCurrentRegion();

            log.debug("Checking if the defending army is in same region as attacking army for the next 24h");
            log.debug("Attacking army region: [{}]", attackerRegion);
            log.debug("Defending army region: [{}]", defenderRegion);

            if(!defenderRegion.equals(attackerRegion)) {
                log.warn("Attacking army is not in the same region [{}] as defending army [{}]", attackerRegion, defenderRegion);
                throw BattleServiceException.notInSameRegion(attackingArmy, defendingArmy);
            }

            log.debug("Checking if defending army is moving away");
            if(defendingArmy.getActiveMovement().isPresent()) {
                var activeMovement = defendingArmy.getActiveMovement().get();
                log.debug("Defending army [{}] is moving [{}]", defendingArmy, activeMovement);
                log.debug("Next region: [{}] - Duration until next region: [{}]", activeMovement.getNextRegion(), ServiceUtils.formatDuration(activeMovement.getDurationUntilNextRegion()));

                if(activeMovement.getDurationUntilNextRegion().minusHours(24).isNegative()) {
                    log.debug("Next region is reached in <= 24h");
                    log.warn("Cannot declare battle - defending army cannot be reached because it is moving away in [{}]!", ServiceUtils.formatDuration(activeMovement.getDurationUntilNextRegion()));
                    throw BattleServiceException.defendingArmyIsMovingAway(defendingArmy);
                }
                log.debug("Defending army is moving but is still in the region for the next 24h");

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

            log.debug("Checking if claimbuild [{}] is starter hamlet of faction [{}]", attackedClaimbuild.getName(), defendingFaction.getName());
            if(attackedClaimbuild.getName().toLowerCase().endsWith("starter hamlet")) {
                log.warn("Cannot declare battle on clamibuild [{}] because it is the starter hamlet of faction [{}]", attackedClaimbuild.getName(), defendingFaction.getName());
                throw BattleServiceException.cannotAttackStarterHamlet();
            }


            if(!attackingArmy.getCurrentRegion().equals(attackedClaimbuild.getRegion())) {
                log.debug("Army is not in region of claimbuild");
                log.debug("Calling pathfinder to find shortest way from regions [{}] to [{}]", attackingArmy.getCurrentRegion(), battleRegion);
                path = pathfinder.findShortestWay(attackingArmy.getCurrentRegion(), attackedClaimbuild.getRegion(),executorPlayer,true);
                log.debug("Path: [{}], duration: [{} days]", ServiceUtils.buildPathString(path), ServiceUtils.getTotalPathCost(path));

                log.debug("Checking if claimbuild is reachable in 24 hours");
                if(ServiceUtils.getTotalPathCost(path) > 24){
                    log.warn("Cannot declare battle - Claimbuild [{}] is not in 24 hour reach of attacking army [{}]", attackedClaimbuild.getName(), attackingArmy.getName());
                    throw BattleServiceException.battleNotAbleDueHours();
                }
            }
            else
                log.debug("Army [{}] is already in region [{}] of Claimbuild [{}]", attackingArmy.getName(), attackingArmy.getCurrentRegion().getId(), attackedClaimbuild.getName());


            if(stationedArmies.isEmpty())
                log.debug("No armies stationed at claim build with name: [{}]", createBattleDto.claimBuildName());
            else {
                log.debug("[{}] armies stationed at claim build with name: [{}]", stationedArmies.size(), createBattleDto.claimBuildName());
                defendingArmies.addAll(stationedArmies);
            }
        }

        val wars = warRepository.queryWarsBetweenFactions(attackingFaction, defendingFaction, QueryWarStatus.ACTIVE);
        if(wars.isEmpty()){
            log.warn("Cannot create battle: The attacking faction [{}] and the defending faction [{}] are not at war with each other", attackingFaction.getName(), defendingFaction.getName());
            throw BattleServiceException.factionsNotAtWar(attackingFaction.getName(), defendingFaction.getName());
        }

        log.debug("Creating BattleLocation");
        BattleLocation battleLocation = new BattleLocation(battleRegion, createBattleDto.isFieldBattle(), attackedClaimbuild);
        log.debug("Created BattleLocation [{}]", battleLocation);

        log.debug("War information: " + wars);

        log.trace("Assembling Battle Object");
        final Battle createdBattle = new Battle(wars,
                createBattleDto.battleName(),
                Set.of(attackingArmy),
                defendingArmies,
                OffsetDateTime.now(),
                null,
                null,
                null,
                battleLocation);

        log.debug("Calling start24hTimer()");
        val timer = timeFreezeService.start24hTimer(() -> startBattle(createdBattle));
        log.debug("Setting battle.timeFrozenFrom to end date of 24h timer");
        createdBattle.setTimeFrozenFrom(timer.finishesAt());

        Battle battle;
        log.debug("Trying to persist the battle object");
        try {
            battle = secureSave(createdBattle, battleRepository);
        }
        catch (Exception e) {
            log.warn("Cancelling 24h timer since there was an error while persisting battle [{}]", createdBattle);
            timer.future().cancel(true);
            throw e;
        }

        discordService.sendMessageToRpChannel(BattleMessages.declareBattle(battle, discordService));

        log.info("Successfully created battle [{}]!", battle.getName());
        return battle;
    }

    private Battle startBattle(Battle battle) {
        log.debug("Starting battle [{}]", battle);

        Objects.requireNonNull(battle, "battle in startBattle() must not be null!");

        log.debug("Setting BattlePhase to {}", BattlePhase.ONGOING);
        battle.setBattlePhase(BattlePhase.ONGOING);

        log.debug("Calling freezeTime()");
        timeFreezeService.freezeTime();

        //TODO: teleport all aiding armies to battle location

        return battle;
    }
}
