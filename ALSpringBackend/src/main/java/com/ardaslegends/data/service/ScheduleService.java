package com.ardaslegends.data.service;

import com.ardaslegends.data.domain.*;
import com.ardaslegends.data.repository.ArmyRepository;
import com.ardaslegends.data.repository.MovementRepository;
import com.ardaslegends.data.repository.PlayerRepository;
import com.ardaslegends.data.repository.RegionRepository;
import com.ardaslegends.data.service.exceptions.ServiceException;
import com.ardaslegends.data.service.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

@RequiredArgsConstructor

@Slf4j
@Service
public class ScheduleService {

    private final MovementRepository movementRepository;
    private final ArmyRepository armyRepository;
    private final PlayerRepository playerRepository;
    private final MovementService movementService;
    private final ArmyService armyService;
    private final PlayerService playerService;
    private final Clock clock;

    @Scheduled(cron = "0 */15 * ? * *")
    @Transactional(readOnly = false)
    public void handleMovements() {
        LocalDateTime startDateTime = LocalDateTime.now(clock);
        long startNanos = System.nanoTime();
        log.info("Starting scheduled handling of movement - System time: [{}]", startDateTime);

        log.debug("Getting all active movements");
        List<Movement> allActiveMoves = movementRepository.findMovementsByIsCurrentlyActive(true);
        log.debug("Found [{}] active movements - continuing with handling", allActiveMoves.size());

        log.debug("Calling parallelStream handleSingleMovement");
        allActiveMoves.parallelStream().forEach(movement -> handleSingleMovement(movement, startDateTime));

        log.debug("Saving all movements");
        allActiveMoves = movementService.saveMovements(allActiveMoves);

        long endNanos = System.nanoTime();
        BigDecimal neededTime = BigDecimal.valueOf((double) MILLISECONDS.convert(endNanos - startNanos, NANOSECONDS) / 1000);
        log.debug("Needed time in nanos: [{}]", endNanos - startNanos);
        log.info("Finished handling movements. Updated movements: [{}] - finished in {} seconds", allActiveMoves.size(), neededTime.toPlainString());
    }

    @Scheduled(cron = "0 */15 * ? * *")
    @Transactional(readOnly = false)
    public void handleHealings() {
        LocalDateTime startDateTime = LocalDateTime.now(clock);
        long startNanos = System.nanoTime();
        log.info("Starting scheduled handling of healings - System time: [{}]", startDateTime);

        log.debug("Getting all armies that are healing");
        List<Army> healingArmies = armyRepository.findArmyByIsHealingTrue();
        log.debug("Found [{}] healing armies - continuing with handling", healingArmies.size());

        log.debug("Getting all characters that are healing");
        List<Player> healingPlayers = playerRepository.findPlayerByRpCharIsHealingTrue();
        log.debug("Found [{}] healing chars - continuing with handling", healingPlayers.size());

        log.debug("Calling parallelStream handleHealingArmy");
        healingArmies.parallelStream().forEach(army -> handleHealingArmy(army, startDateTime));

        log.debug("Calling parallelStream handleHealingPlayer");
        healingPlayers.parallelStream().forEach(player -> handleHealingPlayer(player, startDateTime));

        log.trace("Persisting data");
        healingArmies = armyService.saveArmies(healingArmies);
        healingPlayers = playerService.savePlayers(healingPlayers);

        long endNanos = System.nanoTime();
        BigDecimal neededTime = BigDecimal.valueOf((double) MILLISECONDS.convert(endNanos - startNanos, NANOSECONDS) / 1000);
        log.debug("Needed time in nanos: [{}]", endNanos - startNanos);
        log.info("Finished handling healing. Updated armies: [{}], updated chars: [{}] - finished in {} seconds", healingArmies.size(), healingPlayers.size(), neededTime.toPlainString());
    }

