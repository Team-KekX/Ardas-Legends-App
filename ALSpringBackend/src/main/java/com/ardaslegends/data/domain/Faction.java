package com.ardaslegends.data.domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "factions")
public class Faction {

    @Id
    private String name; //unique, name of the faction

    @OneToOne
    private Player leader; //the player who leads this faction

    @OneToMany(mappedBy = "faction")
    private List<Army> armies; //all current armies of this faction
    @OneToMany(mappedBy = "faction")
    private List<Player> players; //all current players of this faction
    @ManyToMany(mappedBy = "claimedBy")
    private Set<Region> regions; //all regions this faction claims
    @OneToMany(mappedBy = "ownedBy")
    private List<ClaimBuild> claimBuilds; //all claimbuilds of this faction
    
    private String colorcode; //the faction's colorcode, used for painting the map


}
