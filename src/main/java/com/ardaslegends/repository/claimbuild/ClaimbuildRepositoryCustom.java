package com.ardaslegends.repository.claimbuild;

import com.ardaslegends.domain.ClaimBuild;

public interface ClaimbuildRepositoryCustom {
    ClaimBuild queryByNameIgnoreCase(String claimbuildName);
}
