package com.ardaslegends.alroleplaysystem.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor

public class Faction {

    public String name;
    public Player leader;
    public List<Army> armies;
    public List<Player> players;
    public List<Region> regions;
    public List<ClaimBuild> claimBuilds;
    public String colorcode;

}
