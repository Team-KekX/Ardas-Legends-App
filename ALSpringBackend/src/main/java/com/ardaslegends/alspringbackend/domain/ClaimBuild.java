package com.ardaslegends.alspringbackend.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "claimbuilds")
public class ClaimBuild {
    @Id
    private String name; //unique, name of the claimbuild

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "region", foreignKey = @ForeignKey(name = "fk_region"))
    @NotNull(message = "Claimbuild: Region must not be null")
    private Region region; //the region the claimbuild is in

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Claimbuild: ClaimbuildType must not be null")
    private ClaimBuildType type; //Type of claimbuild, e.g. HAMLET

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "owned_by", foreignKey = @ForeignKey(name = "fk_owned_by"))
    @NotNull(message = "Claimbuild: ownedBy must not be null")
    private Faction ownedBy; //faction which owns this CB

    @Embedded
    @NotNull(message = "Claimbuild: Coordinate must not be null")
    private Coordinate coordinates; //coordinate locations

    @OneToMany(mappedBy = "stationedAt")
    private List<Army> stationedArmies; //armies which are stationed in this CB

    @OneToMany(mappedBy = "originalClaimbuild")
    private List<Army> createdArmies; //armies which were created from this CB. Usually only 1 army, but capitals can create 2

    @OneToMany(mappedBy = "originalClaimbuild")
    private List<Army> createdTradingCompanies; //TCs which were created from this CB. Seperated from armies so you can search for them more easily.

    @OneToMany
    private List<ProductionSite> productionSites; //the production sites in this cb

    @ElementCollection(targetClass = SpecialBuilding.class)
    @Column(name = "special_buildings")
    @Enumerated(EnumType.STRING)
    private List<SpecialBuilding> specialBuildings; //special buildings in this cb, e.g. House of Healing

    private String traders; //traders in this CB. e.g. Dwarven Smith. Only relevant for staff so they know which traders need to be added

    private String numberOfHouses; //houses in this CB, e.g. 4 large 12 small. Only relevant for staff

    @OneToMany
    private List<Player> builtBy; //the player who built the CB

    public void setType(ClaimBuildType type) {
        this.type = type;
    }

    public void setOwnedBy(Faction ownedBy) {
        this.ownedBy = ownedBy;
    }

    public void setStationedArmies(List<Army> stationedArmies) {
        this.stationedArmies = stationedArmies;
    }

    public void setCreatedArmies(List<Army> createdArmies) {
        this.createdArmies = createdArmies;
    }

    public void setCreatedTradingCompanies(List<Army> createdTradingCompanies) {
        this.createdTradingCompanies = createdTradingCompanies;
    }

    public void setProductionSites(List<ProductionSite> productionSites) {
        this.productionSites = productionSites;
    }

    public void setSpecialBuildings(List<SpecialBuilding> specialBuildings) {
        this.specialBuildings = specialBuildings;
    }

    public void setTraders(String traders) {
        this.traders = traders;
    }

    public void setNumberOfHouses(String numberOfHouses) {
        this.numberOfHouses = numberOfHouses;
    }

}
