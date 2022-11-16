package com.ardaslegends.repository;

import com.ardaslegends.domain.ClaimBuild;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClaimBuildRepository extends JpaRepository<ClaimBuild, Long> {

    Optional<ClaimBuild> findClaimBuildByName(String name);
}