    private void handleSingleMovement(Movement movement, LocalDateTime now) {
        log.debug("Handling movement [{}]", movement);
        LocalDateTime endTime = movement.getEndTime();

        log.debug("Getting the hours between current time [{}] and end date [{}]", now, endTime);

        /*
        Get difference between now and endTime in hours
        This shows us how many hours are left in the movement
         */

        //we have to add 1 here because we want to know how many hours have passed
        //HOURS.between returns 0 if you have 00:59:59 minutes/seconds
        int hoursLeft = (int) HOURS.between(now, endTime) + 1;
        if(now.isAfter(endTime)) {
            log.debug("Current date is after end date, setting hours left to 0");
            hoursLeft = 0;
        }
        log.debug("Hours left: [{}]", hoursLeft);

        /*
        We get the hours moved since last time by subtracting the current hours left
        with the last hours left (the value that was last stored in the movement)
         */

        int hoursMovedSinceLastTime = movement.getHoursUntilComplete() - hoursLeft;
        log.debug("Hours moved since last time: [{}]", hoursMovedSinceLastTime);

        //If we didn't move an hour since last time, exit function

        if(hoursMovedSinceLastTime == 0) {
            log.debug("No hour has passed for this movement - exiting function");
            return;
        }


        log.debug("Updating movement data");

        //Get the amount of hours a movement has been going on by getting the last hoursMoved and adding hoursMovedSinceLastTime
        int newHoursMoved = movement.getHoursMoved() + hoursMovedSinceLastTime;
        log.trace("Incrementing hoursMoved from [{}] to [{}]", movement.getHoursMoved(), newHoursMoved);
        movement.setHoursMoved(newHoursMoved);

        //Set the old hoursUntilComplete to the newly calculated value
        log.trace("Setting hoursUntilComplete from [{}] to [{}]", movement.getHoursUntilComplete(), hoursLeft);
        movement.setHoursUntilComplete(hoursLeft);

        /*
        Now we have to get the current region of the army/character. We have to check if the movement is a character movement.
        If yes, get the region of the player's rpchar
        If not, get the region of the army
         */

        Region currentRegion = null;
        if(movement.getIsCharMovement())
            currentRegion = movement.getPlayer().getRpChar().getCurrentRegion();
        else
            currentRegion = movement.getArmy().getCurrentRegion();

        /*
        Now we calculate the hours until the next region by subtracting hoursMovedSinceLast time from the old value.
        However, the hoursUntilNextRegion could be negative when the bot hasn't checked movements for a while
        For this reason, we enter a while loop that constantly updates the current region of the army/character
        We keep doing this until we have arrived in the region that the army/char should be at
         */

        int hoursUntilNextRegion = movement.getHoursUntilNextRegion() - hoursMovedSinceLastTime;
        log.trace("Hours until next region: [{}] - [{}] = [{}]", movement.getHoursUntilNextRegion(), hoursMovedSinceLastTime, hoursUntilNextRegion);

        log.trace("Entering while loop as long as hoursUntilNextRegion is negative");
        List<PathElement> path = movement.getPath();
        Region finalCurrentRegion = currentRegion;
        int currentRegionIndex = path.indexOf(path.stream().filter(pe -> pe.hasRegion(finalCurrentRegion)).findFirst().get());
        PathElement nextPathRegion = null;
        while(hoursUntilNextRegion <= 0) {
            log.trace("Hours until next region: [{}]", hoursUntilNextRegion);

            /*
            Fetch the next region, but only if nextRegion is null
            This happens only on the first iteration, because on the other ones we already have the instance of the next region
            I did this so we don't fetch the same region twice
             */

            if(nextPathRegion == null) {
                log.trace("Setting nextRegion");
                nextPathRegion = path.get(currentRegionIndex + 1);
                log.trace("Set next region to [{}]", nextPathRegion);
            }

            /*
            If movement is a char movement, set the char's currentRegion to the next region
            If it's an army movement, set army's region to nextRegion
            If the army is bound to a character, set also the character's currentRegion
             */

            if(movement.getIsCharMovement()) {
                log.trace("Movement is char movement, setting current region to [{}]", nextPathRegion);
                movement.getPlayer().getRpChar().setCurrentRegion(nextPathRegion.getRegion());
            }
            else {
                log.trace("Movement is army movement, setting current region to [{}]", nextPathRegion);
                movement.getArmy().setCurrentRegion(nextPathRegion.getRegion());
                if(movement.getArmy().getBoundTo() != null) {
                    log.trace("Army is bound to a character, setting the character's region to [{}]", nextPathRegion);
                    movement.getArmy().getBoundTo().getRpChar().setCurrentRegion(nextPathRegion.getRegion());
                }
            }

            /*
            If the next region is the destination, set isActive to false and also set the hoursUntilNextRegion to 0
            so we break out of the loop
             */

            log.trace("Checking if next region is destination");
            if(path.get(path.size()-1).equals(nextPathRegion)) {
                if(movement.getIsCharMovement())
                    log.info("Movement of character [{}] with path [{}] reached its destination, setting isActive to false"
                            , movement.getPlayer().getRpChar(), ServiceUtils.buildPathString(path));
                else
                    log.info("Movement of army [{}] with path [{}] reached its destination, setting isActive to false"
                            , movement.getArmy(), ServiceUtils.buildPathString(path));
                log.trace("Next region is destination");
                log.trace("Setting hoursUntilNextRegion to 0");
                log.trace("Setting movement isActive = false");
                movement.setIsCurrentlyActive(false);
                break;
            }
            else {

                /*
                If the next region was not the last, fetch the region after that one and calculate the new
                hoursUntilNextRegion
                 */

                log.trace("Now current region is not destination - fetching next region and calculating new hoursUntilNextRegion");

                //Incrementing the currentRegionIndex because we iterated to the next region
                currentRegionIndex++;
                //Get the Id of the new next region
                nextPathRegion = path.get(currentRegionIndex + 1);
                log.trace("Set region [{}] as next path region", nextPathRegion);

                log.trace("Calculating new hoursUntilNextRegion");

                hoursUntilNextRegion = hoursUntilNextRegion + nextPathRegion.getActualCost();
                movement.setHoursUntilNextRegion(hoursUntilNextRegion);
                log.trace("New hoursUntilNextRegion: [{}]", hoursUntilNextRegion);
            }

        }
        log.trace("Exited while loop");
    }

