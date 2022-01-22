package com.ardaslegends.alroleplaysystem.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor

public class Faction {

    private final String name; //unique, name of the faction
    private Player leader; //the player who leads this faction
    private List<Army> armies; //all current armies of this faction
    private List<Player> players; //all current players of this faction
    private List<Region> regions; //all regions this faction claims
    private List<ClaimBuild> claimBuilds; //all claimbuilds of this faction
    private final String colorcode; //the faction's colorcode, used for painting the map

    public void setLeader(Player leader) {
        this.leader = leader;
    }

    public void setArmies(List<Army> armies) {
        this.armies = armies;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void setRegions(List<Region> regions) {
        this.regions = regions;
    }

    public void setClaimBuilds(List<ClaimBuild> claimBuilds) {
        this.claimBuilds = claimBuilds;
    }
}
