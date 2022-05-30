package com.ardaslegends.data.domain;

public enum RegionType {

    // The number next to the Enum represensts the cost/duration to move into such a region

    SEA(1),
    LAND(1),
    MOUNTAIN(1),
    FOREST(1),
    JUNGLE(1),
    DESERT(1),
    ICE(1),
    SWAMP(1),
    HILL(1);

    private final int cost;
    private RegionType(int cost){
        this.cost = cost;
    }

    public int getCost() {
        return cost;
    }
}
