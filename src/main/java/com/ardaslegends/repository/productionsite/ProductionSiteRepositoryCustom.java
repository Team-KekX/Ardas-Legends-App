package com.ardaslegends.repository.productionsite;

import com.ardaslegends.domain.ProductionSite;
import com.ardaslegends.domain.ProductionSiteType;
import com.ardaslegends.service.dto.productionSite.ProductionSiteDto;

import java.util.Optional;
import java.util.Set;

public interface ProductionSiteRepositoryCustom {

    Set<ProductionSite> queryAll();
    ProductionSite queryByTypeAndResource(ProductionSiteType type, String resource);
    Optional<ProductionSite> queryByTypeAndResourceOptional(ProductionSiteType type, String resource);
}
