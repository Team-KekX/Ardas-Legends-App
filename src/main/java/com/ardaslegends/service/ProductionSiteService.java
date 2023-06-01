package com.ardaslegends.service;

import com.ardaslegends.domain.ProductionSite;
import com.ardaslegends.repository.ProductionSiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductionSiteService {

    private final ProductionSiteRepository productionSiteRepository;

    public Set<ProductionSite> getAll() {
        return productionSiteRepository.queryAll();
    }
}
