package com.ardaslegends.service.dto.productionSite;

import com.ardaslegends.domain.ProductionSiteType;

public record ProductionSiteDto(ProductionSiteType type, String resource, long count) {
}
