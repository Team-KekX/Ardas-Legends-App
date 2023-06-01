package com.ardaslegends.presentation.api.response.region;

import com.ardaslegends.domain.Region;

public record RegionResponse(String id) {
    public RegionResponse(Region region) {
        this(region.getId());
    }
}
