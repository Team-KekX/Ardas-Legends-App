package com.ardaslegends.service;

import com.ardaslegends.domain.*;
import com.ardaslegends.domain.war.War;
import com.ardaslegends.repository.RegionRepository;
import com.ardaslegends.repository.WarRepository;
import com.ardaslegends.service.exceptions.PathfinderServiceException;
import com.ardaslegends.service.exceptions.ServiceException;
import com.ardaslegends.service.utils.ServiceUtils;
import com.ardaslegends.service.war.WarService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Builder

@Service
@Slf4j
public class Pathfinder {
    private final RegionRepository regionRepository;
    private final WarService warService;

    public static final int UNREACHABLE_COST = 999999;

    /**
     * Find the shortest path
     * Return an object which contains the path and sum of weights
     */
    public List<PathElement> findShortestWay(Region startRegion, Region endRegion, Player player, boolean isCharacterMove) {
        log.info("Finding Path for player '{}': from '{}' to '{}' isRpChar: {}", player.getIgn(), startRegion.getId(), endRegion.getId(), isCharacterMove);
        log.debug("Movement is army move: {}", (!isCharacterMove));

        if (startRegion.equals(endRegion)) {
            log.warn("StartRegion [{}] is the same as endRegion [{}]", startRegion, endRegion);
            throw PathfinderServiceException.alreadyInRegion();
        }

        var wars = warService.getWarsOfFaction(player.getFaction());

        log.debug("Initializing data for Pathfinding...");
        //smallest weights between startRegion and all the other nodes
        Map<Region, Integer> smallestWeights = new HashMap<>();
        //for convenience, mark distance from startRegion to itself as 0
        smallestWeights.put(startRegion, 0);

        //implicit graph of all nodes and previous node in ideal paths
        Map<Region, Region> previousRegions = new HashMap<>();

        //use queue for breadth first search
        //for convenience, we'll use an array, but linked list would be preferred
        ArrayList<Region> regionsToVisit = new ArrayList<>();

        //record visited nodes with a set. The string is the toString of a HexID
        Set<String> visitedRegions = new HashSet<>();
        visitedRegions.add(startRegion.getId());

        Region currentRegion = startRegion;
        log.debug("Starting the loop...");
        while (currentRegion != endRegion) {
            //get the shortest path so far from start to currentNode
            final int dist = smallestWeights.get(currentRegion);

            log.debug("Checking region {}", currentRegion.getId());

            //iterate over current child's nodes and process
            Set<Region> neighbourRegions = currentRegion.getNeighboringRegions();
            log.trace("Iterating over Region {}'s neighbours", currentRegion.getId());
            for (Region neighbourRegion : neighbourRegions) {

                log.trace("Checking if neighbor has been visited yet...");
                if (!visitedRegions.contains(neighbourRegion.getId()) && !regionsToVisit.contains(neighbourRegion)) {
                    log.debug("Region {} hasn't been visited yet - adding to queue", neighbourRegion.getId());
                    regionsToVisit.add(neighbourRegion);
                }

                log.debug("Calculating the cost for Region {}", neighbourRegion.getId());

                int currentRegionCost = 0;

                currentRegionCost += calculateCostDependingOnRegionType(currentRegion, dist, neighbourRegion, currentRegionCost);

                if(!isCharacterMove)
                    currentRegionCost += applyArmyMovementRules(player, neighbourRegion, currentRegionCost, wars);


                log.debug("Calculated Cost for this Region -> {}", currentRegionCost);

                log.debug("Checking if there is a shorter path already");
                if (previousRegions.containsKey(neighbourRegion)) {
                    log.trace("Found another path - checking which is shorter...");
                    final int lowestPreviousCost = smallestWeights.get(neighbourRegion);

                    if (currentRegionCost < lowestPreviousCost) {
                        log.debug("Current path is shorter, setting as new shortest Path (old: {} - new: {})", lowestPreviousCost, currentRegionCost);
                        previousRegions.put(neighbourRegion, currentRegion);
                        smallestWeights.put(neighbourRegion, currentRegionCost);
                    }
                } else {
                    log.debug("No path found - setting as new shortest Path (cost: {})", currentRegionCost);
                    previousRegions.put(neighbourRegion, currentRegion);
                    smallestWeights.put(neighbourRegion, currentRegionCost);
                }
            }

            log.trace("Setting current node as visited");
            visitedRegions.add(currentRegion.getId());

            //exit if done
            //pull the next node to visit, if any
            log.trace("Getting the next node to visit and removing current one from queue");
            regionsToVisit.remove(currentRegion);
            currentRegion = regionsToVisit.stream()
                    .min(Comparator.comparingInt(smallestWeights::get))
                    .orElseThrow(() -> {
                        log.warn("No Region to visit!");
                        throw ServiceException.pathfinderNoRegions(startRegion, endRegion);
                    });
        }

        var path = buildShortestPath(startRegion, endRegion, isCharacterMove, smallestWeights, previousRegions);
        log.trace("Final path is now: {}", ServiceUtils.buildPathString(path));

        int summedCost = ServiceUtils.getTotalPathCost(path);
        log.debug("Final cost is {}", summedCost);

        log.info("Finished finding shortest path from {} to {}", startRegion.getId(), endRegion.getId());
        log.info("Cost: {}h - Path: {}", summedCost, path.stream().map(PathElement::getRegion).map(Region::getId).collect(Collectors.joining(" -> ")));

        return path;
    }

