package com.ardaslegends.service;

import com.ardaslegends.domain.Region;
import com.ardaslegends.repository.region.RegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor

@Slf4j
@Service
public class RegionService extends AbstractService<Region, RegionRepository> {

    private final RegionRepository regionRepository;

    @Transactional(readOnly = false)
    public List<Region> resetHasOwnership() {
        log.debug("Resetting all regions with hasOwnership set to true");

        log.trace("Fetching all regions with ownership true");
        var regions = regionRepository.findAllByHasOwnershipChangedTrue();

        log.trace("Changing every region to true with stream [size:{}]", regions.size());
        regions.stream().forEach(region -> {
            log.trace("Region [{}: ownership: {}] gets set to false", region.getId(), region.isHasOwnershipChanged());
            region.setHasOwnershipChanged(false);
        });

        log.debug("Persisting changed regions");
        regions = secureSaveAll(regions, regionRepository);

        log.info("Finished setting all to false!");
        return regions;
    }

    @Transactional(readOnly = true)
    public List<Region> getAll() {
        return regionRepository.queryAll();
    }
}
