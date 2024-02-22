package com.ardaslegends.service.war;

import com.ardaslegends.domain.*;
import com.ardaslegends.domain.war.battle.Battle;
import com.ardaslegends.domain.war.battle.BattleLocation;
import com.ardaslegends.domain.war.battle.BattleResult;
import com.ardaslegends.domain.war.battle.UnitCasualty;
import com.ardaslegends.repository.war.WarRepository;
import com.ardaslegends.repository.war.QueryWarStatus;
import com.ardaslegends.repository.war.battle.BattleRepository;
import com.ardaslegends.service.*;
import com.ardaslegends.service.dto.war.ConcludeBattleDto;
import com.ardaslegends.service.dto.war.SurvivingUnitsDto;
import com.ardaslegends.service.dto.war.battle.CreateBattleDto;
import com.ardaslegends.service.exceptions.logic.war.BattleServiceException;
import com.ardaslegends.service.exceptions.logic.army.ArmyServiceException;
import com.ardaslegends.service.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
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
    private final FactionService factionService;

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

        //ToDo: Add proper War object when query exists that fetches a specific wars between two factions
        //ToDo: Add 24h in reach check, if the attacking army is in reach of the defending army / CB
        //ToDo: Add proper BattleLocation when the 24h check is available

        log.debug("Creating BattleLocation");
        BattleLocation battleLocation = new BattleLocation(battleRegion, createBattleDto.isFieldBattle(), attackedClaimbuild);
        log.debug("Created BattleLocation [{}]", battleLocation);

        log.debug("War inforamtion: " + wars);

        log.trace("Assembling Battle Object");
        Battle battle = new Battle(wars,
                createBattleDto.battleName(),
                Set.of(attackingArmy),
                defendingArmies,
                OffsetDateTime.now(),
                null,
                null,
                null,
                battleLocation);

        log.debug("Trying to persist the battle object");
        battle = secureSave(battle, battleRepository);

        log.info("Successfully created battle [{}]!", battle.getName());
        return battle;
    }

    @Transactional(readOnly = false)
    public Battle concludeBattle(ConcludeBattleDto concludeBattleDto) {
        log.debug("Concluding battle with data {}", concludeBattleDto);
        Objects.requireNonNull(concludeBattleDto, "ConcludeBattleDto must not be null!");
        ServiceUtils.checkAllNulls(concludeBattleDto);
        Arrays.stream(concludeBattleDto.playersKilled())
                .forEach(discordIdDto -> {
                    ServiceUtils.checkAllNulls(discordIdDto);
                    ServiceUtils.checkAllBlanks(discordIdDto);
                });

        Arrays.stream(concludeBattleDto.survivingUnits())
                .forEach(unitsDto -> {
                    ServiceUtils.checkAllNulls(unitsDto);
                    ServiceUtils.checkAllBlanks(unitsDto);
                });

        log.debug("Finding battle with id [{}]", concludeBattleDto.battleId());
        val battle = battleRepository.findByIdOrElseThrow(concludeBattleDto.battleId());

        if(battle.getBattleResult() != null) {
            log.warn("Battle [{}] already has a result [{}]", battle.getId(), battle.getBattleResult());
            throw BattleServiceException.battleAlreadyConcluded();
        }

        log.debug("Trying to find winner faction [{}]", concludeBattleDto.winnerFaction());
        val winnerFaction = factionService.getFactionByName(concludeBattleDto.winnerFaction());
        log.debug("Found faction [{}]", winnerFaction);

        log.debug("Looking if winner faction [{}] is on attacking side", winnerFaction.getName());
        val isWinnerOnAttackerSide = battle.getAttackingArmies().stream()
                .map(Army::getFaction)
                .anyMatch(faction -> faction.equals(winnerFaction));
        log.debug("Winner is on attacking side: [{}]", isWinnerOnAttackerSide);

        if(!isWinnerOnAttackerSide) {
            log.debug("Looking if winner faction [{}] is on defending side", winnerFaction.getName());
            val isWinnerOnDefendingSide = battle.getDefendingArmies().stream()
                    .map(Army::getFaction)
                    .anyMatch(faction -> faction.equals(winnerFaction));
            log.debug("Winner is on defending side: [{}]", isWinnerOnDefendingSide);

            if(!isWinnerOnDefendingSide) {
                log.warn("Faction [{}] is not part of battle [{}]", winnerFaction.getName(), battle.getId());
                throw BattleServiceException.factionNotPartOfBattle(winnerFaction.getName(), battle.getId());
            }
        }

        log.debug("Getting the initial faction of the winner side");
        val winnerInitialFaction = isWinnerOnAttackerSide ? battle.getInitialAttacker().getFaction() : battle.getInitialDefender();
        log.debug("Initial faction of winner side is [{}]", winnerInitialFaction.getName());

        //TODO calculate unit casualties
        //TODO calculate player casualties

        //TODO create and set BattleResult

        //TODO set BattleStatus to COMPLETE
        //TODO unfreeze time

        return battle;
    }

    /**
     * Updates the units of the army specified in the SurvivingUnitsDto to only include the surviving
     * units also specified in the dto
     * @param dto Dto which contains the army and its surviving units
     * @param battle The battle which the casualties happened in
     * @return A UnitCasualty object of the units that died in the battle. Returns null if no casualties happened
     */
    private Set<UnitCasualty> updateSurvivingUnits(SurvivingUnitsDto dto, Battle battle) {
        log.debug("Updating the surviving units of army [{}]", dto.army());

        log.debug("Finding army [{}] in battle [{}]", dto.army(), battle);
        val foundArmy = battle.getPartakingArmies().stream()
                .filter(army -> army.getName().equals(dto.army()))
                .findFirst();

        if(foundArmy.isEmpty()) {
            log.warn("No army with name [{}] found in battle [{}]!", dto.army(), battle);
            throw BattleServiceException.armyNotPartOfBattle(dto.army(), battle.getId());
        }

        val army = foundArmy.get();
        log.debug("Found army [{}]", army);
        val units = army.getUnits();

        val unitCasualties = new HashSet<UnitCasualty>();

        log.debug("Starting to loop through units");
        Arrays.stream(dto.survivingUnits()).forEach(unitDto -> {
            log.debug("Starting calculation for dto [{}]", unitDto);
            log.debug("Checking if unit [{}] is present in army [{}]", unitDto.unitTypeName(), army.getName());
            val foundUnit = units.stream().filter(unit -> unit.getUnitType().getUnitName().equals(unitDto.unitTypeName())).findFirst();

            if(foundUnit.isEmpty()) {
                log.warn("Army [{}] does not contain unit [{}]!", army.getName(), unitDto.unitTypeName());
                throw BattleServiceException.armyDoesNotContainUnit(army.getName(), unitDto.unitTypeName());
            }

            log.debug("Unit [{}] is present in army [{}]", unitDto.unitTypeName(), army.getName());
            val unit = foundUnit.get();
            val oldAmount = unit.getAmountAlive();
            log.debug("Old amount alive of [{}]: [{}]", unitDto.unitTypeName(), oldAmount);
            val newAmount = unitDto.amount();
            log.debug("New amount alive of [{}]: [{}]", unitDto.unitTypeName(), newAmount);

            if(newAmount < 0) {
                log.warn("New amount [{}] is <0", newAmount);
                throw BattleServiceException.newUnitAmountNegative(unitDto.unitTypeName(), newAmount);
            }

            if(newAmount > oldAmount) {
                log.warn("New amount alive [{}] is larger than old amount alive [{}] for unit [{}]!", newAmount, oldAmount, unitDto.unitTypeName());
                throw BattleServiceException.newUnitAmountTooLarge(army.getName(), oldAmount, unitDto.unitTypeName());
            }

            val unitsLost = (long) (oldAmount - newAmount);
            log.debug("Amount of [{}] lost in battle: [{}]", unitDto.unitTypeName(), unitsLost);

            if(unitsLost > 0) {
                log.debug("Amount lost is >0 -> creating UnitCasualty");
                val unitCasualty = new UnitCasualty(unit, unitsLost);

                log.debug("Setting amountAlive of unit [{}] from [{}] to [{}]", unitDto.unitTypeName(), oldAmount, newAmount);
                unit.setAmountAlive(newAmount);

                unitCasualties.add(unitCasualty);
                log.debug("Added UnitCasualty {}", unitCasualty);
            }
        });

        log.debug("Finished updating units");
        log.debug("Created [{}] UnitCasualties", unitCasualties.size());

        return Collections.unmodifiableSet(unitCasualties);
    }
}
