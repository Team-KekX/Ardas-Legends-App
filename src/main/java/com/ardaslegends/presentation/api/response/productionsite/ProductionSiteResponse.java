package com.ardaslegends.presentation.api.response.productionsite;

import com.ardaslegends.domain.ProductionSite;
import com.ardaslegends.domain.ProductionSiteType;

public record ProductionSiteResponse(String type, String resource) {
    public ProductionSiteResponse(ProductionSite productionSite) {
        this(
                productionSite.getType().getName(),
                productionSite.getProducedResource().getResourceName()
        );
    }
}
