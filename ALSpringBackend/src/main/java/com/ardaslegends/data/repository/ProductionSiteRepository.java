package com.ardaslegends.data.repository;

import com.ardaslegends.data.domain.ProductionSite;
import com.ardaslegends.data.domain.ProductionSiteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductionSiteRepository extends JpaRepository<ProductionSite, Long> {
    //TODO Test this class

    Optional<ProductionSite> findProductionSiteByTypeAndProducedResource(ProductionSiteType type, String resource);
}
