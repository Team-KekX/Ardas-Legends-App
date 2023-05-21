package com.ardaslegends.repository.applications.claimbuildapp;

import com.ardaslegends.domain.applications.ApplicationState;
import com.ardaslegends.domain.applications.ClaimbuildApplication;

import java.util.Optional;

public interface ClaimbuildApplicationRepositoryCustom {
    ClaimbuildApplication queryByNameIgnoreCaseAndState(String claimbuildName, ApplicationState state);
    Optional<ClaimbuildApplication> queryByNameIgnoreCaseAndStateOptional(String claimbuildName, ApplicationState state);
}
