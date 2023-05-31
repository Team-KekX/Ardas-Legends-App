package com.ardaslegends.repository.productionsite;

import com.ardaslegends.domain.ProductionSite;
import com.ardaslegends.domain.ProductionSiteType;
import com.ardaslegends.domain.QProductionSite;
import com.ardaslegends.repository.exceptions.ProductionSiteRepositoryException;
import com.ardaslegends.service.dto.productionSite.ProductionSiteDto;
import lombok.val;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ProductionSiteRepositoryImpl extends QuerydslRepositorySupport implements ProductionSiteRepositoryCustom {
    public ProductionSiteRepositoryImpl() {
        super(ProductionSite.class);
    }

    @Override
    public ProductionSite queryByTypeAndResource(ProductionSiteType type, String resource) {
        val fetchedSite = queryByTypeAndResourceOptional(type, resource);

        return fetchedSite.orElseThrow(() -> ProductionSiteRepositoryException.entityNotFound("(type, resource)", "(" + type.getName() + ", " + resource + ")"));
    }

    @Override
    public Optional<ProductionSite> queryByTypeAndResourceOptional(ProductionSiteType type, String resource) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(resource);

        QProductionSite qProductionSite = QProductionSite.productionSite;

        val fetchedSite = from(qProductionSite)
                .where(qProductionSite.type.eq(type).and(qProductionSite.producedResource().resourceName.equalsIgnoreCase(resource)))
                .fetchFirst();

        return Optional.ofNullable(fetchedSite);
    }
}
