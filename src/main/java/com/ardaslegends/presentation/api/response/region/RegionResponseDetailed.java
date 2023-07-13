package com.ardaslegends.presentation.api.response.region;

import com.ardaslegends.domain.ClaimBuild;
import com.ardaslegends.domain.Faction;
import com.ardaslegends.domain.Region;
import com.ardaslegends.domain.RegionType;

public record RegionResponseDetailed(
        String id,
        String name,
        String regionType,
        String[] claimedBy,
        String[] claimbuilds,
        String[] neighbours

) {
    public RegionResponseDetailed(Region region) {
        this(
                region.getId(),
                region.getName(),
                region.getRegionType().getName(),
                region.getClaimedBy().stream().map(Faction::getName).toArray(String[]::new),
                region.getClaimBuilds().stream().map(ClaimBuild::getName).toArray(String[]::new),
                region.getNeighboringRegions().stream().map(Region::getId).toArray(String[]::new)
        );
    }
}