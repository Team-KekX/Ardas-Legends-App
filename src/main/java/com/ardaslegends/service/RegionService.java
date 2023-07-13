package com.ardaslegends.service;

import com.ardaslegends.domain.Region;
import com.ardaslegends.repository.region.RegionRepository;
import com.ardaslegends.service.exceptions.region.RegionServiceException;
import com.ardaslegends.service.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    public Region getRegion(String regionId) {
        log.debug("Getting single region with id {}", regionId);

        log.debug("Checking if regionId is null or empty");
        Objects.requireNonNull(regionId, "Region ID must not be null");
        ServiceUtils.checkBlankString(regionId, "regionId");

        log.debug("Fetching region {} from repository", regionId);
        Optional<Region> foundRegion = regionRepository.findById(regionId);

        if(foundRegion.isEmpty()) {
            log.warn("Found no region with id [{}]", regionId);
            throw RegionServiceException.noRegionFound(regionId);
        }

        log.info("Fetched region with id [{}]!", regionId);
        return foundRegion.get();
    }

    @Transactional(readOnly = true)
    public List<Region> getAll() {
        return regionRepository.queryAll();
    }
}
