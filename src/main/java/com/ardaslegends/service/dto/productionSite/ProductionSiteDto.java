package com.ardaslegends.service.dto.productionSite;

import com.ardaslegends.domain.ProductionSiteType;
import com.ardaslegends.domain.applications.EmbeddedProductionSite;

public record ProductionSiteDto(ProductionSiteType type, String resource, long count) {
    public ProductionSiteDto(EmbeddedProductionSite embeddedProductionSite) {
        this(
                embeddedProductionSite.getProductionSite().getType(),
                embeddedProductionSite.getProductionSite().getProducedResource().getResourceName(),
                embeddedProductionSite.getCount()
        );
    }
}
