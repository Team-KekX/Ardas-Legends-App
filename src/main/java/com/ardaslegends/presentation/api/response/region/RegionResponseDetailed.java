package com.ardaslegends.presentation.api.response.region;

import com.ardaslegends.domain.*;

public record RegionResponseDetailed(
        String id,
        String name,
        String regionType,
        String[] claimedBy,
        String[] claimbuilds,
        String[] neighbours,
        String[] characters

) {
    public RegionResponseDetailed(Region region) {
        this(
                region.getId(),
                region.getName(),
                region.getRegionType().getName(),
                region.getClaimedBy().stream().map(Faction::getName).toArray(String[]::new),
                region.getClaimBuilds().stream().map(ClaimBuild::getName).toArray(String[]::new),
                region.getNeighboringRegions().stream().map(Region::getId).toArray(String[]::new),
                region.getCharsInRegion().stream().map(RPChar::getName).toArray(String[]::new)
        );
    }
}
