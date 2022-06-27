package com.ardaslegends.data.service;

import com.ardaslegends.data.domain.Region;
import com.ardaslegends.data.repository.RegionRepository;
import com.ardaslegends.data.service.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor

@Slf4j
@Service
@Transactional(readOnly = true)
public class RegionService {

    private final RegionRepository regionRepository;

    public Region getRegionById(String id) {
        log.debug("Getting the region with id {}", id);

        log.trace("Checking if id is null");
        Objects.requireNonNull(id, "Region id must not be null!");

        log.debug("Fetching the region with id {} from the DB", id);
        Optional<Region> foundRegion = regionRepository.findById(id);

        if(foundRegion.isEmpty()) {
            log.warn("Region with ID {} could not be found in the database!", id);
            throw ServiceException.cannotReadEntityDueToNotExisting(Region.class.getSimpleName(), "region", id);
        }

        log.info("Successfully fetched Region '{}' from the database!", id);
        return foundRegion.get();
    }

}
