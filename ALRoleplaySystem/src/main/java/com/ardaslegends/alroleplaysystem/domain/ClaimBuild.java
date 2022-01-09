package com.ardaslegends.alroleplaysystem.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor

public class ClaimBuild {
    public String name; //unique, name of the claimbuild
    public ClaimBuildType type; //Type of claimbuild, e.g. HAMLET
    public Faction ownedBy; //faction which owns this CB
    public Coordinate coordinates; //coordinate locations
    public List<Army> stationedArmies; //armies which are stationed in this CB
    public List<Army> createdArmies; //armies which where created from this CB. Usually only 1 army, but capitals can create 2
    public List<ProductionSite> productionSites; //the production sites in this cb
    public List<SpecialBuilding> specialBuildings; //special buildings in this cb, e.g. House of Healing
    public String traders; //traders in this CB. e.g. Dwarven Smith. Only relevant for staff so they know which traders need to be added
    public List<Player> builtBy; //the player who built the CB
}