    private ArrayList<PathElement> buildShortestPath(Region startRegion, Region endRegion, boolean isCharacterMove, Map<Region, Integer> smallestWeights, Map<Region, Region> previousRegions) {
        Region currentRegion;
        ArrayList<PathElement> path = new ArrayList<>();

        log.trace("Building the Path");
        currentRegion = endRegion;
        while (!Objects.equals(currentRegion.getId(), startRegion.getId())) {
            log.trace("Getting the cost of the path");
            if (smallestWeights.get(currentRegion) >= UNREACHABLE_COST) {
                log.warn("Could not find a valid path from region [{}] to region [{}]", startRegion, endRegion);
                throw PathfinderServiceException.noPathFound(startRegion.getId(), endRegion.getId());
            }

            int baseCost = currentRegion.getCost();
            int actualCost = baseCost;

            if (isCharacterMove) {
                log.trace("Halving the cost since the movement is a RpChar move");
                actualCost = (int) (baseCost / 2.0);
            }

            PathElement pathElement = new PathElement(actualCost, baseCost, currentRegion);
            currentRegion = previousRegions.get(currentRegion);
            path.add(pathElement);
        }
        PathElement pathElement = new PathElement(0, startRegion.getCost(), startRegion);
        path.add(pathElement);

        log.trace("Reversing the path so it starts with the start region");
        Collections.reverse(path);

        return path;
    }

    private int calculateCostDependingOnRegionType(Region currentNode, int dist, Region neighbourRegion, int currentRegionCost) {
        log.debug("Checking if dis-/embarking...");

        currentRegionCost += dist + neighbourRegion.getCost();

        // Check if we are embarking or disembarking
        if (currentNode.getRegionType() != RegionType.SEA && neighbourRegion.getRegionType() == RegionType.SEA) { //Checks if the current Region is land and has a Sea Region as neighbor
            log.trace("Current region is Land and neighbors a Sea region - continue check for harbour");

            log.trace("Checking Region's claimbuilds for harbour");
            boolean hasHarbor = currentNode.getClaimBuilds().stream()
                    .anyMatch(claimBuild -> claimBuild.getSpecialBuildings().contains(SpecialBuilding.HARBOUR));

            if(hasHarbor) {
                log.debug("Found Harbour in current Region ({}) - can embark", neighbourRegion.getId());
                currentRegionCost += 1;
            }
            else {
                log.debug("No Harbour found in current Region ({}) - cannot embark", neighbourRegion.getId());
                currentRegionCost = UNREACHABLE_COST;
            }

        }
        else if (currentNode.getRegionType() == RegionType.SEA && neighbourRegion.getRegionType() != RegionType.SEA) { //Checks if current region is Sea and neighbor is land
            log.debug("Current region is Sea region and neighbors land region - can disembark");
            currentRegionCost += 1;
        }

        return currentRegionCost;
    }

    private int applyArmyMovementRules(Player player, Region neighbourRegion, int currentRegionCost, Set<War> wars) {
        log.debug("Checking if army can move through Region {}", neighbourRegion.getId());

        boolean isClaimedByPlayersFaction = neighbourRegion.getClaimedBy().contains(player.getFaction());
        log.trace("Region {} is claimed by Faction: {}", neighbourRegion.getId(), isClaimedByPlayersFaction);

        boolean isClaimedByAlly = player.getFaction().getAllies().stream()
                .anyMatch(faction -> neighbourRegion.getClaimedBy().contains(faction));
        log.trace("Region {} is claimed by ally: {}", neighbourRegion.getId(), isClaimedByAlly);

        boolean isUnclaimed = neighbourRegion.getClaimedBy().isEmpty();
        log.trace("Region {} is unclaimed: {}", neighbourRegion.getId(), isUnclaimed);

        boolean isAtWarWithFactionInRegion = wars.parallelStream()
                .map(war -> war.getEnemies(player.getFaction()))
                .flatMap(Collection::stream)
                .distinct()
                .map(participant -> participant.getWarParticipant())
                .anyMatch(enemyFaction -> neighbourRegion.getClaimedBy().contains(enemyFaction));
        log.trace("Region {} isAtWarWithFactionInRegion: {}", neighbourRegion.getId(), isAtWarWithFactionInRegion);

        if (!isClaimedByAlly && !isClaimedByPlayersFaction && !isUnclaimed && !isAtWarWithFactionInRegion) {
            log.debug("Army cannot move through Region {} - it is not unclaimed or claimed by the player's faction or its allies or at war with another faction in the region!", neighbourRegion.getId());
            currentRegionCost = UNREACHABLE_COST;
        }
        return currentRegionCost;
    }
}