package com.ardaslegends.data.service;

import com.ardaslegends.data.domain.*;
import com.ardaslegends.data.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor

@Service
public class Pathfinder {
  private final RegionRepository _regionRepository;

  /**
   * Find the shortest path
   * Return an object which contains the path and sum of weights
   */

  public Path findShortestWay(
    String startRegionID,
    String endRegionID,
    Player player,
    boolean isLeaderMove
  ){
    Optional<Region> startRegionOpt = this._regionRepository.findById(startRegionID);
    Optional<Region> endRegionOpt = this._regionRepository.findById(endRegionID);
    if(startRegionOpt.isEmpty() || endRegionOpt.isEmpty()){
      return null;
    }
    Region startRegion = startRegionOpt.get();
    Region endRegion = endRegionOpt.get();
    //smallest weights between startRegion and all the other nodes
    Map<Region, Integer> smallestWeights = new HashMap<>();
    //for convenience, mark distance from startRegion to itself as 0
    smallestWeights.put(startRegion, 0);

    //implicit graph of all nodes and previous node in ideal paths
    Map<Region, Region> prevNodes = new HashMap<>();

    //use queue for breadth first search
    //for convenience, we'll use an array, but linked list would be preferred
    ArrayList<Region> nodesToVisitQueue= new ArrayList<>();

    //record visited nodes with a set. The string is the toString of a HexID
    Set<String> visitedNodes = new HashSet<>();
    visitedNodes.add(startRegion.getId());

    Region currentNode = startRegion;
    //loop through nodes
    while (true) {
      //get the shortest path so far from start to currentNode
      final int dist = smallestWeights.get(currentNode);

      //iterate over current child's nodes and process
      Set<Region> neighbourRegions = currentNode.getNeighboringRegions();
      for (Region neighbourRegion : neighbourRegions) {
        //add node to queue if not already visited
        if (
          !visitedNodes.contains(neighbourRegion.getId()) &&
          !nodesToVisitQueue.contains(neighbourRegion)
        ) {
          nodesToVisitQueue.add(neighbourRegion);
        }

        // CALCULATE COST
        int thisDist = 0;

        // Check if we can move to that region as an army
        if (player.getRpChar().getBoundTo() != null || isLeaderMove) {
          if (player.getFaction().getAllies().stream().noneMatch(faction ->
                  neighbourRegion.getClaimedBy().contains(faction))
                  && !neighbourRegion.getClaimedBy().contains(player.getFaction())
                  && !neighbourRegion.getClaimedBy().isEmpty()) {
            thisDist = 1000;
          }
        }

        // Check if we are embarking or disembarking
        if (currentNode.getRegionType() != RegionType.SEA && neighbourRegion.getRegionType() == RegionType.SEA) {
          for (ClaimBuild claimbuild : currentNode.getClaimBuilds()) {
            if (claimbuild.getSpecialBuildings().contains(SpecialBuilding.HARBOUR)) {
              thisDist++;
              break;
            }
          }
        } else if (currentNode.getRegionType() == RegionType.SEA && neighbourRegion.getRegionType() != RegionType.SEA) {
          thisDist += dist + neighbourRegion.getCost() + 1;
        } else {
          thisDist += dist + neighbourRegion.getCost();
          // Check if there is no army bound to character
          if (player.getRpChar().getBoundTo() == null && !isLeaderMove) {
            thisDist /= 2;
          }
        }


        //if we already have a distance to neighbourRegion, compare with this distance
        if (prevNodes.containsKey(neighbourRegion)) {
          //get the recorded smallest distance
          final int altDist = smallestWeights.get(neighbourRegion);

          //if this distance is better, update the smallest distance + prev node
          if (thisDist < altDist) {
            prevNodes.put(neighbourRegion, currentNode);
            smallestWeights.put(neighbourRegion, thisDist);
          }
        } else {
          //if there is no distance recoded yet, add now
          prevNodes.put(neighbourRegion, currentNode);
          smallestWeights.put(neighbourRegion, thisDist);
        }
      }

      //mark that we've visited this node
      visitedNodes.add(currentNode.getId());

      //exit if done
      if (nodesToVisitQueue.size() == 0) {
        break;
      }

      //pull the next node to visit, if any
      currentNode = nodesToVisitQueue.remove(0);
      if (currentNode == null) {
        throw new Error("nodes to visit is empty");
      }
    }

    //get the shortest path into an array
    ArrayList<String> path = new ArrayList<>();

    currentNode = endRegion;
    while (!Objects.equals(currentNode.getId(), startRegion.getId())) {
      path.add(currentNode.getId());
      currentNode = prevNodes.get(currentNode);
    }
    path.add(startRegion.getId());

    //reverse the path so it starts with startRegion
    ArrayList<String> reversed_path = new ArrayList<>();
    for (String region : path) {
      reversed_path.add(region);
    }
    int cost = smallestWeights.get(endRegion);
    if (cost >= 1000) {
      cost = -1;
    }
    return new Path(cost, reversed_path);
  }
}