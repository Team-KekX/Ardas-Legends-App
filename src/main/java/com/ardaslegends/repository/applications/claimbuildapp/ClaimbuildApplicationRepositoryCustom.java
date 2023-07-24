package com.ardaslegends.repository.applications.claimbuildapp;

import com.ardaslegends.domain.applications.ApplicationState;
import com.ardaslegends.domain.applications.ClaimbuildApplication;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;
import java.util.Set;

public interface ClaimbuildApplicationRepositoryCustom {
    ClaimbuildApplication queryById(long id);
    Set<ClaimbuildApplication> queryAllByState(ApplicationState state);
    ClaimbuildApplication queryByNameIgnoreCaseAndState(String claimbuildName, ApplicationState state);
    Optional<ClaimbuildApplication> queryByNameIgnoreCaseAndStateOptional(String claimbuildName, ApplicationState state);
    boolean existsByNameIgnoreCaseAndState(String claimbuildName, ApplicationState state);
}
