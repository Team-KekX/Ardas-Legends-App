package com.ardaslegends.alspringbackend.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "factions")
public class Faction {

    @Id
    private String name; //unique, name of the faction
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
