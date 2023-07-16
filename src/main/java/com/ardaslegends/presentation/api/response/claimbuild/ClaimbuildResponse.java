package com.ardaslegends.presentation.api.response.claimbuild;

import com.ardaslegends.domain.ClaimBuild;
import com.ardaslegends.domain.ClaimBuildType;

public record ClaimbuildResponse(
        Long id,
        String name,
        String region,
        String claimBuildType,
        String faction,
        int armiesStationedCount
) {
    public ClaimbuildResponse(ClaimBuild claimBuild) {
        this(
                claimBuild.getId(),
                claimBuild.getName(),
                claimBuild.getRegion().getId(),
                claimBuild.getType().getName(),
                claimBuild.getOwnedBy().getName(),
                claimBuild.getStationedArmies().size()
        );
    }
}
