package com.ardaslegends.service.dto.claimbuild;

public record CreateClaimBuildDto
        (String name, String regionId, String type, String faction, Integer xCoord, Integer yCoord, Integer zCoord,
        String productionSites, String specialBuildings, String traders, String siege, String numberOfHouses, String builtBy) {
}
