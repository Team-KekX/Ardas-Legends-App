package com.ardaslegends.data.repository;

import com.ardaslegends.data.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<Region, String> {

    @Override
    Optional<Region> findById(String s);
}
