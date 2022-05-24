package com.ardaslegends.alspringbackend.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "regions")
public class Region {

    @Id
    private String id; //unique, the region id

    private String name; //the name of the region (prob also unique)

    @Enumerated(EnumType.STRING)
    private RegionType regionType; // type of the region

    @ManyToMany
    private List<Faction> claimedBy; //the list of factions which the region is claimed by

    @OneToMany(mappedBy = "ownedBy")
    private List<ClaimBuild> claimBuilds; //list of claimbuilds in this region

    @OneToMany
    private List<Region> neighboringRegions; //list of neighboring regions

    public void setClaimedBy(List<Faction> claimedBy) {
        this.claimedBy = claimedBy;
    }

    public void setClaimBuilds(List<ClaimBuild> claimBuilds) {
        this.claimBuilds = claimBuilds;
    }

}
