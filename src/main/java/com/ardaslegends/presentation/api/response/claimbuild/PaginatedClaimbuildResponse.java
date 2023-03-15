package com.ardaslegends.presentation.api.response.claimbuild;

import com.ardaslegends.domain.ClaimBuild;
import com.ardaslegends.domain.ClaimBuildType;

public record PaginatedClaimbuildResponse(
        Long id,
        String nameOfClaimbuild,
        String region,
        ClaimBuildType claimBuildType,
        String nameOfFaction,
        int armiesStationedCount
) {
    public PaginatedClaimbuildResponse(ClaimBuild claimBuild) {
        this(
                claimBuild.getId(),
                claimBuild.getName(),
                claimBuild.getRegion().getId(),
                claimBuild.getType(),
                claimBuild.getOwnedBy().getName(),
                claimBuild.getStationedArmies().size()
        );
    }
}
