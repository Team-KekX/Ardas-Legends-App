package com.ardaslegends.repository.region;

import com.ardaslegends.domain.Region;

import java.util.Optional;

public interface RegionRepositoryCustom {

    Region queryById(String id);
    Optional<Region> queryByIdOptional(String id);

}
