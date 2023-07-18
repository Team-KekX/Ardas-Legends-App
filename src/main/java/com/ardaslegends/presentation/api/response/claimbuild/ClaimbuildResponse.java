package com.ardaslegends.presentation.api.response.claimbuild;

import com.ardaslegends.domain.ClaimBuild;
import com.ardaslegends.domain.ClaimBuildType;
import com.ardaslegends.domain.SpecialBuilding;
import com.ardaslegends.presentation.api.response.army.ArmyResponse;
import com.ardaslegends.presentation.api.response.claimbuild.coordinate.CoordinateResponse;
import com.ardaslegends.presentation.api.response.player.PlayerResponse;
import com.ardaslegends.presentation.api.response.productionsite.claimbuild.ClaimbuildProductionSiteResponse;

public record ClaimbuildResponse(
        Long id,
        String name,
        String region,
        String claimBuildType,
        String faction,
        int armiesStationedCount,
        String siege,
        String houses,
        CoordinateResponse coordinates,
        String traders,
        PlayerResponse[] builtBy,
        String[] specialBuildings,
        ClaimbuildProductionSiteResponse[] productionSites,
        ArmyResponse[] stationedArmies,
        ArmyResponse[] createdArmies,
        Integer freeArmiesRemaining,
        Integer createdArmyCount,
        Integer createdCompaniesCount,
        Integer freeCompaniesRemaining,
        boolean atMaxArmies,
        boolean atMaxCompanies


) {
    public ClaimbuildResponse(ClaimBuild claimBuild) {
        this(
                claimBuild.getId(),
                claimBuild.getName(),
                claimBuild.getRegion().getId(),
                claimBuild.getType().getName(),
                claimBuild.getOwnedBy().getName(),
                claimBuild.getStationedArmies().size(),
                claimBuild.getSiege(),
                claimBuild.getNumberOfHouses(),
                new CoordinateResponse(claimBuild.getCoordinates()),
                claimBuild.getTraders(),
                claimBuild.getBuiltBy().stream().map(PlayerResponse::new).toArray(PlayerResponse[]::new),
                claimBuild.getSpecialBuildings().stream().map(SpecialBuilding::getName).toArray(String[]::new),
                claimBuild.getProductionSites().stream().map(ClaimbuildProductionSiteResponse::new).toArray(ClaimbuildProductionSiteResponse[]::new),
                claimBuild.getStationedArmies().stream().map(ArmyResponse::new).toArray(ArmyResponse[]::new),
                claimBuild.getCreatedArmies().stream().map(ArmyResponse::new).toArray(ArmyResponse[]::new),
                claimBuild.getFreeArmiesRemaining(),
                claimBuild.getCountOfArmies(),
                claimBuild.getCountOfTradingCompanies(),
                claimBuild.getFreeTradingCompaniesRemaining(),
                claimBuild.atMaxArmies(),
                claimBuild.atMaxTradingCompanies()
        );
    }
}
