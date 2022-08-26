package com.ardaslegends.data.service.dto.claimbuild;

public record CreateClaimBuildDto
        (String name, String regionId, String type, String faction, Double xCoord, Double yCoord, Double zCoord,
        String productionSites, String specialBuildings, String traders, String numberOfHouses, String builtBy) {
}
