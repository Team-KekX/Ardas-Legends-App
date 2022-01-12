package com.ardaslegends.alroleplaysystem.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor

public class Region {

    private String id; //unique, the region id
    private String name; //the name of the region (prob also unique)
    private List<Faction> claimedBy; //the list of factions which the region is claimed by
    private List<ClaimBuild> claimBuilds; //list of claimbuilds in this region
    private List<Region> neighboringRegions; //list of neighboring regions

}
