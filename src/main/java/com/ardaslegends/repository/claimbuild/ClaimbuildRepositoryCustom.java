package com.ardaslegends.repository.claimbuild;

import com.ardaslegends.domain.ClaimBuild;

import java.util.Optional;

public interface ClaimbuildRepositoryCustom {
    ClaimBuild queryByNameIgnoreCase(String claimbuildName);
    Optional<ClaimBuild> queryByNameIgnoreCaseOptional(String claimbuildName);
    boolean existsByNameIgnoreCase(String claimbuildName);
}
