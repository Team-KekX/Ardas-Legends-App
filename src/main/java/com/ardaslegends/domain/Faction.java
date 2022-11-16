package com.ardaslegends.domain;

import com.ardaslegends.service.exceptions.FactionServiceException;
import com.fasterxml.jackson.annotation.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Slf4j
@Entity
@Table(name = "factions")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "name")
public final class Faction extends AbstractDomainEntity {

    @Id
    private Long id;

    @Column(unique = true)
    private String name; //unique, name of the faction

    private InitialFaction initialFaction;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Player leader; //the player who leads this faction

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "faction")
    @JsonIdentityReference(alwaysAsId=true)
    private List<Army> armies; //all current armies of this faction
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "faction")
    private List<Player> players; //all current players of this faction
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "claimedBy")
    private Set<Region> regions; //all regions this faction claims
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "ownedBy")
    private List<ClaimBuild> claimBuilds; //all claimbuilds of this faction

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "faction_allies",
            joinColumns = { @JoinColumn(name = "faction", foreignKey = @ForeignKey(name = "fk_faction"))},
            inverseJoinColumns = { @JoinColumn(name = "ally_faction", foreignKey = @ForeignKey(name = "fk_ally_faction")) })
    private List<Faction> allies; //allies of this faction
    private String colorcode; //the faction's colorcode, used for painting the map

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Region homeRegion; //Homeregion of the faction

    @Length(max = 512)
    private String factionBuffDescr; //The description of this faction's buff

    @Column(name = "food_stockpile")
    private Integer foodStockpile = 0; // Food stacks in a factions stockpile, these are used for army movements

    @ElementCollection
    @CollectionTable(name = "army_sieges")
    private List<String> aliases;

    public Faction(String name, Player leader, List<Army> armies, List<Player> players, Set<Region> regions, List<ClaimBuild> claimBuilds, List<Faction> allies, String colorcode, Region homeRegion, String factionBuffDescr) {
        this.name = name;
        this.leader = leader;
        this.armies = armies;
        this.players = players;
        this.regions = regions;
        this.claimBuilds = claimBuilds;
        this.allies = allies;
        this.colorcode = colorcode;
        this.homeRegion = homeRegion;
        this.factionBuffDescr = factionBuffDescr;
        this.foodStockpile = 0;
    }

    @JsonIgnore
    public void addFoodToStockpile(int amount) {
        log.debug("Adding food [amount:{}] to stockpile of faction [{}]", amount, this.name);
        if(amount < 0) {
            log.warn("Amount to add is below 0 [{}]", amount);
            throw FactionServiceException.negativeStockpileAddNotSupported();
        }
        this.foodStockpile += amount;
    }

    @JsonIgnore
    public void subtractFoodFromStockpile(int amount) {
        log.debug("Removing food [amount: {}] from stockpile of faction [{}]", amount, this.name);
        if(amount < 0) {
            log.warn("Amount to remove is above 0 [{}]", amount);
            throw FactionServiceException.negativeStockpileSubtractNotSupported();
        }

        if(this.foodStockpile - amount < 0) {
            log.warn("Subtract would set the stockpile of faction [{}] to below zero!", this.name);
            throw FactionServiceException.notEnoughFoodInStockpile(this.name, this.foodStockpile, amount);
        }
        this.foodStockpile -= amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Faction faction = (Faction) o;
        return name.equals(faction.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
