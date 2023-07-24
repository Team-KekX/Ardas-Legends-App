package com.ardaslegends.presentation.api.response.productionsite.claimbuild;

import com.ardaslegends.domain.ProductionClaimbuild;
import com.ardaslegends.presentation.api.response.productionsite.ProductionSiteResponse;

public record ClaimbuildProductionSiteResponse(
        Long amount,
        ProductionSiteResponse productionSite
) {
    public ClaimbuildProductionSiteResponse(ProductionClaimbuild productionClaimbuild) {
        this(
                productionClaimbuild.getCount(),
                new ProductionSiteResponse(productionClaimbuild.getProductionSite())
        );
    }
}
