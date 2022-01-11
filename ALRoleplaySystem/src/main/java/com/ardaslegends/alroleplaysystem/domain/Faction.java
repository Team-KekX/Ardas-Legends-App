package com.ardaslegends.alroleplaysystem.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor

public class Faction {

    private String name; //unique, name of the faction
    private Player leader; //the player who leads this faction
    private List<Army> armies; //all current armies of this faction
    private List<Player> players; //all current players of this faction
    private List<Region> regions; //all regions this faction claims
    private List<ClaimBuild> claimBuilds; //all claimbuilds of this faction
    private String colorcode; //the faction's colorcode, used for painting the map

}
