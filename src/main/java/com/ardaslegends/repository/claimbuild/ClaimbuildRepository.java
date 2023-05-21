package com.ardaslegends.repository.claimbuild;

import com.ardaslegends.domain.ClaimBuild;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClaimbuildRepository extends JpaRepository<ClaimBuild, Long>, ClaimbuildRepositoryCustom {

    Optional<ClaimBuild> findClaimBuildByName(String name);
}
