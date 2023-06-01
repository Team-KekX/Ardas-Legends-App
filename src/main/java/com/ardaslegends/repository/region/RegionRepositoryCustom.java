package com.ardaslegends.repository.region;

import com.ardaslegends.domain.Region;

import java.util.Optional;
import java.util.Set;

public interface RegionRepositoryCustom {

    Set<Region> queryAll();
    Region queryById(String id);
    Optional<Region> queryByIdOptional(String id);

}
