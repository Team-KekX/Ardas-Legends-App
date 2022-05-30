/**
public class Pathfinder {
  private final GameMap _map;
  private final Pathfinder _pathfinder;

  private Pathfinder(GameMap map) {
    this._map = map;
  }

  public static Pathfinder getInstance(GameMap map){
      if(_pathfinder==null){
          this._pathfinder=new Pathfinder(map);
      }
      return this._pathfinder;
  }

  /**
   * Find the shortest path
   * Return an object which contains the path and sum of weights
   */
/**
  public Pair<Integer[], Integer> findShortestWay(
    int startRegionID,
    int endRegionID,
    Player player,
    isLeaderMove = false,
  ){
    Region startRegion = this._map.findRegion(startRegionID);
    Region endRegion = this._map.findRegion(endRegionID);
    //smallest weights between startRegion and all the other nodes
    Map<Region, Integer> smallestWeights = new Map<>();
    //for convenience, mark distance from startRegion to itself as 0
    smallestWeights.set(startRegion, 0);

    //implicit graph of all nodes and previous node in ideal paths
    Map<Region, Region> prevNodes = new Map<Region, Region>();

    //use queue for breadth first search
    //for convenience, we'll use an array, but linked list would be preferred
    Region[] nodesToVisitQueue=[];

    //record visited nodes with a set. The string is the toString of a HexID
    Set<Integer> visitedNodes = new Set<>();
    visitedNodes.add(startRegion.getId());

    let currentNode = startRegion;
    //loop through nodes
    while (true) {
      //get the shortest path so far from start to currentNode
      int dist = smallestWeights.get(currentNode);

      //iterate over current child's nodes and process
      Region[] neighbourRegions = currentNode.getNeighbours();
      for (const neighbourRegion of neighbourRegions) {
        //add node to queue if not already visited
        if (
          !visitedNodes.has(neighbourRegion.getId()) &&
          !nodesToVisitQueue.includes(neighbourRegion)
        ) {
          nodesToVisitQueue.push(neighbourRegion);
        }

        // CALCULATE COST

        //if we already have a distance to neighbourRegion, compare with this distance
        if (prevNodes.has(neighbourRegion)) {
          //get the recorded smallest distance
          int altDist = smallestWeights.get(neighbourRegion);

          //if this distance is better, update the smallest distance + prev node
          if (thisDist < altDist) {
            prevNodes.set(neighbourRegion, currentNode);
            smallestWeights.set(neighbourRegion, thisDist);
          }
        } else {
          //if there is no distance recoded yet, add now
          prevNodes.set(neighbourRegion, currentNode);
          smallestWeights.set(neighbourRegion, thisDist);
        }
      }

      //mark that we've visited this node
      visitedNodes.add(currentNode.getId());

      //exit if done
      if (nodesToVisitQueue.length === 0) {
        break;
      }

      //pull the next node to visit, if any
      Region temp = nodesToVisitQueue.shift();
      if (temp === null) {
        throw new Error("nodes to visit is empty");
      }
      currentNode = temp;
    }

    //get the shortest path into an array
    int[] path = [];

    currentNode = endRegion;
    while (currentNode !== startRegion) {
      path.push(currentNode.getId());

      Region temp = prevNodes.get(currentNode);
      currentNode = temp;
    }
    path.push(startRegion.getId());

    //reverse the path so it starts with startRegion
    path.reverse();
    int cost = smallestWeights.get(endRegion);
    return new Pair<path, cost>;
  }
}**/