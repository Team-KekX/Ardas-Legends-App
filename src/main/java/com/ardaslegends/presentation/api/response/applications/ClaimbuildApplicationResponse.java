package com.ardaslegends.presentation.api.response.applications;

import com.ardaslegends.domain.ClaimBuildType;
import com.ardaslegends.domain.Coordinate;
import com.ardaslegends.domain.ProductionSite;
import com.ardaslegends.domain.SpecialBuilding;
import com.ardaslegends.domain.applications.ClaimbuildApplication;
import com.ardaslegends.presentation.api.response.player.PlayerResponse;
import com.ardaslegends.service.dto.productionSite.ProductionSiteDto;

import java.time.OffsetDateTime;

public record ClaimbuildApplicationResponse(
        long id,
        PlayerResponse application,
        String name,
        String faction,
        String region,
        ClaimBuildType type,
        Coordinate coordinate,
        ProductionSiteDto[] productionSites,
        String[] specialBuildings,
        String traders,
        String siege,
        String houses,
        PlayerResponse[] builtBy,
        OffsetDateTime appliedAt,
        PlayerResponse[] acceptedBy,
        short voteCount
) {
    public ClaimbuildApplicationResponse(ClaimbuildApplication application) {
        this(
                application.getId(),
                new PlayerResponse(application.getApplicant()),
                application.getClaimbuildName(),
                application.getOwnedBy().getName(),
                application.getRegion().getId(),
                application.getClaimBuildType(),
                application.getCoordinate(),
                application.getProductionSites().stream().map(ProductionSiteDto::new).toArray(ProductionSiteDto[]::new),
                application.getSpecialBuildings().stream().map(SpecialBuilding::getName).toArray(String[]::new),
                application.getTraders(),
                application.getSiege(),
                application.getNumberOfHouses(),
                application.getBuiltBy().stream().map(PlayerResponse::new).toArray(PlayerResponse[]::new),
                application.getAppliedAt(),
                application.getAcceptedBy().stream().map(PlayerResponse::new).toArray(PlayerResponse[]::new),
                application.getVoteCount()
        );
    }
}
