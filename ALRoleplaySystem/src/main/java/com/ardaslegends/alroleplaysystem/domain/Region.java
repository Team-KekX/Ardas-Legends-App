package com.ardaslegends.alroleplaysystem.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor

public class Region {

    public Integer id; //unique, the region id
    public String name; //the name of the region
    public List<Faction> claimedBy; //the list of factions which the region is claimed by
    public List<ClaimBuild> claimBuilds; //list of claimbuilds in this region
    public List<Region> neighboringRegions; //list of neighboring regions

}
