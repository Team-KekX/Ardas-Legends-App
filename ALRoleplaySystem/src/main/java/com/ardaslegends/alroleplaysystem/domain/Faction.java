package com.ardaslegends.alroleplaysystem.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor

public class Faction {

    public String name; //unique, name of the faction
    public Player leader; //the player who leads this faction
    public List<Army> armies; //all current armies of this faction
    public List<Player> players; //all current players of this faction
    public List<Region> regions; //all regions this faction claims
    public List<ClaimBuild> claimBuilds; //all claimbuilds of this faction
    public String colorcode; //the faction's colorcode, used for painting the map

}
