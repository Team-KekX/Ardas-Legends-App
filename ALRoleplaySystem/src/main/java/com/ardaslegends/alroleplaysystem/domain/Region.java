package com.ardaslegends.alroleplaysystem.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor

public class Region {

    private final String id; //unique, the region id
    private final String name; //the name of the region (prob also unique)
    private final RegionType regionType; // type of the region
    private List<Faction> claimedBy; //the list of factions which the region is claimed by
    private List<ClaimBuild> claimBuilds; //list of claimbuilds in this region
    private final List<Region> neighboringRegions; //list of neighboring regions

    public void setClaimedBy(List<Faction> claimedBy) {
        this.claimedBy = claimedBy;
    }

    public void setClaimBuilds(List<ClaimBuild> claimBuilds) {
        this.claimBuilds = claimBuilds;
    }
}