    private void handleHealingArmy(Army army, LocalDateTime now) {
        log.debug("Handling healing army [{}]", army);
        LocalDateTime endTime = army.getHealEnd();

        log.debug("Getting the hours between end date [{}] and current time [{}]", endTime, now);

        /*
        Get difference between now and endTime in hours
        This shows us how many hours are left of healing process
         */

        //we have to add 1 here because we want to know how many hours have passed
        //HOURS.between returns 0 if you have 00:59:59 minutes/seconds
        int hoursLeft = (int) HOURS.between(now, endTime) + 1;
        if(now.isAfter(endTime)) {
            log.debug("Current date is after end date, setting hours left to 0");
            hoursLeft = 0;
        }
        log.debug("Hours left: [{}]", hoursLeft);


        /*
        We get the hours healed since last time by subtracting the current hours left
        with the last hours left (the value that was last stored in army)
         */

        int hoursHealedSinceLastTime = army.getHoursLeftHealing() - hoursLeft;
        log.debug("Hours healed since last time: [{}]", hoursHealedSinceLastTime);

        //If we didn't move an hour since last time, exit function

        if(hoursHealedSinceLastTime == 0) {
            log.debug("No hour has passed for this healing - exiting function");
            return;
        }

        /*
        Sets the divisor to 24
        If stationed at a stronghold, set it to half the amount (x2 heal speed)
        This is explained in the later comment
         */

        int divisor = 24;
        log.trace("Setting divisor to [{}]", divisor);
        if(army.getStationedAt().getType().equals(ClaimBuildType.STRONGHOLD)) {
            divisor /= 2;
            log.trace("Army is stationed at Stronghold, setting divisor to [{}]", divisor);
        }

        //The amount of hours the army has healed after the last replenish (excluding hoursHealedSinceLastTime)
        int hoursSinceLastReplenishWithoutCurrent = army.getHoursHealed() % divisor;
        //The amount of hours the army has healed after the last replenish (including hoursHealedSinceLastTime)
        int hoursHealedSinceLastReplenish = hoursSinceLastReplenishWithoutCurrent + hoursHealedSinceLastTime;

        /*
        We enter a loop that lasts as long as we hoursHealedSinceLastReplenish > divisor
        We replenish the troops in every iteration and subtract hoursHealedSinceLastReplenish - divisor
        For example 48 - 24 = 24 - 24 = 0 -> replenish alive units 2 times
        When army stationed claimbuild is a STRONGHOLD, then subtract by 12 (replenish every 12h)
         */

        log.trace("Entering loop");
        while(hoursHealedSinceLastReplenish >= divisor) {
            log.trace("hoursHealedSinceLastReplenish: [{}]", hoursHealedSinceLastReplenish);
            log.trace("Subtracting divisor [{}] from hoursHealedSinceLastReplenish [{}]", divisor, hoursHealedSinceLastReplenish);
            hoursHealedSinceLastReplenish -= divisor;
            log.trace("New hoursHealedSinceLastReplenish: [{}]", hoursHealedSinceLastReplenish);

            //only get units that are not fully replenished sorted by token cost ascending
            List<Unit> units = army.getUnits().stream()
                    .filter(unit -> unit.getAmountAlive() < unit.getCount())
                    .sorted(Comparator.comparing(Unit::getCost))
                    .toList();

            double replenishTokens = 6.0;
            int currentUnitIndex = 0;
            Unit currentUnit = units.get(currentUnitIndex);

            while(replenishTokens > 0) {
                log.trace("Starting to replenish unit: [{}]", currentUnit);
                log.trace("Unit [{}] has currently {}/{} alive units", currentUnit.getUnitType(), currentUnit.getAmountAlive(), currentUnit.getCount());
                log.trace("Replenish tokens left: [{}]", replenishTokens);

                int deadUnits = currentUnit.getCount() - currentUnit.getAmountAlive();
                log.trace("Dead units: [{}]", deadUnits);
                double tokensToHeal = deadUnits * currentUnit.getCost();
                log.trace("Tokens to heal: [{}]", tokensToHeal);
                if(tokensToHeal >= replenishTokens) {
                    log.trace("More tokens to heal than replenish tokens available - using all replenish tokens on unit [{}]", currentUnit);
                    int canHealUnits = (int) (replenishTokens / currentUnit.getCost());
                    log.trace("Unit has cost [{}] - can heal [{}] with [{}] refresh tokens", currentUnit.getCost(), canHealUnits, replenishTokens);
                    currentUnit.setAmountAlive(currentUnit.getAmountAlive() + canHealUnits);
                    log.trace("Unit now has {}/{} units", currentUnit.getAmountAlive(), currentUnit.getCount());
                    replenishTokens = 0;
                    log.trace("Set replenish tokens to [{}]", replenishTokens);
                }
                else {
                    log.trace("Less units to heal than replenish tokens available - healing unit [{}] to full", currentUnit.getUnitType());
                    currentUnit.setAmountAlive(currentUnit.getCount());
                    log.trace("Unit now has {}/{} units", currentUnit.getAmountAlive(), currentUnit.getCount());
                    replenishTokens -= tokensToHeal;
                    log.trace("Set replenish tokens to [{}]", replenishTokens);

                    log.trace("Setting the next unit");
                    if(currentUnitIndex == units.size()-1) {
                        log.trace("current unit was last in list, not setting new current unit");
                        log.info("Army [{}] has finished its healing process!", army.getName());
                        army.resetHealingStats();
                        replenishTokens = 0;
                    }
                    else {
                        currentUnitIndex++;
                        currentUnit = units.get(currentUnitIndex);
                        log.trace("Set new current unit to [{}]", currentUnit);
                    }
                }
            }
            log.trace("Exited loop");

            if(!army.allUnitsAlive()) {
                log.trace("Army not fully healed - updating parameters");
                log.trace("Setting the new amount of hours healed to [{}]", army.getHoursHealed() + hoursHealedSinceLastTime);
                army.setHoursHealed(army.getHoursHealed() + hoursHealedSinceLastTime);
                log.trace("Setting new hours left to [{}]", hoursLeft);
                army.setHoursLeftHealing(hoursLeft);
            }

            log.debug("Finished army [{}]", army);
        }
    }

