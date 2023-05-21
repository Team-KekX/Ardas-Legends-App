package com.ardaslegends.repository.region;

import com.ardaslegends.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<Region, String> {

    @Override
    Optional<Region> findById(String s);

    List<Region> findAllByHasOwnershipChangedTrue();
}
