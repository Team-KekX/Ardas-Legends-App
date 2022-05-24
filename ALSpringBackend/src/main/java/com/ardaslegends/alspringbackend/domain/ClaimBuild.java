package com.ardaslegends.alspringbackend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor

public class ClaimBuild {
    private String name; //unique, name of the claimbuild
    private String region; //the Id of the region the claimbuild is in
    private ClaimBuildType type; //Type of claimbuild, e.g. HAMLET
    private Faction ownedBy; //faction which owns this CB
    private Coordinate coordinates; //coordinate locations
    private List<Army> stationedArmies; //armies which are stationed in this CB
    private List<Army> createdArmies; //armies which were created from this CB. Usually only 1 army, but capitals can create 2
    private List<Army> createdTradingCompanies; //TCs which were created from this CB. Seperated from armies so you can search for them more easily.
    private List<ProductionSite> productionSites; //the production sites in this cb
    private List<SpecialBuilding> specialBuildings; //special buildings in this cb, e.g. House of Healing
    private String traders; //traders in this CB. e.g. Dwarven Smith. Only relevant for staff so they know which traders need to be added
    private String numberOfHouses; //houses in this CB, e.g. 4 large 12 small. Only relevant for staff
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