    private void handleHealingPlayer(Player player, LocalDateTime now) {
        log.debug("Handling healing player [{}]", player);
        RPChar rpChar = player.getRpChar();
        log.trace("Got player's rpchar [{}]", rpChar);
        LocalDateTime endTime = rpChar.getHealEnds();

        log.debug("Getting the hours between end date [{}] and current time [{}]", endTime, now);

        /*
        Get difference between now and endTime in hours
        This shows us how many hours are left of healing process
         */

        //we have to add 1 here because we want to know how many hours have passed
        //HOURS.between returns 0 if you have 00:59:59 minutes/seconds
        int hoursLeft = (int) HOURS.between(now, endTime) + 1;
        if(now.isAfter(endTime)) {
            log.debug("Current date is after end date, setting hours left to 0");
            hoursLeft = 0;
        }
        log.debug("Hours left: [{}]", hoursLeft);

        if(hoursLeft <= 0) {
            log.info("Character [{}] of player [{}] finished healing - setting isInjured and isHealing to false", rpChar, player);
            rpChar.setInjured(false);
            rpChar.setIsHealing(false);
            log.trace("Exiting function");
            return;
        }
        log.debug("Character [{}] of player [{}] still has [{}] hours left for healing and therefore has not finished yet!", rpChar, player, hoursLeft);

    }

}
