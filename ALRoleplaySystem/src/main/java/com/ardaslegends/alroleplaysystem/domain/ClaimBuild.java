package com.ardaslegends.alroleplaysystem.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor

public class ClaimBuild {
    private String name; //unique, name of the claimbuild
    private String region; //the Id of the region the claimbuild is in
    private ClaimBuildType type; //Type of claimbuild, e.g. HAMLET
    private Faction ownedBy; //faction which owns this CB
    private Coordinate coordinates; //coordinate locations
    private List<Army> stationedArmies; //armies which are stationed in this CB
    private List<Army> createdArmies; //armies which where created from this CB. Usually only 1 army, but capitals can create 2
    private List<ProductionSite> productionSites; //the production sites in this cb
    private List<SpecialBuilding> specialBuildings; //special buildings in this cb, e.g. House of Healing
    private String traders; //traders in this CB. e.g. Dwarven Smith. Only relevant for staff so they know which traders need to be added
    private List<Player> builtBy; //the player who built the CB
}
