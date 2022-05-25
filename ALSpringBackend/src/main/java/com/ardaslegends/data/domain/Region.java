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
@Table(name = "regions")
public class Region {

    @Id
    private String id; //unique, the region id

    private String name; //the name of the region (prob also unique)

    @Enumerated(EnumType.STRING)
    private RegionType regionType; // type of the region

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "faction_claimed_regions",
            joinColumns = { @JoinColumn(name = "region", foreignKey = @ForeignKey(name = "fk_region"))},
            inverseJoinColumns = { @JoinColumn(name = "faction", foreignKey = @ForeignKey(name = "fk_faction")) })
    private Set<Faction> claimedBy; //the list of factions which the region is claimed by

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "ownedBy")
    private List<ClaimBuild> claimBuilds; //list of claimbuilds in this region

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "region_neighbours",
            joinColumns = { @JoinColumn(name = "region", foreignKey = @ForeignKey(name = "fk_region"))},
            inverseJoinColumns = { @JoinColumn(name = "neighbour", foreignKey = @ForeignKey(name = "fk_neighbour")) })
    private Set<Region> neighboringRegions; //list of neighboring regions


}
