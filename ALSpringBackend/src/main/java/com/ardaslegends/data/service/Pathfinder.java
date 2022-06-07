package com.ardaslegends.data.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.ardaslegends.data.domain.Player;
import com.ardaslegends.data.domain.Region;
import com.ardaslegends.data.repository.RegionRepository;

class Path{
  private int _cost;
  private ArrayList<String> _path;

  public Path(int cost, ArrayList<String> path){
    this._cost=cost;
    this._path=path;
  }

  public int getCost(){
    return this._cost;
  }

  public ArrayList<String> getPath(){
    return this._path;
  }


}

public class Pathfinder {
  private final RegionRepository _regionRepository;
  private static Pathfinder _pathfinder = null;

  private Pathfinder(RegionRepository map) {
    this._regionRepository = map;
  }

  public static Pathfinder getInstance(RegionRepository map){
      if(_pathfinder==null){
          _pathfinder=new Pathfinder(map);
      }
      return _pathfinder;
  }

  /**
   * Find the shortest path
   * Return an object which contains the path and sum of weights
   */

  public Path findShortestWay(
    String startRegionID,
    String endRegionID,
    Player player
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
      int dist = smallestWeights.get(currentNode);

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
        int thisDist=0;


        //if we already have a distance to neighbourRegion, compare with this distance
        if (prevNodes.containsKey(neighbourRegion.getId())) {
          //get the recorded smallest distance
          int altDist = smallestWeights.get(neighbourRegion);

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
      Region temp = nodesToVisitQueue.remove(0);
      if (temp == null) {
        throw new Error("nodes to visit is empty");
      }
      currentNode = temp;
    }

    //get the shortest path into an array
    ArrayList<String> path = new ArrayList<>();

    currentNode = endRegion;
    while (currentNode != startRegion) {
      path.add(currentNode.getId());

      Region temp = prevNodes.get(currentNode);
      currentNode = temp;
    }
    path.add(startRegion.getId());

    //reverse the path so it starts with startRegion
    ArrayList<String> reversed_path = new ArrayList<>();
    for(String region : path){
      reversed_path.add(region);
    }
    int cost = smallestWeights.get(endRegion);
    return new Path(cost, reversed_path);
  }
}