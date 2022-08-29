package com.ardaslegends.data.service;

import com.ardaslegends.data.domain.Movement;
import com.ardaslegends.data.domain.Path;
import com.ardaslegends.data.domain.Region;
import com.ardaslegends.data.repository.MovementRepository;
import com.ardaslegends.data.repository.RegionRepository;
import com.ardaslegends.data.service.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.*;

@RequiredArgsConstructor

@Slf4j
@Service
public class ScheduleService {

    private final MovementRepository movementRepository;
    private final MovementService movementService;
    private final RegionRepository regionRepository;

    @Scheduled(cron = "0 */15 * ? * *")
    public void handleMovements() {
        LocalDateTime startTime = LocalDateTime.now();
        log.info("Starting scheduled handling of movement - System time: [{}]", startTime);

        log.debug("Getting all active movements");
        List<Movement> allActiveMoves = movementRepository.findMovementsByIsCurrentlyActive(true);
        log.debug("Found [{}] active movements - continuing with handling", allActiveMoves.size());

        log.debug("Calling parallelStream handleSingleMovement");
        allActiveMoves.parallelStream().forEach(movement -> handleSingleMovement(movement, startTime));

        LocalDateTime endTime = LocalDateTime.now();
        double neededTime = ((double) (MILLIS.between(startTime, endTime)) / 1000);
        log.info("Finished handling army movements. Updated armies: [{}] - finished in {} seconds", allActiveMoves.size(), neededTime);
    }

    private void handleSingleMovement(Movement movement, LocalDateTime now) {
        log.debug("Handling movement [{}]", movement);
        LocalDateTime endTime = movement.getEndTime();

        log.debug("Getting the hours between end date [{}] and current time [{}]", endTime, now);

        /*
        Get difference between now and endTime in hours
        This shows us how many hours are left in the movement
         */

        int hoursLeft = (int) HOURS.between(now, endTime);
        log.trace("Hours left: [{}]", hoursLeft);

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
        List<String> path = movement.getPath().getPath();
        Region nextRegion = null;
        while(hoursUntilNextRegion < 0) {
            log.trace("Hours until next region: [{}]", hoursUntilNextRegion);

            //Getting the ID of the next region in the path list
            int currentRegionIndex = path.indexOf(currentRegion.getId());
            String nextRegionId = path.get(currentRegionIndex + 1);
            log.trace("Id of next region: [{}]", nextRegionId);

            /*
            Fetch the next region, but only if nextRegion is null
            This happens only on the first iteration, because on the other ones we already have the instance of the next region
            I did this so we don't fetch the same region twice
             */

            if(nextRegion == null) {
                log.trace("Fetching region with id [{}]", nextRegionId);
                Optional<Region> fetchedRegion = regionRepository.findById(nextRegionId);
                if(fetchedRegion.isEmpty()) {
                    //This should never be reached because movements are only created with valid regions
                    log.error("FATAL WHILE HANDLING MOVEMENT: Could not find a region with id [{}]!", nextRegionId);
                    throw ServiceException.regionDoesNotExist(nextRegionId);
                }
                nextRegion = fetchedRegion.get();
                log.trace("Found region [{}]", nextRegion);
            }

            /*
            If movement is a char movement, set the char's currentRegion to the next region
            If it's an army movement, set army's region to nextRegion
            If the army is bound to a character, set also the character's currentRegion
             */

            if(movement.getIsCharMovement()) {
                log.trace("Movement is char movement, setting current region to [{}]", nextRegion);
                movement.getPlayer().getRpChar().setCurrentRegion(nextRegion);
            }
            else {
                log.trace("Movement is army movement, setting current region to [{}]", nextRegion);
                movement.getArmy().setCurrentRegion(nextRegion);
                if(movement.getArmy().getBoundTo() != null) {
                    log.trace("Army is bound to a character, setting the character's region to [{}]", nextRegion);
                    movement.getArmy().getBoundTo().getRpChar().setCurrentRegion(nextRegion);
                }
            }

            /*
            If the next region is the destination, set isActive to false and also set the hoursUntilNextRegion to 0
            so we break out of the loop
             */

            log.trace("Checking if next region is destination");
            if(movement.getPath().getDestination().equals(nextRegionId)) {
                if(movement.getIsCharMovement())
                    log.info("Movement of character [{}] with path [{}] reached its destination, setting isActive to false"
                            , movement.getPlayer().getRpChar(), String.join(" -> ", path));
                else
                    log.info("Movement of army [{}] with path [{}] reached its destination, setting isActive to false"
                            , movement.getArmy(), String.join(" -> ", path));
                log.trace("Next region is destination");
                log.trace("Setting hoursUntilNextRegion to 0");
                hoursUntilNextRegion = 0;
                log.trace("Setting movement isActive = false");
                movement.setIsCurrentlyActive(false);
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
                nextRegionId = path.get(currentRegionIndex + 1);

                log.trace("Fetching next region with id [{}]", nextRegionId);
                Optional<Region> fetchedRegion = regionRepository.findById(nextRegionId);
                if(fetchedRegion.isEmpty()) {
                    //This should never be reached because movements are only created with valid regions
                    log.error("FATAL WHILE HANDLING MOVEMENT: Could not find a region with id [{}]!", nextRegionId);
                    throw ServiceException.regionDoesNotExist(nextRegionId);
                }
                nextRegion = fetchedRegion.get();
                log.trace("Found region [{}]", nextRegion);

                log.trace("Calculating new hoursUntilNextRegion");
                hoursUntilNextRegion = hoursUntilNextRegion + nextRegion.getCostInHours();
            }

        }
        log.trace("Exited while loop");

        log.debug("Saving movement - current region is now ");
        movementService.saveMovement(movement);
    }
}
