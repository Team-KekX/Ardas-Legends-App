package com.ardaslegends.alroleplaysystem.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor

public class Region {

    public Integer id;
    public String name;
    public Faction claimedBy;
    public List<ClaimBuild> claimBuilds;
    public List<Region> neighboringRegions;

}
