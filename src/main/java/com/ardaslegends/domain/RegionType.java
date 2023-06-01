package com.ardaslegends.domain;

import lombok.Getter;

public enum RegionType {

    // The number next to the Enum represensts the cost/duration to move into such a region

    SEA("Sea", 1),
    LAND("Land", 1),
    HILL("Hill", 2),
    ICE("Ice",2),
    DESERT("Desert", 3),
    FOREST("Forest", 3),
    SWAMP("Swamp", 4),
    JUNGLE("Jungle", 5),
    MOUNTAIN("Mountain", 6);

    private final int cost;

    @Getter
    private final String name;
    private RegionType(String name, int cost){
        this.name = name;
        this.cost = cost;
    }

    public int getCost() {
        return cost * 24;
    }
}
